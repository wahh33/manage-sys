DELIMITER $$

ALTER ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_client_order_count` AS (
SELECT
  `vw_order`.`clientId`   AS `clientId`,
  `vw_order`.`clientName` AS `clientName`,
  `vw_order`.`discount`   AS `discount`,
  `vw_order`.`type`       AS `clientType`,
  SUM(IF(`vw_order`.`isReturn`,0,1)) AS `orderCount`,
  SUM(IF(`vw_order`.`isReturn`,1,0)) AS `returnCount`,
  SUM(IF(`vw_order`.`isReturn`,(0 - `vw_order`.`finishPrice`),`vw_order`.`finishPrice`)) AS `priceCount`,
  `vw_order`.`state`      AS `state`,
  `vw_order`.`deleted`    AS `deleted`
FROM `vw_order`
GROUP BY `vw_order`.`clientId`
HAVING (((`vw_order`.`state` = 2)
          OR (`vw_order`.`state` = 1))
        AND (`vw_order`.`deleted` = 0)))$$

DELIMITER ;