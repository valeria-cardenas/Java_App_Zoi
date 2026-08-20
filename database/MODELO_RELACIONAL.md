# Modelo relacional de Zoi

```mermaid
erDiagram
    USUARIO {
        INT id_usuario PK
        VARCHAR nombre
        VARCHAR apellido
        VARCHAR correo UK
        VARCHAR contrasena
        VARCHAR telefono
        DATE fecha_nacimiento
        VARCHAR tipo_perfil
    }
    ADMINISTRADOR {
        INT id_administrador PK
        INT id_usuario FK,UK
        DATETIME fecha_creacion
    }
    VETERINARIO_PERFIL {
        INT id_veterinario_perfil PK
        INT id_usuario FK,UK
        VARCHAR especialidad
        VARCHAR numero_documento
        VARCHAR experiencia
        VARCHAR tarjeta_profesional
        VARCHAR clinica
        VARCHAR localidad
        VARCHAR telefono
        VARCHAR foto_url
        VARCHAR titulo_url
    }
    MASCOTA {
        INT id_mascota PK
        INT id_dueno FK
        VARCHAR nombre
        VARCHAR especie
        VARCHAR tipo_mascota
        VARCHAR raza
        VARCHAR sexo
        DATE fecha_nacimiento
        BOOLEAN vacunas_vigentes
        VARCHAR foto_url
    }
    CONVERSACION {
        INT id_conversacion PK
        INT id_dueno FK
        INT id_veterinario_perfil FK
        INT id_mascota FK
        BOOLEAN pendiente_veterinario
        VARCHAR estado
        VARCHAR prioridad
        DATETIME actualizada_en
    }
    MENSAJE {
        INT id_mensaje PK
        INT id_conversacion FK
        INT id_autor FK
        VARCHAR contenido
        VARCHAR imagen_url
        DATETIME enviado_en
        BOOLEAN leido
    }
    RECORDATORIO {
        INT id_recordatorio PK
        INT id_mascota FK
        VARCHAR tipo
        VARCHAR titulo
        VARCHAR descripcion
        DATETIME fecha_hora
        VARCHAR repeticion
        BOOLEAN completado
    }
    CALIFICACION_VETERINARIO {
        INT id_calificacion PK
        INT id_dueno FK
        INT id_veterinario_perfil FK
        INT estrellas
        VARCHAR comentario
        DATETIME actualizada_en
    }
    USUARIO ||--o| ADMINISTRADOR : "puede tener"
    USUARIO ||--o| VETERINARIO_PERFIL : "puede tener"
    USUARIO ||--o{ MASCOTA : "posee"
    USUARIO ||--o{ CONVERSACION : "inicia como dueño"
    USUARIO ||--o{ MENSAJE : "escribe"
    USUARIO ||--o{ CALIFICACION_VETERINARIO : "publica"
    VETERINARIO_PERFIL ||--o{ CONVERSACION : "atiende"
    VETERINARIO_PERFIL ||--o{ CALIFICACION_VETERINARIO : "recibe"
    MASCOTA ||--o{ CONVERSACION : "es consultada en"
    MASCOTA ||--o{ RECORDATORIO : "tiene"
    CONVERSACION ||--o{ MENSAJE : "contiene"
```

## Reglas principales

- `usuario.correo` es único.
- Un usuario solo puede tener un perfil administrador y un perfil veterinario.
- Una conversación es única para la combinación dueño, veterinario y mascota.
- Un dueño solo puede calificar una vez a cada veterinario.
- Las estrellas deben estar entre 1 y 5.
- Todas las tablas dependientes cuentan con claves foráneas e índices para sus búsquedas frecuentes.
- Las eliminaciones de entidades principales se propagan mediante `ON DELETE CASCADE`, evitando registros huérfanos.
- Hibernate usa `ddl-auto=validate`; el esquema solo cambia mediante migraciones revisadas.
