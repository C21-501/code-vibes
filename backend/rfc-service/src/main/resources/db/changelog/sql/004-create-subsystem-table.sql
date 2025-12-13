-- Create subsystem table
CREATE TABLE subsystem (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    system_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_subsystem_system FOREIGN KEY (system_id) REFERENCES system(id) ON DELETE CASCADE,
    CONSTRAINT fk_subsystem_team FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE RESTRICT
);

-- Create indexes for foreign keys
CREATE INDEX idx_subsystem_system_id ON subsystem(system_id);
CREATE INDEX idx_subsystem_team_id ON subsystem(team_id);

-- Create index on name for faster lookups
CREATE INDEX idx_subsystem_name ON subsystem(name);

-- Add comment to table
COMMENT ON TABLE subsystem IS 'Таблица подсистем';
COMMENT ON COLUMN subsystem.id IS 'Уникальный идентификатор подсистемы';
COMMENT ON COLUMN subsystem.name IS 'Название подсистемы';
COMMENT ON COLUMN subsystem.description IS 'Описание подсистемы';
COMMENT ON COLUMN subsystem.system_id IS 'ID родительской системы';
COMMENT ON COLUMN subsystem.team_id IS 'ID ответственной команды';
COMMENT ON COLUMN subsystem.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN subsystem.update_datetime IS 'Дата и время последнего обновления записи';