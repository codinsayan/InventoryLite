InventoryLite – CSS-only UI Refresh
- Removed Bootstrap dependency and implemented a Google-style (Material-inspired) design with pure CSS.
- Common layout via /inc/header.jsp and /inc/footer.jsp (navbar + footer).
- All pages now include these fragments automatically.
- Styles live in /assets/css/app.css – includes utilities to replace Bootstrap classes used in JSPs (d-flex, gap, table, btn, form-control, etc.).
- No external fonts; system font stack with Roboto fallback if present.
