package com.example.qrcode.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class CodeGenerator {

    public static Bitmap generateQRCode(String content, int size) throws WriterException {
        return generateCode(content, BarcodeFormat.QR_CODE, size, size);
    }

    public static Bitmap generateBarcode(String content, BarcodeFormat format, int width, int height) throws WriterException {
        return generateCode(content, format, width, height);
    }

    private static Bitmap generateCode(String content, BarcodeFormat format, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, format, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
}
