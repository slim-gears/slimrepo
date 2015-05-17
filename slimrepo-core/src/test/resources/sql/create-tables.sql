CREATE TABLE IF NOT EXISTS `RoleEntity` (
    `roleId` INTEGER PRIMARY KEY ASC,
    `roleDescription` TEXT)
{Params: []}
CREATE TABLE IF NOT EXISTS `UserEntity` (
    `userId` INTEGER PRIMARY KEY ASC,
    `userFirstName` TEXT,
    `userLastName` TEXT,
    `lastVisitDate` INTEGER,
    `role` INTEGER REFERENCES `RoleEntity` (`roleId`),
    `accountStatus` INTEGER,
    `comments` BLOB)
{Params: []}
