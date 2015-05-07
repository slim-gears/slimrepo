SELECT COUNT(*)
FROM `UserEntity`
WHERE `UserEntity`.`userFirstName` LIKE ?
LIMIT 10 OFFSET 2

{Params: [%John%]}