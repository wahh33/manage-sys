/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.5.62 : Database - business_management
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`business_management` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `business_management`;

/*Table structure for table `client` */

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `phoneNum` varchar(20) NOT NULL,
  `eMail` varchar(50) NOT NULL,
  `address` varchar(50) DEFAULT NULL,
  `imgUrl` varchar(200) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `discount` double NOT NULL COMMENT '折扣，例:7.5折',
  `type` int(11) NOT NULL COMMENT '0是供应商，1是客户',
  `deleted` bit(1) NOT NULL COMMENT '0:未删除，1:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `client` */

insert  into `client`(`id`,`name`,`phoneNum`,`eMail`,`address`,`imgUrl`,`description`,`discount`,`type`,`deleted`) values (1,'物料供应商','13511111111','1@1.com',NULL,NULL,NULL,10,0,'\0');

/*Table structure for table `goods` */

DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` int(11) NOT NULL COMMENT '0是物料，1是产品，2是物料+产品',
  `defaultPrice` double NOT NULL,
  `count` bigint(20) NOT NULL,
  `high` bigint(20) DEFAULT NULL,
  `low` bigint(20) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `imgUrl` varchar(200) DEFAULT NULL,
  `isWarn` bit(1) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `state` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `goods` */

insert  into `goods`(`id`,`name`,`type`,`defaultPrice`,`count`,`high`,`low`,`deleted`,`imgUrl`,`isWarn`,`description`,`state`) values (1,'手机',1,1000,0,NULL,10,'\0',NULL,'',NULL,1),(2,'芯片',0,100,15,10,NULL,'\0',NULL,'',NULL,2);

/*Table structure for table `need_plan` */

DROP TABLE IF EXISTS `need_plan`;

CREATE TABLE `need_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creatorId` bigint(20) NOT NULL,
  `surerId` bigint(20) NOT NULL,
  `level` int(11) NOT NULL COMMENT '紧急程度0最低',
  `startTime` date NOT NULL,
  `endTime` date NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `needPlanType` int(11) NOT NULL COMMENT '0是需求，1是计划',
  `buySellType` int(11) NOT NULL COMMENT '0是采购，1是生产，2是销售',
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `syncTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `need_plan` */

/*Table structure for table `need_plan_detail` */

DROP TABLE IF EXISTS `need_plan_detail`;

CREATE TABLE `need_plan_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goodsId` bigint(20) NOT NULL,
  `needPlanId` bigint(20) NOT NULL,
  `count` bigint(20) DEFAULT NULL,
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `finishCount` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `need_plan_detail` */

/*Table structure for table `order` */

DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `clientId` bigint(20) NOT NULL,
  `createTime` date NOT NULL,
  `type` int(11) NOT NULL COMMENT '0是物料订单，1是产品订单',
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `totalPrice` double DEFAULT NULL,
  `finishPrice` double DEFAULT NULL,
  `isReturn` bit(1) NOT NULL COMMENT '是否退货单',
  `syncTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `order` */

/*Table structure for table `order_detail` */

DROP TABLE IF EXISTS `order_detail`;

CREATE TABLE `order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `goodsId` bigint(20) NOT NULL,
  `count` bigint(20) NOT NULL,
  `finishCount` bigint(20) NOT NULL,
  `price` double DEFAULT NULL,
  `npDetailIds` varchar(500) NOT NULL,
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `order_detail` */

/*Table structure for table `pay_record` */

DROP TABLE IF EXISTS `pay_record`;

