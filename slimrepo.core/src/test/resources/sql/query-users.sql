SELECT `UserEntity`.`userId`, `UserEntity`.`userFirstName`, `UserEntity`.`userLastName`, `UserEntity`.`lastVisitDate`, `UserEntity`.`role`, `RoleEntity`.`roleId`, `RoleEntity`.`roleDescription`
FROM `UserEntity`
JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE ((`UserEntity`.`userFirstName` LIKE ?) AND (`UserEntity`.`userId` > ?)) OR (`UserEntity`.`userLastName` LIKE ?)
ORDER BY `UserEntity`.`userLastName` ASC, `UserEntity`.`userFirstName` ASC, `UserEntity`.`userId` ASC
LIMIT 10 OFFSET 3

{Params: [%John%, 20, Smi%]}