-- Create rfc_affected_system table
CREATE TABLE rfc_affected_system (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    system_id BIGINT NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_affected_system_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_affected_system_system FOREIGN KEY (system_id) REFERENCES system(id) ON DELETE RESTRICT,
    CONSTRAINT uk_rfc_affected_system_rfc_system UNIQUE (rfc_id, system_id)
);

-- Create indexes
CREATE INDEX idx_rfc_affected_system_rfc_id ON rfc_affected_system(rfc_id);
CREATE INDEX idx_rfc_affected_system_system_id ON rfc_affected_system(system_id);

-- Add comments
COMMENT ON TABLE rfc_affected_system IS 'Таблица связи RFC с затронутыми системами';
COMMENT ON COLUMN rfc_affected_system.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN rfc_affected_system.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_affected_system.system_id IS 'ID затронутой системы';
COMMENT ON COLUMN rfc_affected_system.create_datetime IS 'Дата и время создания записи';