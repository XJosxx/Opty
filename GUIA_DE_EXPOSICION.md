# 🎓 Guía Completa de Exposición y Sustentación: Sistema Opty

Esta guía contiene la estructura cronometrada, el **guion palabra por palabra** y el banco de respuestas clave para defender con éxito el proyecto de **Bases de Datos** ante el docente o jurado.

---

## ⏱️ Cronograma de la Exposición (Máx. 5 minutos)

| Tiempo | Sección | Foco Principal | Acción en Pantalla |
| :--- | :--- | :--- | :--- |
| **0:00 - 0:45** *(45s)* | **1. Introducción y Arquitectura** | Estructura del proyecto, HikariCP y JDBC | Pantalla de Login / Estructura del proyecto en el IDE. |
| **0:45 - 2:00** *(1m 15s)* | **2. Lógica Programable en BD** | Procedimientos, Triggers, Vistas | Mostrar código SQL en DBeaver/Workbench o clases DAO en el IDE. |
| **2:00 - 2:30** *(30s)* | **3. Motor de Migraciones** | Versionamiento y control con `schema_version` | Mostrar logs de consola o la tabla `schema_version`. |
| **2:30 - 4:30** *(2m)* | **4. Demostración en Vivo** | Flujo completo e interacción con la BD | Ejecutar aplicación: Login ➔ Dashboard ➔ Consulta ➔ Venta/Caja. |
| **4:30 - 5:00** *(30s)* | **5. Conclusión y Cierre** | Valor de delegar la lógica al motor de BD | Dashboard general o diapositiva de conclusiones. |

---

## 🎙️ Guion Palabra por Palabra (Qué decir y qué hacer)

### 1. Introducción y Arquitectura (0:00 - 0:45)

*   **Acción en Pantalla**: Muestra el proyecto abierto en el IDE (IntelliJ o VS Code) enfocando la estructura de carpetas `model`, `repository`, `service` y `view`. Deja visible la pantalla de Login del programa ejecutándose.
*   **Guion (Qué decir)**:
    > "Buenas tardes, estimado profesor y miembros del jurado. Hoy les presentamos **Opty**, un sistema de escritorio empresarial diseñado específicamente para la gestión de cadenas de ópticas multiclínica. El flujo de negocio en una óptica no es una simple venta de productos; involucra una consulta médica visual, la emisión de una receta oftálmica, la generación de una orden de taller en laboratorio y, finalmente, el cobro y control de inventario.
    >
    > Para soportar este flujo de forma robusta, hemos implementado una **Arquitectura en 4 Capas** bien desacoplada: Presentación (en JavaFX con estilos CSS personalizados), Negocio (en servicios Java), Datos (repositorios con interfaces DAO) y el motor de Base de Datos **MySQL 8.0**. 
    > Cabe destacar que para el acceso a datos utilizamos **JDBC Plano** en lugar de ORMs pesados, logrando consultas sumamente veloces. Además, implementamos el pool de conexiones **HikariCP**, el cual nos permite reutilizar hilos de conexión de manera eficiente, evitando el consumo innecesario de recursos al abrir y cerrar conexiones en cada interacción de la interfaz."

---

### 2. Uso y Lógica de la Base de Datos (0:45 - 2:00)

