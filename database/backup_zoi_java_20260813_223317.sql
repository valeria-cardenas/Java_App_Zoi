-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: zoi_java
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `administrador` (
  `id_administrador` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `apellido` varchar(100) DEFAULT NULL,
  `contrasena` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  PRIMARY KEY (`id_administrador`),
  UNIQUE KEY `UKferp3xx2iyuy3qltd4ey5pf7l` (`id_usuario`),
  CONSTRAINT `FK1qhrpnsf67wotd52ge3ncpqum` FOREIGN KEY (`id_usuario`) REFERENCES `dueno` (`id_dueno`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES (1,NULL,NULL,NULL,'2026-08-10 16:14:35.000000',1);
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calificacion_veterinario`
--

DROP TABLE IF EXISTS `calificacion_veterinario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calificacion_veterinario` (
  `id_calificacion` int(11) NOT NULL AUTO_INCREMENT,
  `actualizada_en` datetime(6) NOT NULL,
  `comentario` varchar(800) NOT NULL,
  `estrellas` int(11) NOT NULL,
  `id_dueno` int(11) NOT NULL,
  `id_veterinario_perfil` int(11) NOT NULL,
  PRIMARY KEY (`id_calificacion`),
  UNIQUE KEY `UKdagos9ji2p5bivmuj20xqht2v` (`id_dueno`,`id_veterinario_perfil`),
  KEY `FKckbuuelwfoqanq2x9y9hpi55w` (`id_veterinario_perfil`),
  CONSTRAINT `FKaukhhp90anscaobhjfax76g74` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`),
  CONSTRAINT `FKckbuuelwfoqanq2x9y9hpi55w` FOREIGN KEY (`id_veterinario_perfil`) REFERENCES `veterinario_perfil` (`id_veterinario_perfil`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calificacion_veterinario`
--

LOCK TABLES `calificacion_veterinario` WRITE;
/*!40000 ALTER TABLE `calificacion_veterinario` DISABLE KEYS */;
/*!40000 ALTER TABLE `calificacion_veterinario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `caso`
--

DROP TABLE IF EXISTS `caso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caso` (
  `id_caso` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_historia_clinica` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_caso`),
  KEY `id_historia_clinica` (`id_historia_clinica`),
  CONSTRAINT `caso_ibfk_1` FOREIGN KEY (`id_historia_clinica`) REFERENCES `historia_clinica` (`id_historia_clinica`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caso`
--

LOCK TABLES `caso` WRITE;
/*!40000 ALTER TABLE `caso` DISABLE KEYS */;
/*!40000 ALTER TABLE `caso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversacion`
--

DROP TABLE IF EXISTS `conversacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversacion` (
  `id_conversacion` int(11) NOT NULL AUTO_INCREMENT,
  `actualizada_en` datetime(6) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `pendiente_veterinario` bit(1) NOT NULL,
  `prioridad` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `id_dueno` int(11) NOT NULL,
  `id_mascota` int(11) NOT NULL,
  `id_veterinario_perfil` int(11) NOT NULL,
  PRIMARY KEY (`id_conversacion`),
  UNIQUE KEY `UKgr6jn1kuiw2vviu3fp2l3baie` (`id_dueno`,`id_veterinario_perfil`,`id_mascota`),
  KEY `FK7p6oitg0b088odd2lndatk3qr` (`id_mascota`),
  KEY `FKkob4p9mvpv5vnm1rajweyg51e` (`id_veterinario_perfil`),
  CONSTRAINT `FK4gyyfqbhxxp4nwef1ceefb2j0` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`),
  CONSTRAINT `FK7p6oitg0b088odd2lndatk3qr` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`),
  CONSTRAINT `FKkob4p9mvpv5vnm1rajweyg51e` FOREIGN KEY (`id_veterinario_perfil`) REFERENCES `veterinario_perfil` (`id_veterinario_perfil`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversacion`
--

LOCK TABLES `conversacion` WRITE;
/*!40000 ALTER TABLE `conversacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dueno`
--

DROP TABLE IF EXISTS `dueno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dueno` (
  `id_dueno` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `correo` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `id_administrador` int(11) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `tipo_perfil` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_dueno`),
  KEY `id_administrador` (`id_administrador`),
  CONSTRAINT `dueno_ibfk_1` FOREIGN KEY (`id_administrador`) REFERENCES `administrador` (`id_administrador`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dueno`
--

LOCK TABLES `dueno` WRITE;
/*!40000 ALTER TABLE `dueno` DISABLE KEYS */;
INSERT INTO `dueno` VALUES (1,'Administrador','Zoi','admin@zoi.com',NULL,NULL,NULL,'$2a$10$aKB/Ze00pzoEhCbhpGywSuR7Ulrn60dKr7m8g7sL/etq2GmEploqe','ADMIN'),(3,'Martin','Romero','martin.romero13@gmail.com','3052441571',NULL,NULL,'$2a$10$ami6.aQkLru2Aoeop3JU.uzFcqrFwck4o23wpUM7GrHEwPGo6W4By','VETERINARIO'),(4,'Kevin','Romero','kevin.romero@gmail.com','3214567474',NULL,NULL,'$2a$10$hh/yFKFcm1TDAADE16hmX.hojVVdZaY1sKiXvRi9DB1JVhkH8vQpO','VETERINARIO');
/*!40000 ALTER TABLE `dueno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enfermedad`
--

DROP TABLE IF EXISTS `enfermedad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enfermedad` (
  `id_enfermedad` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_enfermedad_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_enfermedad`),
  KEY `id_enfermedad_mascota` (`id_enfermedad_mascota`),
  CONSTRAINT `enfermedad_ibfk_1` FOREIGN KEY (`id_enfermedad_mascota`) REFERENCES `enfermedad_mascota` (`id_enfermedad_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enfermedad`
--

LOCK TABLES `enfermedad` WRITE;
/*!40000 ALTER TABLE `enfermedad` DISABLE KEYS */;
/*!40000 ALTER TABLE `enfermedad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enfermedad_mascota`
--

DROP TABLE IF EXISTS `enfermedad_mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enfermedad_mascota` (
  `id_enfermedad_mascota` int(11) NOT NULL AUTO_INCREMENT,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_final` date DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_enfermedad_mascota`),
  KEY `id_mascota` (`id_mascota`),
  CONSTRAINT `enfermedad_mascota_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enfermedad_mascota`
--

LOCK TABLES `enfermedad_mascota` WRITE;
/*!40000 ALTER TABLE `enfermedad_mascota` DISABLE KEYS */;
/*!40000 ALTER TABLE `enfermedad_mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historia_clinica`
--

DROP TABLE IF EXISTS `historia_clinica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historia_clinica` (
  `id_historia_clinica` int(11) NOT NULL AUTO_INCREMENT,
  `antecedentes` varchar(255) DEFAULT NULL,
  `diagnostico` varchar(255) DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  `id_veterinario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_historia_clinica`),
  KEY `id_mascota` (`id_mascota`),
  KEY `id_veterinario` (`id_veterinario`),
  CONSTRAINT `historia_clinica_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`),
  CONSTRAINT `historia_clinica_ibfk_2` FOREIGN KEY (`id_veterinario`) REFERENCES `veterinario` (`id_veterinario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historia_clinica`
--

LOCK TABLES `historia_clinica` WRITE;
/*!40000 ALTER TABLE `historia_clinica` DISABLE KEYS */;
/*!40000 ALTER TABLE `historia_clinica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mascota`
--

DROP TABLE IF EXISTS `mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mascota` (
  `id_mascota` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(600) DEFAULT NULL,
  `foto` blob DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `edad` int(11) DEFAULT NULL,
  `esterilizado` varchar(20) DEFAULT NULL,
  `id_dueno` int(11) DEFAULT NULL,
  `antecedentes` varchar(800) DEFAULT NULL,
  `diagnostico` varchar(800) DEFAULT NULL,
  `enfermedades` varchar(600) DEFAULT NULL,
  `especie` varchar(60) DEFAULT NULL,
  `foto_url` varchar(500) DEFAULT NULL,
  `raza` varchar(80) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `tipo_mascota` varchar(80) DEFAULT NULL,
  `tipo_sangre` varchar(30) DEFAULT NULL,
  `vacunas` varchar(600) DEFAULT NULL,
  `carnet_vacunacion_url` varchar(500) DEFAULT NULL,
  `vacunas_vigentes` bit(1) NOT NULL,
  PRIMARY KEY (`id_mascota`),
  KEY `id_dueno` (`id_dueno`),
  CONSTRAINT `mascota_ibfk_1` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mascota`
--

LOCK TABLES `mascota` WRITE;
/*!40000 ALTER TABLE `mascota` DISABLE KEYS */;
/*!40000 ALTER TABLE `mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mensaje`
--

DROP TABLE IF EXISTS `mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mensaje` (
  `id_mensaje` int(11) NOT NULL AUTO_INCREMENT,
  `contenido` varchar(2000) NOT NULL,
  `enviado_en` datetime(6) NOT NULL,
  `imagen_url` varchar(500) DEFAULT NULL,
  `leido` tinyint(1) NOT NULL DEFAULT 0,
  `leido_en` datetime(6) DEFAULT NULL,
  `id_autor` int(11) NOT NULL,
  `id_conversacion` int(11) NOT NULL,
  PRIMARY KEY (`id_mensaje`),
  KEY `FKo1cs27c1200n35ka5ay481off` (`id_autor`),
  KEY `FKgdkmmb0imo98aihe9e0t9tjxu` (`id_conversacion`),
  CONSTRAINT `FKgdkmmb0imo98aihe9e0t9tjxu` FOREIGN KEY (`id_conversacion`) REFERENCES `conversacion` (`id_conversacion`),
  CONSTRAINT `FKo1cs27c1200n35ka5ay481off` FOREIGN KEY (`id_autor`) REFERENCES `dueno` (`id_dueno`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mensaje`
--

LOCK TABLES `mensaje` WRITE;
/*!40000 ALTER TABLE `mensaje` DISABLE KEYS */;
/*!40000 ALTER TABLE `mensaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones_dueno`
--

DROP TABLE IF EXISTS `notificaciones_dueno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notificaciones_dueno` (
  `id_notificacion_dueno` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `visto` tinyint(1) DEFAULT NULL,
  `id_dueno` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_notificacion_dueno`),
  KEY `id_dueno` (`id_dueno`),
  CONSTRAINT `notificaciones_dueno_ibfk_1` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones_dueno`
--

LOCK TABLES `notificaciones_dueno` WRITE;
/*!40000 ALTER TABLE `notificaciones_dueno` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificaciones_dueno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones_veterinario`
--

DROP TABLE IF EXISTS `notificaciones_veterinario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notificaciones_veterinario` (
  `id_notificacion_veterinario` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `visto` tinyint(1) DEFAULT NULL,
  `id_veterinario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_notificacion_veterinario`),
  KEY `id_veterinario` (`id_veterinario`),
  CONSTRAINT `notificaciones_veterinario_ibfk_1` FOREIGN KEY (`id_veterinario`) REFERENCES `veterinario` (`id_veterinario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones_veterinario`
--

LOCK TABLES `notificaciones_veterinario` WRITE;
/*!40000 ALTER TABLE `notificaciones_veterinario` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificaciones_veterinario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `raza`
--

DROP TABLE IF EXISTS `raza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raza` (
  `id_raza` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_raza`),
  KEY `id_mascota` (`id_mascota`),
  CONSTRAINT `raza_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `raza`
--

LOCK TABLES `raza` WRITE;
/*!40000 ALTER TABLE `raza` DISABLE KEYS */;
/*!40000 ALTER TABLE `raza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recordatorio`
--

DROP TABLE IF EXISTS `recordatorio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recordatorio` (
  `id_recordatorio` int(11) NOT NULL AUTO_INCREMENT,
  `completado` bit(1) NOT NULL,
  `descripcion` varchar(600) DEFAULT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `repeticion` varchar(20) NOT NULL,
  `tipo` varchar(30) NOT NULL,
  `titulo` varchar(120) NOT NULL,
  `id_mascota` int(11) NOT NULL,
  PRIMARY KEY (`id_recordatorio`),
  KEY `FKlnly5tr71wjmuk8gomscv6sfn` (`id_mascota`),
  CONSTRAINT `FKlnly5tr71wjmuk8gomscv6sfn` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recordatorio`
--

LOCK TABLES `recordatorio` WRITE;
/*!40000 ALTER TABLE `recordatorio` DISABLE KEYS */;
/*!40000 ALTER TABLE `recordatorio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_mascota`
--

DROP TABLE IF EXISTS `tipo_mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_mascota` (
  `id_tipo_mascota` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_mascota`),
  KEY `id_mascota` (`id_mascota`),
  CONSTRAINT `tipo_mascota_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_mascota`
--

LOCK TABLES `tipo_mascota` WRITE;
/*!40000 ALTER TABLE `tipo_mascota` DISABLE KEYS */;
/*!40000 ALTER TABLE `tipo_mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_sangre`
--

DROP TABLE IF EXISTS `tipo_sangre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_sangre` (
  `id_tipo_sangre` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_sangre`),
  KEY `id_mascota` (`id_mascota`),
  CONSTRAINT `tipo_sangre_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_sangre`
--

LOCK TABLES `tipo_sangre` WRITE;
/*!40000 ALTER TABLE `tipo_sangre` DISABLE KEYS */;
/*!40000 ALTER TABLE `tipo_sangre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vacuna`
--

DROP TABLE IF EXISTS `vacuna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vacuna` (
  `id_vacuna` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_vacunas_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_vacuna`),
  KEY `id_vacunas_mascota` (`id_vacunas_mascota`),
  CONSTRAINT `vacuna_ibfk_1` FOREIGN KEY (`id_vacunas_mascota`) REFERENCES `vacunas_mascota` (`id_vacunas_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vacuna`
--

LOCK TABLES `vacuna` WRITE;
/*!40000 ALTER TABLE `vacuna` DISABLE KEYS */;
/*!40000 ALTER TABLE `vacuna` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vacunas_mascota`
--

DROP TABLE IF EXISTS `vacunas_mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vacunas_mascota` (
  `id_vacunas_mascota` int(11) NOT NULL AUTO_INCREMENT,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_final` date DEFAULT NULL,
  `id_mascota` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_vacunas_mascota`),
  KEY `id_mascota` (`id_mascota`),
  CONSTRAINT `vacunas_mascota_ibfk_1` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vacunas_mascota`
--

LOCK TABLES `vacunas_mascota` WRITE;
/*!40000 ALTER TABLE `vacunas_mascota` DISABLE KEYS */;
/*!40000 ALTER TABLE `vacunas_mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veterinario`
--

DROP TABLE IF EXISTS `veterinario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `veterinario` (
  `id_veterinario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `apellido` varchar(100) DEFAULT NULL,
  `numero_documento` int(11) DEFAULT NULL,
  `experiencia` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `foto` blob DEFAULT NULL,
  `img_titulo` blob DEFAULT NULL,
  `contrasena` varchar(255) DEFAULT NULL,
  `id_administrador` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_veterinario`),
  KEY `id_administrador` (`id_administrador`),
  CONSTRAINT `veterinario_ibfk_1` FOREIGN KEY (`id_administrador`) REFERENCES `administrador` (`id_administrador`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veterinario`
--

LOCK TABLES `veterinario` WRITE;
/*!40000 ALTER TABLE `veterinario` DISABLE KEYS */;
/*!40000 ALTER TABLE `veterinario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veterinario_perfil`
--

DROP TABLE IF EXISTS `veterinario_perfil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `veterinario_perfil` (
  `id_veterinario_perfil` int(11) NOT NULL AUTO_INCREMENT,
  `clinica` varchar(120) DEFAULT NULL,
  `descripcion` varchar(800) DEFAULT NULL,
  `especialidad` varchar(120) NOT NULL,
  `experiencia` varchar(120) DEFAULT NULL,
  `foto_url` varchar(500) DEFAULT NULL,
  `numero_documento` varchar(30) DEFAULT NULL,
  `tarjeta_profesional` varchar(80) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `titulo_url` varchar(500) DEFAULT NULL,
  `id_dueno` int(11) NOT NULL,
  `localidad` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id_veterinario_perfil`),
  UNIQUE KEY `UK1brv3lsubkagdpb5lq2mhuy0q` (`id_dueno`),
  CONSTRAINT `FKayubt4dnnitfgem35v1ei7sm7` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veterinario_perfil`
--

LOCK TABLES `veterinario_perfil` WRITE;
/*!40000 ALTER TABLE `veterinario_perfil` DISABLE KEYS */;
INSERT INTO `veterinario_perfil` VALUES (1,'Huellitas','Médico Veterinario Especialista en Medicina Felina | Acreditación Cat Friendly | Bienestar y Salud del Gato','Felinos','5 años en medicina felina','/uploads/veterinarios/70fd25e0-73fb-4e1b-baac-760ee6fe8454.png','12345685','147852','3052441571','/uploads/titulos-veterinarios/624e07c2-6d8f-4be2-8061-4010118eef8a.pdf',3,'Usaquén'),(2,'Huellitas','ivhfdoifs','Felinos','5 años en medicina felina','/uploads/veterinarios/38bfcda2-45a5-4d4e-85e1-042ef60253e9.png','12345685','147852','3052441572','/uploads/titulos-veterinarios/aa4fb7aa-80c2-4277-a1e4-5dfbab16b295.pdf',4,'Bosa');
/*!40000 ALTER TABLE `veterinario_perfil` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veterinarios_favoritos`
--

DROP TABLE IF EXISTS `veterinarios_favoritos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `veterinarios_favoritos` (
  `id_dueno` int(11) NOT NULL,
  `id_veterinario` int(11) NOT NULL,
  PRIMARY KEY (`id_dueno`,`id_veterinario`),
  KEY `id_veterinario` (`id_veterinario`),
  CONSTRAINT `veterinarios_favoritos_ibfk_1` FOREIGN KEY (`id_dueno`) REFERENCES `dueno` (`id_dueno`),
  CONSTRAINT `veterinarios_favoritos_ibfk_2` FOREIGN KEY (`id_veterinario`) REFERENCES `veterinario` (`id_veterinario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veterinarios_favoritos`
--

LOCK TABLES `veterinarios_favoritos` WRITE;
/*!40000 ALTER TABLE `veterinarios_favoritos` DISABLE KEYS */;
/*!40000 ALTER TABLE `veterinarios_favoritos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'zoi_java'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-13 22:33:17
