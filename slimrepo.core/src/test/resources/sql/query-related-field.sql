SELECT `UserEntity`.`userId`, `UserEntity`.`userFirstName`, `UserEntity`.`userLastName`, `UserEntity`.`lastVisitDate`, `UserEntity`.`role`, `RoleEntity`.`roleId`, `RoleEntity`.`roleDescription`
FROM `UserEntity`
JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` IN (?)

{Params: [Admin]}
