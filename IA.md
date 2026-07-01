# Opty — Guía de Contexto y Arquitectura para Inteligencia Artificial (IA)

Este documento ha sido creado para proporcionar a cualquier IA (Gemini, Claude, GPT, etc.) el contexto técnico y funcional completo del proyecto **Opty**. Permite comprender de forma inmediata la arquitectura, flujos de datos, reglas de negocio, tecnologías utilizadas y los pasos sugeridos para continuar con el desarrollo de los módulos pendientes.

---

## 1. Información General del Proyecto
**Opty** es una aplicación de escritorio diseñada para la gestión integral de una cadena de ópticas multiclínica. No solo administra la venta de productos tradicionales (lentes, monturas, accesorios), sino que también cubre el ciclo clínico y operativo completo:
- **Consulta Médica**: Registro del paciente, historia clínica y graduación de medidas ópticas.
- **Taller y Laboratorio**: Emisión de órdenes de trabajo para fabricación, reparación o montaje de cristales.
- **Inventario**: Kardex unificado para el seguimiento físico de insumos (materia prima) y productos terminados.
- **Caja y Tesorería**: Registro de ingresos y egresos de efectivo y banca electrónica enlazados a cada sucursal.

---

## 2. Stack Tecnológico

El proyecto está construido bajo un enfoque académico de alto rendimiento, evitando frameworks pesados como Spring Boot o JPA/Hibernate en favor de un control granular sobre las consultas y conexiones SQL.

- **Lenguaje**: Java 21 (utiliza features modernos de Java como `var`, Pattern Matching para `instanceof`, etc.).
- **Interfaz Gráfica**: JavaFX 23 (diseño modular con FXML y estilos personalizados en CSS).
- **Gestor de Dependencias**: Maven 3.9+
- **Base de Datos**: MySQL 8.0+ (Local o Cloud).
- **Acceso a Datos**: JDBC Plano (sin ORM), llamadas directas a procedimientos almacenados (`CallableStatement`) y sentencias preparadas (`PreparedStatement`).
- **Librerías Adicionales**:
  - `mysql-connector-j` (9.3.0) — Driver de MySQL.
  - `controlsfx` (11.2.1) — Componentes de UI enriquecidos para JavaFX.

---

## 3. Arquitectura del Software (4 Capas)

El código Java sigue los principios SOLID y la programación orientada a objetos en 4 capas limpias bajo el paquete raíz [opty](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty):

```
opty
│
├── config/        --> Configuración global (ej. DatabaseConfig de JDBC)
├── model/         --> Entidades POJO (con validación interna) y Enums
├── repository/    --> Interfaces y clases de acceso a datos JDBC (Acceso a BD)
├── service/       --> Capa de negocio e idempotencia (Interfaces y clases Impl)
└── view/          --> Controladores JavaFX y archivos FXML/CSS
```

### Detalle de Capas
1. **Model** ([opty/model](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/model)):
   - **Entity**: Contiene 15 clases POJO que mapean las tablas físicas. Tienen validación defensiva en sus setters para evitar datos corruptos.
   - **Enums**: 14 enums para campos restrictivos en base de datos (e.g., `Rol`, `TipoDocumento`, `MetodoPago`, `CategoriaInsumo`, etc.).
2. **Repository** ([opty/repository](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/repository)):
   - Define un [CrudRepository](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/repository/CrudRepository.java) genérico.
   - Implementaciones concretas como `VentaRepositoryImpl` heredan de `BaseJdbcRepository`, la cual provee la conexión activa de base de datos.
3. **Service** ([opty/service](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/service)):
   - Controla las transacciones y lógica lógica de negocio pura.
   - Separa la especificación (interfaz) de su implementación (`impl/`) para permitir pruebas unitarias limpias.
4. **View** ([opty/view](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/view) & [resources/opty/view](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/resources/opty/view)):
   - Controladores de JavaFX vinculados a archivos `.fxml`.
   - Estilizado unificado a través de [opty.css](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/resources/opty/view/styles/opty.css).

---

## 4. Arquitectura y Flujo de la Base de Datos

La base de datos se llama `optica_db` y su diseño se gestiona a través de tres scripts ubicados en el directorio [sql](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/sql):
- [01_tablas.sql](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/sql/01_tablas.sql): Definición de tablas y claves primarias/foráneas.
- [02_logica.sql](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/sql/02_logica.sql): Índices, funciones, disparadores (triggers), procedimientos almacenados y vistas.
- [03_datos.sql](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/sql/03_datos.sql): Carga de datos semilla (seeds) para pruebas locales.

