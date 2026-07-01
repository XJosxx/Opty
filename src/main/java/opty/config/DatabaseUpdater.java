package opty.config;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseUpdater {
    public static void main(String[] args) {
        DatabaseConfig.load();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Updating stored procedures in active database...");
            
            // Drop old sp_procesar_venta
            stmt.execute("DROP PROCEDURE IF EXISTS sp_procesar_venta");
            // Recreate sp_procesar_venta
            stmt.execute("CREATE PROCEDURE sp_procesar_venta(\n" +
                    "    IN  p_paciente_id      INT,\n" +
                    "    IN  p_usuario_id       INT,\n" +
                    "    IN  p_tienda_id        INT,\n" +
                    "    IN  p_tipo_comprobante ENUM('BOLETA','FACTURA'),\n" +
                    "    IN  p_producto_id      INT,\n" +
                    "    IN  p_cantidad         INT,\n" +
                    "    IN  p_metodo_pago      ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA'),\n" +
                    "    OUT p_resultado        INT,\n" +
                    "    OUT p_mensaje          VARCHAR(200)\n" +
                    ")\n" +
                    "BEGIN\n" +
                    "    DECLARE v_stock        INT DEFAULT 0;\n" +
                    "    DECLARE v_precio       DECIMAL(10,2) DEFAULT 0;\n" +
                    "    DECLARE v_venta_id     INT;\n" +
                    "    DECLARE v_ticket       VARCHAR(50);\n" +
                    "\n" +
                    "    DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                    "    BEGIN\n" +
                    "        ROLLBACK;\n" +
                    "        SET p_resultado = 0;\n" +
                    "        SET p_mensaje   = 'ERROR: No se pudo procesar la venta.';\n" +
                    "        RESIGNAL;\n" +
                    "    END;\n" +
                    "\n" +
                    "    SELECT stock_actual, precio_venta\n" +
                    "    INTO v_stock, v_precio\n" +
                    "    FROM productos\n" +
                    "    WHERE id = p_producto_id\n" +
                    "      AND tienda_id = p_tienda_id\n" +
                    "      AND activo = TRUE;\n" +
                    "\n" +
                    "    IF v_stock IS NULL THEN\n" +
                    "        SET p_resultado = 0;\n" +
                    "        SET p_mensaje   = 'ERROR: Producto no encontrado.';\n" +
                    "\n" +
                    "    ELSEIF v_stock < p_cantidad THEN\n" +
                    "        SET p_resultado = 0;\n" +
                    "        SET p_mensaje   = CONCAT('ERROR: Stock insuficiente. Disponible: ', v_stock);\n" +
                    "\n" +
                    "    ELSE\n" +
                    "        START TRANSACTION;\n" +
                    "\n" +
                    "        SET v_ticket = CONCAT('TK-', UNIX_TIMESTAMP(), '-', FLOOR(100 + RAND() * 900));\n" +
                    "\n" +
                    "        INSERT INTO ventas_cabecera\n" +
                    "            (paciente_id, usuario_id, tienda_id, numero_ticket,\n" +
                    "             tipo_comprobante, estado_financiero,\n" +
                    "             monto_subtotal, monto_igv, monto_total)\n" +
                    "        VALUES\n" +
                    "            (p_paciente_id, p_usuario_id, p_tienda_id, v_ticket,\n" +
                    "             p_tipo_comprobante, 'PAGADO', 0, 0, 0);\n" +
                    "\n" +
                    "        SET v_venta_id = LAST_INSERT_ID();\n" +
                    "\n" +
                    "        INSERT INTO ventas_detalle\n" +
                    "            (venta_id, producto_id, cantidad, precio_unitario, subtotal)\n" +
                    "        VALUES\n" +
                    "            (v_venta_id, p_producto_id, p_cantidad, v_precio, 0);\n" +
                    "\n" +
                    "        INSERT INTO kardex\n" +
                    "            (tienda_id, usuario_id, producto_id, venta_id,\n" +
                    "             tipo_movimiento, motivo, cantidad, cantidad_saldo)\n" +
                    "        VALUES\n" +
                    "            (p_tienda_id, p_usuario_id, p_producto_id, v_venta_id,\n" +
                    "             'SALIDA', 'Venta registrada', p_cantidad, 0);\n" +
                    "\n" +
                    "        INSERT INTO movimientos_caja\n" +
                    "            (tienda_id, usuario_id, venta_id, tipo,\n" +
                    "             metodo_pago, monto, descripcion)\n" +
                    "        VALUES\n" +
                    "            (p_tienda_id, p_usuario_id, v_venta_id, 'ENTRADA',\n" +
                    "             p_metodo_pago, p_cantidad * v_precio, 'Venta de producto');\n" +
                    "\n" +
                    "        COMMIT;\n" +
                    "        SET p_resultado = 1;\n" +
                    "        SET p_mensaje   = CONCAT('Venta registrada. Ticket: ', v_ticket);\n" +
                    "    END IF;\n" +
                    "END");
            
            // Drop old sp_registrar_compra
            stmt.execute("DROP PROCEDURE IF EXISTS sp_registrar_compra");
            // Recreate sp_registrar_compra
            stmt.execute("CREATE PROCEDURE sp_registrar_compra(\n" +
                    "    IN  p_proveedor_id  INT,\n" +
                    "    IN  p_usuario_id    INT,\n" +
                    "    IN  p_tienda_id     INT,\n" +
                    "    IN  p_insumo_id     INT,\n" +
                    "    IN  p_cantidad      INT,\n" +
                    "    IN  p_precio_unit   DECIMAL(10,2),\n" +
                    "    IN  p_metodo_pago   ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA'),\n" +
                    "    OUT p_resultado     INT,\n" +
                    "    OUT p_mensaje       VARCHAR(200)\n" +
                    ")\n" +
                    "BEGIN\n" +
                    "    DECLARE v_compra_id     INT;\n" +
                    "    DECLARE v_numero_orden  VARCHAR(50);\n" +
                    "\n" +
                    "    DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                    "    BEGIN\n" +
                    "        ROLLBACK;\n" +
                    "        SET p_resultado = 0;\n" +
                    "        SET p_mensaje   = 'ERROR: No se pudo registrar la compra.';\n" +
                    "        RESIGNAL;\n" +
                    "    END;\n" +
                    "\n" +
                    "    START TRANSACTION;\n" +
                    "\n" +
                    "    SET v_numero_orden = CONCAT('OC-', UNIX_TIMESTAMP(), '-', FLOOR(100 + RAND() * 900));\n" +
                    "\n" +
                    "    INSERT INTO compras_cabecera\n" +
                    "        (proveedor_id, usuario_id, tienda_id, numero_orden,\n" +
                    "         estado_fisico, estado_financiero, monto_total)\n" +
                    "    VALUES\n" +
                    "        (p_proveedor_id, p_usuario_id, p_tienda_id, v_numero_orden,\n" +
                    "         'PENDIENTE', 'POR_PAGAR', 0);\n" +
                    "\n" +
                    "    SET v_compra_id = LAST_INSERT_ID();\n" +
                    "\n" +
                    "    INSERT INTO compras_detalle\n" +
                    "        (compra_id, insumo_id, cantidad, precio_unitario, subtotal)\n" +
                    "    VALUES\n" +
                    "        (v_compra_id, p_insumo_id, p_cantidad, p_precio_unit, 0);\n" +
                    "\n" +
                    "    INSERT INTO kardex\n" +
                    "        (tienda_id, usuario_id, insumo_id, compra_id,\n" +
                    "         tipo_movimiento, motivo, cantidad, cantidad_saldo)\n" +
                    "    VALUES\n" +
                    "        (p_tienda_id, p_usuario_id, p_insumo_id, v_compra_id,\n" +
                    "         'ENTRADA', 'Compra a proveedor', p_cantidad, 0);\n" +
                    "\n" +
                    "    INSERT INTO movimientos_caja\n" +
                    "        (tienda_id, usuario_id, compra_id, tipo,\n" +
                    "         metodo_pago, monto, descripcion)\n" +
                    "    VALUES\n" +
                    "        (p_tienda_id, p_usuario_id, v_compra_id, 'SALIDA',\n" +
                    "         p_metodo_pago, p_cantidad * p_precio_unit, CONCAT('Compra de insumo #', p_insumo_id));\n" +
                    "\n" +
                    "    COMMIT;\n" +
                    "    SET p_resultado = 1;\n" +
                    "    SET p_mensaje   = CONCAT('Compra registrada. Orden: ', v_numero_orden);\n" +
                    "END");
            
            System.out.println("Database stored procedures updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConfig.shutdown();
        }
    }
}
