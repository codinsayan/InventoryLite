
-- V2: Serial tracking, RMA, Tickets, BOM, Attach rules
CREATE TABLE IF NOT EXISTS serial_unit(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  serial_or_imei VARCHAR(64) UNIQUE NOT NULL,
  status ENUM('IN','SOLD','RMA','REFURB') NOT NULL DEFAULT 'IN',
  warranty_start DATE, warranty_end DATE, notes TEXT,
  INDEX(product_id), INDEX(status)
);
CREATE TABLE IF NOT EXISTS rma(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT, serial_id BIGINT NOT NULL,
  issue VARCHAR(255), status ENUM('OPEN','DOA','REPAIR','SWAP','CLOSED') DEFAULT 'OPEN',
  intake_date DATETIME DEFAULT NOW(), resolution TEXT
);
CREATE TABLE IF NOT EXISTS ticket(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT, device_desc VARCHAR(255),
  diagnosis TEXT, labor_minutes INT DEFAULT 0,
  parts_used_json TEXT, status ENUM('OPEN','IN_PROGRESS','DONE','DELIVERED') DEFAULT 'OPEN'
);
CREATE TABLE IF NOT EXISTS bom(
  parent_product_id BIGINT, component_product_id BIGINT, qty INT NOT NULL,
  PRIMARY KEY(parent_product_id, component_product_id)
);
CREATE TABLE IF NOT EXISTS attach_rules(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  when_category VARCHAR(64), then_suggest_product_id BIGINT,
  message VARCHAR(160), priority INT DEFAULT 0
);