*   **Acción en Pantalla**: Abre en el IDE o en el gestor SQL la definición de procedimientos almacenados (como `sp_procesar_venta`) y de los triggers de stock en el Kardex.
*   **Guion (Qué decir)**:
    > "Uno de los pilares del diseño de Opty es que delegamos la consistencia crítica de la información y la lógica de negocio compleja al motor de la base de datos, en lugar de sobrecargar la aplicación Java.
    >
    > Para ello implementamos:
    > 1. **Procedimientos Almacenados Transaccionales**: Como `sp_procesar_venta` y `sp_registrar_compra`. Al procesar una venta, este procedimiento encapsula en una sola **transacción atómica** el registro de la cabecera, la inserción de detalles, el movimiento en el Kardex y el ingreso a Caja. Si ocurre algún fallo o no hay stock, MySQL realiza un *rollback* automático, impidiendo que la base de datos quede en un estado corrupto.
    > 2. **Triggers (Disparadores Automáticos)**: En la tabla `kardex` implementamos `trg_kardex_actualizar_stock`. Cada vez que se registra un movimiento físico de entrada, salida o ajuste, este trigger actualiza automáticamente la columna `stock_actual` en la tabla de insumos o productos. Adicionalmente, tenemos triggers como `trg_ventas_detalle_total_cabecera` que calculan de forma automática los subtotales, el 18% de IGV (apoyándose en la función SQL `fn_calcular_igv`) y actualizan la cabecera de la venta.
    > 3. **Vistas Optimistas**: El Dashboard consume vistas como `v_resumen_ventas_tienda` y `v_ordenes_pendientes`. Java no realiza agrupaciones ni cálculos matemáticos en memoria; simplemente hace un `SELECT *` de la vista, permitiendo que la base de datos resuelva los joins complejos de forma óptima."

---

### 3. Flujo y Algoritmo de Migraciones Automáticas (2:00 - 2:30)

*   **Acción en Pantalla**: Muestra el archivo `DatabaseMigrationManager.java` en el IDE y, opcionalmente, haz un `SELECT * FROM schema_version;` en la base de datos para mostrar la tabla de control.
*   **Guion (Qué decir)**:
    > "Para resolver el versionamiento de la base de datos, diseñamos un motor de migraciones nativo inspirado en herramientas como *Flyway*. La clase `DatabaseMigrationManager` se ejecuta al iniciar la aplicación.
    >
    > Si el sistema detecta que la base de datos es nueva, crea la tabla de control `schema_version`. Si ya existían tablas previamente creadas por el usuario, aplica una estrategia de **Baseline**, registrando las versiones previas (V1, V2 y V3) como ya ejecutadas para no sobreescribir datos existentes.
    > Luego, lee y ejecuta incrementalmente los archivos SQL pendientes. En la **Versión 4** cargamos datos históricos adicionales y recetas clínicas estructuradas en formato **JSON** para las graduaciones oculares. Y en la **Versión 5** ejecutamos comandos DDL dinámicos (`ALTER TABLE`) para agregar la columna `email` a la tabla `pacientes` sin alterar la información preexistente. Esto garantiza que cualquier programador del equipo tenga exactamente el mismo esquema de base de datos con solo iniciar el programa."

---

### 4. Demostración Práctica en Vivo (2:30 - 4:30)

*   **Acción en Pantalla**: Abre el programa ejecutándose. Realiza las siguientes acciones fluidamente mientras hablas:

#### Paso A: Autenticación (Login)
*   *Acción*: Ingresa usuario `admin` y contraseña `admin123`. Presiona Enter.
*   *Guion*: *"Iniciamos sesión como administrador. El sistema valida las credenciales y el rol asignado en la tabla `usuarios` para cargar dinámicamente los módulos permitidos."*

#### Paso B: Visualización del Dashboard
*   *Acción*: Muestra los gráficos de ventas y el leaderboard de vendedores del Dashboard.
*   *Guion*: *"Al ingresar, el Dashboard carga métricas clave al instante. Los gráficos que ven aquí leen directamente de la vista `v_resumen_ventas_tienda`, la cual consolida miles de registros de forma inmediata."*

#### Paso C: Gestión de Pacientes (Módulo Pacientes)
*   *Acción*: Ve a **Pacientes**, selecciona un paciente, haz clic en **Editar Paciente** para corregir sus datos (ej: cambiar teléfono o fecha de nacimiento) en el formulario de la derecha y guárdalo. A continuación, inicia una **Nueva Consulta**. Rellena los datos de refracción (Ojo Derecho e Izquierdo), agrega el motivo de consulta y guárdala.
*   *Guion*: *"En este módulo, el personal registra a los pacientes y sus evaluaciones visuales. Implementamos la edición de pacientes en el mismo panel de forma fluida. Al guardar una consulta, el sistema invoca al procedimiento `sp_registrar_paciente_y_consulta` que guarda la información clínica y genera la boleta de servicio en un solo paso. Adicionalmente, las graduaciones se analizan de manera robusta desde un formato flexible JSON."*

