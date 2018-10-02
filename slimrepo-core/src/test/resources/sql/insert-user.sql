INSERT INTO `UserEntity` (`userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`, `accountStatus`, `comments`, `age`)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
@wildcard({Params: [*, John, Doe, NULL, 0, NULL, NULL, 5]})
