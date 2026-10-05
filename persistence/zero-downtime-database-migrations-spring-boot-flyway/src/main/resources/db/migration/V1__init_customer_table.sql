CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100)
);

-- Insert some legacy data
INSERT INTO customer (email, first_name, last_name) VALUES ('john.doe@example.com', 'John', 'Doe');
INSERT INTO customer (email, first_name, last_name) VALUES ('jane.smith@example.com', 'Jane', 'Smith');
