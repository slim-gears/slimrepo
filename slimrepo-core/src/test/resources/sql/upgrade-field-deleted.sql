DROP TABLE IF EXISTS `UserEntity_Backup`
{Params: []}
CREATE TABLE `UserEntity_Backup` AS SELECT * FROM `UserEntity`
{Params: []}
DROP TABLE IF EXISTS `UserEntity`
{Params: []}
CREATE TABLE IF NOT EXISTS `UserEntity` (
    `userId` TEXT PRIMARY KEY,
    `userFirstName` TEXT,
    `userLastName` TEXT,
    `lastVisitDate` INTEGER,
    `role` INTEGER REFERENCES `RoleEntity` (`roleId`),
    `accountStatus` INTEGER,
    `age` INTEGER NOT NULL)
{Params: []}
INSERT INTO `UserEntity` (`userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`, `accountStatus`, `age`)
SELECT `userId`, `userFirstName`, `userLastName`, `lastVisitDate`, `role`, `accountStatus`, `age` FROM `UserEntity_Backup`
{Params: []}
