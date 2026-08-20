# Base de datos de Zoi

El modelo vigente contiene únicamente ocho tablas funcionales:

1. `usuario`: identidad, autenticación y rol.
2. `administrador`: extensión uno a uno de un usuario administrador.
3. `veterinario_perfil`: extensión uno a uno de un usuario veterinario.
4. `mascota`: mascotas pertenecientes a un usuario dueño.
5. `conversacion`: relación entre dueño, veterinario y mascota.
6. `mensaje`: mensajes pertenecientes a una conversación y escritos por un usuario.
7. `recordatorio`: agenda de cuidados de una mascota.
8. `calificacion_veterinario`: valoración única de un dueño a un veterinario.

## Aplicación de la migración

1. Detener la aplicación.
2. Crear una copia de seguridad completa de `zoi_java`.
3. Ejecutar `V2_normalizar_base_datos.sql` una sola vez.
4. Iniciar la aplicación. Hibernate está configurado con `ddl-auto=validate`: comprobará el esquema, pero no creará ni alterará tablas silenciosamente.

La migración preserva usuarios y perfiles vigentes, elimina tablas heredadas sin uso y reconstruye las claves foráneas con eliminación en cascada controlada por la base de datos.
