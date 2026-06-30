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
| 5 | Módulo Pacientes + Consultas | ⏳ |
| 6 | Módulo Ventas | ⏳ |
| 7 | Módulo Compras | ⏳ |
| 8 | Módulo Inventario (productos, insumos, kardex) | ⏳ |
| 9 | Módulo Usuarios (solo ADMIN) | ⏳ |
| 10 | Módulo Órdenes de Trabajo + alertas vencidas | ⏳ |

## Requisitos

- Java Development Kit 25
- Apache Maven 3.9+
- MySQL 8.0+ con base `optica_db` poblada
- Conexión de red al servidor MySQL

## Configurar conexión

La base de datos ya está en un servidor compartido. Solo necesitas crear `config/config.properties` a partir de la plantilla:

```bash
cp config/config.properties.example config/config.properties
# Windows: copiar config\config.properties.example config\config.properties
```

### Usuarios de prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMIN |
| vendedor1 | vende123 | VENDEDOR |
| optometra1 | opto123 | OPTOMETRA |

## Compilar y ejecutar

```bash
mvn clean compile
mvn javafx:run
```

## Stack tecnológico

- Java 25, JavaFX 23, Maven 3.9.11
- mysql-connector-j 9.3 (JDBC plano, sin Spring/JPA/Hibernate)
- ControlsFX 11.2.1
- MySQL 8.0+ (local o Aiven)
