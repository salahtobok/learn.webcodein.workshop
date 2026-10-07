-- V1__init.sql: Create schema and enable Row-Level Security

CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING'
);

-- Enable RLS on the table
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

-- Create policy to isolate tenants
-- We use current_setting('app.current_tenant', true) to read the session variable.
-- The 'true' parameter means it returns NULL instead of throwing an error if not set.
CREATE POLICY tenant_isolation_policy ON tasks
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant', true));

-- Flyway runs as the same user, we must ensure the owner/superuser is subject to RLS if we want it strictly applied.
ALTER TABLE tasks FORCE ROW LEVEL SECURITY;