#### Paso D: Venta de Trabajo Personalizado (Módulo Ventas)
*   *Acción*: Ve a **Ventas**, selecciona el cliente, entra a la pestaña **Lunas / Trabajo Personalizado**. Elige el Tipo de Trabajo (ej: `SOLO_LUNAS`), Material (ej: `Resina Blue Defense`), Lado (ej: `Luna Ojo Derecho (OD)`), y pon un precio. Presiona **Agregar Personalizado**. En el formulario de abajo, pon un pago "A Cuenta" (abono parcial) menor al total de la venta, y presiona **Procesar Venta**.
*   *Guion*: *"El sistema es sumamente flexible. Si un cliente viene solo por una luna o requiere un trabajo de laboratorio formulado, la pestaña personalizada permite configurarlo al instante sin necesidad de registrar previamente cada combinación en el catálogo. Al procesar la venta, si el cliente abona un pago parcial 'A cuenta', el sistema calcula el saldo, registra el abono real en Caja y marca el estado financiero como PAGO_PARCIAL."*

#### Paso E: Trazabilidad en Taller y Cobro de Saldo (Módulo Órdenes)
*   *Acción*: Navega al módulo **Órdenes de Trabajo** y selecciona la orden generada. Haz clic para avanzar el estado físico a **ENTREGADO**. El sistema detectará que hay un saldo pendiente de cobro y te preguntará si deseas cobrarlo en ese momento. Acepta el diálogo de cobro.
*   *Guion*: *"Como la venta involucraba un trabajo formulado, la base de datos generó automáticamente una Orden de Trabajo en estado PENDIENTE. Al avanzar el estado físico a ENTREGADO en el taller, el sistema detecta de manera inteligente que hay una deuda pendiente en la venta asociada, solicita el cobro en una ventana emergente, registra el ingreso restante en movimientos de caja, y actualiza el estado financiero a PAGADO automáticamente."*

#### Paso F: Inventario y Ajustes Físicos de Stock
*   *Acción*: Ve a **Inventario**, selecciona un producto o insumo y haz clic en **Ajustar Stock**. Ingresa el nuevo stock actual y un motivo (ej: 'Merma por rotura').
*   *Guion*: *"En el módulo de Inventario y Kardex, el administrador puede auditar el stock físico. En lugar de hacer una actualización directa que destruya la auditoría, implementamos una inserción de tipo AJUSTE en el Kardex. De esta forma, el trigger de la base de datos recalcula el stock actual, mantiene el registro histórico con el motivo justificado y nos ofrece una trazabilidad completa."*

#### Paso G: Gestión de Proveedores (Acceso Restringido)
*   *Acción*: Ve al botón **Proveedores** en el menú lateral (que solo es visible para el administrador) y registra un nuevo proveedor.
*   *Guion*: *"Finalmente, el administrador tiene acceso exclusivo al registro de Proveedores comerciales directamente desde el menú lateral para gestionar la cadena de suministros y abastecer las compras de insumos."*

---

### 5. Cierre y Conclusiones (4:30 - 5:00)

*   **Acción en Pantalla**: Regresa al Dashboard principal de la aplicación.
*   **Guion (Qué decir)**:
    > "Para concluir, el proyecto **Opty** demuestra la enorme ventaja de construir aplicaciones empresariales centrando la lógica de integridad en el motor relacional. Al delegar validaciones de stock, impuestos, auditorías y transacciones a procedimientos almacenados y triggers en MySQL, la aplicación Java se vuelve ligera, escalable y sumamente rápida. 
    >
    > Los datos están protegidos a nivel del servidor de base de datos, lo que garantiza que si mañana decidimos crear una aplicación web o móvil sobre este mismo servidor MySQL, las reglas del negocio no se romperán y la información seguirá siendo 100% consistente. Muchas gracias por su atención, quedamos atentos a sus preguntas."

---

## 🙋‍♂️ Balotas de Preguntas Frecuentes del Jurado (FAQ)

