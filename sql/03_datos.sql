USE optica_db;

-- 1. CONFIG_TIENDA
INSERT INTO config_tienda (id, codigo, nombre_optica, ruc, direccion, telefono) VALUES
(1, 'C1', 'Óptica Visión Centro',     '20100200301', 'Av. Abancay 123, Cercado de Lima',    '01-4271001'),
(2, 'C2', 'Óptica Visión San Isidro', '20100200302', 'Av. Rivera Navarrete 456, San Isidro','01-4271002'),
(3, 'C3', 'Óptica Visión Miraflores', '20100200303', 'Av. Larco 789, Miraflores',           '01-4271003');

-- 2. USUARIOS
INSERT INTO usuarios (id, username, password, nombre, apellido_p, apellido_m, tipo_documento, num_documento, rol, activo, tienda_id) VALUES
(1,  'jadmin_c1',  'hash_001', 'Jorge',    'Ramírez',   'Silva',    'DNI', '40112201', 'ADMIN',    TRUE,  1),
(2,  'mrojas',     'hash_002', 'María',    'Rojas',     'Castro',   'DNI', '41223302', 'VENDEDOR', TRUE,  1),
(3,  'lfernandez', 'hash_003', 'Luis',     'Fernández', 'Soto',     'DNI', '42334403', 'VENDEDOR', TRUE,  1),
(4,  'aperez_c2',  'hash_004', 'Ana',      'Pérez',     'Gómez',    'DNI', '43445504', 'ADMIN',    TRUE,  2),
(5,  'cquispe',    'hash_005', 'Carlos',   'Quispe',    'Mamani',   'DNI', '44556605', 'VENDEDOR', TRUE,  2),
(6,  'sflores',    'hash_006', 'Sofía',    'Flores',    'Arce',     'DNI', '45667706', 'VENDEDOR', FALSE, 2),
(7,  'rherrera',   'hash_007', 'Raúl',     'Herrera',   'Paz',      'DNI', '46778807', 'ADMIN',    TRUE,  3),
(8,  'emendoza',   'hash_008', 'Elena',    'Mendoza',   'Ríos',     'DNI', '47889908', 'MEDICO',   TRUE,  3),
(9,  'dcastro',    'hash_009', 'David',    'Castro',    'Luna',     'DNI', '48990009', 'VENDEDOR', TRUE,  3),
(10, 'pvargas',    'hash_010', 'Patricia', 'Vargas',    'Vega',     'DNI', '49001110', 'MEDICO',   TRUE,  1);

-- 3. PACIENTES
INSERT INTO pacientes (id, nombre, apellido_p, apellido_m, tipo_documento, num_documento, telefono, fecha_nacimiento, es_destacado, tipo_destacado, tienda_id) VALUES
(1,  'Alberto',   'Gutiérrez', 'Paredes',  'DNI', '10203001', '987001001', '1985-03-12', TRUE,  'AUTO',   1),
(2,  'Beatriz',   'Sánchez',   'Torres',   'DNI', '10203002', '987001002', '1990-07-24', FALSE, NULL,     1),
(3,  'Carmen',    'Delgado',   'Ruiz',     'DNI', '10203003', NULL,        '1978-11-05', FALSE, NULL,     1),
(4,  'Daniel',    'Romero',    'Chávez',   'DNI', '10203004', '987001004', '2000-01-30', FALSE, NULL,     1),
(5,  'Fernando',  'Chávez',    'Quispe',   'DNI', '10203005', '987001005', '1965-09-18', TRUE,  'MANUAL', 2),
(6,  'Gloria',    'Palomino',  'Vega',     'DNI', '10203006', '987001006', '1992-04-22', FALSE, NULL,     2),
(7,  'Hugo',      'Salazar',   'Mendoza',  'DNI', '10203007', '987001007', '1988-06-14', FALSE, NULL,     2),
(8,  'Inés',      'Campos',    'Huanca',   'DNI', '10203008', NULL,        '1975-12-03', TRUE,  'AUTO',   2),
(9,  'Javier',    'Montoya',   'Ccori',    'DNI', '10203009', '987001009', '1995-08-27', FALSE, NULL,     3),
(10, 'Lucía',     'Navarro',   'Paucar',   'DNI', '10203010', '987001010', '1983-02-09', FALSE, NULL,     3),
(11, 'Manuel',    'Torres',    'Gómez',    'DNI', '10203011', '987001011', '1970-05-15', TRUE,  'MANUAL', 3),
(12, 'Norma',     'Aguirre',   'Díaz',     'DNI', '10203012', '987001012', '2001-08-20', FALSE, NULL,     3),
(13, 'Óscar',     'Huanca',    'Quispe',   'DNI', '10203013', '987001013', '1960-12-01', FALSE, NULL,     1),
(14, 'Paula',     'Ríos',      'Flores',   'DNI', '10203014', NULL,        '1998-03-17', FALSE, NULL,     2),
(15, 'Ricardo',   'Vega',      'Salinas',  'DNI', '10203015', '987001015', '1987-07-08', TRUE,  'AUTO',   3);