CREATE TABLE `pay_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `orderId` bigint(20) NOT NULL,
  `totalPrice` double NOT NULL,
  `proofUrl` varchar(200) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `createTime` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `pay_record` */

/*Table structure for table `store_apply` */

DROP TABLE IF EXISTS `store_apply`;

CREATE TABLE `store_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `entityId` bigint(20) DEFAULT NULL,
  `needPlanOrderType` int(11) NOT NULL COMMENT '0是需求，1是计划，2是订单',
  `inOutType` int(11) NOT NULL COMMENT '0是入，1是出',
  `createTime` date NOT NULL,
  `proofUrl` varchar(200) DEFAULT NULL,
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `syncTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `store_apply` */

/*Table structure for table `store_apply_detail` */

DROP TABLE IF EXISTS `store_apply_detail`;

CREATE TABLE `store_apply_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goodsId` bigint(20) NOT NULL,
  `storeApplyId` bigint(20) NOT NULL,
  `preInOutCount` bigint(20) NOT NULL,
  `reaInOutCount` bigint(20) DEFAULT NULL,
  `state` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `store_apply_detail` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `password` varchar(20) NOT NULL COMMENT '密码',
  `phoneNum` varchar(20) NOT NULL COMMENT '手机号码',
  `eMail` varchar(50) NOT NULL COMMENT '邮箱',
  `imgUrl` varchar(200) DEFAULT NULL,
  `deleted` bit(1) NOT NULL COMMENT '0:未删除，1:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`name`,`role`,`password`,`phoneNum`,`eMail`,`imgUrl`,`deleted`) values (1,'admin','admin','123456','13511111111','111@111.com',NULL,'\0'),(2,'小销','seller','123456','13544444444','444@444.com',NULL,'\0'),(3,'小生','producer','123456','13533333333','333@333.com',NULL,'\0'),(4,'小采','buyer','123456','13522222222','222@222.com',NULL,'\0'),(5,'小库','storeman','123456','13555555555','555@555.com',NULL,'\0'),(6,'小管','manager','123456','13566666666','666@666.com',NULL,'\0');

/*Table structure for table `vw_client_order_count` */

DROP TABLE IF EXISTS `vw_client_order_count`;

/*!50001 DROP VIEW IF EXISTS `vw_client_order_count` */;
/*!50001 DROP TABLE IF EXISTS `vw_client_order_count` */;

/*!50001 CREATE TABLE  `vw_client_order_count`(
 `clientId` bigint(20) ,
 `clientName` varchar(50) ,
 `discount` double ,
 `clientType` int(11) ,
 `orderCount` decimal(23,0) ,
 `returnCount` decimal(23,0) ,
 `priceCount` double ,
 `state` int(11) ,
 `deleted` bit(1) 
)*/;

/*Table structure for table `vw_need_plan` */

DROP TABLE IF EXISTS `vw_need_plan`;

/*!50001 DROP VIEW IF EXISTS `vw_need_plan` */;
/*!50001 DROP TABLE IF EXISTS `vw_need_plan` */;

/*!50001 CREATE TABLE  `vw_need_plan`(
 `id` bigint(20) ,
 `creatorId` bigint(20) ,
 `level` int(11) ,
 `startTime` date ,
 `endTime` date ,
 `description` varchar(200) ,
 `needPlanType` int(11) ,
 `buySellType` int(11) ,
 `state` int(11) ,
 `deleted` bit(1) ,
 `surerId` bigint(20) ,
 `syncTime` bigint(20) ,
 `creatorName` varchar(50) ,
 `surerName` varchar(50) 
)*/;

/*Table structure for table `vw_need_plan_detail` */

DROP TABLE IF EXISTS `vw_need_plan_detail`;

/*!50001 DROP VIEW IF EXISTS `vw_need_plan_detail` */;
/*!50001 DROP TABLE IF EXISTS `vw_need_plan_detail` */;

/*!50001 CREATE TABLE  `vw_need_plan_detail`(
 `id` bigint(20) ,
 `goodsId` bigint(20) ,
 `needPlanId` bigint(20) ,
 `count` bigint(20) ,
 `finishCount` bigint(20) ,
 `state` int(11) ,
 `deleted` bit(1) ,
 `goodsName` varchar(50) ,
 `goodsCount` bigint(20) ,
 `defaultPrice` double ,
 `goodsType` int(11) ,
 `buySellType` int(11) ,
 `needPlanType` int(11) ,
 `npState` int(11) 
)*/;

/*Table structure for table `vw_order` */

DROP TABLE IF EXISTS `vw_order`;

/*!50001 DROP VIEW IF EXISTS `vw_order` */;
/*!50001 DROP TABLE IF EXISTS `vw_order` */;

/*!50001 CREATE TABLE  `vw_order`(
 `id` bigint(20) ,
 `userId` bigint(20) ,
 `clientId` bigint(20) ,
 `createTime` date ,
 `type` int(11) ,
 `state` int(11) ,
 `isReturn` bit(1) ,
 `deleted` bit(1) ,
 `totalPrice` double ,
 `finishPrice` double ,
 `syncTime` bigint(20) ,
 `creatorName` varchar(50) ,
 `clientName` varchar(50) ,
 `discount` double 
)*/;

/*Table structure for table `vw_order_detail` */

DROP TABLE IF EXISTS `vw_order_detail`;

/*!50001 DROP VIEW IF EXISTS `vw_order_detail` */;
/*!50001 DROP TABLE IF EXISTS `vw_order_detail` */;

/*!50001 CREATE TABLE  `vw_order_detail`(
 `id` bigint(20) ,
 `orderId` bigint(20) ,
 `goodsId` bigint(20) ,
 `count` bigint(20) ,
 `finishCount` bigint(20) ,
 `price` double ,
 `npDetailIds` varchar(500) ,
 `state` int(11) ,
 `deleted` bit(1) ,
 `orderType` int(11) ,
 `isReturn` bit(1) ,
 `goodsName` varchar(50) ,
 `defaultPrice` double ,
 `goodsCount` bigint(20) 
)*/;

/*Table structure for table `vw_pay_record` */

DROP TABLE IF EXISTS `vw_pay_record`;

/*!50001 DROP VIEW IF EXISTS `vw_pay_record` */;
/*!50001 DROP TABLE IF EXISTS `vw_pay_record` */;

/*!50001 CREATE TABLE  `vw_pay_record`(
 `id` bigint(20) ,
 `userId` bigint(20) ,
 `orderId` bigint(20) ,
 `totalPrice` double ,
 `proofUrl` varchar(200) ,
 `createTime` date ,
 `deleted` bit(1) ,
 `creatorName` varchar(50) ,
 `isReturn` bit(1) ,
 `orderType` int(11) ,
 `clientId` bigint(20) 
)*/;

/*Table structure for table `vw_store_apply` */

DROP TABLE IF EXISTS `vw_store_apply`;

/*!50001 DROP VIEW IF EXISTS `vw_store_apply` */;
/*!50001 DROP TABLE IF EXISTS `vw_store_apply` */;

/*!50001 CREATE TABLE  `vw_store_apply`(
 `id` bigint(20) ,
 `userId` bigint(20) ,
 `entityId` bigint(20) ,
 `needPlanOrderType` int(11) ,
 `inOutType` int(11) ,
 `createTime` date ,
 `proofUrl` varchar(200) ,
 `state` int(11) ,
 `deleted` bit(1) ,
 `syncTime` bigint(20) ,
 `creatorName` varchar(50) 
)*/;

/*Table structure for table `vw_store_apply_detail` */

DROP TABLE IF EXISTS `vw_store_apply_detail`;

/*!50001 DROP VIEW IF EXISTS `vw_store_apply_detail` */;
/*!50001 DROP TABLE IF EXISTS `vw_store_apply_detail` */;

/*!50001 CREATE TABLE  `vw_store_apply_detail`(
 `id` bigint(20) ,
 `goodsId` bigint(20) ,
 `storeApplyId` bigint(20) ,
 `preInOutCount` bigint(20) ,
 `reaInOutCount` bigint(20) ,
 `state` int(11) ,
 `deleted` bit(1) ,
 `inOutType` int(11) ,
 `createTime` date ,
 `goodsName` varchar(50) ,
 `goodsCount` bigint(20) ,
 `goodsType` int(11) 
)*/;

/*View structure for view vw_client_order_count */

/*!50001 DROP TABLE IF EXISTS `vw_client_order_count` */;
/*!50001 DROP VIEW IF EXISTS `vw_client_order_count` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_client_order_count` AS (select `vw_order`.`clientId` AS `clientId`,`vw_order`.`clientName` AS `clientName`,`vw_order`.`discount` AS `discount`,`vw_order`.`type` AS `clientType`,sum(if(`vw_order`.`isReturn`,0,1)) AS `orderCount`,sum(if(`vw_order`.`isReturn`,1,0)) AS `returnCount`,sum(if(`vw_order`.`isReturn`,(0 - `vw_order`.`finishPrice`),`vw_order`.`finishPrice`)) AS `priceCount`,`vw_order`.`state` AS `state`,`vw_order`.`deleted` AS `deleted` from `vw_order` group by `vw_order`.`clientId` having (((`vw_order`.`state` = 2) or (`vw_order`.`state` = 1)) and (`vw_order`.`deleted` = 0))) */;

