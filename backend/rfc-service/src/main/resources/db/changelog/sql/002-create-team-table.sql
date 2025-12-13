-- Create team table
CREATE TABLE team (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create index on name for faster lookups
CREATE INDEX idx_team_name ON team(name);

-- Add comment to table
COMMENT ON TABLE team IS 'Таблица команд';
COMMENT ON COLUMN team.id IS 'Уникальный идентификатор команды';
COMMENT ON COLUMN team.name IS 'Название команды';
COMMENT ON COLUMN team.description IS 'Описание команды';
COMMENT ON COLUMN team.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN team.update_datetime IS 'Дата и время последнего обновления записи';