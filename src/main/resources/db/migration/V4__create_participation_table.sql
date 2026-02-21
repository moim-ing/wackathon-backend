CREATE TABLE participation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    user_id BIGINT NULL, -- 로그인 유저 (선택)
    guest_name VARCHAR(100) NULL, -- 비로그인 게스트용 이름
    verified_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_participation_session_id FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);