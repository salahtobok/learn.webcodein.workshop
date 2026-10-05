UPDATE customer 
SET full_name = CONCAT(first_name, ' ', last_name)
WHERE full_name IS NULL;
