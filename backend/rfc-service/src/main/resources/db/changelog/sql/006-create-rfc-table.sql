-- Create rfc table
CREATE TABLE rfc (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    implementation_date TIMESTAMPTZ NOT NULL,
    urgency VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    requester_id BIGINT NOT NULL,
    deleted_datetime TIMESTAMPTZ,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_requester FOREIGN KEY (requester_id) REFERENCES "users"(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_rfc_requester_id ON rfc(requester_id);
CREATE INDEX idx_rfc_status ON rfc(status);
CREATE INDEX idx_rfc_urgency ON rfc(urgency);
CREATE INDEX idx_rfc_implementation_date ON rfc(implementation_date);
CREATE INDEX idx_rfc_deleted_datetime ON rfc(deleted_datetime);

-- Add check constraint for urgency enum
ALTER TABLE rfc ADD CONSTRAINT chk_rfc_urgency
    CHECK (urgency IN ('EMERGENCY', 'URGENT', 'PLANNED'));

-- Add check constraint for status enum
ALTER TABLE rfc ADD CONSTRAINT chk_rfc_status
    CHECK (status IN ('NEW', 'UNDER_REVIEW', 'APPROVED', 'IN_PROGRESS', 'IMPLEMENTED', 'REJECTED'));

-- Add comments
COMMENT ON TABLE rfc IS 'Таблица запросов на изменение (Request for Change)';
COMMENT ON COLUMN rfc.id IS 'Уникальный идентификатор RFC';
COMMENT ON COLUMN rfc.title IS 'Название RFC';
COMMENT ON COLUMN rfc.description IS 'Описание изменения';
COMMENT ON COLUMN rfc.implementation_date IS 'Дата релиза изменений';
COMMENT ON COLUMN rfc.urgency IS 'Срочность: EMERGENCY, URGENT, PLANNED';
COMMENT ON COLUMN rfc.status IS 'Статус: NEW, UNDER_REVIEW, APPROVED, IMPLEMENTED, REJECTED';
COMMENT ON COLUMN rfc.requester_id IS 'ID создателя RFC';
COMMENT ON COLUMN rfc.deleted_datetime IS 'Дата и время удаления записи (NULL если не удалена)';
COMMENT ON COLUMN rfc.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN rfc.update_datetime IS 'Дата и время последнего обновления записи';