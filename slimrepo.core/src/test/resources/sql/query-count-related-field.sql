SELECT COUNT(*)
FROM `UserEntity`
JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` IN (?)

{Params: [Admin]}
