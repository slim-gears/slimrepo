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
WHERE `RoleEntity`.`roleDescription` IN (?)

{Params: [Admin]}
