SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`userLastName` IS NULL

{Params: []}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`userFirstName` IS NOT NULL

{Params: []}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` = ?

{Params: [1]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` <> ?

{Params: [0]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` IN (?, ?)

{Params: [1, 2]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` IN (?, ?)

{Params: [1, 2]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` NOT IN (?, ?)

{Params: [0, 2]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`accountStatus` NOT IN (?, ?)

{Params: [0, 2]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`lastVisitDate` BETWEEN ? AND ?

{Params: [949363200000, 949449600000]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`lastVisitDate` >= ?

{Params: [949363200000]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`lastVisitDate` <= ?

{Params: [949449600000]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`lastVisitDate` > ?

{Params: [949363200000]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`lastVisitDate` < ?

{Params: [949449600000]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` LIKE ?

{Params: [A%]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` LIKE ?

{Params: [%B]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` LIKE ?

{Params: [%C%]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`userFirstName` NOT LIKE ?

{Params: [A%]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`userFirstName` NOT LIKE ?

{Params: [%B]}
SELECT
    `UserEntity`.`userId` AS `UserEntity_userId`,
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`,
    `UserEntity`.`lastVisitDate` AS `UserEntity_lastVisitDate`,
    `UserEntity`.`role` AS `UserEntity_role`,
    `UserEntity`.`accountStatus` AS `UserEntity_accountStatus`,
    `UserEntity`.`comments` AS `UserEntity_comments`,
    `UserEntity`.`age` AS `UserEntity_age`,
    `RoleEntity`.`roleId` AS `RoleEntity_roleId`,
    `RoleEntity`.`roleDescription` AS `RoleEntity_roleDescription`
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `UserEntity`.`userFirstName` NOT LIKE ?

{Params: [%C%]}