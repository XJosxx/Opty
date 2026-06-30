CREATE DATABASE IF NOT EXISTS optica_db;
USE optica_db;

-- Configuración de sucursales
CREATE TABLE config_tienda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre_optica VARCHAR(100) NOT NULL,
    ruc VARCHAR(20) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NULL
) ENGINE=InnoDB;

-- Trabajadores y credenciales
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido_p VARCHAR(100) NOT NULL,
    apellido_m VARCHAR(100) NOT NULL,
    tipo_documento ENUM('DNI', 'CE', 'PASAPORTE'),
    num_documento VARCHAR(20) NOT NULL UNIQUE,
    rol ENUM('ADMIN','VENDEDOR','MEDICO') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso TIMESTAMP NULL,
    tienda_id INT NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Pacientes y clientes para facturación
CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_p VARCHAR(100) NOT NULL,
    apellido_m VARCHAR(100) NOT NULL,
    tipo_documento ENUM('DNI', 'CE', 'PASAPORTE'),
    num_documento VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NULL,
    fecha_nacimiento DATE NULL,
    es_destacado BOOLEAN DEFAULT FALSE,
    tipo_destacado ENUM('AUTO', 'MANUAL') NULL, 
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tienda_id INT NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Proveedores de insumos
CREATE TABLE proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(150) NOT NULL,
    nombre_contacto VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(100) NULL,
    ruc VARCHAR(20) UNIQUE,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tienda_id INT NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Catálogo y stock de materia prima
CREATE TABLE insumos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tienda_id INT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    categoria ENUM('LUNA','MONTURA','ACCESORIO','CONSUMIBLE') NOT NULL,
    material VARCHAR(100) NULL,
    precio_costo DECIMAL(10,2) NOT NULL,
    stock_actual INT DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    UNIQUE(codigo, tienda_id),
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Catálogo y stock de despacho y servicios
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tienda_id INT NOT NULL,
    codigo VARCHAR(50) NULL,
    nombre VARCHAR(150) NOT NULL,
    categoria ENUM('LENTE','LENTE_SOL','ACCESORIO','SERVICIO') NOT NULL,
    genero_objetivo ENUM('HOMBRE','MUJER','UNISEX','NINOS') NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    stock_actual INT DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    UNIQUE(codigo, tienda_id),
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Cabecera de órdenes de compra
CREATE TABLE compras_cabecera (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INT NOT NULL,
    usuario_id INT NOT NULL,
    tienda_id INT NOT NULL,
    numero_orden VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_fisico ENUM('PENDIENTE','RECIBIDO') DEFAULT 'PENDIENTE',
    estado_financiero ENUM('POR_PAGAR','PAGO_PARCIAL','PAGADO','ANULADO') NOT NULL DEFAULT 'POR_PAGAR',
    monto_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Detalle de insumos comprados
CREATE TABLE compras_detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL,
    insumo_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (compra_id) REFERENCES compras_cabecera(id) ON DELETE CASCADE,
    FOREIGN KEY (insumo_id) REFERENCES insumos(id)
) ENGINE=InnoDB;

-- Cabecera de comprobantes de venta
CREATE TABLE ventas_cabecera (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    tienda_id INT NOT NULL,
    numero_ticket VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_financiero ENUM('POR_COBRAR','PAGO_PARCIAL','PAGADO','ANULADO') NOT NULL DEFAULT 'POR_COBRAR',
    tipo_comprobante ENUM('BOLETA','FACTURA') NOT NULL,
    monto_subtotal DECIMAL(10,2) NOT NULL,
    monto_igv DECIMAL(10,2) NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id)
) ENGINE=InnoDB;

-- Detalle de productos vendidos
CREATE TABLE ventas_detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas_cabecera(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;

-- Registro de citas médicas
CREATE TABLE consultas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    tienda_id INT NOT NULL,
    venta_detalle_id INT NULL,  
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id),
    FOREIGN KEY (venta_detalle_id) REFERENCES ventas_detalle(id) 
) ENGINE=InnoDB;

-- Detalle clínico de la medida (1:1)
CREATE TABLE historial_clinico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id INT NOT NULL UNIQUE,
    graduacion_od VARCHAR(255) NULL,
    graduacion_oi VARCHAR(255) NULL,
    observaciones TEXT NULL,
    FOREIGN KEY (consulta_id) REFERENCES consultas(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bitácora de taller y laboratorio
CREATE TABLE ordenes_trabajo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    historial_clinico_id INT NULL,
    estado_fisico ENUM('PENDIENTE','LABORATORIO','LISTO','ENTREGADO') NOT NULL DEFAULT 'PENDIENTE',
    tipo_trabajo ENUM('LENTE_COMPLETO', 'SOLO_LUNAS', 'SOLO_MONTURA', 'REPARACION') NOT NULL,
    usa_montura_cliente BOOLEAN NOT NULL DEFAULT FALSE,
    detalles_montura_cliente VARCHAR(255),
    usa_luna_cliente BOOLEAN NOT NULL DEFAULT FALSE,
    detalles_luna_cliente VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_prometida DATETIME NULL, 
    FOREIGN KEY (venta_id) REFERENCES ventas_cabecera(id),
    FOREIGN KEY (historial_clinico_id) REFERENCES historial_clinico(id)
) ENGINE=InnoDB;

-- Historial unificado de movimientos físicos
CREATE TABLE kardex (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tienda_id INT NOT NULL,
    usuario_id INT NOT NULL,
    insumo_id INT NULL,
    producto_id INT NULL,
    compra_id INT NULL,
    venta_id INT NULL,
    tipo_movimiento ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
    motivo VARCHAR(255) NULL,
    cantidad INT NOT NULL,
    cantidad_saldo INT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (insumo_id) REFERENCES insumos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (compra_id) REFERENCES compras_cabecera(id),
    FOREIGN KEY (venta_id) REFERENCES ventas_cabecera(id)
) ENGINE=InnoDB;

-- Flujo de dinero en efectivo
CREATE TABLE movimientos_caja (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tienda_id INT NOT NULL,
    usuario_id INT NOT NULL,
    venta_id INT NULL,
    compra_id INT NULL,
    tipo ENUM('ENTRADA','SALIDA') NOT NULL,
    metodo_pago ENUM('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA') NOT NULL, 
    monto DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(255) NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tienda_id) REFERENCES config_tienda(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (venta_id) REFERENCES ventas_cabecera(id),
    FOREIGN KEY (compra_id) REFERENCES compras_cabecera(id)
) ENGINE=InnoDB;