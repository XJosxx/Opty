# Opty — Guía Técnica de Base de Datos para Entregable Académico

Este documento detalla el flujo de datos, la conectividad, el uso de lógica programable (procedimientos almacenados, triggers y vistas) y el algoritmo de migraciones implementados en el sistema **Opty**. Ha sido redactado para servir como sustento del entregable final del curso de **Bases de Datos**.

---

## 1. Conectividad y Arquitectura de Acceso a Datos

El sistema **Opty** está diseñado bajo una arquitectura limpia en 4 capas. El acceso a la base de datos MySQL 8.0 se realiza de forma directa y optimizada sin ORMs pesados, utilizando **JDBC Plano** y un pool de conexiones gestionado por **HikariCP**.

```
[ Capa de Vista (FX/FXML/CSS) ]
               │
               ▼
[ Capa de Servicio (Lógica de Negocio) ]
               │
               ▼
[ Capa de Repositorio (Interfaces DAO) ]
               │
               ▼
[ Connection Pool (HikariCP / JDBC) ] <─── [ config/config.properties ]
               │
               ▼
  [ Base de Datos (MySQL) ]
```

### Componentes de Conectividad:
*   **Archivo de Configuración (`config/config.properties`)**: Almacena las variables del servidor de base de datos (Host, puerto, nombre de base de datos, usuario, contraseña y uso de SSL).
*   **Clase de Configuración (`DatabaseConfig.java`)**: Carga las propiedades del archivo y configura el pool de conexiones de alto rendimiento a través de un Singleton de `HikariDataSource`.
*   **Repositorio Base (`BaseJdbcRepository.java`)**: Es la clase madre de la cual heredan todas las implementaciones de repositorios. Expone el método `getConnection()` para suministrar una conexión libre del pool en cada consulta.

---

## 2. Dónde y Cómo se Usan los Elementos de la Base de Datos

Para optimizar el rendimiento y garantizar la integridad referencial, el sistema delega operaciones críticas a la lógica programable de MySQL. A continuación, se detalla la correspondencia exacta de dónde se ejecuta cada cosa en el código Java.

### A. Procedimientos Almacenados (Stored Procedures)

| Procedimiento | Descripción en la Base de Datos | Invocación en el Código Java |
| :--- | :--- | :--- |
| **`sp_procesar_venta`** | Recibe el cliente, vendedor, tienda, producto y cantidad. Valida el stock actual. Registra la cabecera de la venta, el detalle, genera una salida en el Kardex y registra un ingreso de dinero en caja. Todo de forma atómica (transaccional). | Invocado mediante `CallableStatement` en: `VentaRepositoryImpl.processSale` (Líneas 196-221). |
| **`sp_registrar_compra`** | Registra el abastecimiento de insumos comprados a un proveedor. Inserta la cabecera, el detalle, genera una entrada en el Kardex y crea un egreso de dinero en caja para pagar al proveedor. | Invocado mediante `CallableStatement` en: `CompraRepositoryImpl.processPurchase` (Líneas 178-203). |
| **`sp_registrar_paciente_y_consulta`** | Si el paciente no existe, lo crea. Genera una venta de boleta/factura por el servicio de consulta optométrica y crea el registro de la consulta de forma integrada. | Invocado mediante `CallableStatement` en: `ConsultaRepositoryImpl.registrarConsultaConVenta` (Líneas 157-230). |

---

### B. Disparadores (Triggers)

Los triggers de MySQL se ejecutan de manera automática e invisible tras acciones de escritura (INSERT, DELETE). En Java no se llaman explícitamente, pero el sistema depende de ellos para el cálculo de totales y stocks:

1.  **`trg_kardex_actualizar_stock` (Tabla `kardex` - AFTER INSERT)**:
    *   *Qué hace*: Identifica si el movimiento es una ENTRADA, SALIDA o AJUSTE, y si afecta a un `insumo` o a un `producto`. Suma o resta la cantidad directamente en `stock_actual` de la tabla correspondiente y calcula el saldo histórico.
    *   *Impacto en Java*: Permite que los controladores de UI muestren stocks actualizados al instante en la pantalla de inventarios sin necesidad de realizar recálculos manuales en el backend de Java.
2.  **`trg_ventas_detalle_subtotal` / `trg_compras_detalle_subtotal` (BEFORE INSERT)**:
    *   *Qué hace*: Multiplica automáticamente la cantidad por el precio unitario antes de guardar, asegurando que la columna `subtotal` del detalle esté siempre calculada correctamente en base de datos.
3.  **`trg_ventas_detalle_total_cabecera` / `trg_compras_detalle_total_cabecera` (AFTER INSERT / AFTER DELETE)**:
    *   *Qué hace*: Al registrarse un detalle, suma los subtotales de la venta/compra. Calcula el IGV (18% usando la función SQL `fn_calcular_igv`) y actualiza el `monto_subtotal`, `monto_igv` y `monto_total` en la cabecera correspondiente.
    *   *Impacto en Java*: En `VentaRepositoryImpl.registrarVentaMultiproducto` (Líneas 278-416), Java inserta múltiples detalles dentro de una transacción y luego lee el monto final de la cabecera ya autocalculado por los triggers de MySQL.

---

### C. Vistas (Views)

