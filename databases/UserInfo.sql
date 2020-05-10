CREATE DATABASE  IF NOT EXISTS `UserInfo` /*!40100 DEFAULT CHARACTER SET latin1 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `UserInfo`;
-- MySQL dump 10.13  Distrib 8.0.19, for Linux (x86_64)
--
-- Host: 192.168.43.235    Database: UserInfo
-- ------------------------------------------------------
-- Server version	8.0.19-0ubuntu0.19.10.3

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
-- Table structure for table `Course`
--

DROP TABLE IF EXISTS `Course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Course` (
  `courseId` bigint NOT NULL AUTO_INCREMENT,
  `courseCode` varchar(10) DEFAULT NULL,
  `courseName` varchar(45) DEFAULT NULL,
  `courseYear` year DEFAULT NULL,
  `courseSemester` enum('winter','monsoon','summer') DEFAULT NULL,
  `courseType` varchar(45) DEFAULT NULL,
  `professorId` bigint NOT NULL,
  PRIMARY KEY (`courseId`),
  KEY `fk_Course_1_idx` (`professorId`),
  CONSTRAINT `fk_Course_1` FOREIGN KEY (`professorId`) REFERENCES `Professor` (`professorId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Course`
--

LOCK TABLES `Course` WRITE;
/*!40000 ALTER TABLE `Course` DISABLE KEYS */;
INSERT INTO `Course` VALUES (5,'CIV105','Enviromental Science',2020,'winter','Lectures',4),(6,'CHEM200','Organic Chemistry',2019,'summer','practical',1),(7,'COM103','Introduction to C Programming',2019,'monsoon','Lab based',10);
/*!40000 ALTER TABLE `Course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Person`
--

DROP TABLE IF EXISTS `Person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Person` (
  `personFirstName` varchar(45) DEFAULT NULL,
  `personMiddleName` varchar(45) DEFAULT NULL,
  `personLastName` varchar(45) DEFAULT NULL,
  `personDOB` date DEFAULT NULL,
  `personContactNum` varchar(10) DEFAULT NULL,
  `personEmail` varchar(45) DEFAULT NULL,
  `personGender` enum('male','female') DEFAULT NULL,
  `personUserName` varchar(50) NOT NULL,
  PRIMARY KEY (`personUserName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Person`
--

LOCK TABLES `Person` WRITE;
/*!40000 ALTER TABLE `Person` DISABLE KEYS */;
INSERT INTO `Person` VALUES ('Biraj','P','Dhaduk','1998-11-05','7567593939','bdhaduk@gmail.com','male','biraj'),('het','H','jagani','1998-10-13','9879219608','hetpatel572@gmail.com','male','hetjagani'),('jainam','H','patel','1998-09-29','854976213','jainampatel@gmail.com','male','jainam'),('paresh','V','patel','1980-01-01','8317438546','hetpatel572@gmail.com','male','paresh'),('Rajesh','N','Shah','1993-06-11','845697123','hetjagani572@gmail.com','male','rajesh'),('walt','J','white','1990-11-02','9517538246','het.j.btechi16@ahduni.edu.in','male','white');
/*!40000 ALTER TABLE `Person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Professor`
--

DROP TABLE IF EXISTS `Professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Professor` (
  `professorId` bigint NOT NULL AUTO_INCREMENT,
  `professorOfficeNum` varchar(45) DEFAULT NULL,
  `professorDepartment` varchar(45) DEFAULT NULL,
  `professorDesignation` varchar(45) DEFAULT NULL,
  `personUserName` varchar(50) NOT NULL,
  PRIMARY KEY (`professorId`),
  KEY `fk_Professor_1_idx` (`personUserName`),
  CONSTRAINT `fk_Professor_1` FOREIGN KEY (`personUserName`) REFERENCES `Person` (`personUserName`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Professor`
--

LOCK TABLES `Professor` WRITE;
/*!40000 ALTER TABLE `Professor` DISABLE KEYS */;
INSERT INTO `Professor` VALUES (1,'214','Chemistry','Lecturer','white'),(4,'211','Civil','Head Of Dept','paresh'),(10,'219','Computer Science','Assistant Professor','rajesh');
/*!40000 ALTER TABLE `Professor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Student`
--

DROP TABLE IF EXISTS `Student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Student` (
  `studentEnrollNum` varchar(10) NOT NULL,
  `studentAdmitYear` varchar(4) DEFAULT NULL,
  `personUserName` varchar(50) NOT NULL,
  PRIMARY KEY (`studentEnrollNum`),
  KEY `fk_Student_1_idx` (`personUserName`),
  CONSTRAINT `fk_Student_1` FOREIGN KEY (`personUserName`) REFERENCES `Person` (`personUserName`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Student`
--

LOCK TABLES `Student` WRITE;
/*!40000 ALTER TABLE `Student` DISABLE KEYS */;
INSERT INTO `Student` VALUES ('1641006','2016','jainam'),('1641007','2016','hetjagani'),('itnu134002','2016','biraj');
/*!40000 ALTER TABLE `Student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-14 10:23:59
