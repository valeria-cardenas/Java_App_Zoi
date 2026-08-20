# Zoi

Aplicación web para gestionar mascotas, recordatorios de cuidado, consultas con veterinarios y administración de usuarios.

## Tecnologías

- Java 21 y Spring Boot 4.
- Spring MVC, Spring Security, Thymeleaf y Spring Data JPA.
- MariaDB 10.6 o superior.
- Maven Wrapper.

## Preparación de la base de datos

1. Crear la base `zoi_java` con codificación `utf8mb4`.
2. En una instalación existente, aplicar una sola vez `database/V2_normalizar_base_datos.sql`.
3. Conservar el respaldo generado antes de cualquier migración.

Hibernate usa `ddl-auto=validate`: comprueba la estructura, pero no crea ni altera tablas automáticamente.

## Configuración

La aplicación acepta estas variables de entorno:

| Variable | Uso | Valor local predeterminado |
|---|---|---|
| `ZOI_DB_URL` | URL JDBC de MariaDB | `jdbc:mariadb://localhost:3306/zoi_java` |
| `ZOI_DB_USER` | Usuario de base de datos | `root` |
| `ZOI_DB_PASSWORD` | Contraseña de base de datos | Vacía |
| `ZOI_ADMIN_PASSWORD` | Contraseña inicial del administrador principal | Sin valor |
| `ZOI_SHOW_SQL` | Mostrar consultas Hibernate | `false` |

`ZOI_ADMIN_PASSWORD` solo se utiliza si `admin@zoi.com` todavía no existe. La aplicación nunca restablece automáticamente una contraseña existente.

Ejemplo en PowerShell para una instalación nueva:

```powershell
$env:ZOI_ADMIN_PASSWORD="CambiaEstaClave123"
```

## Ejecución

```powershell
.\mvnw.cmd spring-boot:run
```

Abrir `http://localhost:8080`.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas utilizan H2 en memoria y no modifican MariaDB. Incluyen validaciones, almacenamiento, autenticación, privacidad de archivos y protección de la contraseña administrativa.

## Organización

- `controller`: rutas HTTP y preparación de vistas.
- `service`: almacenamiento, autenticación y eliminación de cuentas.
- `repository`: acceso a datos mediante JPA.
- `model`: entidades persistentes.
- `resources/templates`: vistas Thymeleaf.
- `resources/static`: estilos, JavaScript e imágenes públicas.
- `database`: migración, respaldo, documentación y diagrama MER.

Los archivos de usuarios se almacenan fuera de `static` y se entregan mediante autorización. Un usuario solo puede consultar archivos públicos de veterinarios o archivos de mascotas y conversaciones en las que participa.
