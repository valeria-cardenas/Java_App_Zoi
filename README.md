# Zoi Java

Aplicacion Spring Boot con Thymeleaf, Spring Security, JPA y MySQL.

## Requisitos

- JDK 17 o superior.
- MySQL en `localhost:3306`.
- Base de datos `zoi_java`.

## Ejecutar

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Abrir `http://localhost:8080`.

## Probar

```powershell
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria y no alteran MySQL.

## Modulos principales

- `model`: entidades de base de datos.
- `repository`: consultas JPA.
- `service`: autenticacion.
- `controller`: rutas y logica web.
- `resources/templates`: vistas Thymeleaf.
- `resources/static`: CSS e imagenes.
- `docs`: evidencias y proceso de calidad.
