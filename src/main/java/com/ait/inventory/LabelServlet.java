
package com.ait.inventory;

import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LabelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String data = req.getParameter("data");
        if (data == null || data.isEmpty()) data = "SKU-0000";
        Code128Writer w = new Code128Writer();
        BitMatrix m = w.encode(data, BarcodeFormat.CODE_128, 340, 120);
        resp.setContentType("image/png");
        MatrixToImageWriter.writeToStream(m, "PNG", resp.getOutputStream());
    }
}