/*View structure for view vw_need_plan */

/*!50001 DROP TABLE IF EXISTS `vw_need_plan` */;
/*!50001 DROP VIEW IF EXISTS `vw_need_plan` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_need_plan` AS (select `np`.`id` AS `id`,`np`.`creatorId` AS `creatorId`,`np`.`level` AS `level`,`np`.`startTime` AS `startTime`,`np`.`endTime` AS `endTime`,`np`.`description` AS `description`,`np`.`needPlanType` AS `needPlanType`,`np`.`buySellType` AS `buySellType`,`np`.`state` AS `state`,`np`.`deleted` AS `deleted`,`np`.`surerId` AS `surerId`,`np`.`syncTime` AS `syncTime`,`creator`.`name` AS `creatorName`,`surer`.`name` AS `surerName` from ((`need_plan` `np` join `user` `creator` on((`np`.`creatorId` = `creator`.`id`))) left join `user` `surer` on((`np`.`surerId` = `surer`.`id`)))) */;

/*View structure for view vw_need_plan_detail */

/*!50001 DROP TABLE IF EXISTS `vw_need_plan_detail` */;
/*!50001 DROP VIEW IF EXISTS `vw_need_plan_detail` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_need_plan_detail` AS (select `npd`.`id` AS `id`,`npd`.`goodsId` AS `goodsId`,`npd`.`needPlanId` AS `needPlanId`,`npd`.`count` AS `count`,`npd`.`finishCount` AS `finishCount`,`npd`.`state` AS `state`,`npd`.`deleted` AS `deleted`,`g`.`name` AS `goodsName`,`g`.`count` AS `goodsCount`,`g`.`defaultPrice` AS `defaultPrice`,`g`.`type` AS `goodsType`,`np`.`buySellType` AS `buySellType`,`np`.`needPlanType` AS `needPlanType`,`np`.`state` AS `npState` from ((`need_plan_detail` `npd` join `goods` `g` on((`npd`.`goodsId` = `g`.`id`))) join `need_plan` `np` on((`npd`.`needPlanId` = `np`.`id`)))) */;