### Relación de Tablas Críticas
```
                                  +-------------------+
                                  |   config_tienda   |
                                  +---------+---------+
                                            |
         +-------------------+--------------+---------------+-------------------+
         |                   |                              |                   |
+--------v--------+ +--------v--------+            +--------v--------+ +--------v--------+
|    usuarios     | |    pacientes    |            |   proveedores   | |    insumos      |
+--------+--------+ +--------+--------+            +--------+--------+ +--------+--------+
         |                   |                              |                   |
         |         +---------+                              |                   |
         |         |                                        |                   |
+--------v---------v-+                                      |                   |
|  ventas_cabecera   |                                      |                   |
+--------+-----------+                                      |                   |
         |                                                  |                   |
+--------v--------+        +-------------------+            |                   |
| ventas_detalle  |<-------|     productos     |            |                   |
+--------+--------+        +---------+---------+            |                   |
         |                           |                      |                   |
         |                           |                      |                   |
+--------v--------+                  |             +--------v--------+          |
|    consultas    |<-----------------+             | compras_cabecera|          |
+--------+--------+                                +--------+--------+          |
         |                                                  |                   |
+--------v--------+                                +--------v--------+          |
|historial_clinico|                                | compras_detalle |<---------+
+--------+--------+                                +-----------------+
         |
+--------v--------+
|ordenes_trabajo  |
+-----------------+
```

### Automatizaciones en BD (Triggers)
Para evitar la duplicación de código en Java y garantizar la integridad referencial, la base de datos se encarga de varias tareas de forma automática:
- **Actualización de Stock y Kardex** (`trg_kardex_actualizar_stock`): Al insertar un registro en `kardex` (entrada, salida o ajuste), el disparador recalcula y actualiza el `stock_actual` en la tabla `insumos` o `productos`, asignando además el saldo restante en `cantidad_saldo`.
- **Cálculo de Subtotales**: Los disparadores `trg_ventas_detalle_subtotal` y `trg_compras_detalle_subtotal` multiplican automáticamente la `cantidad * precio_unitario` para guardarla en `subtotal`.
- **Actualización de Cabeceras de Monto**: Al insertar o eliminar en detalles (`ventas_detalle` o `compras_detalle`), los triggers recalculan el `monto_subtotal`, calculan el IGV (18% usando la función `fn_calcular_igv`) y actualizan el `monto_total` en la cabecera correspondiente.

### Procedimientos Almacenados Clave
El sistema centraliza las operaciones complejas y transaccionales en stored procedures:
1. **`sp_registrar_paciente_y_consulta`**:
   - Verifica si un paciente existe (por número de documento). Si no, lo registra automáticamente.
   - Crea un ticket de venta en `ventas_cabecera` por el servicio de consulta médica.
   - Inserta el detalle de venta asociado.
   - Registra la consulta en la tabla `consultas` enlazada a la venta.
2. **`sp_procesar_venta`**:
   - Valida si hay stock del producto en la tienda solicitada.
   - Registra la venta (cabecera + detalle).
   - Genera una salida en el `kardex` (lo que dispara la reducción del stock físico).
   - Genera un ingreso de dinero en `movimientos_caja` según el método de pago indicado.
3. **`sp_registrar_compra`**:
   - Registra una compra de insumos (cabecera + detalle).
   - Genera una entrada en el `kardex` (lo que dispara el incremento de stock del insumo).
   - Genera un egreso de dinero en `movimientos_caja` para pagar al proveedor.

---

## 5. Estado Actual del Proyecto y Hoja de Ruta

| Fase | Módulo / Componente | Estado | Notas |
|---|---|---|---|
| 1 | Estructura base Maven, Modelos, Enums | ✅ Completo | Todo el dominio de datos está listo. |
| 2 | Repositorios JDBC con Procedimientos | ✅ Completo | Los DAOs acceden a la BD y ejecutan triggers/SPs. |
| 3 | Capa de Servicios | ✅ Completo | Implementación de lógica básica y delegación a repos. |
| 4 | Login + Dashboard Básico | ✅ Completo | Flujo de autenticación funcional y estadísticas del Dashboard. |
| 5 | Módulo Pacientes + Consultas UI | ⏳ Pendiente | Crear FXML y controlador para registrar pacientes y agendar consultas. |
| 6 | Módulo Ventas UI | ⏳ Pendiente | Interfaz para seleccionar productos y procesar ventas. |
| 7 | Módulo Compras UI | ⏳ Pendiente | Interfaz para registrar compras de insumos a proveedores. |
| 8 | Módulo Inventario UI | ⏳ Pendiente | Tablas de stock actual, alertas de bajo stock y movimientos de Kardex. |
| 9 | Módulo Usuarios UI | ⏳ Pendiente | Gestión de trabajadores (Crear/Modificar/Suspender) reservado para ADMIN. |
| 10| Módulo Órdenes de Trabajo UI | ⏳ Pendiente | Visualización de órdenes de taller, alertas de vencimiento y cambios de estado. |

---

## 6. Análisis del Flujo y Recomendaciones de Cambios (Refactorización)

Durante el análisis del código actual, se detectaron las siguientes limitaciones estructurales y áreas de mejora. Se recomienda a la IA encargada de las siguientes fases resolver estos puntos preferiblemente antes de construir las pantallas de UI:

