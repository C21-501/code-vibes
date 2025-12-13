-- Create rfc_history table for tracking all RFC changes with full data
CREATE TABLE rfc_history (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    operation VARCHAR(20) NOT NULL,
    changed_by_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    implementation_date TIMESTAMPTZ NOT NULL,
    urgency VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    requester_id BIGINT NOT NULL,
    affected_subsystems TEXT,
    attachment_ids TEXT,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_history_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_history_changed_by FOREIGN KEY (changed_by_id) REFERENCES "users"(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rfc_history_requester FOREIGN KEY (requester_id) REFERENCES "users"(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_rfc_history_rfc_id ON rfc_history(rfc_id);
CREATE INDEX idx_rfc_history_changed_by_id ON rfc_history(changed_by_id);
CREATE INDEX idx_rfc_history_create_datetime ON rfc_history(create_datetime);
CREATE INDEX idx_rfc_history_operation ON rfc_history(operation);

-- Add check constraint for operation enum
ALTER TABLE rfc_history ADD CONSTRAINT chk_rfc_history_operation
    CHECK (operation IN ('CREATE', 'UPDATE', 'DELETE'));

-- Add check constraint for urgency enum
ALTER TABLE rfc_history ADD CONSTRAINT chk_rfc_history_urgency
    CHECK (urgency IN ('EMERGENCY', 'URGENT', 'PLANNED'));

-- Add check constraint for status enum
ALTER TABLE rfc_history ADD CONSTRAINT chk_rfc_history_status
    CHECK (status IN ('NEW', 'UNDER_REVIEW', 'APPROVED', 'IMPLEMENTED', 'REJECTED'));

-- Add comments
COMMENT ON TABLE rfc_history IS 'Таблица истории изменений RFC с полными данными (для POST/PUT операций)';
COMMENT ON COLUMN rfc_history.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN rfc_history.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_history.operation IS 'Тип операции: CREATE, UPDATE';
COMMENT ON COLUMN rfc_history.changed_by_id IS 'ID пользователя, выполнившего изменение';
COMMENT ON COLUMN rfc_history.title IS 'Название RFC на момент изменения';
COMMENT ON COLUMN rfc_history.description IS 'Описание RFC на момент изменения';
COMMENT ON COLUMN rfc_history.implementation_date IS 'Дата релиза на момент изменения';
COMMENT ON COLUMN rfc_history.urgency IS 'Срочность на момент изменения: EMERGENCY, URGENT, PLANNED';
COMMENT ON COLUMN rfc_history.status IS 'Статус на момент изменения: NEW, UNDER_REVIEW, APPROVED, IMPLEMENTED, REJECTED';
COMMENT ON COLUMN rfc_history.requester_id IS 'ID создателя RFC';
COMMENT ON COLUMN rfc_history.affected_subsystems IS 'ID затронутых подсистем через запятую (например: 1,2,3)';
COMMENT ON COLUMN rfc_history.attachment_ids IS 'ID прикрепленных файлов через запятую (например: 1,2,3)';
COMMENT ON COLUMN rfc_history.create_datetime IS 'Дата и время изменения';
