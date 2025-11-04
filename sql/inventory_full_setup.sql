-- create_inventorylite_minimal.sql
-- Creates database and tables with the column names exactly as in your screenshots:
-- products(id, name, sku, category, quantity, price)
-- order_items(id, order_id, product_id, product_name, qty, price)
-- users(id, username, salt, password_hash, role)
--
-- Safe to run multiple times (uses IF NOT EXISTS).

DROP DATABASE IF EXISTS `inventorylite`;
CREATE DATABASE `inventorylite` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `inventorylite`;

-- ======== users ========
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(100) NOT NULL UNIQUE,
  `salt` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` VARCHAR(32) NOT NULL DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======== products ========
CREATE TABLE IF NOT EXISTS `products` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `sku` VARCHAR(128) NOT NULL UNIQUE,
  `category` VARCHAR(128) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  `price` DECIMAL(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======== orders ========
-- Minimal orders table so order_items.order_id links to something.
CREATE TABLE IF NOT EXISTS `orders` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `customer` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total` DECIMAL(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======== order_items ========
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `product_name` VARCHAR(255) NOT NULL,
  `qty` INT NOT NULL DEFAULT 1,
  `price` DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes to speed up queries
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);

-- ===================================================================
-- Optional: sample inserts (uncomment block below if you want sample data)
-- Note: You already have some rows; run this only if you want additional test data.
-- ===================================================================
/*
INSERT INTO `products` (`name`, `sku`, `category`, `quantity`, `price`) VALUES
('Lenovo IdeaPad Slim 5', 'LAP-0001-IPS5', 'Laptops', 15, 68990.00),
('HP Pavilion 15-eg2', 'LAP-0002-HP15', 'Laptops', 12, 72990.00),
('ASUS TUF Gaming F15', 'LAP-0003-TUFF', 'Laptops', 7, 89990.00),
('Acer Aspire 7', 'LAP-0004-ASP7', 'Laptops', 16, 62990.00),
('Apple MacBook Air M2', 'LAP-0005-MBA2', 'Laptops', 4, 119900.00),
('Dell XPS 13 Plus', 'LAP-0006-XPSP', 'Laptops', 6, 154900.00),
('MSI Modern 14', 'LAP-0007-MSM14', 'Laptops', 14, 56990.00),
('Lenovo Legion 5', 'LAP-0008-LEG5', 'Laptops', 9, 119990.00),
('HP Victus 16', 'LAP-0009-VIC16', 'Laptops', 11, 104990.00),
('ASUS ZenBook 14 OLED', 'LAP-0010-ZB14', 'Laptops', 7, 129990.00),
('Logitech MX Keys', 'KEY-0021-LGMX', 'Keyboards', 25, 9995.00),
('Razer BlackWidow V3', 'KEY-0023-RZBW', 'Keyboards', 12, 12999.00),
('Logitech MX Master 3s', 'MOU-0026-LGMM', 'Mice', 24, 9495.00),
('Razer DeathAdder V2', 'MOU-0027-RZDA', 'Mice', 20, 5999.00),
('Samsung 970 EVO Plus 1TB', 'SSD-0036-970P', 'Storage', 19, 7490.00),
('WD Blue SN570 1TB', 'SSD-0037-SN57', 'Storage', 25, 6890.00);
*/

-- ===================================================================
-- Optional: insert demo admin user?
-- ===================================================================
-- IMPORTANT: your app uses salt+hash via PasswordUtil. Creating a user here with a raw password
-- won't match unless you compute the same salt+hash method. Recommended: create admin using /signup.
-- If you want me to add a precomputed admin user, tell me how PasswordUtil.hash produces the hash
-- (algorithm + encoding), and I will prepare the insert.

-- End of script
