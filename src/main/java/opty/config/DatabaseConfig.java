package opty.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DB = "optica_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static String host = DEFAULT_HOST;
    private static int port = DEFAULT_PORT;
    private static String db = DEFAULT_DB;
    private static String user = DEFAULT_USER;
    private static String pass = DEFAULT_PASS;
    private static boolean useSSL = false;
    private static boolean loaded = false;

    private DatabaseConfig() {
    }

    public static void load() {
        if (loaded) return;
        var path = Paths.get("config/config.properties");
        if (Files.exists(path)) {
            loadFromFile(path);
        } else {
            try (var is = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (is != null) loadFromStream(is);
            } catch (IOException e) {
                System.err.println("[Opty] No se encontró config.properties. Usando defaults locales.");
            }
        }
        loaded = true;
    }

    private static void loadFromFile(Path path) {
        try (var is = Files.newInputStream(path)) {
            loadFromStream(is);
            System.out.println("[Opty] Configuración cargada desde " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[Opty] Error al leer " + path + ": " + e.getMessage());
        }
    }

    private static void loadFromStream(InputStream is) throws IOException {
        var props = new Properties();
        props.load(is);
        configureFromProperties(props);
    }

    public static void configure(String host, int port, String db, String user, String pass, boolean useSSL) {
        DatabaseConfig.host = host;
        DatabaseConfig.port = port;
        DatabaseConfig.db = db;
        DatabaseConfig.user = user;
        DatabaseConfig.pass = pass;
        DatabaseConfig.useSSL = useSSL;
        loaded = true;
    }

    public static void configureFromProperties(Properties props) {
        host = props.getProperty("db.host", DEFAULT_HOST);
        port = Integer.parseInt(props.getProperty("db.port", String.valueOf(DEFAULT_PORT)));
        db = props.getProperty("db.name", DEFAULT_DB);
        user = props.getProperty("db.user", DEFAULT_USER);
        pass = props.getProperty("db.password", DEFAULT_PASS);
        useSSL = Boolean.parseBoolean(props.getProperty("db.useSSL", "false"));
    }

    public static Connection getConnection() throws SQLException {
        var url = new StringBuilder("jdbc:mysql://")
                .append(host).append(":").append(port).append("/")
                .append(db)
                .append("?useSSL=").append(useSSL)
                .append("&serverTimezone=UTC")
                .append("&allowPublicKeyRetrieval=").append(!useSSL)
                .toString();

        return DriverManager.getConnection(url, user, pass);
    }
}
