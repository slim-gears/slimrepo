DROP TABLE IF EXISTS `UserEntity_Backup`
{Params: []}
CREATE TABLE `UserEntity_Backup` AS SELECT * FROM `UserEntity`
{Params: []}
DROP TABLE IF EXISTS `UserEntity`
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
INSERT INTO `UserEntity` (`userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`, `accountStatus`)
SELECT `userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`, `accountStatus` FROM `UserEntity_Backup`
{Params: []}
