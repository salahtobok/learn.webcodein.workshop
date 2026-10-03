CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    tenant_id VARCHAR(50) NOT NULL
);

-- Enable Row Level Security on the table
ALTER TABLE documents ENABLE ROW LEVEL SECURITY;

-- Create policy to ensure users can only access their own tenant's data
-- We use current_setting('rls.tenant_id') to read the session variable
CREATE POLICY tenant_isolation_policy ON documents
    USING (tenant_id = current_setting('rls.tenant_id', true));

-- The default Postgres user (myuser) is a superuser in testcontainers or owner
-- Owners bypass RLS by default. To force RLS even for the owner, use FORCE ROW LEVEL SECURITY.
ALTER TABLE documents FORCE ROW LEVEL SECURITY;
