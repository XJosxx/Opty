-- ==========================================================================
-- REGISTROS ADICIONALES DE PRUEBA (V4__datos_extra.sql)
-- Para robustecer el testing, poblar gráficos históricos y simular transacciones reales.
-- ==========================================================================

-- 1. PACIENTES ADICIONALES
INSERT INTO pacientes (id, nombre, apellido_p, apellido_m, tipo_documento, num_documento, telefono, fecha_nacimiento, es_destacado, tipo_destacado, tienda_id) VALUES
(16, 'Andrés', 'Sarmiento', 'Cáceres', 'DNI', '20304016', '981112223', '1995-10-15', TRUE, 'MANUAL', 1),
(17, 'Clara', 'Mendizábal', 'Ponce', 'DNI', '20304017', '982223334', '1982-04-03', FALSE, NULL, 1),
(18, 'Eduardo', 'Paredes', 'Vásquez', 'DNI', '20304018', '983334445', '1970-12-25', FALSE, NULL, 2),
(19, 'Gabriela', 'Miranda', 'Loayza', 'CE', '90304019', '984445556', '1988-08-19', TRUE, 'AUTO', 2),
(20, 'Hernán', 'Bustamante', 'Ramos', 'DNI', '20304020', '985556667', '2005-01-14', FALSE, NULL, 3),
(21, 'Isabel', 'Soto', 'Guerrero', 'DNI', '20304021', '986667778', '1963-06-30', TRUE, 'AUTO', 3),
(22, 'Julio', 'Vargas', 'Alvarado', 'PASAPORTE', 'PAS00222', '987778889', '1991-03-22', FALSE, NULL, 1),
(23, 'Katia', 'Ortiz', 'Espinoza', 'DNI', '20304023', '988889990', '2010-09-05', FALSE, NULL, 2);

-- 2. INVENTARIO INICIAL ADICIONAL (Para evitar rotura de stock en nuevas pruebas)
INSERT INTO kardex (tienda_id, usuario_id, insumo_id, producto_id, compra_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo) VALUES
-- C1
(1, 1, NULL, 1,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 20, 0),
(1, 1, NULL, 2,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 15, 0),
(1, 1, NULL, 3,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 30, 0),
(1, 1, NULL, 13, NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 10, 0),
-- C2
(2, 4, NULL, 5,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 15, 0),
(2, 4, NULL, 6,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 20, 0),
(2, 4, NULL, 7,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 18, 0),
(2, 4, NULL, 14, NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 15, 0),
-- C3
(3, 7, NULL, 9,  NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 25, 0),
(3, 7, NULL, 10, NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 12, 0),
(3, 7, NULL, 11, NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 40, 0),
(3, 7, NULL, 15, NULL, NULL, 'ENTRADA', 'Ajuste Stock Extra', 20, 0);

-- 3. VENTAS CABECERA ADICIONALES (Para poblar historial de transacciones y comparativas de gráficos)
-- Días anteriores para reflejar histórico real de ingresos
INSERT INTO ventas_cabecera (id, paciente_id, usuario_id, tienda_id, numero_ticket, fecha_emision, estado_financiero, tipo_comprobante, monto_subtotal, monto_igv, monto_total) VALUES
(11, 16, 2, 1, 'TK-2026-011', DATE_SUB(NOW(), INTERVAL 5 DAY), 'PAGADO', 'BOLETA',  101.69, 18.31,  120.00),
(12, 17, 3, 1, 'TK-2026-012', DATE_SUB(NOW(), INTERVAL 4 DAY), 'PAGADO', 'FACTURA',  105.93, 19.07,  125.00),
(13, 18, 5, 2, 'TK-2026-013', DATE_SUB(NOW(), INTERVAL 4 DAY), 'PAGADO', 'BOLETA',   127.12, 22.88,  150.00),
(14, 19, 5, 2, 'TK-2026-014', DATE_SUB(NOW(), INTERVAL 3 DAY), 'PAGADO', 'BOLETA',   110.17, 19.83,  130.00),
(15, 20, 9, 3, 'TK-2026-015', DATE_SUB(NOW(), INTERVAL 3 DAY), 'PAGADO', 'BOLETA',   101.69, 18.31,  120.00),
(16, 21, 9, 3, 'TK-2026-016', DATE_SUB(NOW(), INTERVAL 2 DAY), 'PAGADO', 'FACTURA',  245.76, 44.24,  290.00),
(17, 22, 2, 1, 'TK-2026-017', DATE_SUB(NOW(), INTERVAL 2 DAY), 'PAGADO', 'BOLETA',   169.49, 30.51,  200.00),
(18, 23, 5, 2, 'TK-2026-018', DATE_SUB(NOW(), INTERVAL 1 DAY), 'PAGADO', 'BOLETA',   152.54, 27.46,  180.00);

