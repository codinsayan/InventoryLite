
package com.ait.inventory;

import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class QrServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String data = req.getParameter("data");
        if (data == null || data.isEmpty()) data = "InventoryLite";
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 220, 220);
            resp.setContentType("image/png");
            MatrixToImageWriter.writeToStream(matrix, "PNG", resp.getOutputStream());
        } catch (WriterException e) {
            resp.sendError(400, "QR error: " + e.getMessage());
        }
    }
}
