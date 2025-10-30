-- Create rfc_affected_subsystem_history table
CREATE TABLE rfc_affected_subsystem_history (
    id BIGSERIAL PRIMARY KEY,
    rfc_affected_subsystem_id BIGINT NOT NULL,
    status_type VARCHAR(50) NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by_id BIGINT NOT NULL,
    comment TEXT,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_affected_subsystem_history_subsystem FOREIGN KEY (rfc_affected_subsystem_id) REFERENCES rfc_affected_subsystem(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_affected_subsystem_history_changed_by FOREIGN KEY (changed_by_id) REFERENCES "users"(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_rfc_affected_subsystem_history_subsystem_id ON rfc_affected_subsystem_history(rfc_affected_subsystem_id);
CREATE INDEX idx_rfc_affected_subsystem_history_changed_by_id ON rfc_affected_subsystem_history(changed_by_id);
CREATE INDEX idx_rfc_affected_subsystem_history_create_datetime ON rfc_affected_subsystem_history(create_datetime);
CREATE INDEX idx_rfc_affected_subsystem_history_status_type ON rfc_affected_subsystem_history(status_type);

-- Add check constraint for status_type enum
ALTER TABLE rfc_affected_subsystem_history ADD CONSTRAINT chk_rfc_affected_subsystem_history_status_type
    CHECK (status_type IN ('CONFIRMATION', 'EXECUTION'));

-- Add check constraint for confirmation statuses
ALTER TABLE rfc_affected_subsystem_history ADD CONSTRAINT chk_rfc_affected_subsystem_history_confirmation
    CHECK (status_type != 'CONFIRMATION' OR (old_status IS NULL OR old_status IN ('PENDING', 'CONFIRMED', 'REJECTED')) AND new_status IN ('PENDING', 'CONFIRMED', 'REJECTED'));

-- Add check constraint for execution statuses
ALTER TABLE rfc_affected_subsystem_history ADD CONSTRAINT chk_rfc_affected_subsystem_history_execution
    CHECK (status_type != 'EXECUTION' OR (old_status IS NULL OR old_status IN ('PENDING', 'IN_PROGRESS', 'DONE')) AND new_status IN ('PENDING', 'IN_PROGRESS', 'DONE'));

-- Add comments
COMMENT ON TABLE rfc_affected_subsystem_history IS 'Таблица истории изменений статусов подсистем RFC (confirmation_status, execution_status)';
COMMENT ON COLUMN rfc_affected_subsystem_history.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN rfc_affected_subsystem_history.rfc_affected_subsystem_id IS 'ID затронутой подсистемы RFC';
COMMENT ON COLUMN rfc_affected_subsystem_history.status_type IS 'Тип статуса: CONFIRMATION или EXECUTION';
COMMENT ON COLUMN rfc_affected_subsystem_history.old_status IS 'Старое значение статуса (NULL при первом создании)';
COMMENT ON COLUMN rfc_affected_subsystem_history.new_status IS 'Новое значение статуса';
COMMENT ON COLUMN rfc_affected_subsystem_history.changed_by_id IS 'ID пользователя, изменившего статус';
COMMENT ON COLUMN rfc_affected_subsystem_history.comment IS 'Комментарий к изменению статуса';
COMMENT ON COLUMN rfc_affected_subsystem_history.create_datetime IS 'Дата и время изменения статуса';