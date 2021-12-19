CREATE DATABASE  IF NOT EXISTS `vacationplanner` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `vacationplanner`;
-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vacationplanner
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `antragsstatus`
--

DROP TABLE IF EXISTS `antragsstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `antragsstatus` (
  `idStatus` int NOT NULL,
  `bezeichnung` varchar(45) NOT NULL,
  PRIMARY KEY (`idStatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `antragsstatus`
--

LOCK TABLES `antragsstatus` WRITE;
/*!40000 ALTER TABLE `antragsstatus` DISABLE KEYS */;
INSERT INTO `antragsstatus` VALUES (1,'offen'),(2,'in bearbeitung'),(3,'genehmigt'),(4,'abgelehnt'),(5,'zur ueberpruefung');
/*!40000 ALTER TABLE `antragsstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feiertag`
--

DROP TABLE IF EXISTS `feiertag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feiertag` (
  `idFeier` int NOT NULL,
  `datum` date NOT NULL,
  PRIMARY KEY (`idFeier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feiertag`
--

LOCK TABLES `feiertag` WRITE;
/*!40000 ALTER TABLE `feiertag` DISABLE KEYS */;
INSERT INTO `feiertag` VALUES (1,'2022-01-15'),(2,'2022-02-15'),(3,'2022-03-15'),(4,'2022-04-15'),(5,'2022-05-15'),(6,'2022-06-15'),(7,'2022-07-15'),(8,'2022-08-15'),(9,'2022-09-15'),(10,'2022-10-15'),(11,'2022-11-15'),(12,'2022-12-15');
/*!40000 ALTER TABLE `feiertag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kritischerbereich`
--

DROP TABLE IF EXISTS `kritischerbereich`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kritischerbereich` (
  `idK` int NOT NULL,
  `start` date NOT NULL,
  `ende` date NOT NULL,
  PRIMARY KEY (`idK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kritischerbereich`
--

LOCK TABLES `kritischerbereich` WRITE;
/*!40000 ALTER TABLE `kritischerbereich` DISABLE KEYS */;
INSERT INTO `kritischerbereich` VALUES (1,'2022-06-01','2022-08-31'),(2,'2022-10-03','2022-10-15');
/*!40000 ALTER TABLE `kritischerbereich` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mitarbeiter`
--

DROP TABLE IF EXISTS `mitarbeiter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mitarbeiter` (
  `idM` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `adresse` varchar(45) NOT NULL,
  `anzahlUrlaubstage` int NOT NULL,
  PRIMARY KEY (`idM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mitarbeiter`
--

LOCK TABLES `mitarbeiter` WRITE;
/*!40000 ALTER TABLE `mitarbeiter` DISABLE KEYS */;
INSERT INTO `mitarbeiter` VALUES (1,'Max Mustermann','Musterstraße 1, 12345 Musterstadt',30),(2,'Erika Musterfrau','Dorfstraße 6, 12345 Musterstadt',30),(3,'John Doe','Bachstraße 45, 10555 Berlin',30);
/*!40000 ALTER TABLE `mitarbeiter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projekt`
--

DROP TABLE IF EXISTS `projekt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projekt` (
  `idP` int NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`idP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projekt`
--

LOCK TABLES `projekt` WRITE;
/*!40000 ALTER TABLE `projekt` DISABLE KEYS */;
INSERT INTO `projekt` VALUES (1,'Urlaubsplaner'),(2,'Produktentwicklung'),(3,'Raumgestaltung'),(4,'Weihnachtsfeier');
/*!40000 ALTER TABLE `projekt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projekt_has_mitarbeiter`
--

DROP TABLE IF EXISTS `projekt_has_mitarbeiter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projekt_has_mitarbeiter` (
  `idMP` int NOT NULL,
  `idP` int NOT NULL,
  `idM` int NOT NULL,
  PRIMARY KEY (`idMP`),
  KEY `fk_Projekte_has_Mitarbeiter:innen_Mitarbeiter:innen1_idx` (`idM`,`idMP`),
  KEY `fk_Projekte_has_Mitarbeiter:innen_Projekte1_idx` (`idP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projekt_has_mitarbeiter`
--

LOCK TABLES `projekt_has_mitarbeiter` WRITE;
/*!40000 ALTER TABLE `projekt_has_mitarbeiter` DISABLE KEYS */;
INSERT INTO `projekt_has_mitarbeiter` VALUES (1,1,1),(2,2,3),(3,3,2),(4,4,2),(5,2,1),(6,4,1),(7,4,3),(8,4,4);
/*!40000 ALTER TABLE `projekt_has_mitarbeiter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `urlaubsantrag`
--

DROP TABLE IF EXISTS `urlaubsantrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `urlaubsantrag` (
  `idUA` int NOT NULL,
  `idM` int NOT NULL,
  `startDatum` date NOT NULL,
  `endDatum` date NOT NULL,
  `idStatus` int NOT NULL,
  `dauer` int NOT NULL,
  PRIMARY KEY (`idUA`),
  KEY `fk_UrlaubsAntrag_AntragsStatus1_idx` (`idStatus`),
  KEY `fk_UrlaubsAntrag_Mitarbeiter1_idx` (`idM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `urlaubsantrag`
--

LOCK TABLES `urlaubsantrag` WRITE;
/*!40000 ALTER TABLE `urlaubsantrag` DISABLE KEYS */;
/*!40000 ALTER TABLE `urlaubsantrag` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-14 16:52:42
