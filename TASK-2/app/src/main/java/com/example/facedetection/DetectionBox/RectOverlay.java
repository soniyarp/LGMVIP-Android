package com.example.facedetection.DetectionBox;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.mlkit.vision.face.Face;

public class RectOverlay extends GraphicOverlay.Graphic{


    private int RECT_COLOR= Color.GREEN;
    private float STROKE_WIDTH=4.0f;
    private Paint rectPaint;

    private GraphicOverlay graphicOverlay;
    private Rect rect;
    private final Face face;

    public RectOverlay(GraphicOverlay graphicOverlay, Rect rect, Face face){

        super(graphicOverlay);
        rectPaint=new Paint();
        rectPaint.setColor(RECT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        this.graphicOverlay=graphicOverlay;
        this.rect=rect;
        this.face=face;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {


        RectF rectF = new RectF(rect);

        rectF.left=translateX(rectF.left);
        rectF.right=translateX(rectF.right);
        rectF.top=translateY(rectF.top);
        rectF.bottom=translateY(rectF.bottom);

        canvas.drawRect(rectF, rectPaint);


    }
}