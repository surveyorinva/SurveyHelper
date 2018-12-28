package com.survlogic.surveyhelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GraphicDrawingUtils {
    private static final String TAG = "GraphicDrawingUtils";

    private Context mContext;

    public GraphicDrawingUtils(Context context) {
        this.mContext = context;
    }

    /**
     * Provides an overlay with larger text detailing that there are +# of items in set.
     * @param src
     * @param watermark
     * @param createOverlay
     * @return
     */
    public Bitmap setFullScreenWatermark(Bitmap src, String watermark, Boolean createOverlay){
        Paint rectBlackStroke,rectBlackFill;
        Rect rectWatermarkBounds = new Rect();
        RectF rectOverlay;

        int mTextDim = 150, mTextAlpha = 245;
        int bottomPadding = 20;
        float mLineWidth = 3;

        int w = src.getWidth();
        int h = src.getHeight();

        float mTextSize = mTextDim * mContext.getResources().getDisplayMetrics().scaledDensity;


        Bitmap result = Bitmap.createBitmap(w,h,src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src,0,0,null);

//        Text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(mTextAlpha);
        paint.setTextSize(mTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        paint.getTextBounds(watermark,0, watermark.length(),rectWatermarkBounds);
        int headerHeight = rectWatermarkBounds.height();
        int headerWidth = rectWatermarkBounds.width();

//        Overlay
        if (createOverlay) {
            rectBlackFill = new Paint();
            rectBlackFill.setStyle(Paint.Style.FILL);
            rectBlackFill.setColor(Color.BLACK);
            rectBlackFill.setAlpha(180);

            rectBlackStroke = new Paint();
            rectBlackStroke.setStyle(Paint.Style.STROKE);
            rectBlackStroke.setColor(Color.BLACK);
            rectBlackStroke.setStrokeWidth(mLineWidth);
            rectBlackStroke.setAntiAlias(true);

            int intStartBoxX = 0;
            int StartBoxY = 0;

            rectOverlay = new RectF(intStartBoxX, StartBoxY, w, h);
            canvas.drawRect(rectOverlay, rectBlackFill);

        }

        canvas.drawText(watermark, w/2 - headerWidth/2, h/2 + headerHeight/2, paint);

        canvas.save();

        return result;
    }

}
