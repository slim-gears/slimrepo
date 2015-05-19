CREATE TABLE IF NOT EXISTS `RoleEntity` (
    `roleId` INTEGER PRIMARY KEY ASC,
    `roleDescription` TEXT);

CREATE TABLE IF NOT EXISTS `UserEntity` (
    `userId` INTEGER PRIMARY KEY ASC,
    `userFirstName` TEXT,
    `lastVisitDate` INTEGER,
    `role` INTEGER REFERENCES `RoleEntity` (`roleId`),
    `accountStatus` INTEGER,
    `comments` BLOB);

INSERT OR REPLACE INTO `RoleEntity` (`roleId`,`roleDescription`)
VALUES
(1, 'Role1'),
(2, 'Role2'),
(3, 'Role3'),
(4, 'Role4');

INSERT OR REPLACE INTO `UserEntity` (`userId`,`userFirstName`,`lastVisitDate`,`role`,`accountStatus`)
VALUES
(1, 'FirstName1',1,1,1),
(2, 'FirstName2',1,2,1),
(3, 'FirstName3',1,3,1),
(4, 'FirstName4',1,4,1),
(5, 'FirstName5',1,1,1),
(6, 'FirstName6',1,2,1),
(7, 'FirstName7',1,3,1),
(8, 'FirstName8',1,4,1);