/*View structure for view vw_order */

/*!50001 DROP TABLE IF EXISTS `vw_order` */;
/*!50001 DROP VIEW IF EXISTS `vw_order` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_order` AS (select `o`.`id` AS `id`,`o`.`userId` AS `userId`,`o`.`clientId` AS `clientId`,`o`.`createTime` AS `createTime`,`o`.`type` AS `type`,`o`.`state` AS `state`,`o`.`isReturn` AS `isReturn`,`o`.`deleted` AS `deleted`,`o`.`totalPrice` AS `totalPrice`,`o`.`finishPrice` AS `finishPrice`,`o`.`syncTime` AS `syncTime`,`u`.`name` AS `creatorName`,`c`.`name` AS `clientName`,`c`.`discount` AS `discount` from ((`order` `o` join `user` `u` on((`o`.`userId` = `u`.`id`))) join `client` `c` on((`o`.`clientId` = `c`.`id`)))) */;

/*View structure for view vw_order_detail */

/*!50001 DROP TABLE IF EXISTS `vw_order_detail` */;
/*!50001 DROP VIEW IF EXISTS `vw_order_detail` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_order_detail` AS (select `od`.`id` AS `id`,`od`.`orderId` AS `orderId`,`od`.`goodsId` AS `goodsId`,`od`.`count` AS `count`,`od`.`finishCount` AS `finishCount`,`od`.`price` AS `price`,`od`.`npDetailIds` AS `npDetailIds`,`od`.`state` AS `state`,`od`.`deleted` AS `deleted`,`o`.`type` AS `orderType`,`o`.`isReturn` AS `isReturn`,`g`.`name` AS `goodsName`,`g`.`defaultPrice` AS `defaultPrice`,`g`.`count` AS `goodsCount` from ((`order_detail` `od` join `goods` `g` on((`od`.`goodsId` = `g`.`id`))) join `order` `o` on((`od`.`orderId` = `o`.`id`)))) */;