-- 4. VENTAS DETALLE ADICIONALES
INSERT INTO ventas_detalle (id, venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(16, 11, 1,  1, 120.00, 120.00),
(17, 12, 2,  1, 95.00,  95.00),
(18, 12, 3,  6, 5.00,   30.00),
(19, 13, 7,  1, 150.00, 150.00),
(20, 14, 5,  1, 120.00, 120.00),
(21, 14, 6,  1, 10.00,  10.00),
(22, 15, 9,  1, 120.00, 120.00),
(23, 16, 10, 1, 280.00, 280.00),
(24, 16, 11, 1, 10.00,  10.00),
(25, 17, 13, 1, 180.00, 180.00),
(26, 17, 3,  4, 5.00,   20.00),
(27, 18, 14, 1, 110.00, 110.00),
(28, 18, 6,  2, 35.00,  70.00);

-- 5. COMPRAS CABECERA ADICIONALES (Egresos históricos para gráficos comparativos)
INSERT INTO compras_cabecera (id, proveedor_id, usuario_id, tienda_id, numero_orden, fecha_emision, estado_fisico, estado_financiero, monto_total) VALUES
(11, 1, 1, 1, 'OC-2026-011', DATE_SUB(NOW(), INTERVAL 6 DAY), 'RECIBIDO', 'PAGADO',  425.00),
(12, 2, 1, 1, 'OC-2026-012', DATE_SUB(NOW(), INTERVAL 5 DAY), 'RECIBIDO', 'PAGADO',  350.00),
(13, 4, 4, 2, 'OC-2026-013', DATE_SUB(NOW(), INTERVAL 4 DAY), 'RECIBIDO', 'PAGADO',  255.00),
(14, 5, 4, 2, 'OC-2026-014', DATE_SUB(NOW(), INTERVAL 3 DAY), 'RECIBIDO', 'PAGADO',  550.00),
(15, 7, 7, 3, 'OC-2026-015', DATE_SUB(NOW(), INTERVAL 2 DAY), 'RECIBIDO', 'PAGADO',  340.00);

-- 6. COMPRAS DETALLE ADICIONALES
INSERT INTO compras_detalle (compra_id, insumo_id, cantidad, precio_unitario, subtotal) VALUES
(11, 1,  50, 8.50,  425.00),
(12, 3,  10, 25.00, 250.00),
(12, 4,  2,  50.00, 100.00),
(13, 6,  30, 8.50,  255.00),
(14, 8,  5,  80.00, 400.00),
(14, 9,  10, 15.00, 150.00),
(15, 10, 40, 8.50,  340.00);

-- 7. REGISTRO DE SALIDAS Y ENTRADAS EN KARDEX POR ESTAS TRANSACCIONES HISTÓRICAS
INSERT INTO kardex (tienda_id, usuario_id, insumo_id, producto_id, compra_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo) VALUES
-- Compras (Entradas de insumos)
(1, 1, 1,  NULL, 11, NULL, 'ENTRADA', 'Compra OC-2026-011', 50, 0),
(1, 1, 3,  NULL, 12, NULL, 'ENTRADA', 'Compra OC-2026-012', 10, 0),
(2, 4, 6,  NULL, 13, NULL, 'ENTRADA', 'Compra OC-2026-013', 30, 0),
(2, 4, 8,  NULL, 14, NULL, 'ENTRADA', 'Compra OC-2026-014', 5,  0),
(3, 7, 10, NULL, 15, NULL, 'ENTRADA', 'Compra OC-2026-015', 40, 0),
-- Ventas (Salidas de productos)
(1, 2, NULL, 1,  NULL, 11, 'SALIDA', 'Venta TK-2026-011', 1, 0),
(1, 3, NULL, 2,  NULL, 12, 'SALIDA', 'Venta TK-2026-012', 1, 0),
(2, 5, NULL, 7,  NULL, 13, 'SALIDA', 'Venta TK-2026-013', 1, 0),
(2, 5, NULL, 5,  NULL, 14, 'SALIDA', 'Venta TK-2026-014', 1, 0),
(3, 9, NULL, 9,  NULL, 15, 'SALIDA', 'Venta TK-2026-015', 1, 0),
(3, 9, NULL, 10, NULL, 16, 'SALIDA', 'Venta TK-2026-016', 1, 0),
(1, 2, NULL, 13, NULL, 17, 'SALIDA', 'Venta TK-2026-017', 1, 0),
(2, 5, NULL, 14, NULL, 18, 'SALIDA', 'Venta TK-2026-018', 1, 0);

-- 8. MOVIMIENTOS DE CAJA
INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, compra_id, tipo, metodo_pago, monto, descripcion) VALUES
(1, 2, 11, NULL, 'ENTRADA', 'EFECTIVO',      120.00, 'Ingreso venta TK-2026-011'),
(1, 3, 12, NULL, 'ENTRADA', 'TARJETA',       125.00, 'Ingreso venta TK-2026-012'),
(2, 5, 13, NULL, 'ENTRADA', 'YAPE',          150.00, 'Ingreso venta TK-2026-013'),
(2, 5, 14, NULL, 'ENTRADA', 'PLIN',          130.00, 'Ingreso venta TK-2026-014'),
(3, 9, 15, NULL, 'ENTRADA', 'EFECTIVO',      120.00, 'Ingreso venta TK-2026-015'),
(3, 9, 16, NULL, 'ENTRADA', 'TRANSFERENCIA', 290.00, 'Ingreso venta TK-2026-016'),
(1, 2, 17, NULL, 'ENTRADA', 'EFECTIVO',      200.00, 'Ingreso venta TK-2026-017'),
(2, 5, 18, NULL, 'ENTRADA', 'TARJETA',       180.00, 'Ingreso venta TK-2026-018'),
(1, 1, NULL, 11, 'SALIDA',  'TRANSFERENCIA', 425.00, 'Egreso compra OC-2026-011'),
(1, 1, NULL, 12, 'SALIDA',  'TRANSFERENCIA', 350.00, 'Egreso compra OC-2026-012'),
(2, 4, NULL, 13, 'SALIDA',  'EFECTIVO',      255.00, 'Egreso compra OC-2026-013'),
(2, 4, NULL, 14, 'SALIDA',  'TRANSFERENCIA', 550.00, 'Egreso compra OC-2026-014'),
(3, 7, NULL, 15, 'SALIDA',  'TRANSFERENCIA', 340.00, 'Egreso compra OC-2026-015');

