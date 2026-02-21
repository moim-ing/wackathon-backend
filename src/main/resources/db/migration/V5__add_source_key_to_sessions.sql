ALTER TABLE sessions
  ADD COLUMN source_key VARCHAR(1024) NULL AFTER video_id;