-- 4. PROVEEDORES
INSERT INTO proveedores (id, nombre_empresa, nombre_contacto, telefono, email, ruc, activo, tienda_id) VALUES
(1,  'Laboratorio Óptico del Sur S.A.C.',  'Pedro Salas',    '01-5551001', 'ventas@labsur.com',       '20601111101', TRUE,  1),
(2,  'Distribuidora VisionMax',            'Clara Rondón',   '01-5551002', 'clara@visionmax.pe',      '20601111102', TRUE,  1),
(3,  'Importaciones Marcos de Lujo',       'Tomás Parodi',   '01-5551003', 'tomas@marcoslujo.com',    '20601111103', TRUE,  1),
(4,  'Óptica Mayorista Andina',            'Sandra Vera',    '01-5551004', 'sandra@andina.pe',        '20601111104', TRUE,  2),
(5,  'Tech Lens Perú S.R.L.',              'Marcos Gil',     '01-5551005', 'marcos@techlens.pe',      '20601111105', TRUE,  2),
(6,  'Cristales & Monturas S.A.C.',        'Fiorella Paz',   '01-5551006', 'fiorella@cym.pe',         '20601111106', TRUE,  2),
(7,  'Global Optics Import',               'Henry Ruiz',     '01-5551007', 'henry@globaloptics.com',  '20601111107', TRUE,  3),
(8,  'Proveedor Local Miraflores',         'Diana Chong',    '01-5551008', 'diana@prolocal.pe',       '20601111108', TRUE,  3),
(9,  'Laboratorio Lima Norte',             'Raúl Espejo',    '01-5551009', 'raul@labnorte.pe',        '20601111109', TRUE,  1),
(10, 'Monturas Exclusivas Import.',        'Valeria Toro',   '01-5551010', 'valeria@meimp.pe',        '20601111110', FALSE, 3);

-- 5. INSUMOS
INSERT INTO insumos (id, tienda_id, codigo, nombre, categoria, material, precio_costo, stock_actual, stock_minimo) VALUES
(1,  1, 'INS-LU-001', 'Luna CR-39 Blanco +1.00',           'LUNA',     'CR-39',        8.50,  0, 10),
(2,  1, 'INS-LU-002', 'Luna Policarbonato AR -2.00',       'LUNA',     'Policarbonato',18.00, 0, 8),
(3,  1, 'INS-MO-001', 'Montura Acetato Mod. A1',           'MONTURA',  'Acetato',      25.00, 0, 5),
(4,  1, 'INS-MO-002', 'Montura Metálica Juvenil M4',       'MONTURA',  'Metal',        35.00, 0, 5),
(5,  1, 'INS-CO-001', 'Líquido Biselador Galón',           'CONSUMIBLE',NULL,          45.00, 0, 2),
(6,  2, 'INS-LU-001', 'Luna CR-39 Blanco +1.00',           'LUNA',     'CR-39',        8.50,  0, 10),
(7,  2, 'INS-LU-003', 'Luna Trivex Progresiva',            'LUNA',     'Trivex',       55.00, 0, 5),
(8,  2, 'INS-MO-003', 'Montura Titanio Flex',              'MONTURA',  'Titanio',      80.00, 0, 3),
(9,  2, 'INS-AC-001', 'Plaquetas Silicona Bolsa x100',       'ACCESORIO','Silicona',     15.00, 0, 2),
(10, 3, 'INS-LU-001', 'Luna CR-39 Blanco +1.00',           'LUNA',     'CR-39',        8.50,  0, 10),
(11, 3, 'INS-LU-002', 'Luna Policarbonato AR -2.00',       'LUNA',     'Policarbonato',18.00, 0, 8),
(12, 3, 'INS-MO-001', 'Montura Acetato Mod. A1',           'MONTURA',  'Acetato',      25.00, 0, 5),
(13, 3, 'INS-AC-002', 'Tornillos Ópticos Surtidos Caja',   'ACCESORIO',NULL,           30.00, 0, 2),
(14, 1, 'INS-AC-001', 'Plaquetas Silicona Bolsa x100',       'ACCESORIO','Silicona',     15.00, 0, 3),
(15, 3, 'INS-CO-001', 'Líquido Biselador Galón',           'CONSUMIBLE',NULL,          45.00, 0, 2);