Prepárate para defender técnicamente el proyecto con estas respuestas precisas:

### 1. ¿Por qué utilizaron JDBC Plano y HikariCP en lugar de un ORM moderno como Hibernate o Spring Data JPA?
*   **Respuesta**: 
    > "Decidimos utilizar JDBC Plano y HikariCP por tres razones principales: rendimiento, control absoluto del SQL y peso de la aplicación. 
    > Los ORMs como Hibernate introducen un overhead significativo debido al mapeo objeto-relacional reflexivo y la autogeneración de queries que a veces no son óptimas. Al usar JDBC Puro, controlamos de forma exacta cada consulta que viaja por la red y podemos invocar procedimientos almacenados de MySQL de manera nativa sin configuraciones complejas. 
    > Además, para optimizar el rendimiento, integramos **HikariCP**, que es el pool de conexiones más rápido y ligero del ecosistema Java, asegurando que las conexiones a la base de datos se reutilicen continuamente bajo un patrón Singleton."

### 2. ¿Qué ventajas y desventajas tiene colocar la lógica de negocio en la Base de Datos (Stored Procedures y Triggers) frente a ponerla en el código Java?
*   **Respuesta**:
    *   **Ventajas**:
        *   *Centralización de reglas*: Las reglas de negocio (como el cálculo del IGV o el descuento de stock) residen en el servidor. Si en el futuro creamos un sistema web o una App móvil que se conecte a la misma base de datos, no tendremos que volver a programar la lógica en Java, Node o Swift. Los datos se mantendrán consistentes sin importar la plataforma de origen.
        *   *Eficiencia de red (Performance)*: Una transacción como una venta involucra múltiples consultas (SELECT, INSERT en cabecera, INSERT en detalle, UPDATE de stock, INSERT en caja). Si lo hacemos en Java, son múltiples viajes de ida y vuelta (round-trips) por la red. Al usar un Stored Procedure, viaja un solo comando a la base de datos y todo se procesa localmente en el servidor a velocidad de microsegundos.
    *   **Desventajas**:
        *   *Escalabilidad horizontal*: Es más difícil escalar horizontalmente un servidor de base de datos que servidores de aplicación. Si el sistema tuviera millones de usuarios concurrentes, la CPU de la base de datos podría saturarse con tantos triggers y procedimientos. Sin embargo, para el alcance de este negocio (ópticas locales), la velocidad de procesamiento y la consistencia de datos compensan con creces esta limitación.

### 3. ¿Cómo maneja el sistema la concurrencia al vender un producto con stock limitado? ¿Qué pasa si dos usuarios intentan vender el mismo producto a la vez?
*   **Respuesta**:
    > "La concurrencia y las condiciones de carrera (*race conditions*) se controlan directamente en la capa de la base de datos gracias al uso de transacciones con el nivel de aislamiento por defecto del motor InnoDB (que es **REPEATABLE READ**).
    > Al ejecutarse el procedimiento `sp_procesar_venta`, el motor MySQL adquiere bloqueos implícitos sobre las filas de los productos que se están vendiendo durante la fase de validación y descuento de stock. Si dos vendedores intentan confirmar la venta del último producto en stock simultáneamente, el primer proceso en llegar bloqueará la fila del stock. El segundo proceso esperará y, al liberarse el bloqueo, leerá que el stock ahora es 0, lanzando una excepción controlada de falta de stock y abortando la transacción mediante un *rollback* automático sin alterar los datos."

### 4. ¿Por qué almacena la graduación de los ojos del paciente en un formato JSON en MySQL en lugar de crear columnas tradicionales para esfera, cilindro, eje, etc.?
*   **Respuesta**:
    > "Utilizamos el tipo de datos **JSON** nativo de MySQL 8.0 en la tabla `historial_clinico` por razones de flexibilidad y evolución del esquema de base de datos.
    > Las prescripciones oftálmicas pueden variar significativamente según el tipo de lente (lentes de contacto, lentes progresivos, bifocales o tratamientos especiales como filtros Blue). Si creáramos columnas físicas para cada campo posible, la tabla tendría decenas de columnas vacías (nulos) y tendríamos que alterar la base de datos cada vez que los médicos requieran un nuevo parámetro de refracción. 
    > Al almacenar la graduación del Ojo Derecho y Ojo Izquierdo en formato JSON, ganamos la flexibilidad de un modelo semiestructurado (NoSQL) dentro de un motor relacional sólido, permitiendo que la aplicación Java parsee los datos fácilmente y conservemos la compatibilidad hacia adelante."

