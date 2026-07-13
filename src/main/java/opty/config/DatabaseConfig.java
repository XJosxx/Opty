package opty.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de configuración y gestión de la conexión a la base de datos de Opty.
 * Utiliza HikariCP para manejar un pool de conexiones eficientemente.
 */
public class DatabaseConfig {

    // Valores de configuración por defecto (se usan si no existe el archivo config.properties)
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DB = "optica_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "root";

    // Parámetros de conexión activos
    private static String host = DEFAULT_HOST;
    private static int port = DEFAULT_PORT;
    private static String db = DEFAULT_DB;
    private static String user = DEFAULT_USER;
    private static String pass = DEFAULT_PASS;
    private static boolean useSSL = false;
    private static boolean loaded = false; // Indica si la configuración ya fue cargada

    // Pool de conexiones HikariCP
    private static HikariDataSource dataSource;

    // Constructor privado para evitar instanciación (patrón Singleton / utilitario)
    private DatabaseConfig() {
    }

    /**
     * Carga la configuración desde el archivo 'config.properties'.
     * Intenta buscarlo primero en la ruta externa 'config/config.properties'.
     * Si no existe, intenta cargarlo desde el classpath (recursos internos).
     * Si ninguno existe, usa los valores por defecto (localhost, root, etc.).
     */
    public static synchronized void load() {
        if (loaded) return; // Evita recargar si ya está lista

        var path = Paths.get("config/config.properties");
        if (Files.exists(path)) {
            loadFromFile(path);
        } else {
            try (var is = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (is != null) {
                    loadFromStream(is);
                } else {
                    // Si el archivo no existe en ninguna ruta, lo reporta en consola y usará defaults
                    System.err.println("[Opty] No se encontró config.properties en la ruta externa ni en el classpath. Usando valores locales por defecto.");
                }
            } catch (IOException e) {
                System.err.println("[Opty] Error al leer config.properties desde el classpath. Usando valores locales por defecto: " + e.getMessage());
            }
        }
        initDataSource();
        loaded = true;
    }

    /**
     * Carga el archivo de propiedades desde una ruta específica del sistema de archivos.
     */
    private static void loadFromFile(Path path) {
        try (var is = Files.newInputStream(path)) {
            loadFromStream(is);
            System.out.println("[Opty] Configuración cargada correctamente desde " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[Opty] Error al leer " + path + ": " + e.getMessage());
        }
    }

    /**
     * Procesa un flujo de entrada (Stream) y lee las propiedades.
     */
    private static void loadFromStream(InputStream is) throws IOException {
        var props = new Properties();
        props.load(is);
        configureFromProperties(props);
    }

    /**
     * Permite configurar los parámetros de conexión de manera manual/programática.
     * Útil para entornos de prueba, desarrollo o cuando se requiere una base de datos dinámica.
     * NOTA: Actualmente no se usa en el flujo principal de la aplicación, pero se mantiene como utilidad.
     */
    public static synchronized void configure(String host, int port, String db, String user, String pass, boolean useSSL) {
        DatabaseConfig.host = host;
        DatabaseConfig.port = port;
        DatabaseConfig.db = db;
        DatabaseConfig.user = user;
        DatabaseConfig.pass = pass;
        DatabaseConfig.useSSL = useSSL;

        // Si ya había un pool de conexiones activo, lo cerramos para aplicar la nueva configuración
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
        initDataSource();
        loaded = true;
    }

    /**
     * Lee y asigna las propiedades obtenidas del archivo config.properties.
     * Cuenta con control de fallos en caso de que falten campos o el puerto no sea un número válido.
     */
    public static void configureFromProperties(Properties props) {
        host = props.getProperty("db.host", DEFAULT_HOST);
        
        // Evitamos que la aplicación falle si el puerto en las propiedades no es un número válido
        try {
            port = Integer.parseInt(props.getProperty("db.port", String.valueOf(DEFAULT_PORT)));
        } catch (NumberFormatException e) {
            System.err.println("[Opty] Puerto no válido en config.properties. Usando puerto por defecto: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
        
        db = props.getProperty("db.name", DEFAULT_DB);
        user = props.getProperty("db.user", DEFAULT_USER);
        pass = props.getProperty("db.password", DEFAULT_PASS);
        useSSL = Boolean.parseBoolean(props.getProperty("db.useSSL", "false"));
    }

    /**
     * Inicializa el pool de conexiones HikariCP usando los parámetros cargados.
     */
    private static synchronized void initDataSource() {
        if (dataSource != null) return; // Evita duplicar la inicialización

        // Construcción de la URL de conexión JDBC de MySQL
        var url = new StringBuilder("jdbc:mysql://")
                .append(host).append(":").append(port).append("/")
                .append(db)
                .append("?useSSL=").append(useSSL)
                .append("&serverTimezone=UTC")
                .append("&allowPublicKeyRetrieval=").append(!useSSL)
                .toString();

        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);

        // Configuración de rendimiento y capacidad del pool
        config.setMaximumPoolSize(10); // Máximo de 10 conexiones simultáneas
        config.setMinimumIdle(2);      // Mantiene al menos 2 conexiones inactivas preparadas
        config.setIdleTimeout(30000);  // Cierra conexiones inactivas después de 30 segundos
        config.setConnectionTimeout(10000); // Límite de 10 segundos esperando una conexión antes de fallar
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Ajustes de caché de consultas preparadas (Prepared Statements) recomendados para MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);
        System.out.println("[Opty] HikariCP Connection Pool inicializado correctamente.");
    }

    /**
     * Proporciona una conexión de base de datos activa del pool.
     * Si no se ha cargado la configuración todavía, la inicializa automáticamente.
     */
    public static Connection getConnection() throws SQLException {
        if (!loaded || dataSource == null) {
            load();
        }
        return dataSource.getConnection();
    }

    /**
     * Cierra de manera segura el pool de conexiones HikariCP.
     * Debe llamarse al apagar la aplicación para liberar recursos de red de forma limpia.
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[Opty] HikariCP Connection Pool cerrado.");
        }
    }
}
