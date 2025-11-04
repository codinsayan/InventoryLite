
# InventoryLite

**InventoryLite** — a lightweight, Java/JSP-based Inventory & Sales Management app for small retail or computer-parts stores.  
Provides product/catalog management, POS-style orders, sales reporting (with PDF invoices), a PC-builder compatibility tool, and role-based user access.

---

## Table of contents
- [Features](#features)  
- [Architecture & Tech Stack](#architecture--tech-stack)  
- [Quick demo screenshots](#quick-demo-screenshots) *(refer to repo screenshots)*  
- [Prerequisites](#prerequisites)  
- [Install & Run (development)](#install--run-development)  
- [Tomcat / JDBC DataSource (recommended)](#tomcat--jdbc-datasource-recommended)  
- [Database setup](#database-setup)  
- [Project structure](#project-structure)  
- [Important endpoints / pages](#important-endpoints--pages)  
- [Security & roles](#security--roles)  
- [How to add a user / admin flow](#how-to-add-a-user--admin-flow)  
- [UI conventions & theming](#ui-conventions--theming)  
- [Troubleshooting](#troubleshooting)  
- [Future improvements](#future-improvements)  
- [License & credits](#license--credits)

---

## Features
- **Products** — create / edit / delete / list products (SKU, category, quantity, price).  
- **Orders (POS)** — create orders, add items, calculates totals, and reduces stock.  
- **Sales** — view completed orders, filter, and generate PDF invoices.  
- **PC Builder** — compatibility checks (CPU socket, motherboard, PSU wattage, GPU length, cooler height, etc.).  
- **Users & access** — signup/login/password reset + role-based access (ADMIN / USER / VIEWER).  
- **Dashboard** — central cards for quick access to modules.  
- Lightweight UI with consistent card-based dark theme (no heavy frontend frameworks).

---

## Architecture & Tech Stack
- **Frontend:** JSP, JSTL, HTML5, CSS, vanilla JavaScript  
- **Backend:** Java Servlets (Jakarta EE), DAO pattern  
- **DB:** MySQL 8.x  
- **Server:** Apache Tomcat 10.x  
- **Build:** Maven  
- **Auth:** Session-based; passwords hashed & salted; `AuthFilter` for route protection

---

## Prerequisites
- Java 11+ (project uses JDK 17 in dev)  
- Apache Tomcat 10.x  
- MySQL 8.x (or compatible)  
- Maven 3.x

---

## Install & Run (development)

1. **Clone the repo**
```bash
git clone <repo-url>
cd inventorylite
```

2. **Create DB (local)**
```sql
CREATE DATABASE inventorylite;
```

3. **Configure Tomcat DataSource** (recommended in `conf/context.xml` or `<app>/META-INF/context.xml`).

4. **Build**
```bash
mvn clean package
```

5. **Deploy**
Copy `target/inventorylite-1.0.0.war` into Tomcat's `webapps/` folder OR use your IDE/Tomcat runner.

6. **Start Tomcat**
```bash
startup.bat  # or startup.sh
```

7. **Open app**
```
http://localhost:8080/<your-app-context>/
```

---

## Tomcat / JDBC DataSource (recommended)
```xml
<Resource name="jdbc/inventorylite"
          auth="Container"
          type="javax.sql.DataSource"
          maxTotal="50" maxIdle="10" maxWaitMillis="10000"
          username="root"
          password="your_mysql_password"
          driverClassName="com.mysql.cj.jdbc.Driver"
          url="jdbc:mysql://127.0.0.1:3306/inventorylite?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC"/>
```

---

## Database setup
```sql
CREATE DATABASE inventorylite;
USE inventorylite;

CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  salt VARCHAR(24) NOT NULL,
  password_hash VARCHAR(44) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER'
);
```

---

## Project structure
```
src/main/java/com/ait/inventory/
  ├─ dao/
  ├─ servlets/
  └─ filters/
src/main/webapp/
  ├─ inc/
  ├─ index.jsp
  ├─ products.jsp
  ├─ orders.jsp
  ├─ sales.jsp
  ├─ pc-builder.jsp
  └─ users.jsp
WEB-INF/web.xml
```

---

## Endpoints
- `/` → Dashboard  
- `/login`, `/signup`  
- `/products`, `/orders`, `/sales`, `/pc-builder`, `/users`  
- `/invoice.pdf` → PDF Invoice Generator

---

## Roles
- **ADMIN** — Full access  
- **USER** — Order & product access  
- **VIEWER** — Read-only  
Passwords hashed + salted.  
`AuthFilter` guards routes and enforces roles.

---

## Troubleshooting
- **DB connection issues:** check Tomcat DataSource, MySQL credentials.  
- **404 errors:** verify servlet mappings in `web.xml`.  
- **Empty pages:** access `/users` servlet not JSP directly.  
- **First user not admin:** occurs if `users` table prefilled.

---

## Future improvements
- Add CSV/Excel exports.  
- Low-stock alerts.  
- Advanced permission table.  
- Audit logs.  
- Unit & integration tests.

---

## License
MIT © 2025 — InventoryLite (your name / organization)
