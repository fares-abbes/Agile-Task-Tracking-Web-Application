-- SQL script to fix the role check constraint
-- This will drop the current check constraint and create a new one that matches your Role enum

-- First, identify the exact constraint name (if you don't know it already)
SELECT conname 
FROM pg_constraint 
WHERE conrelid = 'users'::regclass AND contype = 'c' AND conname LIKE '%role%';

-- Then drop the constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Create a new constraint that accepts your enum values
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'TESTER', 'DEVELOPPER', 'TEAMLEAD'));

-- If the above doesn't work, you might need to modify your enum values to match what's expected in the database
-- To find out what values are currently accepted, run:
SELECT column_name, check_clause
FROM information_schema.check_constraints cc
JOIN information_schema.constraint_column_usage ccu 
ON cc.constraint_name = ccu.constraint_name
WHERE ccu.table_name = 'users' AND ccu.column_name = 'role';
