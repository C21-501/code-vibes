-- Create rfc_approval table for storing RFC approvals
CREATE TABLE rfc_approval (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,
    is_approved BOOLEAN NOT NULL,
    comment TEXT,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_approval_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_approval_approver FOREIGN KEY (approver_id) REFERENCES "users"(id) ON DELETE RESTRICT,
    CONSTRAINT uq_rfc_approval_rfc_approver UNIQUE (rfc_id, approver_id)
);

-- Create indexes
CREATE INDEX idx_rfc_approval_rfc_id ON rfc_approval(rfc_id);
CREATE INDEX idx_rfc_approval_approver_id ON rfc_approval(approver_id);
CREATE INDEX idx_rfc_approval_is_approved ON rfc_approval(is_approved);

-- Add comments
COMMENT ON TABLE rfc_approval IS 'Таблица апрувов RFC пользователями с ролью RFC_APPROVER';
COMMENT ON COLUMN rfc_approval.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN rfc_approval.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_approval.approver_id IS 'ID пользователя-апрувера';
COMMENT ON COLUMN rfc_approval.is_approved IS 'Одобрен ли RFC (true - approved, false - rejected)';
COMMENT ON COLUMN rfc_approval.comment IS 'Комментарий апрувера';
COMMENT ON COLUMN rfc_approval.create_datetime IS 'Дата и время создания записи';
COMMENT ON COLUMN rfc_approval.update_datetime IS 'Дата и время последнего обновления';