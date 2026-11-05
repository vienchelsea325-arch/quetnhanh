package com.example.qrcode.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ScannerOverlay extends View {
    private Paint maskPaint, framePaint, linePaint, cornerPaint;
    private RectF frameRect;
    private float lineY;
    private boolean movingDown = true;
    private Path cornerPath;

    public ScannerOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        maskPaint = new Paint();
        maskPaint.setColor(Color.parseColor("#99000000"));

        framePaint = new Paint();
        framePaint.setColor(Color.parseColor("#40FFFFFF"));
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(2f);

        cornerPaint = new Paint();
        cornerPaint.setColor(Color.parseColor("#FF5A1F")); // QuetNhanh Primary
        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeWidth(12f);
        cornerPaint.setStrokeCap(Paint.Cap.ROUND);

        linePaint = new Paint();
        linePaint.setStrokeWidth(6f);

        frameRect = new RectF();
        cornerPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height) * 0.7f;
        float left = (width - size) / 2;
        float top = (height - size) / 2;
        frameRect.set(left, top, left + size, top + size);

        // Mask
        canvas.drawRect(0, 0, width, top, maskPaint);
        canvas.drawRect(0, top, left, top + size, maskPaint);
        canvas.drawRect(left + size, top, width, top + size, maskPaint);
        canvas.drawRect(0, top + size, width, height, maskPaint);

        // Frame
        canvas.drawRoundRect(frameRect, 40, 40, framePaint);

        // Corners
        drawCorners(canvas, left, top, size);

        // Laser line with orange gradient
        if (lineY < top || lineY > top + size) lineY = top;
        
        linePaint.setShader(new LinearGradient(left, lineY, left + size, lineY,
                new int[]{Color.TRANSPARENT, Color.parseColor("#FF5A1F"), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP));
        
        canvas.drawLine(left + 30, lineY, left + size - 30, lineY, linePaint);

        if (movingDown) {
            lineY += 10;
            if (lineY >= top + size) movingDown = false;
        } else {
            lineY -= 10;
            if (lineY <= top) movingDown = true;
        }

        invalidate();
    }

    private void drawCorners(Canvas canvas, float left, float top, float size) {
        float cornerLen = size * 0.15f;
        float radius = 40;

        // Top-Left
        cornerPath.reset();
        cornerPath.moveTo(left, top + cornerLen);
        cornerPath.lineTo(left, top + radius);
        cornerPath.quadTo(left, top, left + radius, top);
        cornerPath.lineTo(left + cornerLen, top);
        canvas.drawPath(cornerPath, cornerPaint);

        // Top-Right
        cornerPath.reset();
        cornerPath.moveTo(left + size - cornerLen, top);
        cornerPath.lineTo(left + size - radius, top);
        cornerPath.quadTo(left + size, top, left + size, top + radius);
        cornerPath.lineTo(left + size, top + cornerLen);
        canvas.drawPath(cornerPath, cornerPaint);

        // Bottom-Left
        cornerPath.reset();
        cornerPath.moveTo(left, top + size - cornerLen);
        cornerPath.lineTo(left, top + size - radius);
        cornerPath.quadTo(left, top + size, left + radius, top + size);
        cornerPath.lineTo(left + cornerLen, top + size);
        canvas.drawPath(cornerPath, cornerPaint);

        // Bottom-Right
        cornerPath.reset();
        cornerPath.moveTo(left + size - cornerLen, top + size);
        cornerPath.lineTo(left + size - radius, top + size);
        cornerPath.quadTo(left + size, top + size, left + size, top + size - radius);
        cornerPath.lineTo(left + size, top + size - cornerLen);
        canvas.drawPath(cornerPath, cornerPaint);
    }
}