-- 6. PRODUCTOS
INSERT INTO productos (id, tienda_id, codigo, nombre, categoria, genero_objetivo, precio_venta, stock_actual, stock_minimo) VALUES
(1,  1, 'PROD-001', 'Lente Completo Medida Básica',       'LENTE',     'UNISEX',  120.00, 0, 3),
(2,  1, 'PROD-002', 'Gafas de Sol Polarizadas Aviador',   'LENTE_SOL', 'HOMBRE',   95.00, 0, 3),
(3,  1, 'PROD-003', 'Paño Microfibra Premium',            'ACCESORIO', NULL,          5.00, 0, 10),
(4,  1,  NULL,      'Consulta Optométrica Integral',      'SERVICIO',  NULL,         40.00, 0, 0),
(5,  2, 'PROD-001', 'Lente Completo Medida Básica',       'LENTE',     'UNISEX',  120.00, 0, 3),
(6,  2, 'PROD-004', 'Estuche de Cuero Reforzado',         'ACCESORIO', NULL,         25.00, 0, 5),
(7,  2, 'PROD-005', 'Lente de Sol Deportivo UV400',       'LENTE_SOL', 'HOMBRE',  150.00, 0, 2),
(8,  2,  NULL,      'Consulta Optométrica Integral',      'SERVICIO',  NULL,         40.00, 0, 0),
(9,  3, 'PROD-001', 'Lente Completo Medida Básica',       'LENTE',     'UNISEX',  120.00, 0, 3),
(10, 3, 'PROD-006', 'Lente Progresivo Premium',           'LENTE',     'UNISEX',  280.00, 0, 2),
(11, 3, 'PROD-007', 'Líquido Limpiador Spray 30ml',       'ACCESORIO', NULL,         10.00, 0, 8),
(12, 3,  NULL,      'Consulta Optométrica Integral',      'SERVICIO',  NULL,         40.00, 0, 0),
(13, 1, 'PROD-008', 'Lente Bifocal Ejecutivo',            'LENTE',     'UNISEX',  180.00, 0, 2),
(14, 2, 'PROD-009', 'Gafas de Sol Polarizadas Mariposa',  'LENTE_SOL', 'MUJER',    110.00, 0, 3),
(15, 3, 'PROD-010', 'Montura Infantil Colores',           'ACCESORIO', 'NINOS',    65.00, 0, 4);

-- 7. COMPRAS_CABECERA (Montos en 0, el trigger suma)
INSERT INTO compras_cabecera (id, proveedor_id, usuario_id, tienda_id, numero_orden, estado_fisico, estado_financiero, monto_total) VALUES
(1,  1, 1, 1, 'OC-2026-001', 'RECIBIDO',  'PAGADO',       0),
(2,  2, 1, 1, 'OC-2026-002', 'RECIBIDO',  'PAGO_PARCIAL', 0),
(3,  3, 1, 1, 'OC-2026-003', 'PENDIENTE', 'POR_PAGAR',    0),
(4,  4, 4, 2, 'OC-2026-004', 'RECIBIDO',  'PAGADO',       0),
(5,  5, 4, 2, 'OC-2026-005', 'RECIBIDO',  'PAGO_PARCIAL', 0),
(6,  6, 4, 2, 'OC-2026-006', 'PENDIENTE', 'POR_PAGAR',    0),
(7,  7, 7, 3, 'OC-2026-007', 'RECIBIDO',  'PAGADO',       0),
(8,  8, 7, 3, 'OC-2026-008', 'RECIBIDO',  'PAGADO',       0),
(9,  9, 1, 1, 'OC-2026-009', 'PENDIENTE', 'POR_PAGAR',    0),
(10, 7, 7, 3, 'OC-2026-010', 'PENDIENTE', 'POR_PAGAR',    0);

