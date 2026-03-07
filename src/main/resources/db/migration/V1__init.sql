CREATE TABLE IF NOT EXISTS url_mapping (
  id BIGSERIAL PRIMARY KEY,
  short_code VARCHAR(7) NOT NULL,
  original_url TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_url_mapping_short_code ON url_mapping(short_code);

CREATE TABLE IF NOT EXISTS click_event (
  id BIGSERIAL PRIMARY KEY,
  url_mapping_id BIGINT NOT NULL REFERENCES url_mapping(id) ON DELETE CASCADE,
  clicked_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  ip VARCHAR(64),
  country VARCHAR(2),
  region VARCHAR(128),
  city VARCHAR(128),
  user_agent TEXT,
  user_agent_family VARCHAR(64),
  device_family VARCHAR(64),
  os_family VARCHAR(64),
  browser_family VARCHAR(64),
  referer TEXT
);

CREATE INDEX IF NOT EXISTS ix_click_event_mapping_time ON click_event(url_mapping_id, clicked_at DESC);
CREATE INDEX IF NOT EXISTS ix_click_event_country ON click_event(country);
CREATE INDEX IF NOT EXISTS ix_click_event_browser ON click_event(browser_family);
