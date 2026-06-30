package opty.repository;

import opty.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseJdbcRepository {

    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    protected void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
