-- Изменение таблицы rfc_attachment для хранения файлов в БД
-- Убираем старые ограничения и колонки
ALTER TABLE rfc_attachment DROP CONSTRAINT IF EXISTS chk_rfc_attachment_file_size;
ALTER TABLE rfc_attachment DROP CONSTRAINT IF EXISTS fk_rfc_attachment_rfc;

-- Делаем rfc_id nullable (файлы создаются до RFC)
ALTER TABLE rfc_attachment ALTER COLUMN rfc_id DROP NOT NULL;

-- Удаляем колонки для хранения на диске
ALTER TABLE rfc_attachment DROP COLUMN IF EXISTS stored_filename;
ALTER TABLE rfc_attachment DROP COLUMN IF EXISTS file_path;

-- Добавляем колонку для хранения файла в БД
ALTER TABLE rfc_attachment ADD COLUMN IF NOT EXISTS file_data BYTEA;

-- Добавляем новое ограничение размера файла (5MB = 5242880 байт)
ALTER TABLE rfc_attachment ADD CONSTRAINT chk_rfc_attachment_file_size
    CHECK (file_size > 0 AND file_size <= 5242880);

-- Восстанавливаем внешний ключ на rfc с SET NULL при удалении
ALTER TABLE rfc_attachment ADD CONSTRAINT fk_rfc_attachment_rfc
    FOREIGN KEY (rfc_id) REFERENCES rfc(id) ON DELETE SET NULL;

-- Обновляем комментарии
COMMENT ON COLUMN rfc_attachment.rfc_id IS 'ID RFC (nullable, заполняется при привязке к RFC)';
COMMENT ON COLUMN rfc_attachment.file_data IS 'Данные файла (максимум 5MB)';
COMMENT ON COLUMN rfc_attachment.file_size IS 'Размер файла в байтах (максимум 5MB)';