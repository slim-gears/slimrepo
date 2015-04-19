SELECT COUNT(*)
FROM `UserEntity`
WHERE `userFirstName` LIKE ?
LIMIT 10 OFFSET 2

{Params: [%John%]}