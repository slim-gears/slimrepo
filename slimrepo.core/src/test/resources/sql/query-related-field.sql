SELECT `userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`
FROM `UserEntity`
INNER JOIN `RoleEntity` ON `role` = `roleId`
WHERE ((`userFirstName` LIKE ?) AND (`userId` > ?)) OR (`userLastName` LIKE ?)

{Params: [%John%, 20, Smi%]}