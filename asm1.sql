-- MySQL dump 10.13  Distrib 8.0.40, for macos14 (arm64)
--
-- Host: localhost    Database: asm1
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `allassets`
--

DROP TABLE IF EXISTS `allassets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `allassets` (
  `assetId` int NOT NULL AUTO_INCREMENT,
  `assetName` varchar(255) NOT NULL,
  `assetType` varchar(255) NOT NULL,
  PRIMARY KEY (`assetId`),
  UNIQUE KEY `assetId` (`assetId`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allassets`
--

LOCK TABLES `allassets` WRITE;
/*!40000 ALTER TABLE `allassets` DISABLE KEYS */;
INSERT INTO `allassets` VALUES (1,'LapTop','HARDWARE'),(2,'LapTop','HARDWARE'),(3,'LapTop','HARDWARE'),(4,'LAPTOP','HARDWARE'),(5,'LAPTOP BAG','HARDWARE'),(6,'PHONE','HARDWARE'),(7,'Antivirus','SOFTWARE');
/*!40000 ALTER TABLE `allassets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `allusers`
--

DROP TABLE IF EXISTS `allusers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `allusers` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userTypeId` int NOT NULL,
  `userName` varchar(255) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userId` (`userId`),
  KEY `allusers_fk1` (`userTypeId`),
  CONSTRAINT `allusers_fk1` FOREIGN KEY (`userTypeId`) REFERENCES `UserTypes` (`userTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allusers`
--

LOCK TABLES `allusers` WRITE;
/*!40000 ALTER TABLE `allusers` DISABLE KEYS */;
INSERT INTO `allusers` VALUES (1,2,'User 1'),(2,1,'USER 2'),(3,2,'USER #'),(4,3,'User 4'),(5,3,'User 5'),(6,3,'user 6'),(7,1,'Sample User 1'),(8,2,'Sample User 2'),(9,3,'Sample User 3');
/*!40000 ALTER TABLE `allusers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Asset`
--

DROP TABLE IF EXISTS `Asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Asset` (
  `assetId` int NOT NULL,
  `assetName` varchar(255) NOT NULL,
  `assetType` varchar(255) NOT NULL,
  `assetCount` int NOT NULL,
  PRIMARY KEY (`assetId`),
  UNIQUE KEY `assetId` (`assetId`),
  CONSTRAINT `Asset_fk0` FOREIGN KEY (`assetId`) REFERENCES `allassets` (`assetId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Asset`
--

LOCK TABLES `Asset` WRITE;
/*!40000 ALTER TABLE `Asset` DISABLE KEYS */;
INSERT INTO `Asset` VALUES (4,'LAPTOP','HARDWARE',31),(5,'LAPTOP CHARGER','HARDWARE',23),(6,'PHONE','HARDWARE',34),(7,'Antivirus','SOFTWARE',30);
/*!40000 ALTER TABLE `Asset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AssetAssignmentsSummary`
--

DROP TABLE IF EXISTS `AssetAssignmentsSummary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AssetAssignmentsSummary` (
  `assignmentId` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `assetId` int NOT NULL,
  `dateTime` datetime NOT NULL,
  `operations` enum('ASSIGN','RETAIN') NOT NULL,
  `status` tinyint(1) NOT NULL,
  PRIMARY KEY (`assignmentId`),
  UNIQUE KEY `assignmentId` (`assignmentId`),
  KEY `AssetAssignmentsSummary_fk1` (`userId`),
  KEY `AssetAssignmentsSummary_fk2` (`assetId`),
  CONSTRAINT `AssetAssignmentsSummary_fk1` FOREIGN KEY (`userId`) REFERENCES `allusers` (`userId`),
  CONSTRAINT `AssetAssignmentsSummary_fk2` FOREIGN KEY (`assetId`) REFERENCES `allassets` (`assetId`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AssetAssignmentsSummary`
--

LOCK TABLES `AssetAssignmentsSummary` WRITE;
/*!40000 ALTER TABLE `AssetAssignmentsSummary` DISABLE KEYS */;
INSERT INTO `AssetAssignmentsSummary` VALUES (3,1,4,'2024-12-25 21:21:42','ASSIGN',0),(4,4,4,'2024-12-25 21:22:55','ASSIGN',0),(5,1,5,'2024-12-25 21:25:55','ASSIGN',0),(6,4,5,'2024-12-25 21:30:20','ASSIGN',0),(7,4,4,'2024-12-25 21:30:37','RETAIN',0),(8,4,5,'2024-12-25 21:30:37','RETAIN',0),(9,1,4,'2024-12-25 21:31:55','RETAIN',0),(10,1,5,'2024-12-25 21:32:30','RETAIN',0),(11,5,4,'2024-12-25 21:38:05','ASSIGN',1),(12,6,4,'2024-12-25 21:38:19','ASSIGN',1),(13,5,6,'2024-12-31 17:33:50','ASSIGN',1);
/*!40000 ALTER TABLE `AssetAssignmentsSummary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_table`
--

DROP TABLE IF EXISTS `request_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_table` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `asset_id` int NOT NULL,
  `operations` enum('ASSIGN','RETAIN') NOT NULL,
  `given_date_time` datetime NOT NULL,
  `completed_date_time` datetime DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  PRIMARY KEY (`request_id`),
  UNIQUE KEY `request_id` (`request_id`),
  KEY `request_table_fk1` (`user_id`),
  KEY `request_table_fk2` (`asset_id`),
  CONSTRAINT `request_table_fk1` FOREIGN KEY (`user_id`) REFERENCES `allusers` (`userId`),
  CONSTRAINT `request_table_fk2` FOREIGN KEY (`asset_id`) REFERENCES `allassets` (`assetId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_table`
--

LOCK TABLES `request_table` WRITE;
/*!40000 ALTER TABLE `request_table` DISABLE KEYS */;
INSERT INTO `request_table` VALUES (1,1,5,'ASSIGN','2024-12-25 21:24:34','2024-12-25 21:25:55',1),(2,5,4,'ASSIGN','2024-12-25 21:37:07','2024-12-25 21:38:05',1),(3,5,5,'ASSIGN','2024-12-25 21:37:17',NULL,0),(4,6,4,'ASSIGN','2024-12-25 21:37:28','2024-12-25 21:38:19',1),(5,6,5,'ASSIGN','2024-12-25 21:37:33',NULL,0),(6,5,6,'ASSIGN','2024-12-31 17:33:24','2024-12-31 17:33:50',1);
/*!40000 ALTER TABLE `request_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RetainedAssets`
--

DROP TABLE IF EXISTS `RetainedAssets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RetainedAssets` (
  `assetId` int NOT NULL,
  `retainedAssetCount` int NOT NULL,
  KEY `RetainedAssets_fk0_idx` (`assetId`),
  CONSTRAINT `RetainedAssets_fk0` FOREIGN KEY (`assetId`) REFERENCES `allassets` (`assetId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RetainedAssets`
--

LOCK TABLES `RetainedAssets` WRITE;
/*!40000 ALTER TABLE `RetainedAssets` DISABLE KEYS */;
INSERT INTO `RetainedAssets` VALUES (2,0),(3,0),(4,2),(5,2),(6,0),(7,0);
/*!40000 ALTER TABLE `RetainedAssets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `userId` int NOT NULL,
  `userTypeId` int NOT NULL,
  `userName` varchar(255) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userId` (`userId`),
  KEY `User_fk1` (`userTypeId`),
  CONSTRAINT `User_fk0` FOREIGN KEY (`userId`) REFERENCES `allusers` (`userId`),
  CONSTRAINT `User_fk1` FOREIGN KEY (`userTypeId`) REFERENCES `UserTypes` (`userTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (5,3,'User 5'),(6,1,'User 6'),(7,1,'Sample User 1'),(8,2,'Sample User 2'),(9,3,'Sample User 3');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserAssetMapping`
--

DROP TABLE IF EXISTS `UserAssetMapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `UserAssetMapping` (
  `assetId` int NOT NULL,
  `userTypeId` int NOT NULL,
  KEY `UserAssetMapping_fk1` (`userTypeId`),
  KEY `UserAssetMapping_fk0_idx` (`assetId`),
  CONSTRAINT `UserAssetMapping_fk0` FOREIGN KEY (`assetId`) REFERENCES `allassets` (`assetId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UserAssetMapping_fk1` FOREIGN KEY (`userTypeId`) REFERENCES `UserTypes` (`userTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserAssetMapping`
--

LOCK TABLES `UserAssetMapping` WRITE;
/*!40000 ALTER TABLE `UserAssetMapping` DISABLE KEYS */;
INSERT INTO `UserAssetMapping` VALUES (4,1),(4,2),(4,3),(5,1),(5,2),(5,3),(6,3),(7,1),(7,2);
/*!40000 ALTER TABLE `UserAssetMapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserTypes`
--

DROP TABLE IF EXISTS `UserTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `UserTypes` (
  `userTypeId` int NOT NULL AUTO_INCREMENT,
  `userType` varchar(255) NOT NULL,
  PRIMARY KEY (`userTypeId`),
  UNIQUE KEY `userTypeId` (`userTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserTypes`
--

LOCK TABLES `UserTypes` WRITE;
/*!40000 ALTER TABLE `UserTypes` DISABLE KEYS */;
INSERT INTO `UserTypes` VALUES (1,'MANAGER'),(2,'EMPLOYEE'),(3,'TRAINEE');
/*!40000 ALTER TABLE `UserTypes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-31 17:39:23
