-- Create rfc_audit_log table for tracking all RFC changes with full snapshots
CREATE TABLE rfc_audit_log (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    operation VARCHAR(20) NOT NULL,
    changed_by_id BIGINT NOT NULL,
    rfc_snapshot JSONB NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_audit_log_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_audit_log_changed_by FOREIGN KEY (changed_by_id) REFERENCES "users"(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_rfc_audit_log_rfc_id ON rfc_audit_log(rfc_id);
CREATE INDEX idx_rfc_audit_log_changed_by_id ON rfc_audit_log(changed_by_id);
CREATE INDEX idx_rfc_audit_log_create_datetime ON rfc_audit_log(create_datetime);
CREATE INDEX idx_rfc_audit_log_operation ON rfc_audit_log(operation);

-- Create GIN index for JSONB queries (optional, for searching within snapshots)
CREATE INDEX idx_rfc_audit_log_snapshot ON rfc_audit_log USING GIN (rfc_snapshot);

-- Add check constraint for operation enum
ALTER TABLE rfc_audit_log ADD CONSTRAINT chk_rfc_audit_log_operation
    CHECK (operation IN ('CREATE', 'UPDATE'));

-- Add comments
COMMENT ON TABLE rfc_audit_log IS 'Таблица аудита всех изменений RFC с полными снимками состояния (для POST/PUT операций)';
COMMENT ON COLUMN rfc_audit_log.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN rfc_audit_log.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_audit_log.operation IS 'Тип операции: CREATE, UPDATE';
COMMENT ON COLUMN rfc_audit_log.changed_by_id IS 'ID пользователя, выполнившего изменение';
COMMENT ON COLUMN rfc_audit_log.rfc_snapshot IS 'Полный снимок состояния RFC в формате JSON после изменения (включая все связанные данные)';
COMMENT ON COLUMN rfc_audit_log.create_datetime IS 'Дата и время изменения';
