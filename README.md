# Opty — Sistema de Gestión Óptica

Aplicación de escritorio JavaFX + FXML + CSS para la gestión integral de una cadena de ópticas multiclínica. Proyecto académico de Base de Datos I — Ingeniería de Software.

## Modelo de negocio

Una óptica no vende solo productos: vende soluciones visuales. El flujo comienza cuando un paciente llega a cualquiera de las sucursales para una consulta optométrica. El médico registra la atención y, si se requiere, genera una receta con la graduación de cada ojo. A partir de allí se elabora una orden de trabajo que indica al taller si debe armar un lente completo, solo las lunas, solo la montura o realizar una reparación. El taller recibe la orden, la procesa y, cuando está lista, se entrega al paciente.

En paralelo, la óptica gestiona su inventario de insumos (lunas, monturas, accesorios) y productos terminados. Cada compra a proveedores registra la entrada de materiales, y cada venta descuenta automáticamente el stock. Un kardex unificado lleva la trazabilidad de todos los movimientos físicos, mientras que la caja registra los ingresos y egresos de dinero.

Todo está atado a una sucursal y a un usuario responsable, lo que permite tener visibilidad granular de qué ocurre en cada tienda, quién lo hizo y cuándo.

## Arquitectura

4 capas siguiendo SOLID y POO:

```
model → repository → service → view
```

- **model** (15 entidades + 14 enums) — POJOs con validación defensiva en setters.
- **repository** (10 interfaces + 12 impls JDBC) — CrudRepository genérico, llamadas a SPs de BD.
- **service** (14 interfaces + 14 impls) — Lógica de negocio con idempotencia.
- **view** (FXML + Controladores + CSS) — Login, Dashboard con sidebar de navegación.

## Estado del proyecto

| Fase | Descripción | Estado |
|------|-------------|--------|
| 1 | Scaffold Maven, modelos, enums, CSS base, SQL scripts | ✅ |
| 2 | Repositorios JDBC con SPs | ✅ |
| 3 | Servicios con lógica de negocio | ✅ |
| 4 | Login + MainView + Dashboard (FXML+CSS+Controladores) | ✅ |
| 5 | Módulo Pacientes + Consultas (Clínica y Refracción) | ✅ |
| 6 | Módulo Ventas (Multi-producto transaccional en Java) | ✅ |
| 7 | Módulo Compras (Abastecimiento de insumos) | ✅ |
| 8 | Módulo Inventario (Productos, insumos y Kardex de almacén) | ✅ |
| 9 | Módulo Usuarios (Roles: ADMIN, VENDEDOR, MEDICO) | ✅ |
| 10 | Módulo Órdenes de Trabajo (Taller y Lab oftálmico) | ✅ |

## Requisitos

- **Java Development Kit (JDK) 17** (Versión de compilación ajustada en pom.xml)
- **Apache Maven 3.9+**
- **MySQL 8.0+** con la base de datos `optica_db` creada y poblada.
- **Lombok** habilitado en el IDE de desarrollo.

## Preparación previa y Configuración

### 1. Configurar conexión de base de datos
Debes configurar las credenciales de tu base de datos local en el archivo de propiedades. Crea una copia de `config/config.properties.example` y nómbrala `config/config.properties`:
```bash
# Windows
copy config\config.properties.example config\config.properties
# Linux/macOS
cp config/config.properties.example config/config.properties
```
Abre el archivo `config/config.properties` y edita los valores correspondientes a tu servidor MySQL:
```properties
db.host=127.0.0.1
db.port=3306
db.name=optica_db
db.user=tu_usuario
db.password=tu_contrasena
db.useSSL=false
```

### 2. Habilitar Procesamiento de Anotaciones (Lombok)
Al utilizar Lombok para la autogeneración de getters, setters y constructores, debes asegurarte de que tu IDE los procese correctamente para evitar falsos errores de compilación en el código fuente:
*   **En IntelliJ IDEA**:
    1. Ve a `File` -> `Settings` (o `Ctrl + Alt + S`).
    2. Navega a `Build, Execution, Deployment` -> `Compiler` -> `Annotation Processors`.
    3. Marca la casilla **"Enable annotation processing"** y haz clic en Apply/OK.
    4. Asegúrate de tener instalado el plugin oficial de Lombok (incluido por defecto en versiones recientes de IntelliJ).

### 3. Actualizar Procedimientos Almacenados (Evitar colisión de tickets)
Se incluye un actualizador automático para recrear los procedimientos `sp_procesar_venta` y `sp_registrar_compra` en la base de datos MySQL activa con un formato de numeración aleatoria para evitar colisiones del timestamp:
```bash
mvn compile exec:java "-Dexec.mainClass=opty.config.DatabaseUpdater"
```

## Compilar y ejecutar

Puedes compilar e inicializar la aplicación de escritorio JavaFX de la siguiente manera:

```bash
# Limpiar clases anteriores y compilar el proyecto
mvn clean compile

# Iniciar la interfaz gráfica JavaFX
mvn javafx:run
```

### Usuarios y Roles de Prueba para Login
La aplicación cuenta con control de accesos y menús laterales dinámicos según el tipo de empleado:

| Usuario | Contraseña | Rol / Permisos |
|---------|-----------|----------------|
| **admin** | admin123 | **ADMIN** (Acceso a todos los módulos y administración de usuarios). |
| **vendedor1** | vende123 | **VENDEDOR** (Acceso a ventas, compras, pacientes, órdenes e inventario. Bloqueado de usuarios). |
| **optometra1** | opto123 | **MEDICO** (Restringido únicamente a las consultas oftálmicas y órdenes del laboratorio). |

## Stack tecnológico del Proyecto

- **Java 17** & **JavaFX 21**
- **Lombok** (Generación de código estructurado)
- **HikariCP** (Pool de conexiones JDBC de alto rendimiento)
- **MySQL Connector/J 9.0+** (Consultas nativas y control de transacciones atómicas a nivel de servicio)
- **ControlsFX** (Componentes visuales para la UI)
- **Vanilla CSS** (Estilos premium aplicados sobre componentes FXML)

---

## 🎓 Recursos para la Sustentación Académica

Para facilitar la presentación y defensa del proyecto frente a los docentes, se han elaborado los siguientes recursos detallados:

*   **[Guía de Exposición - Script Paso a Paso](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/GUIA_DE_EXPOSICION.md)**: Contiene un guion cronometrado palabra por palabra, con instrucciones exactas de qué hacer en pantalla, qué decir en cada minuto y cómo responder a posibles preguntas difíciles del jurado.
*   **[Guía Técnica de Base de Datos](file:///D:/Tareas_momentaneas/Ciclo%205/Base_datos/proyecto_optica_BD/Opty/README_BD_ENTREGABLE.md)**: Sustento técnico completo sobre la arquitectura de acceso a datos (JDBC + HikariCP), mapeo de procedimientos almacenados, triggers, vistas y el algoritmo de migraciones automáticas.
