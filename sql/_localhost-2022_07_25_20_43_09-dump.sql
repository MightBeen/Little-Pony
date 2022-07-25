-- MySQL dump 10.13  Distrib 5.7.37, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: admin_system
-- ------------------------------------------------------
-- Server version	5.7.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ptr_endpoint`
--

DROP TABLE IF EXISTS `ptr_endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ptr_endpoint` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '节点名称',
  `url` varchar(100) DEFAULT NULL,
  `status` int(11) NOT NULL COMMENT '1为up，2为down',
  `updated` datetime DEFAULT NULL,
  `created` datetime NOT NULL,
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `resource_type` int(11) DEFAULT NULL COMMENT '资源类型，1010为独占型，3030为共享型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='即gpu资源虚拟机';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ptr_endpoint`
--

LOCK TABLES `ptr_endpoint` WRITE;
/*!40000 ALTER TABLE `ptr_endpoint` DISABLE KEYS */;
INSERT INTO `ptr_endpoint` VALUES (2,'local','unix:///var/run/docker.sock',1,'2022-07-25 17:25:02','2022-07-12 08:21:51',NULL,1010),(6,'192.168.81.129','tcp://192.168.81.129:2375',1,'2022-07-25 17:25:02','2022-07-12 08:21:51',NULL,1010),(7,'192.168.81.130','tcp://192.168.81.130:2375',1,'2022-07-25 17:25:02','2022-07-12 08:21:51',NULL,1010);
/*!40000 ALTER TABLE `ptr_endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ptr_user`
--

DROP TABLE IF EXISTS `ptr_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ptr_user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(25) DEFAULT NULL COMMENT 'portainer密码，最小为12位',
  `role` int(11) NOT NULL COMMENT 'Portainer 用户权限，1为管理员，2为普通用户',
  `student_job_id` varchar(45) DEFAULT NULL COMMENT '学号或工号',
  `wos_id` bigint(20) DEFAULT NULL COMMENT '工单系统中id',
  `updated` datetime DEFAULT NULL,
  `created` datetime NOT NULL,
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ptr_user_username_uindex` (`username`),
  UNIQUE KEY `ptr_user_job_id_uindex` (`wos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ptr_user`
--

LOCK TABLES `ptr_user` WRITE;
/*!40000 ALTER TABLE `ptr_user` DISABLE KEYS */;
INSERT INTO `ptr_user` VALUES (1,'admin','13215',1,NULL,NULL,'2022-07-25 17:25:02','2022-07-11 21:06:53',NULL),(18,'admin-system','dsaf',1,NULL,NULL,'2022-07-25 17:25:02','2022-07-11 21:06:53',NULL),(77,'工具人','20210110722021011072',2,'2021011072',21,'2022-07-25 17:25:02','2022-07-25 14:56:21','申请xxx工单上网!!!');
/*!40000 ALTER TABLE `ptr_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ptr_user_endpoint`
--

DROP TABLE IF EXISTS `ptr_user_endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ptr_user_endpoint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `endpoint_id` bigint(20) NOT NULL,
  `expired` datetime NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1551461332467867651 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ptr_user_endpoint`
--

LOCK TABLES `ptr_user_endpoint` WRITE;
/*!40000 ALTER TABLE `ptr_user_endpoint` DISABLE KEYS */;
INSERT INTO `ptr_user_endpoint` VALUES (1551461332467867650,77,2,'2022-07-26 14:56:21','2022-07-25 14:56:21','2022-07-25 17:25:02');
/*!40000 ALTER TABLE `ptr_user_endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_check_list`
--

DROP TABLE IF EXISTS `sys_check_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_check_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `related_user_id` bigint(20) DEFAULT NULL COMMENT '相关的ptr用户信息',
  `related_operator_id` bigint(20) DEFAULT NULL COMMENT '相关管理员信息',
  `related_resource_type` int(11) DEFAULT '1010',
  `type` bigint(20) DEFAULT NULL COMMENT '类型，如1为发生异常，2为资源分配等待,0则为在等待队列中',
  `message` varchar(300) DEFAULT NULL COMMENT '相关信息',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '处理状态，0为未处理，1为已完成',
  `created` datetime NOT NULL,
  `updated` datetime DEFAULT NULL,
  `wait_list_id` bigint(20) DEFAULT NULL COMMENT '如果存在，则为对应等待队列中id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_check_list_wait_list_id_uindex` (`wait_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COMMENT='代办事项清单，用于管理员人工进行操作';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_check_list`
--

LOCK TABLES `sys_check_list` WRITE;
/*!40000 ALTER TABLE `sys_check_list` DISABLE KEYS */;
INSERT INTO `sys_check_list` VALUES (1,60,NULL,1010,0,'我要上网',1,'2022-07-18 15:40:26','2022-07-18 16:31:51',1658130025974),(3,61,NULL,1010,0,'我要上网',1,'2022-07-18 16:37:15','2022-07-18 16:37:31',1658133435051),(5,62,NULL,1010,0,'我要上网',1,'2022-07-18 16:50:31','2022-07-18 16:50:33',1658134231197),(7,63,NULL,1010,0,'我要上网',1,'2022-07-18 16:56:25','2022-07-18 16:56:41',1658134584932),(8,64,NULL,1010,1,'我要上网',0,'2022-07-18 16:57:00','2022-07-18 16:57:42',1658134620544),(9,64,NULL,1010,0,'我要上网',1,'2022-07-18 17:11:42','2022-07-18 17:12:00',1658135502328),(10,65,NULL,1010,0,'我要上网',1,'2022-07-18 17:11:57','2022-07-18 17:16:01',1658135516622),(11,65,NULL,1010,0,'我要上网',1,'2022-07-19 19:50:14','2022-07-19 19:52:14',1658231413589),(12,66,NULL,1010,1,'我要上网',0,'2022-07-19 19:52:58','2022-07-19 19:53:09',1658231578364),(13,67,NULL,1010,1,'我要上网',0,'2022-07-19 19:53:02','2022-07-19 19:53:09',1658231581548),(14,68,NULL,1010,0,'我要上网',1,'2022-07-19 19:53:05','2022-07-19 19:53:10',1658231585013),(15,69,NULL,1010,1,'我要上网',0,'2022-07-19 19:54:49','2022-07-19 19:55:10',1658231688815),(16,69,NULL,1010,0,'我要上网',1,'2022-07-19 19:57:36','2022-07-19 19:58:20',1658231856339),(17,70,NULL,1010,0,'我要上网',1,'2022-07-19 19:58:50','2022-07-19 19:59:52',1658231930353),(18,71,NULL,1010,0,'我要上网',1,'2022-07-19 19:58:59','2022-07-19 20:28:05',1658231938880),(20,72,NULL,1010,0,'我要上网',1,'2022-07-19 19:59:09','2022-07-19 20:39:25',1658231949483),(21,73,NULL,1010,0,'我要上网',1,'2022-07-19 22:29:34','2022-07-19 22:29:50',1658240974583),(22,74,NULL,1010,0,'申请xxx工单我要上网',1,'2022-07-25 01:39:42','2022-07-25 01:39:42',1658684381765),(23,74,NULL,1010,0,'申请xxx工单我要上网',1,'2022-07-25 01:41:45','2022-07-25 01:41:46',1658684505429),(24,74,NULL,1010,0,'申请xxx工单我要上网',1,'2022-07-25 01:49:30','2022-07-25 01:49:30',1658684970313),(25,74,NULL,1010,0,'申请xxx工单我要上网',1,'2022-07-25 01:53:10','2022-07-25 01:53:10',1658685190079),(26,74,NULL,1010,0,'申请xxx工单我要上网',1,'2022-07-25 01:54:31','2022-07-25 01:54:31',1658685270561),(27,75,NULL,1010,0,'申请xxx工单上网!!!',1,'2022-07-25 07:46:35','2022-07-25 07:46:35',1658706394795),(28,75,NULL,1010,0,'申请xxx工单上网!!!',1,'2022-07-25 08:29:29','2022-07-25 08:29:29',1658708969073),(30,75,NULL,1010,0,'申请xxx工单上网!!!',1,'2022-07-25 08:33:52','2022-07-25 08:33:52',1658709231989),(31,75,NULL,1010,0,'申请xxx工单上网!!!',1,'2022-07-25 08:38:53','2022-07-25 08:38:53',1658709532741),(32,77,NULL,1010,0,'申请xxx工单上网!!!',1,'2022-07-25 14:56:21','2022-07-25 14:56:21',1658732181064);
/*!40000 ALTER TABLE `sys_check_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `detail` varchar(100) DEFAULT NULL COMMENT '申请描述，如果是异常则为异常信息',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作者id',
  `title` varchar(50) DEFAULT NULL COMMENT '标题，如：“紧急调度”、“异常发生”',
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志类，包括操作日志和请求接收日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(64) NOT NULL,
  `path` varchar(255) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(255) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `component` varchar(255) DEFAULT NULL,
  `type` int(5) NOT NULL COMMENT '类型     0：目录   1：菜单   2：按钮',
  `icon` varchar(32) DEFAULT NULL COMMENT '菜单图标',
  `orderNum` int(11) DEFAULT NULL COMMENT '排序',
  `created` datetime NOT NULL,
  `updated` datetime DEFAULT NULL,
  `statu` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理','','sys:manage','',0,'el-icon-s-operation',1,'2021-01-15 18:58:18','2021-01-15 18:58:20',1),(2,1,'用户管理','/sys/users','sys:user:list','sys/User',1,'el-icon-s-custom',1,'2021-01-15 19:03:45','2021-01-15 19:03:48',1),(3,1,'角色管理','/sys/roles','sys:role:list','sys/Role',1,'el-icon-rank',2,'2021-01-15 19:03:45','2021-01-15 19:03:48',1),(4,1,'菜单管理','/sys/menus','sys:menu:list','sys/Menu',1,'el-icon-menu',3,'2021-01-15 19:03:45','2021-01-15 19:03:48',1),(5,0,'系统工具','','sys:tools',NULL,0,'el-icon-s-tools',2,'2021-01-15 19:06:11',NULL,1),(6,5,'数字字典','/sys/dicts','sys:dict:list','sys/Dict',1,'el-icon-s-order',1,'2021-01-15 19:07:18','2021-01-18 16:32:13',1),(7,5,'测逝用','','sys:demo:test','',1,'',8,'2021-01-15 23:02:25','2022-06-21 16:49:55',1),(9,2,'添加用户',NULL,'sys:user:save',NULL,2,NULL,1,'2021-01-17 21:48:32',NULL,1),(10,2,'修改用户',NULL,'sys:user:update',NULL,2,NULL,2,'2021-01-17 21:49:03','2021-01-17 21:53:04',1),(11,2,'删除用户',NULL,'sys:user:delete',NULL,2,NULL,3,'2021-01-17 21:49:21',NULL,1),(12,2,'分配角色',NULL,'sys:user:role',NULL,2,NULL,4,'2021-01-17 21:49:58',NULL,1),(13,2,'重置密码',NULL,'sys:user:repass',NULL,2,NULL,5,'2021-01-17 21:50:36',NULL,1),(14,3,'修改角色',NULL,'sys:role:update',NULL,2,NULL,2,'2021-01-17 21:51:14',NULL,1),(15,3,'删除角色',NULL,'sys:role:delete',NULL,2,NULL,3,'2021-01-17 21:51:39',NULL,1),(16,3,'分配权限',NULL,'sys:role:perm',NULL,2,NULL,5,'2021-01-17 21:52:02',NULL,1),(17,4,'添加菜单',NULL,'sys:menu:save',NULL,2,NULL,1,'2021-01-17 21:53:53','2021-01-17 21:55:28',1),(18,4,'修改菜单',NULL,'sys:menu:update',NULL,2,NULL,2,'2021-01-17 21:56:12',NULL,1),(19,4,'删除菜单',NULL,'sys:menu:delete',NULL,2,NULL,3,'2021-01-17 21:56:36',NULL,1),(21,0,'测逝菜单',NULL,'sys:demo',NULL,0,NULL,1,'2022-06-21 02:37:02','2022-06-21 16:40:36',1),(32,21,'阿桑的歌',' ','asdfgggsdaf',' ',1,' ',1,'2022-06-21 20:08:06','2022-06-21 20:08:22',1),(33,21,'GPU监控',' /gpu','sys:gpu:monitor',' ',1,' ',1,'2022-06-21 20:16:18','2022-06-21 20:18:34',1),(34,21,'asdfa',' ','asdf',' ',1,'  ',1,'2022-06-24 20:18:35',NULL,1),(35,21,'操作日志','/log','sys:op:log','  ',1,' ',1,'2022-06-28 21:46:46',NULL,1),(36,5,'哈哈哈哈哈哈哈','asdf','asdf','asdfa',1,'sadf',1,'2022-07-14 22:13:42',NULL,1);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `code` varchar(64) NOT NULL,
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `statu` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`) USING BTREE,
  UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (3,'普通用户','normal','只有基本查看功能','2021-01-04 10:09:14','2022-06-23 06:41:44',1),(6,'超级管理员','admin','系统默认最高权限，不可以编辑和任意修改','2021-01-16 13:29:03','2021-01-17 15:50:45',1),(7,'操作员','operator','Gpu管理系统的操作员','2022-07-25 16:24:26','2022-07-25 16:24:37',1);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `menu_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=482 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (238,3,1),(239,3,2),(240,3,3),(241,3,4),(242,3,21),(243,3,7),(245,3,5),(246,3,6),(460,6,1),(461,6,2),(462,6,9),(463,6,10),(464,6,11),(465,6,12),(466,6,13),(467,6,3),(468,6,14),(469,6,15),(470,6,16),(471,6,4),(472,6,17),(473,6,18),(474,6,19),(475,6,21),(476,6,34),(477,6,35),(478,6,36),(479,6,5),(480,6,6),(481,6,7);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `wos_id` bigint(20) DEFAULT NULL COMMENT '在工单系统中对应id',
  `studentJobId` varchar(64) DEFAULT NULL COMMENT '学/工号',
  `city` varchar(64) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `statu` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_USERNAME` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$5FvKOKoibLTH5iBlKSSQ8.gwRebZk.XPLDEHZdjIS2DZLNNxRoqwO','https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg',21,'2021011072','广州','2021-01-12 22:13:53','2021-01-16 16:57:32','2020-12-30 08:38:37',1),(2,'test','$2a$10$5FvKOKoibLTH5iBlKSSQ8.gwRebZk.XPLDEHZdjIS2DZLNNxRoqwO','https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg',21,'2021011072',NULL,'2021-01-30 08:20:22','2021-01-30 08:55:57',NULL,1),(3,'1249773167','$2a$10$NyOc4Qk6RzHF.RDgFVGukOeLYtxXKguaBrEfJzn79e8OxUkTt.14C',NULL,21,'2021011072',NULL,'2022-06-20 13:38:25','2022-06-24 20:29:54',NULL,1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (4,1,6),(7,1,3),(13,2,3),(18,3,3),(19,1,7);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_wait_list`
--

DROP TABLE IF EXISTS `sys_wait_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_wait_list` (
  `id` bigint(20) NOT NULL,
  `related_user_id` bigint(20) NOT NULL,
  `wos_id` bigint(20) NOT NULL COMMENT '工单系统中对应id',
  `resource_type` int(11) NOT NULL,
  `apply_days` int(11) NOT NULL,
  `remark` varchar(300) DEFAULT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_wait_list`
--

LOCK TABLES `sys_wait_list` WRITE;
/*!40000 ALTER TABLE `sys_wait_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_wait_list` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-25 20:43:09