-- 9. CONSULTAS ADICIONALES (Algunas con medidas estructuradas en JSON)
INSERT INTO consultas (id, paciente_id, usuario_id, tienda_id, venta_detalle_id, motivo) VALUES
(11, 16, 8, 1, NULL, 'Dificultad progresiva de visión intermedia'),
(12, 17, 8, 1, NULL, 'Fatiga ocular grave y destellos al conducir de noche'),
(13, 18, 8, 2, NULL, 'Evaluación y descarte de glaucoma familiar'),
(14, 19, 8, 2, NULL, 'Picazón constante e irritación ocular');

-- 10. HISTORIAL CLÍNICO CON JSON ESTRUCTURADO Y TEXTO PLANO MIXTOS
INSERT INTO historial_clinico (id, consulta_id, graduacion_od, graduacion_oi, observaciones) VALUES
(11, 11, '{"esfera":"-2.50","cilindro":"-1.25","eje":"180","adicion":"+1.75","av":"20/20","dp":"33"}', '{"esfera":"-2.25","cilindro":"-1.00","eje":"175","adicion":"+1.75","av":"20/25","dp":"32.5"}', 'Refracción con JSON estructurado. Presbicia inicial.'),
(12, 12, '{"esfera":"-1.00","cilindro":"-0.50","eje":"90","adicion":"","av":"20/20","dp":"34"}', '{"esfera":"-0.75","cilindro":"-0.50","eje":"85","adicion":"","av":"20/20","dp":"33.5"}', 'Uso obligatorio para manejo de vehículos.'),
(13, 13, '+1.50 ESF, -0.75 CIL x 180°', '+1.25 ESF, -0.50 CIL x 175°', 'Texto plano tradicional. Presión intraocular estable.'),
(14, 14, 'Plano, Filtro Blue', 'Plano, Filtro Blue', 'Ojos irritados por fatiga de pantalla. Se recetan gotas humectantes.');

-- 11. ÓRDENES DE TRABAJO ASOCIADAS
INSERT INTO ordenes_trabajo (id, venta_id, historial_clinico_id, estado_fisico, tipo_trabajo, usa_montura_cliente, detalles_montura_cliente, usa_luna_cliente, detalles_luna_cliente, fecha_prometida) VALUES
(11, 11, 11,   'PENDIENTE',   'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      DATE_ADD(NOW(), INTERVAL 3 DAY)),
(12, 12, 12,   'LABORATORIO', 'SOLO_LUNAS',     TRUE,  'Montura Oakley deportiva',    FALSE, NULL,                      DATE_ADD(NOW(), INTERVAL 2 DAY)),
(13, 17, 13,   'LISTO',       'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      DATE_ADD(NOW(), INTERVAL 1 DAY)),
(14, 18, 14,   'PENDIENTE',   'SOLO_MONTURA',   FALSE, NULL,                          TRUE,  'Lunas del cliente 1.50',  DATE_ADD(NOW(), INTERVAL 4 DAY));
