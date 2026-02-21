ALTER TABLE sessions
  ADD COLUMN play_start_time DATETIME(6) NULL AFTER updated_at;
