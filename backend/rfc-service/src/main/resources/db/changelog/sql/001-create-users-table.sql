-- Create users table
CREATE TABLE "users" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON "users"(username);

-- Create index on role for filtering
CREATE INDEX idx_users_role ON "users"(role);

-- Add check constraint for role enum
ALTER TABLE "users" ADD CONSTRAINT chk_users_role
    CHECK (role IN ('REQUESTER', 'EXECUTOR', 'CAB_MANAGER', 'ADMIN'));

-- Add comment to table
COMMENT ON TABLE "users" IS 'Таблица пользователей системы';
COMMENT ON COLUMN "users".id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN "users".username IS 'Имя пользователя (уникальное)';
COMMENT ON COLUMN "users".first_name IS 'Имя пользователя';
COMMENT ON COLUMN "users".last_name IS 'Фамилия пользователя';
COMMENT ON COLUMN "users".role IS 'Роль пользователя в системе';
COMMENT ON COLUMN "users".create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN "users".update_datetime IS 'Дата и время последнего обновления записи';