### A. Ventas y Compras Mono-producto vs. Multi-producto
- **Problema**: El procedimiento `sp_procesar_venta` y `sp_registrar_compra` solo admiten un único `producto_id` / `insumo_id` y su cantidad por transacción. En el negocio real, un cliente compra múltiples productos en una sola transacción (ej. montura + cristales + estuche).
- **Solución propuesta**:
  1. Mantener los Stored Procedures para compras/ventas rápidas de un único ítem.
  2. Implementar transacciones a nivel de Java (`conn.setAutoCommit(false)`) en los servicios de compra/venta. Esto permitirá guardar la cabecera, obtener su ID generado y, recursivamente, guardar múltiples detalles en un bucle antes de hacer `commit()`.

### B. Generación de Tickets con UNIX_TIMESTAMP
- **Problema**: En `sp_procesar_venta` y otros procedimientos, el número de ticket se genera mediante:
  `SET v_ticket = CONCAT('TK-', UNIX_TIMESTAMP());`
  Esto tiene una resolución de un segundo. Si dos vendedores en diferentes tiendas registran una venta al mismo segundo, habrá colisión de clave única en la columna `numero_ticket`.
- **Solución propuesta**: Cambiar a una nomenclatura secuencial por sucursal (`TK-C1-00001`) usando una tabla auxiliar de numeración, o generar un identificador único más complejo que combine el ID de la tienda y un contador correlativo.

### C. Pool de Conexiones a Base de Datos
- **Estado**: ✅ **Implementado** (Integrado con HikariCP).
- **Detalle**: Se reemplazó el uso directo de `DriverManager` por un pool gestionado por `HikariDataSource` en `DatabaseConfig.java`. Esto optimiza sustancialmente la velocidad y reutilización de conexiones SQL sin haber requerido modificar el código de los repositorios.

### D. Hardcoding en Repositorios
- **Problema**: En `VentaRepositoryImpl.processSale`, se llama al procedimiento almacenado pasando `"BOLETA"` fijo:
  `cs.setString(4, "BOLETA");`
  Esto invalida la posibilidad de emitir facturas desde la interfaz del sistema, aun cuando la base de datos acepta ambos tipos en el ENUM `tipo_comprobante`.
- **Solución propuesta**: Sobrecargar el método `processSale` en `VentaRepository` o agregar el parámetro `TipoComprobante` en la firma de la función para enviarlo dinámicamente al procedimiento almacenado.

### E. Seguridad de Contraseñas (Hashing)
- **Problema**: Las contraseñas en `sql/03_datos.sql` se almacenan en texto claro o con representaciones simuladas como `hash_001`. El método `login` de `LoginServiceImpl` compara cadenas de texto directas.
- **Solución propuesta**: Integrar una librería ligera de hashing como BCrypt (por ejemplo, `jbcrypt`) en `pom.xml`, y ajustar `LoginServiceImpl` para que haga `BCrypt.checkpw` al validar credenciales en el login.

### F. Gestión de Sesión de Usuario en UI
- **Problema**: Al navegar entre módulos FXML, la aplicación pasa la referencia del usuario autenticado de forma manual en `MainController.loadModule` mediante reflexividad/interfaces:
  `mc.setUsuario(usuario);`
  Esto puede volverse difícil de escalar a medida que se tengan vistas anidadas o diálogos emergentes.
- **Solución propuesta**: Crear una clase Singleton de contexto de sesión (ej. `UserSession.getInstance()`) que almacene el usuario logueado, la tienda activa y sus permisos para que cualquier controlador pueda acceder a ellos sin acoplamiento de constructores o métodos de inicialización.

### G. Encapsulación y Empaquetado de Maven (Fat JAR)
- **Estado**: ✅ **Implementado** (Integrado con `maven-shade-plugin`).
- **Detalle**: Se creó la clase lanzadora [Main.java](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/Main.java) (independiente de la clase Application de JavaFX) y se configuró el plugin de empaquetado `maven-shade-plugin` en `pom.xml`. Esto permite que Maven encapsule toda la aplicación, dependencias (JavaFX, HikariCP, Driver MySQL, ControlsFX) y recursos en un único archivo JAR autónomo y ejecutable mediante `mvn clean package`.

---

## 7. Instrucciones para Continuar el Desarrollo

Si eres una IA que va a implementar los módulos de UI (Fases 5 a 10), sigue este flujo sugerido para cada módulo:
1. **Modelado FXML**: Diseña la pantalla utilizando componentes FXML en la ruta [src/main/resources/opty/view/fxml](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/resources/opty/view/fxml).
2. **Controlador**: Crea la clase controladora bajo el paquete `opty.view`. Asegúrate de implementar `ModuleController` para recibir la inicialización del Stage y el Usuario.
3. **Navegación**: Modifica [MainController.java](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/src/main/java/opty/view/MainController.java) para sustituir el texto de placeholder `"Módulo — Próximamente"` por la carga del FXML correspondiente (similar a como se hace con el Dashboard).
4. **Pruebas**: Inicia la aplicación usando `mvn javafx:run` (asegúrate de crear previamente el archivo [config/config.properties](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/config/config.properties) con las credenciales de tu base de datos local).
