-- Create rfc_affected_subsystem table
CREATE TABLE rfc_affected_subsystem (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    subsystem_id BIGINT NOT NULL,
    executor_id BIGINT NOT NULL,
    confirmation_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    execution_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_affected_subsystem_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_affected_subsystem_subsystem FOREIGN KEY (subsystem_id) REFERENCES subsystem(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rfc_affected_subsystem_executor FOREIGN KEY (executor_id) REFERENCES "users"(id) ON DELETE RESTRICT,
    CONSTRAINT uk_rfc_affected_subsystem_rfc_subsystem UNIQUE (rfc_id, subsystem_id)
);

-- Create indexes
CREATE INDEX idx_rfc_affected_subsystem_rfc_id ON rfc_affected_subsystem(rfc_id);
CREATE INDEX idx_rfc_affected_subsystem_subsystem_id ON rfc_affected_subsystem(subsystem_id);
CREATE INDEX idx_rfc_affected_subsystem_executor_id ON rfc_affected_subsystem(executor_id);
CREATE INDEX idx_rfc_affected_subsystem_confirmation_status ON rfc_affected_subsystem(confirmation_status);
CREATE INDEX idx_rfc_affected_subsystem_execution_status ON rfc_affected_subsystem(execution_status);

-- Add check constraint for confirmation_status enum
ALTER TABLE rfc_affected_subsystem ADD CONSTRAINT chk_rfc_affected_subsystem_confirmation_status
    CHECK (confirmation_status IN ('PENDING', 'CONFIRMED', 'REJECTED'));

-- Add check constraint for execution_status enum
ALTER TABLE rfc_affected_subsystem ADD CONSTRAINT chk_rfc_affected_subsystem_execution_status
    CHECK (execution_status IN ('PENDING', 'IN_PROGRESS', 'DONE'));

-- Add comments
COMMENT ON TABLE rfc_affected_subsystem IS 'Таблица связи RFC с затронутыми подсистемами и исполнителями';
COMMENT ON COLUMN rfc_affected_subsystem.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN rfc_affected_subsystem.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_affected_subsystem.subsystem_id IS 'ID затронутой подсистемы';
COMMENT ON COLUMN rfc_affected_subsystem.executor_id IS 'ID исполнителя из команды подсистемы';
COMMENT ON COLUMN rfc_affected_subsystem.confirmation_status IS 'Статус подтверждения: PENDING, CONFIRMED, REJECTED';
COMMENT ON COLUMN rfc_affected_subsystem.execution_status IS 'Статус выполнения: PENDING, IN_PROGRESS, DONE';
COMMENT ON COLUMN rfc_affected_subsystem.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN rfc_affected_subsystem.update_datetime IS 'Дата и время последнего обновления записи';
