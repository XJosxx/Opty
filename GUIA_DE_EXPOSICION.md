# 🎓 Guía de Exposición Simplificada: Sistema Opty (Listo para Leer y Grabar)

Esta guía ha sido redactada de forma directa, conversacional y humanizada para que puedas leerla fluidamente mientras grabas tu pantalla. Va directo al grano sobre cada módulo y destaca las tecnologías clave.

---

## 🎙️ GUION PASO A PASO (Qué decir y qué hacer en pantalla)

### 1. Introducción y Arquitectura (0:00 - 0:45)
*   **Qué mostrar**: El proyecto abierto en el IDE (las carpetas `model`, `repository`, `service` y `view`). Deja la aplicación corriendo en la pantalla de Login.
*   **Qué decir**:
    > "Hola con todos. Hoy les voy a mostrar **Opty**, un sistema de escritorio diseñado para la gestión de ópticas. 
    >
    > El sistema está estructurado bajo una **arquitectura de 4 capas**:
    > *   **Presentación (`view`)**: donde controlamos las pantallas de JavaFX.
    > *   **Negocio (`service`)**: donde procesamos las reglas de la óptica.
    > *   **Persistencia (`repository`)**: donde conectamos a la base de datos usando **JDBC Plano** de forma nativa para máxima velocidad.
    > *   **Dominio (`model`)**: que transporta la información de forma segura entre capas.
    >
    > Para optimizar recursos, implementamos **HikariCP**, un pool de conexiones ultrarrápido que evita crear conexiones repetitivas a la base de datos, logrando un rendimiento óptimo."

---

### 2. Base de Datos Inteligente (0:45 - 1:30)
*   **Qué mostrar**: En tu IDE o gestor SQL (DBeaver/Workbench), ten a la mano las líneas de código de `sp_procesar_venta` o los triggers en el archivo `02_logica.sql`.
*   **Qué decir**:
    > "Una gran ventaja de Opty es que delegamos la lógica crítica al motor de la base de datos.
    >
    > Por ejemplo, usamos:
    > 1. **Procedimientos Almacenados Transaccionales**: como `sp_procesar_venta`. Con esto, el registro de la venta, la baja de stock en almacén y el ingreso de dinero en caja se ejecutan como una sola operación. Si algo falla, la base de datos hace *rollback* automático y nada queda corrupto.
    > 2. **Triggers (Disparadores)**: En la tabla `kardex` tenemos un trigger que calcula y actualiza el stock físico al instante ante cualquier entrada o salida. Otro trigger en las ventas calcula automáticamente el subtotal, el 18% de IGV y actualiza la cabecera."

---

### 3. Migraciones Automáticas (1:30 - 2:00)
*   **Qué mostrar**: El archivo `DatabaseMigrationManager.java` en el IDE.
*   **Qué decir**:
    > "Para controlar las versiones de la base de datos entre programadores, creamos un motor de migraciones propio. Al iniciar la aplicación, lee de forma incremental los archivos SQL pendientes.
    >
    > Además, incluye una estrategia de **Baseline** que protege bases de datos previas de ser sobreescritas, y soporta campos dinámicos en formato **JSON** para almacenar las recetas y mediciones ópticas complejas del Ojo Derecho y Ojo Izquierdo sin saturar la tabla con columnas vacías."

---

### 4. Demostración del Software en Vivo (2:00 - 4:30)
*   **Qué mostrar**: Abre la aplicación y realiza las acciones fluidamente mientras las explicas.

#### Paso A: Ingreso y Dashboard
*   *Acción*: Loguéate con usuario `admin` y contraseña `admin123`. Muestra los gráficos del Dashboard.
*   *Guion*: 
    > "Iniciamos sesión como administrador. El Dashboard carga gráficos interactivos en tiempo real leyendo directamente de vistas de base de datos optimizadas que filtran los resultados automáticamente según la sucursal activa."

#### Paso B: Control de Caja Chica (Módulo Caja Chica)
*   *Acción*: Haz clic en **Control Caja**. Muestra la tabla de movimientos e ingresa una transacción manual (ej: un egreso menor por compra de insumos de limpieza de S/ 15.00).
*   *Guion*: 
    > "Aquí vemos el nuevo módulo de **Caja Chica**. Muestra el consolidado de ingresos, egresos y saldo neto de la tienda. Registremos un egreso manual de 15 soles por compra de útiles. Al guardarlo, se actualiza la tabla de movimientos y el saldo neto de caja al instante."

