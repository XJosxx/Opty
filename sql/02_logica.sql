USE optica_db;

-- ============================== INDEXES ==============================

CREATE INDEX idx_kardex_fecha ON kardex(fecha);
CREATE INDEX idx_caja_fecha ON movimientos_caja(fecha);
CREATE INDEX idx_consultas_fecha ON consultas(fecha);
CREATE INDEX idx_ventas_fecha ON ventas_cabecera(fecha_emision);
CREATE INDEX idx_compras_fecha ON compras_cabecera(fecha_emision);

-- ============================== FUNCIONES ==============================

-- Calcula la edad a partir de la fecha de nacimiento
DELIMITER //
CREATE FUNCTION fn_calcular_edad(p_fecha_nacimiento DATE)
RETURNS INT
NOT DETERMINISTIC
BEGIN
    IF p_fecha_nacimiento IS NULL THEN
        RETURN NULL;
    END IF;
    RETURN TIMESTAMPDIFF(YEAR, p_fecha_nacimiento, CURDATE());
END //
DELIMITER ;

-- Calcula el IGV sobre un subtotal dado
DELIMITER //
CREATE FUNCTION fn_calcular_igv(p_subtotal DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN ROUND(p_subtotal * 0.18, 2);
END //
DELIMITER ;

-- Concatena nombre completo desde tres campos separados
DELIMITER //
CREATE FUNCTION fn_concatenar_nombre(p_nombre VARCHAR(100), p_ap VARCHAR(100),p_am VARCHAR(100))
RETURNS VARCHAR(300)
DETERMINISTIC
BEGIN
    RETURN CONCAT(p_nombre, ' ', p_ap, ' ', p_am);
END //
DELIMITER ;

-- ============================== TRIGGERS ==============================

-- Actualiza stock_actual en insumos/productos al insertar en kardex.
-- BEFORE INSERT para poder setear NEW.cantidad_saldo antes de grabar.
DELIMITER //
CREATE TRIGGER trg_kardex_actualizar_stock
BEFORE INSERT ON kardex
FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT DEFAULT 0;
    DECLARE v_nuevo_stock  INT DEFAULT 0;

    IF NEW.insumo_id IS NOT NULL THEN

        SELECT stock_actual INTO v_stock_actual
        FROM insumos
        WHERE id = NEW.insumo_id;

        IF NEW.tipo_movimiento = 'ENTRADA' THEN
            SET v_nuevo_stock = v_stock_actual + NEW.cantidad;
        ELSEIF NEW.tipo_movimiento = 'SALIDA' THEN
            SET v_nuevo_stock = v_stock_actual - NEW.cantidad;
        ELSEIF NEW.tipo_movimiento = 'AJUSTE' THEN
            -- cantidad con signo para sumar o restar (backend)
            SET v_nuevo_stock = v_stock_actual + NEW.cantidad;
        END IF;

        UPDATE insumos
        SET stock_actual = v_nuevo_stock
        WHERE id = NEW.insumo_id;

        SET NEW.cantidad_saldo = v_nuevo_stock;

    ELSEIF NEW.producto_id IS NOT NULL THEN

        SELECT stock_actual INTO v_stock_actual
        FROM productos
        WHERE id = NEW.producto_id;

        IF NEW.tipo_movimiento = 'ENTRADA' THEN
            SET v_nuevo_stock = v_stock_actual + NEW.cantidad;
        ELSEIF NEW.tipo_movimiento = 'SALIDA' THEN
            SET v_nuevo_stock = v_stock_actual - NEW.cantidad;
        ELSEIF NEW.tipo_movimiento = 'AJUSTE' THEN
            SET v_nuevo_stock = v_stock_actual + NEW.cantidad;
        END IF;

        UPDATE productos
        SET stock_actual = v_nuevo_stock
        WHERE id = NEW.producto_id;

        SET NEW.cantidad_saldo = v_nuevo_stock;

    END IF;
END //
DELIMITER ;

-- Calcula subtotal antes de insertar en ventas_detalle.
DELIMITER //
CREATE TRIGGER trg_ventas_detalle_subtotal
BEFORE INSERT ON ventas_detalle
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END //
DELIMITER ;

-- Calcula subtotal antes de insertar en compras_detalle.
DELIMITER //
CREATE TRIGGER trg_compras_detalle_subtotal
BEFORE INSERT ON compras_detalle
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END //
DELIMITER ;

-- Suma el subtotal del detalle a ventas_cabecera y recalcula IGV y total.
DELIMITER //
CREATE TRIGGER trg_ventas_cabecera_sumar
AFTER INSERT ON ventas_detalle
FOR EACH ROW
BEGIN
    DECLARE v_nuevo_subtotal DECIMAL(10,2);

    SELECT monto_subtotal + NEW.subtotal
    INTO v_nuevo_subtotal
    FROM ventas_cabecera
    WHERE id = NEW.venta_id;

    UPDATE ventas_cabecera
    SET
        monto_subtotal = v_nuevo_subtotal,
        monto_igv = fn_calcular_igv(v_nuevo_subtotal),
        monto_total = v_nuevo_subtotal + fn_calcular_igv(v_nuevo_subtotal)
    WHERE id = NEW.venta_id;
END //
DELIMITER ;

-- Resta el subtotal del detalle eliminado a ventas_cabecera y recalcula.
DELIMITER //
CREATE TRIGGER trg_ventas_cabecera_restar
AFTER DELETE ON ventas_detalle
FOR EACH ROW
BEGIN
    DECLARE v_nuevo_subtotal DECIMAL(10,2);

    SELECT monto_subtotal - OLD.subtotal
    INTO v_nuevo_subtotal
    FROM ventas_cabecera
    WHERE id = OLD.venta_id;

    UPDATE ventas_cabecera
    SET
        monto_subtotal = v_nuevo_subtotal,
        monto_igv = fn_calcular_igv(v_nuevo_subtotal),
        monto_total = v_nuevo_subtotal + fn_calcular_igv(v_nuevo_subtotal)
    WHERE id = OLD.venta_id;
END //
DELIMITER ;

-- Suma el subtotal del detalle a compras_cabecera.
DELIMITER //
CREATE TRIGGER trg_compras_cabecera_sumar
AFTER INSERT ON compras_detalle
FOR EACH ROW
BEGIN
    UPDATE compras_cabecera
    SET monto_total = monto_total + NEW.subtotal
    WHERE id = NEW.compra_id;
END //
DELIMITER ;

-- Resta el subtotal del detalle eliminado a compras_cabecera.
DELIMITER //
CREATE TRIGGER trg_compras_cabecera_restar
AFTER DELETE ON compras_detalle
FOR EACH ROW
BEGIN
    UPDATE compras_cabecera
    SET monto_total = monto_total - OLD.subtotal
    WHERE id = OLD.compra_id;
END //
DELIMITER ;

-- ============================== PROCEDURES ==============================

-- Registra un paciente y su consulta médica completa.
-- Flujo: verificar paciente → crear venta del servicio → crear detalle
--        → crear consulta vinculada al detalle de venta.
DELIMITER //
CREATE PROCEDURE sp_registrar_paciente_y_consulta(
    -- Datos del paciente
    IN  p_tipo_documento   ENUM('DNI','CE','PASAPORTE'),
    IN  p_num_documento	   VARCHAR(20),
    IN  p_nombre 		   VARCHAR(100),
    IN  p_apellido_p       VARCHAR(100),
    IN  p_apellido_m       VARCHAR(100),
    IN  p_telefono         VARCHAR(20),
    IN  p_fecha_nacimiento DATE,
    -- Datos de la consulta
    IN  p_usuario_id       INT,
    IN  p_tienda_id        INT,
    IN  p_motivo           TEXT,
    IN  p_producto_id      INT,
    IN  p_precio_servicio  DECIMAL(10,2),
    IN  p_tipo_comprobante ENUM('BOLETA','FACTURA'),
    -- Resultado
    OUT p_resultado        INT,
    OUT p_mensaje          VARCHAR(200)
)
BEGIN
    DECLARE v_paciente_id   INT DEFAULT NULL;
    DECLARE v_venta_id      INT;
    DECLARE v_detalle_id    INT;
    DECLARE v_numero_ticket VARCHAR(50);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_resultado = 0;
        SET p_mensaje   = 'ERROR: Problema al registrar.';
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. Verificar si el paciente ya existe 
    SELECT id INTO v_paciente_id
    FROM pacientes
    WHERE num_documento = p_num_documento
    LIMIT 1;

    -- 2. Si no existe, crearlo
    IF v_paciente_id IS NULL THEN
        INSERT INTO pacientes
            (tipo_documento, num_documento, nombre, apellido_p, apellido_m,
             telefono, fecha_nacimiento, tienda_id)
        VALUES
            (p_tipo_documento, p_num_documento, p_nombre, p_apellido_p,
             p_apellido_m, p_telefono, p_fecha_nacimiento, p_tienda_id);

        SET v_paciente_id = LAST_INSERT_ID();
    END IF;

    -- 3. Generar número de ticket único
    SET v_numero_ticket = CONCAT('TK-', UNIX_TIMESTAMP());

    -- 4. Crear la venta cabecera
    -- monto_subtotal, monto_igv y monto_total inician en 0
    -- los triggers los actualizan al insertar el detalle
    INSERT INTO ventas_cabecera
        (paciente_id, usuario_id, tienda_id, numero_ticket,
         tipo_comprobante, estado_financiero,
         monto_subtotal, monto_igv, monto_total)
    VALUES
        (v_paciente_id, p_usuario_id, p_tienda_id, v_numero_ticket,
         p_tipo_comprobante, 'PAGADO', 0, 0, 0);

    SET v_venta_id = LAST_INSERT_ID();

    -- 5. Insertar el detalle del servicio
    -- El trigger trg_ventas_detalle_subtotal calcula el subtotal
    -- El trigger trg_ventas_cabecera_sumar actualiza la cabecera
    INSERT INTO ventas_detalle
        (venta_id, producto_id, cantidad, precio_unitario, subtotal)
    VALUES
        (v_venta_id, p_producto_id, 1, p_precio_servicio, 0);

    SET v_detalle_id = LAST_INSERT_ID();

    -- 6. Registrar la consulta vinculada al detalle de venta
    INSERT INTO consultas
        (paciente_id, usuario_id, tienda_id, venta_detalle_id, motivo)
    VALUES
        (v_paciente_id, p_usuario_id, p_tienda_id, v_detalle_id, p_motivo);

    COMMIT;

    SET p_resultado = 1;
    SET p_mensaje   = CONCAT('Consulta registrada. Ticket: ', v_numero_ticket);
END //
DELIMITER ;

-- Registra venta cabecera + N detalles + movimiento de caja
-- El kardex lo mueve el trigger automáticamente al insertar detalle
DELIMITER //
CREATE PROCEDURE sp_procesar_venta(
    IN  p_paciente_id      INT,
    IN  p_usuario_id       INT,
    IN  p_tienda_id        INT,
    IN  p_tipo_comprobante ENUM('BOLETA','FACTURA'),
    IN  p_producto_id      INT,
    IN  p_cantidad         INT,
    IN  p_metodo_pago      ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA'),
    OUT p_resultado        INT,
    OUT p_mensaje          VARCHAR(200)
)
BEGIN
    DECLARE v_stock        INT DEFAULT 0;
    DECLARE v_precio       DECIMAL(10,2) DEFAULT 0;
    DECLARE v_venta_id     INT;
    DECLARE v_ticket       VARCHAR(50);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_resultado = 0;
        SET p_mensaje   = 'ERROR: No se pudo procesar la venta.';
        RESIGNAL;
    END;

    -- 1. Verificar stock disponible
    SELECT stock_actual, precio_venta
    INTO v_stock, v_precio
    FROM productos
    WHERE id = p_producto_id
      AND tienda_id = p_tienda_id
      AND activo = TRUE;

    IF v_stock IS NULL THEN
        SET p_resultado = 0;
        SET p_mensaje   = 'ERROR: Producto no encontrado.';

    ELSEIF v_stock < p_cantidad THEN
        SET p_resultado = 0;
        SET p_mensaje   = CONCAT('ERROR: Stock insuficiente. Disponible: ', v_stock);

    ELSE
        START TRANSACTION;

        SET v_ticket = CONCAT('TK-', UNIX_TIMESTAMP());

        -- 2. Crear cabecera (los triggers calculan los montos)
        INSERT INTO ventas_cabecera
            (paciente_id, usuario_id, tienda_id, numero_ticket,
             tipo_comprobante, estado_financiero,
             monto_subtotal, monto_igv, monto_total)
        VALUES
            (p_paciente_id, p_usuario_id, p_tienda_id, v_ticket,
             p_tipo_comprobante, 'PAGADO', 0, 0, 0);

        SET v_venta_id = LAST_INSERT_ID();

        -- 3. Insertar detalle — dispara trg_ventas_detalle_subtotal
        --    y trg_ventas_cabecera_sumar automáticamente
        INSERT INTO ventas_detalle
            (venta_id, producto_id, cantidad, precio_unitario, subtotal)
        VALUES
            (v_venta_id, p_producto_id, p_cantidad, v_precio, 0);

        -- 4. Registrar salida en kardex — dispara trg_kardex_actualizar_stock
        INSERT INTO kardex
            (tienda_id, usuario_id, producto_id, venta_id,
             tipo_movimiento, motivo, cantidad, cantidad_saldo)
        VALUES
            (p_tienda_id, p_usuario_id, p_producto_id, v_venta_id,
             'SALIDA', 'Venta registrada', p_cantidad, 0);

        -- 5. Registrar ingreso en caja
        INSERT INTO movimientos_caja
            (tienda_id, usuario_id, venta_id, tipo,
             metodo_pago, monto, descripcion)
        VALUES
            (p_tienda_id, p_usuario_id, v_venta_id, 'ENTRADA',
             p_metodo_pago, v_precio * p_cantidad,
             CONCAT('Venta ticket ', v_ticket));
        COMMIT;

        SET p_resultado = 1;
        SET p_mensaje   = CONCAT('Venta registrada. Ticket: ', v_ticket);
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE sp_registrar_compra(
    IN  p_proveedor_id  INT,
    IN  p_usuario_id    INT,
    IN  p_tienda_id     INT,
    IN  p_insumo_id     INT,
    IN  p_cantidad      INT,
    IN  p_precio_unit   DECIMAL(10,2),
    IN  p_metodo_pago   ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA'),
    OUT p_resultado     INT,
    OUT p_mensaje       VARCHAR(200)
)
BEGIN
    DECLARE v_compra_id     INT;
    DECLARE v_numero_orden  VARCHAR(50);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_resultado = 0;
        SET p_mensaje   = 'ERROR: No se pudo registrar la compra.';
        RESIGNAL;
    END;

    START TRANSACTION;

    SET v_numero_orden = CONCAT('OC-', UNIX_TIMESTAMP());

    -- 1. Crear cabecera (monto_total lo actualiza el trigger)
    INSERT INTO compras_cabecera
        (proveedor_id, usuario_id, tienda_id, numero_orden,
         estado_fisico, estado_financiero, monto_total)
    VALUES
        (p_proveedor_id, p_usuario_id, p_tienda_id, v_numero_orden,
         'PENDIENTE', 'POR_PAGAR', 0);

    SET v_compra_id = LAST_INSERT_ID();

    -- 2. Insertar detalle — dispara trg_compras_detalle_subtotal
    --    y trg_compras_cabecera_sumar automáticamente
    INSERT INTO compras_detalle
        (compra_id, insumo_id, cantidad, precio_unitario, subtotal)
    VALUES
        (v_compra_id, p_insumo_id, p_cantidad, p_precio_unit, 0);

    -- 3. Registrar entrada en kardex — dispara trg_kardex_actualizar_stock
    INSERT INTO kardex
        (tienda_id, usuario_id, insumo_id, compra_id,
         tipo_movimiento, motivo, cantidad, cantidad_saldo)
    VALUES
        (p_tienda_id, p_usuario_id, p_insumo_id, v_compra_id,
         'ENTRADA', 'Compra a proveedor', p_cantidad, 0);

    -- 4. Registrar salida de caja (sale dinero para pagar)
    INSERT INTO movimientos_caja
        (tienda_id, usuario_id, compra_id, tipo,
         metodo_pago, monto, descripcion)
    VALUES
        (p_tienda_id, p_usuario_id, v_compra_id, 'SALIDA',
         p_metodo_pago, p_cantidad * p_precio_unit,
         CONCAT('Pago compra ', v_numero_orden));
    COMMIT;

    SET p_resultado = 1;
    SET p_mensaje   = CONCAT('Compra registrada. Orden: ', v_numero_orden);
END //
DELIMITER ;

-- ============================== VIEWS ==============================

-- Productos disponibles para vender
CREATE VIEW v_productos_disponibles AS
SELECT
    p.id                AS producto_id,
    ct.codigo           AS tienda,
    p.codigo            AS codigo_producto,
    p.nombre,
    p.categoria,
    p.genero_objetivo,
    p.precio_venta,
    p.stock_actual,
    p.stock_minimo
FROM productos p
JOIN config_tienda ct ON ct.id = p.tienda_id
WHERE p.activo = TRUE AND p.stock_actual > 0;

-- Insumos bajo stock mínimo
CREATE VIEW v_insumos_bajo_minimo AS
SELECT
    ct.codigo           AS tienda,
    i.codigo            AS codigo_insumo,
    i.nombre,
    i.categoria,
    i.stock_actual,
    i.stock_minimo,
    (i.stock_minimo - i.stock_actual) AS unidades_faltantes
FROM insumos i
JOIN config_tienda ct ON ct.id = i.tienda_id
WHERE i.activo = TRUE AND i.stock_actual < i.stock_minimo
ORDER BY unidades_faltantes DESC;

-- Resumen de ventas por tienda
CREATE VIEW v_resumen_ventas_tienda AS
SELECT
    ct.codigo                           AS tienda,
    ct.nombre_optica,
    COUNT(vc.id)                        AS total_ventas,
    SUM(vc.monto_subtotal)              AS total_subtotal,
    SUM(vc.monto_igv)                   AS total_igv,
    SUM(vc.monto_total)                 AS total_con_igv,
    SUM(CASE WHEN mc.metodo_pago = 'EFECTIVO'      THEN mc.monto ELSE 0 END) AS cobrado_efectivo,
    SUM(CASE WHEN mc.metodo_pago = 'YAPE'          THEN mc.monto ELSE 0 END) AS cobrado_yape,
    SUM(CASE WHEN mc.metodo_pago = 'TARJETA'       THEN mc.monto ELSE 0 END) AS cobrado_tarjeta,
    SUM(CASE WHEN mc.metodo_pago = 'TRANSFERENCIA' THEN mc.monto ELSE 0 END) AS cobrado_transferencia
FROM config_tienda ct
LEFT JOIN ventas_cabecera vc ON vc.tienda_id = ct.id
LEFT JOIN movimientos_caja mc ON mc.venta_id = vc.id AND mc.tipo = 'ENTRADA'
GROUP BY ct.id, ct.codigo, ct.nombre_optica;

-- Historial clínico completo por paciente
CREATE VIEW v_historial_clinico_paciente AS
SELECT
    fn_concatenar_nombre(p.nombre, p.apellido_p, p.apellido_m) AS paciente,
    p.num_documento,
    c.fecha AS fecha_consulta,
    c.motivo,
    hc.graduacion_od,
    hc.graduacion_oi,
    hc.observaciones,
    fn_calcular_edad(p.fecha_nacimiento) AS edad,
    ct.codigo AS tienda
FROM pacientes p
JOIN consultas c ON c.paciente_id = p.id
JOIN historial_clinico hc ON hc.consulta_id = c.id
JOIN config_tienda ct ON ct.id = c.tienda_id
ORDER BY p.apellido_p, c.fecha DESC;

-- Órdenes de trabajo pendientes
CREATE VIEW v_ordenes_pendientes AS
SELECT
    ot.id AS orden_id,
    ot.estado_fisico,
    ot.tipo_trabajo,
    ot.fecha_creacion,
    ot.fecha_prometida,
    CASE
        WHEN ot.fecha_prometida < NOW()
         AND ot.estado_fisico != 'ENTREGADO'
        THEN 'VENCIDA'
        ELSE 'EN TIEMPO'
    END AS alerta,
    fn_concatenar_nombre(p.nombre, p.apellido_p, p.apellido_m) AS paciente,
    ct.codigo AS tienda
FROM ordenes_trabajo ot
JOIN ventas_cabecera vc ON vc.id = ot.venta_id
JOIN pacientes p ON p.id = vc.paciente_id
JOIN config_tienda ct ON ct.id = vc.tienda_id
WHERE ot.estado_fisico != 'ENTREGADO'
ORDER BY ot.fecha_prometida ASC;