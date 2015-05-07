SELECT
    `UserEntity`.`userFirstName` AS `UserEntity_userFirstName`,
    `UserEntity`.`userLastName` AS `UserEntity_userLastName`
FROM `UserEntity`
WHERE `UserEntity`.`userFirstName` IN (?, ?)

{Params: [John, Jake]}