-- 8. COMPRAS_DETALLE (Despierta triggers de compras)
INSERT INTO compras_detalle (compra_id, insumo_id, cantidad, precio_unitario, subtotal) VALUES
(1,  1,  50, 8.50,  0),
(1,  3,  10, 25.00, 0),
(2,  2,  20, 18.00, 0),
(2,  4,  10, 35.00, 0),
(3,  5,   4, 45.00, 0),
(4,  6,  30, 8.50,  0),
(4,  8,   5, 80.00, 0),
(5,  7,  10, 55.00, 0),
(5,  9,  20, 15.00, 0),
(6,  8,   8, 80.00, 0),
(7,  10, 40, 8.50,  0),
(7,  12, 15, 25.00, 0),
(8,  11, 25, 18.00, 0),
(9,  14, 30, 15.00, 0),
(10, 13,  5, 30.00, 0);

-- 9. VENTAS_CABECERA (Montos en 0, el trigger suma)
INSERT INTO ventas_cabecera (id, paciente_id, usuario_id, tienda_id, numero_ticket, estado_financiero, tipo_comprobante, monto_subtotal, monto_igv, monto_total) VALUES
(1,  1,  2, 1, 'TK-2026-001', 'PAGADO',      'BOLETA',   0, 0, 0),
(2,  2,  2, 1, 'TK-2026-002', 'PAGADO',      'FACTURA',  0, 0, 0),
(3,  3, 10, 1, 'TK-2026-003', 'POR_COBRAR',  'BOLETA',   0, 0, 0),
(4,  4,  3, 1, 'TK-2026-004', 'PAGADO',      'BOLETA',   0, 0, 0),
(5,  5,  5, 2, 'TK-2026-005', 'PAGADO',      'FACTURA',  0, 0, 0),
(6,  6,  5, 2, 'TK-2026-006', 'PAGO_PARCIAL','BOLETA',   0, 0, 0),
(7,  7,  4, 2, 'TK-2026-007', 'PAGADO',      'BOLETA',   0, 0, 0),
(8,  9,  9, 3, 'TK-2026-008', 'PAGADO',      'BOLETA',   0, 0, 0),
(9,  10, 9, 3, 'TK-2026-009', 'POR_COBRAR',  'FACTURA',  0, 0, 0),
(10, 11, 7, 3, 'TK-2026-010', 'PAGADO',      'BOLETA',   0, 0, 0);

-- 10. VENTAS_DETALLE (Despierta triggers de ventas)
INSERT INTO ventas_detalle (id, venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1,  1,  1,  1, 120.00, 0),
(2,  1,  3,  1,   5.00, 0),
(3,  2,  2,  1,  95.00, 0),
(4,  3,  4,  1,  40.00, 0),
(5,  4, 13,  1, 180.00, 0),
(6,  4,  3,  2,   5.00, 0),
(7,  5,  5,  1, 120.00, 0),
(8,  5,  6,  1,  25.00, 0),
(9,  6,  8,  1,  40.00, 0),
(10, 7,  7,  1, 150.00, 0),
(11, 7, 14,  1, 110.00, 0),
(12, 8,  9,  1, 120.00, 0),
(13, 9, 10,  1, 280.00, 0),
(14, 9, 11,  1,  10.00, 0),
(15,10, 15,  2,  65.00, 0);

-- 11. CONSULTAS
INSERT INTO consultas (id, paciente_id, usuario_id, tienda_id, venta_detalle_id, motivo) VALUES
(1,  3,  10, 1, 4,    'Cansancio visual por computadora'),
(2,  6,   5, 2, 9,    'Control post-operatorio referencial'),
(3,  1,  10, 1, NULL, 'Visión borrosa de lejos, sin compra'),
(4,  5,   8, 2, NULL, 'Primera visita, evaluación general'),
(5,  9,   8, 3, NULL, 'Dificultad para leer de cerca'),
(6,  10,  8, 3, NULL, 'Dolor de cabeza frecuente'),
(7,  11,  8, 3, NULL, 'Renovación de medida anual'),
(8,  2,  10, 1, NULL, 'Revisión por cambio de graduación'),
(9,  7,   5, 2, NULL, 'Ardor en los ojos al usar pantallas'),
(10, 13, 10, 1, NULL, 'Chequeo de rutina solicitado por empresa');

