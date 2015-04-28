SELECT COUNT(*)
FROM `UserEntity`
LEFT JOIN `RoleEntity` ON `UserEntity`.`role` = `RoleEntity`.`roleId`
WHERE `RoleEntity`.`roleDescription` IN (?)

{Params: [Admin]}
