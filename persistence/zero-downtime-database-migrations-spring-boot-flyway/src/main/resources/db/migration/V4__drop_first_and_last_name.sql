ALTER TABLE customer DROP COLUMN first_name;
ALTER TABLE customer DROP COLUMN last_name;

-- Finally, make the new column not null since all data is migrated
ALTER TABLE customer ALTER COLUMN full_name SET NOT NULL;
