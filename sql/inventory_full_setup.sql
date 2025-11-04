-- create_inventorylite.sql
-- Creates the `inventorylite` database and all required tables (safe to run multiple times).

DROP DATABASE IF EXISTS `inventorylite`;
CREATE DATABASE `inventorylite` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `inventorylite`;

-- ===================================================================
-- users: authentication and role storage
-- ===================================================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(100) NOT NULL UNIQUE,
  `salt` VARCHAR(64) NOT NULL,
  `password_hash` VARCHAR(128) NOT NULL,
  `role` VARCHAR(30) NOT NULL DEFAULT 'USER',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_at` TIMESTAMP NULL DEFAULT NULL,
  INDEX (`role`),
  INDEX (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- password_resets: tokens for password reset flows
-- ===================================================================
CREATE TABLE IF NOT EXISTS `password_resets` (
  `token` VARCHAR(128) NOT NULL PRIMARY KEY,
  `user_id` INT NOT NULL,
  `expires_at` TIMESTAMP NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- products: product catalog; spec column stores attributes (JSON) for PC Builder
-- ===================================================================
CREATE TABLE IF NOT EXISTS `products` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `sku` VARCHAR(100) NOT NULL UNIQUE,
  `category` VARCHAR(100) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `unit_price` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `spec` JSON NULL,         -- optional structured specs (socket, ram_type, wattage, length_mm, cooler_height_mm, etc.)
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX (`category`),
  INDEX (`sku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- orders: a single order header
-- ===================================================================
CREATE TABLE IF NOT EXISTS `orders` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_number` VARCHAR(60) NOT NULL UNIQUE,
  `customer_name` VARCHAR(255) NULL,
  `customer_phone` VARCHAR(30) NULL,
  `created_by` INT NULL,                -- user id who created the order
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(40) NOT NULL DEFAULT 'COMPLETED', -- e.g., DRAFT, COMPLETED, CANCELLED
  `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `tax` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `shipping` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX (`created_at`),
  INDEX (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- order_items: items belonging to an order
-- ===================================================================
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `sku` VARCHAR(100) NOT NULL,
  `product_name` VARCHAR(255) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `unit_price` DECIMAL(12,2) NOT NULL,
  `line_total` DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT,
  INDEX (`order_id`),
  INDEX (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- optional: simple audit log table for important actions (role changes, price changes, orders)
-- ===================================================================
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `who_user_id` INT NULL,
  `action` VARCHAR(120) NOT NULL,
  `detail` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`who_user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Optional receipts/invoices table (stores generated invoice metadata)
-- ===================================================================
CREATE TABLE IF NOT EXISTS `invoices` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `file_path` VARCHAR(1024) NULL,
  `generated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  INDEX (`generated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================================================
-- Sample products to help you test (50 products can be added — below are a few examples)
-- ===================================================================
INSERT INTO `products` (`name`, `sku`, `category`, `quantity`, `unit_price`, `spec`) VALUES
('Lenovo IdeaPad Slim 5', 'LAP-0001-IPS5', 'Laptops', 15, 68990.00, NULL),
('HP Pavilion 15-eg2', 'LAP-0002-HP15', 'Laptops', 12, 72990.00, NULL),
('ASUS TUF Gaming F15', 'LAP-0003-TUFF', 'Laptops', 10, 89990.00, NULL),
('Acer Aspire 7', 'LAP-0004-ASP7', 'Laptops', 18, 62990.00, NULL),
('Apple MacBook Air M2', 'LAP-0005-MBA2', 'Laptops', 8, 119900.00, NULL),
('Dell XPS 13 Plus', 'LAP-0006-XPSP', 'Laptops', 6, 154900.00, NULL),
('MSI Modern 14', 'LAP-0007-MSM14', 'Laptops', 14, 56990.00, NULL),
('Lenovo Legion 5', 'LAP-0008-LEG5', 'Laptops', 9, 119990.00, NULL),
('HP Victus 16', 'LAP-0009-VIC16', 'Laptops', 11, 104990.00, NULL),
('ASUS ZenBook 14 OLED', 'LAP-0010-ZB14', 'Laptops', 7, 129990.00, NULL),
('Logitech MX Keys', 'KEY-0021-LGMX', 'Keyboards', 25, 9995.00, NULL),
('Razer BlackWidow V3', 'KEY-0023-RZBW', 'Keyboards', 12, 12999.00, NULL),
('Logitech MX Master 3s', 'MOU-0026-LGMM', 'Mice', 24, 9495.00, NULL),
('Razer DeathAdder V2', 'MOU-0027-RZDA', 'Mice', 20, 5999.00, NULL),
('Samsung 970 EVO Plus 1TB', 'SSD-0036-970P', 'Storage', 19, 7490.00, JSON_OBJECT('type','NVMe','capacity_gb',1000)),
('WD Blue SN570 1TB', 'SSD-0037-SN57', 'Storage', 25, 6890.00, JSON_OBJECT('type','NVMe','capacity_gb',1000));

-- ===================================================================
-- Example: a small demo order with two items (optional)
-- ===================================================================
-- Uncomment to insert a sample order (requires products above exist).
/*
INSERT INTO `orders` (`order_number`,`customer_name`,`created_by`,`status`,`subtotal`,`tax`,`shipping`,`total`)
VALUES ('ORD-0001','Walk-in Customer', NULL, 'COMPLETED', 81980.00, 0.00, 0.00, 81980.00);

SET @lastOrderId = LAST_INSERT_ID();

INSERT INTO `order_items` (`order_id`,`product_id`,`sku`,`product_name`,`quantity`,`unit_price`,`line_total`)
VALUES
(@lastOrderId, (SELECT id FROM products WHERE sku='LAP-0001-IPS5'), 'LAP-0001-IPS5', 'Lenovo IdeaPad Slim 5', 1, 68990.00, 68990.00),
(@lastOrderId, (SELECT id FROM products WHERE sku='KEY-0021-LGMX'), 'KEY-0021-LGMX', 'Logitech MX Keys', 1, 9995.00, 9995.00);

-- Reduce product quantities to reflect sale
UPDATE products p
JOIN (
  SELECT product_id, SUM(quantity) as sold FROM order_items WHERE order_id = @lastOrderId GROUP BY product_id
) s ON p.id = s.product_id
SET p.quantity = GREATEST(0, p.quantity - s.sold);
*/

-- ===================================================================
-- Convenience: create a view for quick sales reporting (orders + totals)
-- ===================================================================
CREATE OR REPLACE VIEW `vw_orders_summary` AS
SELECT o.id, o.order_number, o.customer_name, o.created_at, o.status, o.total,
       (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.id) AS item_count
FROM orders o;

-- ===================================================================
-- End of script
-- ===================================================================