-- 12. HISTORIAL_CLINICO
INSERT INTO historial_clinico (id, consulta_id, graduacion_od, graduacion_oi, observaciones) VALUES
(1,  1,  'Neutro',                                  'Neutro',                                  'Se recomienda filtro azul. Sin graduación.'),
(2,  2,  '-3.00 ESF',                               '-3.25 ESF',                               'Miopía moderada, usa lentes de contacto ocasionalmente.'),
(3,  3,  '-1.25 ESF, -0.50 CIL x 180°',             '-1.50 ESF',                               'Miopía leve bilateral.'),
(4,  4,  '-1.50 ESF',                               '-1.50 ESF',                               'Miopía leve simétrica.'),
(5,  5,  '+2.25 ESF (Adición +1.50)',               '+2.50 ESF (Adición +1.50)',               'Presbicia, requiere bifocales o progresivos.'),
(6,  6,  '-4.00 ESF, -1.50 CIL x 180°',             '-4.25 ESF, -1.25 CIL x 175°',             'Miopía alta con astigmatismo.'),
(7,  7,  '+1.50 ESF',                               '+1.50 ESF',                               'Sin variaciones respecto al año anterior.'),
(8,  8,  '+1.00 ESF',                               '+1.25 ESF, -0.25 CIL x 90°',              'Hipermetropía estable.'),
(9,  9,  '-0.50 ESF, -0.25 CIL x 180°',             '-0.75 ESF, -0.25 CIL x 175°',             'Miopía muy leve, uso nocturno recomendado.'),
(10,10,  '-2.00 ESF',                               '-1.75 ESF',                               'Miopía moderada, control en 6 meses.');

