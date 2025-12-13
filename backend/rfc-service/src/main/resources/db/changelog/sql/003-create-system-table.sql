-- Create system table
CREATE TABLE system (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create index on name for faster lookups
CREATE INDEX idx_system_name ON system(name);

-- Add comment to table
COMMENT ON TABLE system IS 'Таблица систем';
COMMENT ON COLUMN system.id IS 'Уникальный идентификатор системы';
COMMENT ON COLUMN system.name IS 'Название системы';
COMMENT ON COLUMN system.description IS 'Описание системы';
COMMENT ON COLUMN system.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN system.update_datetime IS 'Дата и время последнего обновления записи';