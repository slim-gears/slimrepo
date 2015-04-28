SELECT `userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`
FROM `UserEntity`
WHERE ((`userFirstName` LIKE ?) AND (`userId` > ?)) OR (`userLastName` LIKE ?)
ORDER BY `userLastName` ASC, `userFirstName` ASC, `userId` ASC
LIMIT 10 OFFSET 3

{Params: [%John%, 20, Smi%]}