Las vistas simplifican las consultas uniendo múltiples tablas en una sola estructura lógica fácil de consultar desde Java:

*   **`v_productos_disponibles`**: Filtra productos activos que tienen un stock mayor a cero en la tienda correspondiente.
    *   *Uso en Java*: Utilizado en `DashboardServiceImpl.getProductosDisponiblesView` (Líneas 368-399) para generar los gráficos de barras del stock por categorías.
*   **`v_insumos_bajo_minimo`**: Muestra los insumos cuyo stock es inferior al stock mínimo parametrizado, calculando la diferencia de unidades faltantes.
    *   *Uso en Java*: Utilizado en `DashboardServiceImpl.getInsumosBajoMinimoView` para el listado de alertas de bajo stock en el Dashboard.
*   **`v_historial_clinico_paciente`**: Consolida datos del paciente, fecha de atención, motivo de consulta, refracción del ojo derecho/izquierdo, observaciones y calcula la edad del paciente mediante la función SQL `fn_calcular_edad`.
    *   *Uso en Java*: Utilizado en `DashboardServiceImpl.getHistorialClinicoPaciente` para poblar el feed de actividades clínicas recientes del médico.
*   **`v_ordenes_pendientes`**: Agrupa órdenes de taller, pacientes y tiempos de entrega, determinando mediante lógica de fechas si una orden está "A TIEMPO" o "VENCIDA".
    *   *Uso en Java*: Utilizado en `DashboardServiceImpl.getOrdenesPendientesView` para alimentar los listados y alertas de producción en la UI.

---

## 3. Algoritmo y Flujo de Migraciones Automáticas

A solicitud del docente del curso, se ha diseñado e integrado un **Algoritmo de Migración de Base de Datos** a nivel de aplicación (`DatabaseMigrationManager.java`). Este flujo emula las capacidades de herramientas industriales de versionamiento de bases de datos como *Flyway* o *Liquibase*.

### Flujo de Ejecución del Algoritmo
Cuando se inicia la aplicación, el método `start` de `OptyApp` ejecuta el flujo de migración en la base de datos activa de forma automática:

```
[Inicio de OptyApp]
       │
       ▼
[DatabaseConfig.load()]  <── Carga parámetros de conexión
       │
       ▼
[DatabaseMigrationManager.runMigrations()]
       │
       ├─► 1. ¿Existe la tabla 'schema_version'?
       │      ├── NO ──► Crea la tabla 'schema_version'
       │      │          ¿Existe la tabla 'pacientes'? (Legacy DB)
       │      │             ├── SI ──► Aplica Baseline (Registra V1, V2, V3 como aplicados)
       │      │             └── NO ──► Continúa vacío
       │      └── SI ──► Continúa
       │
       ├─► 2. Consulta la lista de versiones ya aplicadas en 'schema_version'
       │
       ├─► 3. ¿Está V4 aplicada?
       │      └── NO ──► Ejecuta '/migrations/V4__datos_extra.sql' (Datos estructurados JSON)
       │                 Registra versión 4 en la tabla
       │
       ├─► 4. ¿Está V5 aplicada?
       │      └── NO ──► Ejecuta 'ALTER TABLE pacientes ADD COLUMN email ...' (Alteración de esquema)
       │                 Registra versión 5 en la tabla
       │
       ▼
[Fin de Migraciones - Carga Pantalla de Login]
```

### Características del Motor de Migraciones:
1.  **Tabla de Control (`schema_version`)**: Lleva la bitácora de qué scripts se han ejecutado con su descripción y fecha. Ningún script se ejecuta dos veces, evitando duplicar datos o lanzar errores de llaves primarias.
2.  **Mecanismo de Baselines (Database Legada)**: Si el sistema detecta que el usuario ya tiene la base de datos con tablas creadas antes de la incorporación del migrador, inicializa la bitácora y marca las versiones previas (V1, V2, V3) como aplicadas para no alterar las tablas existentes.
3.  **Migración de Datos Estructurados (V4)**: Inserta 8 nuevos pacientes de prueba, genera flujos de caja y ventas de días pasados (para poblar gráficos de línea), e inserta refracciones oculares estructuradas en formato JSON en las columnas `graduacion_od` y `graduacion_oi`.
4.  **Migración de Esquema Físico (V5)**: Demuestra la alteración física de tablas al ejecutar un comando `ALTER TABLE` agregando la columna `email` a la tabla `pacientes` dinámicamente sin intervención manual del programador.

---

## 4. Estructura de Control de la Migración

Una vez ejecutada la aplicación por primera vez, puedes revisar en tu gestor MySQL la tabla `schema_version` para comprobar que las migraciones se registraron correctamente:

```sql
SELECT * FROM optica_db.schema_version;
```

### Salida esperada en consola de base de datos:
| version | description | applied_at |
| :---: | :--- | :--- |
| **1** | Baseline Schema Inicial | 2026-07-11 20:55:00 |
| **2** | Baseline Procedimientos y Vistas | 2026-07-11 20:55:00 |
| **3** | Baseline Datos Semilla | 2026-07-11 20:55:00 |
| **4** | Carga de Datos de Prueba y Medidas JSON | 2026-07-11 20:55:05 |
| **5** | Añadir columna email a pacientes | 2026-07-11 20:55:07 |
