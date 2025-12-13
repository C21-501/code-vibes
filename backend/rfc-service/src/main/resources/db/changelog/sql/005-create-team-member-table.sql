-- Create team_member junction table (many-to-many relationship between team and user)
CREATE TABLE team_member (
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_datetime TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE CASCADE,
    CONSTRAINT fk_team_member_user FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE CASCADE
);

-- Create indexes for foreign keys
CREATE INDEX idx_team_member_team_id ON team_member(team_id);
CREATE INDEX idx_team_member_user_id ON team_member(user_id);

-- Add comment to table
COMMENT ON TABLE team_member IS 'Связующая таблица для связи many-to-many между командами и пользователями';
COMMENT ON COLUMN team_member.team_id IS 'ID команды';
COMMENT ON COLUMN team_member.user_id IS 'ID пользователя';
COMMENT ON COLUMN team_member.create_datetime IS 'Дата и время добавления пользователя в команду';