/*View structure for view vw_pay_record */

/*!50001 DROP TABLE IF EXISTS `vw_pay_record` */;
/*!50001 DROP VIEW IF EXISTS `vw_pay_record` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_pay_record` AS (select `p`.`id` AS `id`,`p`.`userId` AS `userId`,`p`.`orderId` AS `orderId`,`p`.`totalPrice` AS `totalPrice`,`p`.`proofUrl` AS `proofUrl`,`p`.`createTime` AS `createTime`,`p`.`deleted` AS `deleted`,`u`.`name` AS `creatorName`,`o`.`isReturn` AS `isReturn`,`o`.`type` AS `orderType`,`o`.`clientId` AS `clientId` from ((`pay_record` `p` join `user` `u` on((`p`.`userId` = `u`.`id`))) join `order` `o` on((`p`.`orderId` = `o`.`id`)))) */;

/*View structure for view vw_store_apply */

/*!50001 DROP TABLE IF EXISTS `vw_store_apply` */;
/*!50001 DROP VIEW IF EXISTS `vw_store_apply` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_store_apply` AS (select `s`.`id` AS `id`,`s`.`userId` AS `userId`,`s`.`entityId` AS `entityId`,`s`.`needPlanOrderType` AS `needPlanOrderType`,`s`.`inOutType` AS `inOutType`,`s`.`createTime` AS `createTime`,`s`.`proofUrl` AS `proofUrl`,`s`.`state` AS `state`,`s`.`deleted` AS `deleted`,`s`.`syncTime` AS `syncTime`,`u`.`name` AS `creatorName` from (`store_apply` `s` join `user` `u` on((`s`.`userId` = `u`.`id`)))) */;

/*View structure for view vw_store_apply_detail */

/*!50001 DROP TABLE IF EXISTS `vw_store_apply_detail` */;
/*!50001 DROP VIEW IF EXISTS `vw_store_apply_detail` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_store_apply_detail` AS (select `sad`.`id` AS `id`,`sad`.`goodsId` AS `goodsId`,`sad`.`storeApplyId` AS `storeApplyId`,`sad`.`preInOutCount` AS `preInOutCount`,`sad`.`reaInOutCount` AS `reaInOutCount`,`sad`.`state` AS `state`,`sad`.`deleted` AS `deleted`,`sa`.`inOutType` AS `inOutType`,`sa`.`createTime` AS `createTime`,`g`.`name` AS `goodsName`,`g`.`count` AS `goodsCount`,`g`.`type` AS `goodsType` from ((`store_apply_detail` `sad` join `goods` `g` on((`sad`.`goodsId` = `g`.`id`))) join `store_apply` `sa` on((`sad`.`storeApplyId` = `sa`.`id`)))) */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