-- 13. ORDENES_TRABAJO
INSERT INTO ordenes_trabajo (id, venta_id, historial_clinico_id, estado_fisico, tipo_trabajo, usa_montura_cliente, detalles_montura_cliente, usa_luna_cliente, detalles_luna_cliente, fecha_prometida) VALUES
(1,  1,  3,    'ENTREGADO',   'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      '2026-04-15 18:00:00'),
(2,  2,  NULL, 'ENTREGADO',   'SOLO_LUNAS',     TRUE,  'Montura propia Rayban negro', FALSE, NULL,                      '2026-04-16 18:00:00'),
(3,  4,  NULL, 'LISTO',       'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      '2026-04-20 18:00:00'),
(4,  5,  4,    'ENTREGADO',   'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      '2026-04-18 18:00:00'),
(5,  7,  9,    'LABORATORIO', 'SOLO_LUNAS',     TRUE,  'Montura cliente titanio gris',FALSE, NULL,                      '2026-06-30 18:00:00'),
(6,  8,  5,    'PENDIENTE',   'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      '2026-07-05 18:00:00'),
(7,  9,  6,    'LABORATORIO', 'LENTE_COMPLETO', FALSE, NULL,                          FALSE, NULL,                      '2026-06-28 18:00:00'),
(8,  10, 7,    'LISTO',       'SOLO_MONTURA',   FALSE, NULL,                          TRUE,  'Lunas cliente bifocales', '2026-07-02 18:00:00'),
(9,  6,  2,    'PENDIENTE',   'REPARACION',     FALSE, NULL,                          FALSE, NULL,                      '2026-07-10 18:00:00'),
(10, 3,  1,    'ENTREGADO',   'SOLO_LUNAS',     FALSE, NULL,                          FALSE, NULL,                      '2026-04-25 18:00:00');

-- 14. KARDEX (Al insertar, el trigger va y actualiza los stocks a los catálogos)
INSERT INTO kardex (tienda_id, usuario_id, insumo_id, producto_id, compra_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo) VALUES
-- a. Inventario Inicial añadido para evitar stock negativo de Productos vendidos
(1, 1, NULL, 1,  NULL, NULL, 'ENTRADA', 'Inventario Inicial', 10, 0),
(1, 1, NULL, 2,  NULL, NULL, 'ENTRADA', 'Inventario Inicial', 10, 0),
(2, 4, NULL, 7,  NULL, NULL, 'ENTRADA', 'Inventario Inicial', 10, 0),
(3, 7, NULL, 10, NULL, NULL, 'ENTRADA', 'Inventario Inicial', 10, 0),
-- b. Entradas de insumos por compras recibidas
(1, 1, 1,  NULL, 1, NULL, 'ENTRADA', 'Recepción OC-2026-001',  50, 0),
(1, 1, 3,  NULL, 1, NULL, 'ENTRADA', 'Recepción OC-2026-001',  10, 0),
(1, 1, 2,  NULL, 2, NULL, 'ENTRADA', 'Recepción OC-2026-002',  20, 0),
(1, 1, 4,  NULL, 2, NULL, 'ENTRADA', 'Recepción OC-2026-002',  10, 0),
(2, 4, 6,  NULL, 4, NULL, 'ENTRADA', 'Recepción OC-2026-004',  30, 0),
(2, 4, 8,  NULL, 4, NULL, 'ENTRADA', 'Recepción OC-2026-004',   5, 0),
(2, 4, 7,  NULL, 5, NULL, 'ENTRADA', 'Recepción OC-2026-005',  10, 0),
(2, 4, 9,  NULL, 5, NULL, 'ENTRADA', 'Recepción OC-2026-005',  20, 0),
(3, 7, 10, NULL, 7, NULL, 'ENTRADA', 'Recepción OC-2026-007',  40, 0),
(3, 7, 12, NULL, 7, NULL, 'ENTRADA', 'Recepción OC-2026-007',  15, 0),
(3, 7, 11, NULL, 8, NULL, 'ENTRADA', 'Recepción OC-2026-008',  25, 0),
-- c. Salidas de productos por ventas
(1, 2, NULL, 1,  NULL, 1, 'SALIDA',  'Venta TK-2026-001',       1, 0),
(1, 2, NULL, 2,  NULL, 2, 'SALIDA',  'Venta TK-2026-002',       1, 0),
(2, 5, NULL, 7,  NULL, 7, 'SALIDA',  'Venta TK-2026-007',       1, 0),
(3, 9, NULL, 10, NULL, 9, 'SALIDA',  'Venta TK-2026-009',       1, 0);

-- 15. MOVIMIENTOS_CAJA
INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, compra_id, tipo, metodo_pago, monto, descripcion) VALUES
(1, 2, 1,  NULL, 'ENTRADA', 'YAPE',          127.10, 'Cobro venta TK-2026-001'),
(1, 2, 2,  NULL, 'ENTRADA', 'EFECTIVO',      112.10, 'Cobro venta TK-2026-002'),
(1, 3, 4,  NULL, 'ENTRADA', 'TARJETA',       220.50, 'Cobro venta TK-2026-004'),
(2, 5, 5,  NULL, 'ENTRADA', 'TRANSFERENCIA', 171.10, 'Cobro venta TK-2026-005'),
(2, 5, 6,  NULL, 'ENTRADA', 'EFECTIVO',       24.00, 'Cobro parcial TK-2026-006'),
(2, 4, 7,  NULL, 'ENTRADA', 'YAPE',          307.10, 'Cobro venta TK-2026-007'),
(3, 9, 8,  NULL, 'ENTRADA', 'EFECTIVO',      141.60, 'Cobro venta TK-2026-008'),
(3, 7, 10, NULL, 'ENTRADA', 'PLIN',          153.70, 'Cobro venta TK-2026-010'),
(1, 1, NULL, 1,  'SALIDA',  'TRANSFERENCIA', 675.00, 'Pago OC-2026-001 proveedor Lab. Óptico Sur'),
(1, 1, NULL, 2,  'SALIDA',  'TRANSFERENCIA', 355.00, 'Pago parcial OC-2026-002'),
(2, 4, NULL, 4,  'SALIDA',  'EFECTIVO',      655.00, 'Pago OC-2026-004 Ópt. Mayorista Andina'),
(2, 4, NULL, 5,  'SALIDA',  'TRANSFERENCIA', 425.00, 'Pago parcial OC-2026-005'),
(3, 7, NULL, 7,  'SALIDA',  'TRANSFERENCIA', 715.00, 'Pago OC-2026-007 Global Optics Import'),
(3, 7, NULL, 8,  'SALIDA',  'EFECTIVO',      450.00, 'Pago OC-2026-008 Proveedor Local Miraflores'),
(1, 2, NULL, NULL,'SALIDA', 'EFECTIVO',       18.50, 'Gasto caja chica - útiles de limpieza');