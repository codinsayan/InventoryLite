package com.ait.inventory;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InvoicePdfServlet extends HttpServlet {

    /* ---------------- DS lookup (same as SalesServlet) ---------------- */
    private DataSource lookupDS() {
        Object cached = getServletContext().getAttribute("DS");
        if (cached instanceof DataSource) return (DataSource) cached;
        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            Object o = ic.lookup("java:comp/env/jdbc/inventorylite");
            if (o instanceof DataSource) {
                DataSource ds = (DataSource) o;
                getServletContext().setAttribute("DS", ds);
                return ds;
            }
        } catch (Exception ignore) {}
        String url  = getServletContext().getInitParameter("jdbc.url");
        String user = getServletContext().getInitParameter("jdbc.user");
        String pass = getServletContext().getInitParameter("jdbc.pass");
        if (url != null && user != null) {
            DataSource ds = new SalesServlet.SimpleDriverManagerDS(url, user, pass);
            getServletContext().setAttribute("DS", ds);
            return ds;
        }
        return null;
    }

    /* ---------------- Fonts (Regular + Bold) with ₹ fallback to “Rs.” ---------------- */
    private static final class FontPack {
        final PDFont reg, bold; final boolean rupeeOK;
        FontPack(PDFont r, PDFont b, boolean ok){ reg=r; bold=b; rupeeOK=ok; }
    }

    private FontPack loadFonts(PDDocument doc) {
        PDFont reg = tryLoadFromWar(doc, "/WEB-INF/fonts/NotoSans-Regular.ttf");
        PDFont bld = tryLoadFromWar(doc, "/WEB-INF/fonts/NotoSans-Bold.ttf");

        if (reg == null) reg = tryLoadFirst(doc,
                "C:\\\\Windows\\\\Fonts\\\\segoeui.ttf",
                "C:\\\\Windows\\\\Fonts\\\\arial.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/Library/Fonts/Arial Unicode.ttf",
                "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
                "/Library/Fonts/Arial.ttf");
        if (bld == null) bld = tryLoadFirst(doc,
                "C:\\\\Windows\\\\Fonts\\\\segoeuib.ttf",
                "C:\\\\Windows\\\\Fonts\\\\arialbd.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
                "/Library/Fonts/Arial Bold.ttf",
                "/System/Library/Fonts/Supplemental/Arial Bold.ttf");

        boolean ok = false;
        if (reg instanceof PDType0Font) {
            try { ok = ((PDType0Font) reg).hasGlyph(0x20B9); } catch (Exception ignore) {}
        }
        if (reg == null) reg = PDType1Font.HELVETICA;
        if (bld == null) bld = PDType1Font.HELVETICA_BOLD;
        return new FontPack(reg, bld, ok);
    }

    private PDFont tryLoadFromWar(PDDocument doc, String path) {
        try (InputStream in = getServletContext().getResourceAsStream(path)) {
            if (in != null) return PDType0Font.load(doc, in, true);
        } catch (IOException ignore) {}
        return null;
    }
    private PDFont tryLoadFirst(PDDocument doc, String... candidates) {
        for (String p : candidates) {
            try {
                File f = new File(p);
                if (f.exists()) return PDType0Font.load(doc, f);
            } catch (IOException ignore) {}
        }
        return null;
    }

    /* ---------------- small text helpers ---------------- */
    private static void T(PDPageContentStream cs, PDFont f, float s, float x, float y, String t) throws IOException {
        cs.beginText(); cs.setFont(f, s); cs.newLineAtOffset(x, y); cs.showText(t==null?"":t); cs.endText();
    }
    private static float tw(PDFont f, float s, String t) throws IOException {
        if (t==null || t.isEmpty()) return 0f; return f.getStringWidth(t)/1000f*s;
    }
    private static void TR(PDPageContentStream cs, PDFont f, float s, float rightX, float y, String t) throws IOException {
        T(cs, f, s, rightX - tw(f, s, t), y, t);
    }
    private static String INR(NumberFormat nf, boolean rupeeOK, double val) {
        String s = nf.format(val);
        return rupeeOK ? s : s.replace("\u20B9", "Rs.");
    }

    /* ---------------- main ---------------- */
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendError(400, "Missing id"); return; }
        long id; try { id = Long.parseLong(idStr); } catch(Exception e){ resp.sendError(400,"Invalid id"); return; }

        DataSource ds = lookupDS(); if (ds==null){ resp.sendError(500,"DataSource not available"); return; }

        SalesDao dao = new SalesDao(ds);
        SalesDao.OrderDetail od;
        try { od = dao.getOrder(id); } catch (Exception e) { throw new ServletException(e); }
        if (od == null) { resp.sendError(404, "Order not found"); return; }

        resp.setContentType("application/pdf");
        String disp = "1".equals(req.getParameter("download")) ? "attachment" : "inline";
        resp.setHeader("Content-Disposition", disp + "; filename=\"Invoice-" + id + ".pdf\"");

        NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en","IN"));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

        try (PDDocument doc = new PDDocument()) {
            FontPack F = loadFonts(doc);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDRectangle mb = page.getMediaBox();
            float margin = 46f, cw = mb.getWidth()-2*margin, y = mb.getHeight()-margin;

            // Column widths
            float cNo=28f, cQty=50f, cUnit=90f, cLine=110f, cProd=cw-(cNo+cQty+cUnit+cLine);

            Color accent = new Color(33,150,243), text = new Color(28,28,28),
                  faint = new Color(180,180,180), header = new Color(64,64,64), // dark grey, not pure black
                  zebra = new Color(248,248,248);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // Title
                cs.setNonStrokingColor(text);
                T(cs, F.bold, 26, margin, y, "InventoryLite Invoice");
                y -= 30;

                // Meta (use public fields from DTO)
                T(cs, F.bold, 11, margin,     y, "Invoice #:");
                T(cs, F.reg,  11, margin+68,  y, String.valueOf(id));
                y -= 14;
                T(cs, F.bold, 11, margin,     y, "Date:");
                T(cs, F.reg,  11, margin+68,  y, df.format(od.createdAt));
                y -= 14;
                T(cs, F.bold, 11, margin,     y, "Customer:");
                String cname = (od.customerName==null || od.customerName.isBlank())
                        ? "Walk-in Customer" : od.customerName.trim();
                T(cs, F.reg,  11, margin+68,  y, cname);
                y -= 18;

                // Divider
                cs.setStrokingColor(faint); cs.setLineWidth(0.7f);
                cs.moveTo(margin, y); cs.lineTo(margin+cw, y); cs.stroke(); y -= 12;

                // Header bar
                cs.setNonStrokingColor(header);
                cs.addRect(margin, y-8, cw, 20); cs.fill();
                cs.setNonStrokingColor(Color.WHITE);
                T (cs, F.bold, 11, margin+4,                  y-2, "#");
                T (cs, F.bold, 11, margin+cNo+8,              y-2, "Product");
                TR(cs, F.bold, 11, margin+cNo+cProd+cQty-8,       y-2, "Qty");
                TR(cs, F.bold, 11, margin+cNo+cProd+cQty+cUnit-8, y-2, "Unit");
                TR(cs, F.bold, 11, margin+cw-8,               y-2, "Line Total");
                y -= 24;

                // Rows
                double grand = 0;
                cs.setLineWidth(0.5f);

                for (int i=0; i<od.items.size(); i++) {
                    SalesDao.OrderItem it = od.items.get(i);

                    if (i % 2 == 1) { // zebra
                        cs.setNonStrokingColor(zebra);
                        cs.addRect(margin, y-12, cw, 16); cs.fill();
                    }
                    cs.setNonStrokingColor(text);

                    String prod = it.productName==null ? "" : it.productName.trim();
                    prod = fit(F.reg, 11, prod, cProd-14);

                    double line = it.price * it.qty;
                    grand += line;

                    // text
                    T (cs, F.reg, 11, margin+4,                   y, String.valueOf(i+1));
                    T (cs, F.reg, 11, margin+cNo+8,               y, prod);
                    TR(cs, F.reg, 11, margin+cNo+cProd+cQty-8,       y, String.valueOf(it.qty));
                    TR(cs, F.reg, 11, margin+cNo+cProd+cQty+cUnit-8, y, INR(inr, F.rupeeOK, it.price));
                    TR(cs, F.reg, 11, margin+cw-8,                 y, INR(inr, F.rupeeOK, line));

                    // row underline
                    cs.setStrokingColor(new Color(225,225,225));
                    cs.moveTo(margin, y-4); cs.lineTo(margin+cw, y-4); cs.stroke();

                    y -= 18;
                    if (y < margin+120) break; // keep single page for now
                }

                y -= 6;
                // Grand total panel
                float boxW=260f, boxH=44f, bx=margin+cw-boxW, by=y-boxH+8;
                cs.setNonStrokingColor(new Color(243,247,255));
                cs.addRect(bx, by, boxW, boxH); cs.fill();

                cs.setNonStrokingColor(accent);
                T (cs, F.bold, 12, bx+14, by+boxH-16, "Grand Total");
                cs.setNonStrokingColor(text);
                TR(cs, F.bold, 16, bx+boxW-14, by+12, INR(inr, F.rupeeOK, grand));

                // thank-you
                cs.setNonStrokingColor(new Color(95,95,95));
                T(cs, F.reg, 15, margin, by-26, "Thank you for your purchase!");
            }

            doc.save(resp.getOutputStream());
        }
    }

    /* clip long product names to fit column */
    private static String fit(PDFont f, float size, String s, float maxW) throws IOException {
        if (s==null) return "";
        if (tw(f, size, s) <= maxW) return s;
        String ell = "…";
        int lo=0, hi=s.length(), best=0;
        while (lo<=hi) {
            int m=(lo+hi)>>>1; String t=s.substring(0,m)+ell;
            if (tw(f,size,t)<=maxW) { best=m; lo=m+1; } else hi=m-1;
        }
        return s.substring(0,best)+ell;
    }
}
