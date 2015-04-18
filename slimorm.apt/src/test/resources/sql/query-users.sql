SELECT `userId`, `userFirstName`, `userLastName`
FROM `UserEntity`
WHERE ((`userFirstName` LIKE ?) AND (`userId` > ?)) OR (`userLastName` LIKE ?)
ORDER BY `userLastName` ASC, `userFirstName` ASC, `userId` ASC
LIMIT 10 OFFSET 3

[Params: {@p1=%John%, @p2=20, @p3=Smi%}]