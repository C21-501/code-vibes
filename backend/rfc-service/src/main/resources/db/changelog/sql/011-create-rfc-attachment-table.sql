-- Create rfc_attachment table
CREATE TABLE rfc_attachment (
    id BIGSERIAL PRIMARY KEY,
    rfc_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(255),
    uploaded_by_id BIGINT NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rfc_attachment_rfc FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE CASCADE,
    CONSTRAINT fk_rfc_attachment_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES "users"(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_rfc_attachment_rfc_id ON rfc_attachment(rfc_id);
CREATE INDEX idx_rfc_attachment_uploaded_by_id ON rfc_attachment(uploaded_by_id);
CREATE INDEX idx_rfc_attachment_create_datetime ON rfc_attachment(create_datetime);

-- Add check constraint for file size (max 100MB)
ALTER TABLE rfc_attachment ADD CONSTRAINT chk_rfc_attachment_file_size
    CHECK (file_size > 0 AND file_size <= 104857600);

-- Add comments
COMMENT ON TABLE rfc_attachment IS 'Таблица прикрепленных файлов к RFC';
COMMENT ON COLUMN rfc_attachment.id IS 'Уникальный идентификатор вложения';
COMMENT ON COLUMN rfc_attachment.rfc_id IS 'ID RFC';
COMMENT ON COLUMN rfc_attachment.original_filename IS 'Оригинальное имя файла';
COMMENT ON COLUMN rfc_attachment.stored_filename IS 'Имя файла в хранилище (UUID + расширение)';
COMMENT ON COLUMN rfc_attachment.file_path IS 'Полный путь к файлу на диске';
COMMENT ON COLUMN rfc_attachment.file_size IS 'Размер файла в байтах';
COMMENT ON COLUMN rfc_attachment.content_type IS 'MIME тип файла';
COMMENT ON COLUMN rfc_attachment.uploaded_by_id IS 'ID пользователя, загрузившего файл';
COMMENT ON COLUMN rfc_attachment.create_datetime IS 'Дата и время загрузки файла';