### 5. ¿Qué es la estrategia de Baseline que mencionaron en el motor de migraciones y por qué es necesaria?
*   **Respuesta**:
    > "La estrategia de Baseline es fundamental para evitar la corrupción de datos al implementar sistemas de control de versiones de bases de datos. 
    > Si instalamos el software en una sucursal que ya tiene una base de datos en producción con información de clientes y ventas desde hace meses (base legada), no podemos simplemente ejecutar los scripts de creación de tablas desde cero (V1, V2, V3) porque borraríamos la información existente o lanzaría un error de tablas ya creadas. 
    > El Baseline detecta si la tabla principal `pacientes` ya existe en el servidor. Si es así, marca las versiones iniciales (esquema, lógica y semillas básicas) como 'aplicadas históricamente' en la tabla `schema_version` y solo ejecuta a partir de ese momento las migraciones nuevas o modificaciones del esquema (como agregar la columna `email` en la V5). Esto asegura que el sistema evolucione sin interrumpir el negocio."

### 6. ¿Por qué el ajuste de stock manual en Inventario se realiza mediante inserciones de tipo AJUSTE en el Kardex en lugar de actualizar directamente el stock del producto?
*   **Respuesta**:
    > "En la contabilidad e ingeniería de software de almacenes, actualizar directamente el stock de un producto sin dejar una justificación física o rastro es una mala práctica de auditoría. 
    > Al insertar un registro de tipo **`AJUSTE`** en la tabla `kardex`, logramos documentar el motivo físico exacto (ej. 'Pérdida por rotura' o 'Corrección de inventario anual'), quién lo hizo (asociando el `usuario_id`), en qué tienda y cuándo. 
    > Posteriormente, el trigger `trg_kardex_actualizar_stock` intercepta la inserción del ajuste, calcula la diferencia (+ o -) y actualiza el stock final de forma atómica en la tabla de productos/insumos, manteniendo la base de datos auditada e histórica en todo momento."

### 7. ¿Cómo manejan la consistencia relacional y el control de stock de las lunas personalizadas/formuladas que no pertenecen al catálogo estático?
*   **Respuesta**:
    > "Para evitar violar las restricciones de llave foránea (`FOREIGN KEY`) de la tabla `ventas_detalle` y respetar el control de stock, cuando el usuario agrega un trabajo personalizado, el sistema busca en la base de datos si ya existe un producto virtual con la nomenclatura y especificaciones de esa luna. 
    > Si no existe, lo registra de forma transparente en la tabla `productos` bajo la categoría `LENTE` con un stock inicial virtual alto (`9999`) y activo. De esta manera, el detalle de la venta se vincula a un ID de producto válido y los triggers operan con normalidad sin requerir que el catálogo tenga pre-cargadas combinaciones infinitas de materiales, tratamientos y ojos."

### 8. ¿Cómo manejan a nivel de caja y base de datos los abonos parciales (ventas a cuenta)?
*   **Respuesta**:
    > "Cuando un cliente deja un monto 'A cuenta', el sistema actualiza la cabecera con el estado financiero `PAGO_PARCIAL` o `POR_COBRAR`. 
    > En la tabla `movimientos_caja`, registramos de forma exacta únicamente el dinero ingresado físicamente en ese instante. 
    > Cuando el cliente retira su producto en el taller (módulo de Órdenes), el sistema detecta de forma automática la diferencia entre el total y lo pagado anteriormente, solicita el cobro del saldo pendiente en una ventana de confirmación, inserta la segunda transacción de ingreso en `movimientos_caja` y actualiza la venta a `PAGADO` en una transacción controlada por Java y base de datos."
