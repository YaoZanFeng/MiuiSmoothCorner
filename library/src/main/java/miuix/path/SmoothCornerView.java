package miuix.path;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 平滑圆角对比View
 */
public class SmoothCornerView extends View {

    private float density;
    private float sizePx;
    private float radiusPx;
    private float strokeWidthPx;

    private Paint systemPaint;
    private Paint smoothPaint;
    private Paint labelPaint;
    private Paint dashPaint;

    public SmoothCornerView(Context context) {
        this(context, null);
    }

    public SmoothCornerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothCornerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        density = getContext().getResources().getDisplayMetrics().density;
        sizePx = 150 * density;
        radiusPx = 35 * density;
        strokeWidthPx = 2 * density;

        systemPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        systemPaint.setColor(Color.RED);
        systemPaint.setStyle(Paint.Style.STROKE);
        systemPaint.setStrokeWidth(strokeWidthPx);

        smoothPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smoothPaint.setColor(Color.BLUE);
        smoothPaint.setStyle(Paint.Style.STROKE);
        smoothPaint.setStrokeWidth(strokeWidthPx);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(13 * density);

        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(Color.LTGRAY);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(1 * density);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{10f, 10f}, 0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float leftMargin = 40 * density;
        float topOffset = (getHeight() - sizePx) / 2f;
        RectF rectLeft = new RectF(leftMargin, topOffset, leftMargin + sizePx, topOffset + sizePx);

        Path systemPath = new Path();
        systemPath.addRoundRect(rectLeft, radiusPx, radiusPx, Path.Direction.CW);

        float[] radii = new float[]{
                radiusPx, radiusPx, // TL
                radiusPx, radiusPx, // TR
                radiusPx, radiusPx, // BR
                radiusPx, radiusPx  // BL
        };
        // smoothness 0.9
        Path smoothPath = SmoothPathFactory.createSmoothPath(rectLeft, radii, 0.9);

        canvas.drawText("原始对比 (180dp, R=35dp)", rectLeft.left, rectLeft.top - 20, labelPaint);
        canvas.drawPath(systemPath, systemPaint);
        canvas.drawPath(smoothPath, smoothPaint);

        drawMagnifiedComparison(canvas, rectLeft, systemPath, smoothPath);
        
        drawLegend(canvas);
    }

    private void drawMagnifiedComparison(Canvas canvas, RectF originalRect, Path sysPath, Path smPath) {
        float magnification = 6f;
        float rightAreaCenterX = getWidth() * 0.75f;
        float rightAreaCenterY = getHeight() / 2f;

        canvas.save();

        canvas.translate(rightAreaCenterX, rightAreaCenterY);
        canvas.drawText("放大 6 倍细节 (左上角)", -100f, -sizePx / 2 - 20, labelPaint);

        canvas.scale(magnification, magnification);

        canvas.translate(-originalRect.left, -originalRect.top);

        canvas.drawPath(sysPath, systemPaint);
        canvas.drawPath(smPath, smoothPaint);

        canvas.restore();
    }

    private void drawLegend(Canvas canvas) {
        float legendY = getHeight() - 120f * density;
        float legendX = 40 * density;

        canvas.drawRect(legendX, legendY, legendX + 40, legendY + 5, systemPaint);
        canvas.drawText("红色：Android系统圆角", legendX + 50, legendY + 10, labelPaint);

        canvas.drawRect(legendX, legendY + 40, legendX + 40, legendY + 45, smoothPaint);
        canvas.drawText("蓝色：MIUI系统平滑圆角", legendX + 50, legendY + 50, labelPaint);
    }
}
