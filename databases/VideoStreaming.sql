CREATE DATABASE  IF NOT EXISTS `VideoStreaming` /*!40100 DEFAULT CHARACTER SET latin1 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `VideoStreaming`;
-- MySQL dump 10.13  Distrib 8.0.19, for Linux (x86_64)
--
-- Host: 192.168.43.235    Database: VideoStreaming
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
-- Table structure for table `LectureNote`
--

DROP TABLE IF EXISTS `LectureNote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LectureNote` (
  `lectureNoteId` bigint NOT NULL AUTO_INCREMENT,
  `lectureNoteFileName` text,
  `lectureNoteFilePath` text,
  `lectureNoteFileSize` float DEFAULT NULL,
  `lectureNoteFileType` varchar(20) DEFAULT NULL,
  `videoId` bigint DEFAULT NULL,
  PRIMARY KEY (`lectureNoteId`),
  KEY `fk_LectureNote_1_idx` (`videoId`),
  CONSTRAINT `fk_LectureNote_1` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LectureNote`
--

LOCK TABLES `LectureNote` WRITE;
/*!40000 ALTER TABLE `LectureNote` DISABLE KEYS */;
INSERT INTO `LectureNote` VALUES (13,'EnvCh01Lecture.ppt','1/EnvCh01Lecture.ppt',2323.5,'ppt',24),(14,'env.pdf','2/env.pdf',4032.05,'pdf',25),(15,'EvelineDeMey_przyprawy.ppt','4/EvelineDeMey_przyprawy.ppt',2522.5,'ppt',26),(16,'ijaerv8n15spl_01.pdf','5/ijaerv8n15spl_01.pdf',143.647,'pdf',27),(17,'ctutor.pdf','6/ctutor.pdf',311.704,'pdf',28),(18,'ctutor.pdf','7/ctutor.pdf',311.704,'pdf',29);
/*!40000 ALTER TABLE `LectureNote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Video`
--

DROP TABLE IF EXISTS `Video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Video` (
  `videoId` bigint NOT NULL AUTO_INCREMENT,
  `videoTitle` text,
  `videoDescription` mediumtext,
  `courseId` bigint DEFAULT NULL,
  PRIMARY KEY (`videoId`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Video`
--

LOCK TABLES `Video` WRITE;
/*!40000 ALTER TABLE `Video` DISABLE KEYS */;
INSERT INTO `Video` VALUES (24,'001 - Environmental Science','In this video Paul Andersen outlines the AP Environmental Science course.  He explains how environmental science studies the interaction between earth and human systems.  A planetary boundary model is used to explain the importance of sustainability.  The importance of science practices and knowledge of the APES format is also included.',5),(25,'Environmental Systems','In this video Paul Andersen explains how matter and energy are conserved within the Earth\'s system.  Matter is a closed system and Energy is open to the surroundings.  In natural systems steady state is maintained through feedback loops but can be be affected by human society.',5),(26,'Extracting the Spicy Chemical in Black Pepper','This is a new series called edible chem science, where Ill be able to eat the stuff that I make. For this video, Ill be extracting piperine, but let me know what you would like to see in the future! ',6),(27,'How to make Alcohol at Home (Ethanol)','This is a very basic way that you can make your own DRINKABLE ethanol at home. The main premise of this video is to show just how simple and easy it is to ferment your own alcohol. In a future video, I will try to dispel some myths regarding homebrewing, such as the exaggerated risk of methanol poisoning. I will also demonstrate how easy and safe it can be to distill your own ethanol from the wash that you produce.',6),(28,'Introduction to C Programming- Basic Computer Terms','ntroduction to C Programming- Basic Computer Terms: In this lesson Rajesh Shah explains about Basic Computer Terms. This course will help you understand and use the basic programming construct of C and write small scale C programs using the above skills.',7),(29,'Introduction to Variables','Topics discussed:\r\n1. What is a variable?\r\n2. Declaration of variables.\r\n3. Definition of variables.\r\n4. Initialization of variables.\r\n5. Assignment of variables.',7);
/*!40000 ALTER TABLE `Video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VideoResolutions`
--

DROP TABLE IF EXISTS `VideoResolutions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `VideoResolutions` (
  `videoFileId` bigint NOT NULL AUTO_INCREMENT,
  `videoId` bigint DEFAULT NULL,
  `videoFileName` text,
  `videoFilePath` text,
  `videoFileDuration` time DEFAULT NULL,
  `videoWidth` int DEFAULT NULL,
  `videoHeight` int DEFAULT NULL,
  `videoFPS` float DEFAULT NULL,
  `videoFileSize` float DEFAULT NULL,
  `videoFileFormat` tinytext,
  PRIMARY KEY (`videoFileId`),
  KEY `fk_VideoResolutions_1_idx` (`videoId`),
  CONSTRAINT `fk_VideoResolutions_1` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VideoResolutions`
--

LOCK TABLES `VideoResolutions` WRITE;
/*!40000 ALTER TABLE `VideoResolutions` DISABLE KEYS */;
INSERT INTO `VideoResolutions` VALUES (16,24,'240.mp4','1/240/240.mp4','05:39:07',426,240,30,13567.7,'mp4'),(17,24,'480.mp4','1/480/480.mp4','05:39:07',854,480,30,20685.4,'mp4'),(18,24,'720.mp4','1/720/720.mp4','05:39:07',1280,720,30,26947.6,'mp4'),(19,24,'144.mp4','1/144/144.mp4','05:39:07',256,144,30,11430.3,'mp4'),(20,24,'360.mp4','1/360/360.mp4','05:39:07',640,360,30,16801.9,'mp4'),(21,25,'240.mp4','2/240/240.mp4','05:39:38',426,240,30,14448.2,'mp4'),(22,25,'144.mp4','2/144/144.mp4','05:39:38',256,144,30,12147.2,'mp4'),(23,25,'360.mp4','2/360/360.mp4','05:39:38',640,360,30,18488.6,'mp4'),(24,26,'240.mp4','4/240/240.mp4','05:45:19',426,240,29.97,22258.6,'mp4'),(25,26,'480.mp4','4/480/480.mp4','05:45:19',854,480,29.97,40905.7,'mp4'),(26,26,'720.mp4','4/720/720.mp4','05:45:19',1280,720,29.97,56548.3,'mp4'),(27,26,'144.mp4','4/144/144.mp4','05:45:19',256,144,29.97,17579.1,'mp4'),(28,26,'360.mp4','4/360/360.mp4','05:45:19',640,360,29.97,30271.7,'mp4'),(29,27,'240.mp4','5/240/240.mp4','05:38:20',426,240,29.97,15474.9,'mp4'),(30,27,'480.mp4','5/480/480.mp4','05:38:20',854,480,29.97,31323.3,'mp4'),(31,27,'720.mp4','5/720/720.mp4','05:38:20',1280,720,29.97,49128.7,'mp4'),(32,27,'144.mp4','5/144/144.mp4','05:38:20',256,144,29.97,11363.4,'mp4'),(33,27,'360.mp4','5/360/360.mp4','05:38:20',640,360,29.97,22704.9,'mp4'),(34,28,'240.mp4','6/240/240.mp4','05:35:16',426,240,25,762.75,'mp4'),(35,28,'480.mp4','6/480/480.mp4','05:35:16',854,480,25,1876.15,'mp4'),(36,28,'144.mp4','6/144/144.mp4','05:35:16',256,144,25,451.653,'mp4'),(37,28,'360.mp4','6/360/360.mp4','05:35:16',640,360,25,1300.06,'mp4'),(38,29,'240.mp4','7/240/240.mp4','05:38:23',426,240,30,9038.99,'mp4'),(39,29,'480.mp4','7/480/480.mp4','05:38:23',854,480,30,10838.7,'mp4'),(40,29,'720.mp4','7/720/720.mp4','05:38:23',1280,720,30,12605.1,'mp4'),(41,29,'144.mp4','7/144/144.mp4','05:38:23',256,144,30,8491.2,'mp4'),(42,29,'360.mp4','7/360/360.mp4','05:38:23',640,360,30,9867.65,'mp4');
/*!40000 ALTER TABLE `VideoResolutions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-14 10:25:18
