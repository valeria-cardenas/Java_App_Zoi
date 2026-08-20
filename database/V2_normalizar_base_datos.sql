-- Zoi - migración de la estructura heredada a un modelo relacional limpio.
-- Requiere una copia de seguridad previa. Compatible con MariaDB 10.4.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Tablas heredadas que no tienen entidad, repositorio ni uso en la aplicación actual.
DROP TABLE IF EXISTS caso;
DROP TABLE IF EXISTS enfermedad;
DROP TABLE IF EXISTS enfermedad_mascota;
DROP TABLE IF EXISTS historia_clinica;
DROP TABLE IF EXISTS notificaciones_dueno;
DROP TABLE IF EXISTS notificaciones_veterinario;
DROP TABLE IF EXISTS raza;
DROP TABLE IF EXISTS tipo_mascota;
DROP TABLE IF EXISTS tipo_sangre;
DROP TABLE IF EXISTS vacuna;
DROP TABLE IF EXISTS vacunas_mascota;
DROP TABLE IF EXISTS veterinarios_favoritos;
DROP TABLE IF EXISTS veterinario;

-- Las claves heredadas deben retirarse antes de renombrar la clave primaria.
ALTER TABLE administrador DROP FOREIGN KEY FK1qhrpnsf67wotd52ge3ncpqum;
ALTER TABLE calificacion_veterinario
    DROP FOREIGN KEY FKaukhhp90anscaobhjfax76g74,
    DROP FOREIGN KEY FKckbuuelwfoqanq2x9y9hpi55w;
ALTER TABLE conversacion
    DROP FOREIGN KEY FK4gyyfqbhxxp4nwef1ceefb2j0,
    DROP FOREIGN KEY FK7p6oitg0b088odd2lndatk3qr,
    DROP FOREIGN KEY FKkob4p9mvpv5vnm1rajweyg51e;
ALTER TABLE dueno DROP FOREIGN KEY dueno_ibfk_1;
ALTER TABLE mascota
    DROP FOREIGN KEY mascota_ibfk_1,
    DROP INDEX id_dueno,
    ADD INDEX ix_mascota_dueno (id_dueno);
ALTER TABLE mensaje
    DROP FOREIGN KEY FKgdkmmb0imo98aihe9e0t9tjxu,
    DROP FOREIGN KEY FKo1cs27c1200n35ka5ay481off;
ALTER TABLE veterinario_perfil DROP FOREIGN KEY FKayubt4dnnitfgem35v1ei7sm7;

-- La tabla Dueno almacenaba realmente todos los tipos de usuario.
RENAME TABLE dueno TO usuario;
ALTER TABLE usuario
    CHANGE COLUMN id_dueno id_usuario INT NOT NULL AUTO_INCREMENT,
    DROP COLUMN id_administrador,
    MODIFY nombre VARCHAR(80) NOT NULL,
    MODIFY apellido VARCHAR(80) NOT NULL,
    MODIFY correo VARCHAR(160) NOT NULL,
    MODIFY contrasena VARCHAR(255) NOT NULL,
    MODIFY tipo_perfil VARCHAR(20) NOT NULL;

-- El perfil profesional pertenece a un usuario, no a un "dueño".
ALTER TABLE veterinario_perfil
    CHANGE COLUMN id_dueno id_usuario INT NOT NULL;

-- Columnas heredadas y duplicadas que ya no corresponden al modelo actual.
ALTER TABLE administrador
    DROP COLUMN nombre,
    DROP COLUMN apellido,
    DROP COLUMN contrasena;

ALTER TABLE mascota
    DROP COLUMN foto,
    MODIFY id_dueno INT NOT NULL;

-- Se reconstruyen índices y restricciones con nombres comprensibles.
ALTER TABLE usuario
    ADD CONSTRAINT uq_usuario_correo UNIQUE (correo),
    ADD CONSTRAINT ck_usuario_tipo_perfil CHECK (tipo_perfil IN ('DUENO','VETERINARIO','ADMIN'));

ALTER TABLE administrador
    ADD CONSTRAINT fk_administrador_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE;

ALTER TABLE veterinario_perfil
    ADD CONSTRAINT fk_veterinario_perfil_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE;

ALTER TABLE mascota
    ADD CONSTRAINT fk_mascota_dueno FOREIGN KEY (id_dueno)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE;

ALTER TABLE conversacion
    ADD CONSTRAINT fk_conversacion_dueno FOREIGN KEY (id_dueno)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    ADD CONSTRAINT fk_conversacion_mascota FOREIGN KEY (id_mascota)
        REFERENCES mascota(id_mascota) ON DELETE CASCADE,
    ADD CONSTRAINT fk_conversacion_veterinario FOREIGN KEY (id_veterinario_perfil)
        REFERENCES veterinario_perfil(id_veterinario_perfil) ON DELETE CASCADE,
    ADD CONSTRAINT ck_conversacion_estado CHECK (estado IN ('PENDIENTE','RESPONDIDA','CERRADA')),
    ADD CONSTRAINT ck_conversacion_prioridad CHECK (prioridad IN ('NORMAL','IMPORTANTE','URGENTE'));

ALTER TABLE mensaje
    ADD CONSTRAINT fk_mensaje_conversacion FOREIGN KEY (id_conversacion)
        REFERENCES conversacion(id_conversacion) ON DELETE CASCADE,
    ADD CONSTRAINT fk_mensaje_autor FOREIGN KEY (id_autor)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE;

ALTER TABLE recordatorio
    DROP FOREIGN KEY FKlnly5tr71wjmuk8gomscv6sfn,
    ADD CONSTRAINT fk_recordatorio_mascota FOREIGN KEY (id_mascota)
        REFERENCES mascota(id_mascota) ON DELETE CASCADE;

ALTER TABLE calificacion_veterinario
    ADD CONSTRAINT fk_calificacion_dueno FOREIGN KEY (id_dueno)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    ADD CONSTRAINT fk_calificacion_veterinario FOREIGN KEY (id_veterinario_perfil)
        REFERENCES veterinario_perfil(id_veterinario_perfil) ON DELETE CASCADE,
    ADD CONSTRAINT ck_calificacion_estrellas CHECK (estrellas BETWEEN 1 AND 5);

-- Índices para las búsquedas más frecuentes de la aplicación.
CREATE INDEX ix_conversacion_veterinario_pendiente
    ON conversacion(id_veterinario_perfil, pendiente_veterinario, actualizada_en);
CREATE INDEX ix_conversacion_dueno_actualizada
    ON conversacion(id_dueno, actualizada_en);
CREATE INDEX ix_mensaje_conversacion_fecha
    ON mensaje(id_conversacion, enviado_en);
CREATE INDEX ix_recordatorio_mascota_estado_fecha
    ON recordatorio(id_mascota, completado, fecha_hora);
CREATE INDEX ix_calificacion_veterinario_fecha
    ON calificacion_veterinario(id_veterinario_perfil, actualizada_en);

SET FOREIGN_KEY_CHECKS = 1;
