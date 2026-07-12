package opty.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseMigrationManager {

    private static final String TABLE_SCHEMA_VERSION = "schema_version";

    public static void runMigrations() {
        System.out.println("[Migration] Iniciando flujo de migraciones de base de datos...");
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Crear tabla schema_version si no existe
            if (!tableExists(conn, TABLE_SCHEMA_VERSION)) {
                System.out.println("[Migration] Creando tabla de control '" + TABLE_SCHEMA_VERSION + "'...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE TABLE " + TABLE_SCHEMA_VERSION + " (" +
                            "version INT PRIMARY KEY, " +
                            "description VARCHAR(255) NOT NULL, " +
                            "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ") ENGINE=InnoDB;");
                }

                // 2. Técnica de Baseline: Si la tabla 'pacientes' ya existe en el sistema,
                // significa que la base de datos ya tenía el esquema inicial (V1, V2, V3).
                // Registramos estas versiones para no sobreescribir datos existentes.
                if (tableExists(conn, "pacientes")) {
                    System.out.println("[Migration] Base de datos existente detectada. Aplicando baseline (V1, V2, V3)...");
                    registerMigration(conn, 1, "Baseline Schema Inicial");
                    registerMigration(conn, 2, "Baseline Procedimientos y Vistas");
                    registerMigration(conn, 3, "Baseline Datos Semilla");
                }
            }

            // 3. Obtener versiones aplicadas
            List<Integer> appliedVersions = getAppliedVersions(conn);

            // 4. Definir migraciones pendientes
            // En un entorno de producción, las migraciones se leen de archivos .sql en resources.
            // Para simplicidad académica y portabilidad en el JAR, ejecutamos los scripts pendientes.
            
            // Versión 4: Nuevos Datos de Prueba y Medidas JSON
            if (!appliedVersions.contains(4)) {
                System.out.println("[Migration] Ejecutando migración V4: Carga de Datos de Prueba y Medidas JSON...");
                runSqlScriptFromResource(conn, "/migrations/V4__datos_extra.sql");
                registerMigration(conn, 4, "Carga de Datos de Prueba y Medidas JSON");
            }

            // Versión 5: Actualización de Esquema (Ejemplo: Añadir columna email a pacientes)
            if (!appliedVersions.contains(5)) {
                System.out.println("[Migration] Ejecutando migración V5: Modificación de Esquema (Añadir columna email a pacientes)...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE pacientes ADD COLUMN email VARCHAR(100) NULL AFTER telefono;");
                }
                registerMigration(conn, 5, "Añadir columna email a pacientes");
            }

            System.out.println("[Migration] Flujo de migraciones completado con éxito.");

        } catch (Exception e) {
            System.err.println("[Migration] ERROR CRÍTICO durante el flujo de migraciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws Exception {
        DatabaseMetaData dbmd = conn.getMetaData();
        try (ResultSet rs = dbmd.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static List<Integer> getAppliedVersions(Connection conn) throws Exception {
        List<Integer> versions = new ArrayList<>();
        String sql = "SELECT version FROM " + TABLE_SCHEMA_VERSION + " ORDER BY version ASC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                versions.add(rs.getInt("version"));
            }
        }
        return versions;
    }

    private static void registerMigration(Connection conn, int version, String description) throws Exception {
        String sql = "INSERT INTO " + TABLE_SCHEMA_VERSION + " (version, description) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, version);
            ps.setString(2, description);
            ps.executeUpdate();
        }
    }

    private static void runSqlScriptFromResource(Connection conn, String resourcePath) throws Exception {
        try (var is = DatabaseMigrationManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("No se encontró el script de migración en: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Ignorar comentarios SQL
                    if (line.trim().startsWith("--") || line.trim().startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }
                    sb.append(line).append("\n");
                }

                // Dividir instrucciones por el delimitador ";"
                // Nota: Evitamos dividir dentro de triggers o procedures complejos
                String[] statements = sb.toString().split(";");
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : statements) {
                        if (!sql.trim().isEmpty()) {
                            stmt.execute(sql.trim());
                        }
                    }
                }
            }
        }
    }
}