#### Paso C: Registrar una Venta y Generar OT (Módulo Ventas)
*   *Acción*: Ve a **Ventas**, ingresa el DNI de un cliente, carga sus datos y ve a la pestaña **Lunas / Trabajo Personalizado**. Elige un tipo de trabajo, material, precio y agrégalo. En la parte inferior, ingresa un abono parcial 'A Cuenta' (por ejemplo, deja S/ 50 de abono para una venta de S/ 150) y presiona **Procesar Venta**.
*   *Guion*: 
    > "En la sección de ventas, si seleccionamos un lente oftálmico que requiere montaje y medida, el sistema de forma inteligente sabe que requiere laboratorio y **genera una Orden de Trabajo automática**. Si el cliente abona solo una parte a cuenta, la venta se registra en estado `PAGO_PARCIAL`, cobrando el abono en caja chica y dejando el saldo pendiente."

#### Paso D: Taller y Trazabilidad (Módulo Órdenes)
*   *Acción*: Ve a **Órdenes Trabajo**. Selecciona la orden que acabamos de crear y cámbiala a estado **LISTO** (simulando que el laboratorio terminó de armar los lentes y los envió a la tienda).
*   *Guion*: 
    > "En el módulo de Órdenes de Trabajo vemos los lentes en cola. Los técnicos actualizan el estado físico. Vamos a marcar el trabajo como `LISTO`. El producto ya regresó del laboratorio y está en tienda listo para ser entregado."

#### Paso E: Historial de Ventas, Entrega y Cobro de Saldo
*   *Acción*: Regresa a **Ventas**, ve a la pestaña **Historial de Ventas**. Selecciona el ticket reciente. Verás que el estado de entrega figura como *LISTO*. Presiona el botón verde **Entregar Pedido**. Luego presiona **Cobrar Saldo Restante** para recibir los S/ 100 de deuda.
*   *Guion*: 
    > "Volvemos al historial de ventas. Seleccionamos el ticket del cliente. El sistema nos muestra que su pedido ya está `LISTO` en tienda. Presionamos 'Entregar Pedido' para cambiar la orden a `ENTREGADO`. Acto seguido, el cliente cancela la deuda: presionamos 'Cobrar Saldo', confirmamos, y automáticamente el dinero ingresa a la Caja Chica, cerrando la venta como `PAGADA`."

#### Paso F: Cuentas de Compras a Proveedores (Módulo Compras)
*   *Acción*: Ve a **Compras**, ve a la pestaña **Historial de Compras**. Selecciona una orden de compra pendiente (`POR_PAGAR`), muestra el saldo adeudado y presiona **Pagar Saldo Restante**.
*   *Guion*: 
    > "Para el abastecimiento con proveedores funciona igual. En el historial de compras seleccionamos una factura con deuda, verificamos qué insumos le compramos y presionamos 'Pagar Saldo' para retirar de forma auditada el dinero de la Caja Chica y saldar la deuda."

---

### 5. Conclusión y Cierre (4:30 - 5:00)
*   **Qué mostrar**: Vuelve a la pantalla del Dashboard.
*   **Qué decir**:
    > "Como conclusión, Opty demuestra que delegar la integridad de los datos en MySQL a través de procedimientos y triggers permite tener un sistema Java sumamente ligero, rápido y fácil de mantener, garantizando que los datos financieros e inventarios siempre sean 100% consistentes. Muchas gracias por su atención."

---

## 🙋‍♂️ PREGUNTAS CLAVE DEL JURADO (Acorde a las actualizaciones)

### 1. ¿Cómo manejan el control de deudas con clientes y proveedores en la base de datos?
*   **Respuesta**: 
    > "Lo manejamos a través del estado financiero en la cabecera de la transacción (`PAGADO`, `PAGO_PARCIAL`, `POR_COBRAR`). El flujo real de dinero no altera directamente el total de la venta o compra; en lugar de eso, registramos de forma atómica cada pago parcial como un registro independiente en `movimientos_caja` vinculando el ID de la transacción. El saldo real se calcula dinámicamente restando la sumatoria de cobros/pagos registrados en caja chica al monto total de la orden."

### 2. ¿Qué pasa si el laboratorio termina los lentes pero el cliente no ha pagado el saldo pendiente?
*   **Respuesta**:
    > "El sistema lo tiene previsto. En la pestaña de historial de ventas, el personal de atención al cliente puede verificar el estado físico de la orden (que ya estará como `LISTO`). El botón de 'Entregar Pedido' le permite formalizar la entrega, y el botón de 'Cobrar Saldo Restante' asegura que en ese preciso momento se registre el ingreso del saldo adeudado en caja chica, forzando la consistencia del flujo."

### 3. ¿Cómo determinan si una venta requiere generar una orden de laboratorio (OT) o si es una venta directa?
*   **Respuesta**:
    > "Lo determinamos analizando la categoría de los productos en el carrito. Al procesar la venta, si el sistema detecta algún ítem cuya categoría sea `LENTE` (proveniente del catálogo o configurado de forma personalizada), asume de forma automática que el producto requiere fabricación y montaje médico, y crea el registro de la Orden de Trabajo. Si solo contiene accesorios o monturas solares sin medida, se considera Venta Directa y se marca físicamente como entregada al instante."
