-- SQL script to show the exact values accepted by the role constraint
SELECT pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'users'::regclass AND contype = 'c' AND conname = 'users_role_check';
