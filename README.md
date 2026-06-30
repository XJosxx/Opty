# Opty

**Opty** es un sistema de escritorio diseñado para la gestión integral de una cadena de ópticas con múltiples sucursales. El proyecto nace como trabajo práctico del curso Base de Datos I de la carrera Ingeniería de Software, y busca reflejar cómo un modelo de datos bien estructurado puede sostenerse por sí mismo mediante disparadores, procedimientos almacenados y vistas que trasladan la lógica de negocio directamente a la base de datos.

## Modelo de negocio

Una óptica no vende solo productos: vende soluciones visuales. El flujo comienza cuando un paciente llega a cualquiera de las sucursales para una consulta optométrica. El médico registra la atención y, si se requiere, genera una receta con la graduación de cada ojo. A partir de allí se elabora una orden de trabajo que indica al taller si debe armar un lente completo, solo las lunas, solo la montura o realizar una reparación. El taller recibe la orden, la procesa y, cuando está lista, se entrega al paciente.

En paralelo, la óptica gestiona su inventario de insumos (lunas, monturas, accesorios) y productos terminados. Cada compra a proveedores registra la entrada de materiales, y cada venta descuenta automáticamente el stock. Un kardex unificado lleva la trazabilidad de todos los movimientos físicos, mientras que la caja registra los ingresos y egresos de dinero.

Todo está atado a una sucursal y a un usuario responsable, lo que permite tener visibilidad granular de qué ocurre en cada tienda, quién lo hizo y cuándo.

## Arquitectura del software

El sistema sigue una arquitectura en cuatro capas:

- **model** — Representación de las entidades del negocio como objetos Java simples (POJO). Cada clase refleja una tabla de la base de datos.
- **repository** — Capa de acceso a datos mediante JDBC. Cada repositorio expone operaciones CRUD y consultas específicas, apoyándose en interfaces para mantener el desacoplamiento.
- **service** — Lógica de negocio pura. Los servicios orquestan operaciones que involucran múltiples repositorios, aplican validaciones y garantizan idempotencia en procesos críticos como la emisión de ventas.
- **view** — Interfaz gráfica construida con JavaFX y FXML. Separada en controladores y archivos de descripción de escenas, con estilos CSS que siguen una línea de diseño limpia, moderna y de alto contraste, pensada para entornos clínicos y contables.

## Manual de compilación

### Requisitos

- Java Development Kit 25 o superior
- Apache Maven 3.9 o superior
- MySQL 8.0 o superior (con base de datos `optica_db` creada y los scripts SQL ejecutados)
- Conexión de red al servidor MySQL (por defecto espera una instancia en `localhost:3306`, configurable)

### Preparar la base de datos

Ejecutar los scripts en orden dentro de tu cliente MySQL:

```sql
source 01_tablas.sql
source 02_logica.sql
source 03_datos.sql
```


### Compilar

```bash
mvn clean compile
```

### Ejecutar

```bash
mvn javafx:run
```

También se puede generar un JAR ejecutable:

```bash
mvn package
```

El JAR se encontrará en `target/opty-desktop-1.0.0.jar`.

### Configurar la conexión a la base de datos

La conexión se define en la clase `DatabaseConfig`. Por defecto intenta conectar a `localhost:3306/optica_db`. Si tu servidor es remoto (por ejemplo, Aiven):

```
Host: mysql-xxxx.aivencloud.com
Puerto: 16233
Base: optica_db
Usuario: avnadmin
Contraseña: <tu-contraseña>
SSL: requerido
```

Los valores se pueden cambiar directamente en la clase o cargar desde un archivo de propiedades externo.

### Notas

- Asegúrate de tener el conector MySQL disponible (Maven lo descarga automáticamente).
- La aplicación usa módulos de JavaFX; el plugin `javafx-maven-plugin` se encarga de resolver el módulo-path automáticamente.
- Los scripts SQL se encuentran en la carpeta `sql/` del repositorio.
