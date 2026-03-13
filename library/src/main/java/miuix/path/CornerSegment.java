package miuix.path;

import android.graphics.PointF;
import android.graphics.RectF;

public class CornerSegment {

    public final RectF bounds = new RectF();
    public float radius;
    public double hSmoothness;
    public double vSmoothness;
    public double hAngle;
    public double vAngle;
    public float straightSegmentAngle;
    public final PointF[] hControlPoints = new PointF[4];
    public final PointF[] vControlPoints = new PointF[4];

    public CornerSegment() {
        for (int i = 0; i < 4; i++) {
            hControlPoints[i] = new PointF();
            vControlPoints[i] = new PointF();
        }
    }

    /**
     * 根据给定参数计算该圆角的所有几何数据。
     *
     * @param radius     圆角半径
     * @param bounds     整体矩形区域
     * @param offsetX    水平偏移量（内边距）
     * @param offsetY    垂直偏移量（内边距）
     * @param smoothness 平滑度 (0.0 - 1.0)
     * @param position   圆角位置枚举
     */
    public final void compute(float radius, RectF bounds, float offsetX, float offsetY,
                              double smoothness, CornerPosition position) {
        float clampedVSmoothness;
        double vBezierScale;

        this.radius = radius;

        float rectWidth  = bounds.width();
        float rectHeight = bounds.height();
        float left   = bounds.left;
        float top    = bounds.top;
        float right  = bounds.right;
        float bottom = bounds.bottom;

        float r = this.radius;
        this.hSmoothness = SmoothPathProvider.isWidthConstrained(rectWidth, r, r, smoothness, SmoothPathProvider.SMOOTH_FACTOR)
                ? Math.max(Math.min(((rectWidth / (r * 2.0f)) - 1.0f) / SmoothPathProvider.SMOOTH_FACTOR, 1.0f), 0.0f)
                : smoothness;

        float r2 = this.radius;
        if (SmoothPathProvider.isHeightConstrained(rectHeight, r2, r2, smoothness, SmoothPathProvider.SMOOTH_FACTOR)) {
            clampedVSmoothness = SmoothPathProvider.SMOOTH_FACTOR;
            vBezierScale = Math.max(Math.min(((rectHeight / (r2 * 2.0f)) - 1.0f) / SmoothPathProvider.SMOOTH_FACTOR, 1.0f), 0.0f);
        } else {
            clampedVSmoothness = SmoothPathProvider.SMOOTH_FACTOR;
            vBezierScale = smoothness;
        }
        this.vSmoothness = vBezierScale;

        double hAngle = (this.hSmoothness * Math.PI) / 4.0d;
        this.hAngle = hAngle;
        double vAngle = (vBezierScale * Math.PI) / 4.0d;
        this.vAngle = vAngle;

        this.straightSegmentAngle = (float) SmoothPathProvider.radiansToDegrees(
                (Math.PI / 2.0d - vAngle) - hAngle);

        double smoothFactor    = clampedVSmoothness;
        double hSmoothnessScaled = this.hSmoothness * smoothFactor;
        double hBezierScale;
        if (hAngle == 0.0d) {
            hBezierScale = 0.0d;
        } else {
            double halfH = hAngle / 2.0d;
            hBezierScale = (((Math.cos(hAngle) + 1.0d)
                    * ((Math.tan(halfH) + (hSmoothnessScaled * SmoothPathProvider.SMOOTH_FACTOR_DOUBLE)) * 2.0d))
                    / (Math.tan(halfH) * 3.0d)) - 1.0d;
        }

        double hSin  = (1.0d - Math.sin(this.hAngle)) * this.radius;
        double hCos  = (1.0d - Math.cos(this.hAngle)) * this.radius;
        double hTan  = (1.0d - Math.tan(this.hAngle / 2.0d)) * this.radius;
        double hTan2 = (Math.tan(this.hAngle / 2.0d) * (this.radius * 1.5d))
                / (Math.cos(this.hAngle) + 1.0d);
        double hBezierOffset = hBezierScale * hTan2;

        double vSmoothnessScaled = this.vSmoothness * smoothFactor;
        double vBezierScaleV = 0.0d;
        if (vAngle != 0.0d) {
            double halfV = vAngle / 2.0d;
            vBezierScaleV = (((Math.cos(vAngle) + 1.0d)
                    * ((Math.tan(halfV) + (vSmoothnessScaled * SmoothPathProvider.SMOOTH_FACTOR_DOUBLE)) * 2.0d))
                    / (Math.tan(halfV) * 3.0d)) - 1.0d;
        }

        double vCos  = (1.0d - Math.cos(this.vAngle)) * this.radius;
        double vSin  = (1.0d - Math.sin(this.vAngle)) * this.radius;
        double vTan  = (1.0d - Math.tan(this.vAngle / 2.0d)) * this.radius;
        double vTan2 = (Math.tan(this.vAngle / 2.0d) * (this.radius * 1.5d))
                / (Math.cos(this.vAngle) + 1.0d);
        double vBezierOffset = vBezierScaleV * vTan2;

        PointF[] vCtrl = this.vControlPoints;
        PointF[] hCtrl = this.hControlPoints;

        switch (position) {
            case TOP_LEFT:
                // 左上角
                float cornerLeft = left + offsetX;
                float cornerTop  = top  + offsetY;
                float diameter   = this.radius * 2.0f;
                this.bounds.set(cornerLeft, cornerTop, diameter + cornerLeft, diameter + cornerTop);

                double cx = cornerLeft, cy = cornerTop;
                hCtrl[0].set((float) (hSin + cx),            (float) (hCos + cy));
                hCtrl[1].set((float) (hTan + cx),            cornerTop);
                double hBase = hTan + hTan2;
                hCtrl[2].set((float) (hBase + cx),           cornerTop);
                hCtrl[3].set((float) (hBase + hBezierOffset + cx), cornerTop);

                double vBase = vTan + vTan2;
                vCtrl[0].set(cornerLeft, (float) (vBezierOffset + vBase + cy));
                vCtrl[1].set(cornerLeft, (float) (vBase + cy));
                vCtrl[2].set(cornerLeft, (float) (vTan + cy));
                vCtrl[3].set((float) (vCos + cx), (float) (vSin + cy));
                break;

            case TOP_RIGHT:
                // 右上角
                float cornerTopTR  = top + offsetY;
                float diameterTR   = this.radius * 2.0f;
                float cornerRightTR = right - offsetX;
                this.bounds.set((right - diameterTR) - offsetX, cornerTopTR, cornerRightTR, diameterTR + cornerTopTR);

                double rx = right, hBaseTR = rx - hTan - hTan2, ox = offsetX;
                hCtrl[0].set((float) (hBaseTR - hBezierOffset - ox), cornerTopTR);
                hCtrl[1].set((float) (hBaseTR - ox),                 cornerTopTR);
                hCtrl[2].set((float) (rx - hTan - ox),             cornerTopTR);
                double cyTR = cornerTopTR;
                hCtrl[3].set((float) ((rx - hSin) - ox), (float) (hCos + cyTR));

                double vBaseTR = vTan + vTan2;
                vCtrl[0].set((float) ((rx - vCos) - ox), (float) (vSin + cyTR));
                vCtrl[1].set(cornerRightTR,                 (float) (vTan + cyTR));
                vCtrl[2].set(cornerRightTR,                 (float) (vBaseTR + cyTR));
                vCtrl[3].set(cornerRightTR,                 (float) (vBaseTR + vBezierOffset + cyTR));
                break;

            case BOTTOM_RIGHT:
                // 右下角
                float diameterBR    = this.radius * 2.0f;
                float cornerRightBR  = right - offsetX;
                float cornerBottomBR = bottom - offsetY;
                this.bounds.set((right - diameterBR) - offsetX, (bottom - diameterBR) - offsetY,
                        cornerRightBR, cornerBottomBR);

                double rxBR = right, oxBR = offsetX;
                double hBaseBR = rxBR - hTan - hTan2;
                float hSinX = (float) ((rxBR - hSin) - oxBR);
                double by = bottom, bCos = by - hCos, oy = offsetY;
                hCtrl[0].set(hSinX,                              (float) (bCos - oy));
                hCtrl[1].set((float) (rxBR - hTan - oxBR),          cornerBottomBR);
                hCtrl[2].set((float) (hBaseBR - oxBR),              cornerBottomBR);
                hCtrl[3].set((float) (hBaseBR - hBezierOffset - oxBR), cornerBottomBR);

                double vBase1 = by - vTan, vBase2 = vBase1 - vTan2;
                vCtrl[0].set(cornerRightBR, (float) (vBase2 - vBezierOffset - oy));
                vCtrl[1].set(cornerRightBR, (float) (vBase2 - oy));
                vCtrl[2].set(cornerRightBR, (float) (vBase1 - oy));
                vCtrl[3].set((float) ((rxBR - vCos) - oxBR), (float) ((by - vSin) - oy));
                break;

            case BOTTOM_LEFT:
                // 左下角
                float cornerLeftBL   = left + offsetX;
                float diameterBL     = this.radius * 2.0f;
                float cornerBottomBL = bottom - offsetY;
                this.bounds.set(cornerLeftBL, (bottom - diameterBL) - offsetY,
                        diameterBL + cornerLeftBL, cornerBottomBL);

                double hBaseBL = hTan + hTan2, cxBL = cornerLeftBL;
                hCtrl[0].set((float) (hBezierOffset + hBaseBL + cxBL), cornerBottomBL);
                hCtrl[1].set((float) (hBaseBL + cxBL),                 cornerBottomBL);
                hCtrl[2].set((float) (hTan + cxBL),                  cornerBottomBL);
                double byBL = bottom, oyBL = offsetY;
                hCtrl[3].set((float) (hSin + cxBL), (float) ((byBL - hCos) - oyBL));

                vCtrl[0].set((float) (vCos + cxBL), (float) ((byBL - vSin) - oyBL));
                double vBase1BL = byBL - vTan;
                vCtrl[1].set(cornerLeftBL, (float) (vBase1BL - oyBL));
                double vBase2BL = vBase1BL - vTan2;
                vCtrl[2].set(cornerLeftBL, (float) (vBase2BL - oyBL));
                vCtrl[3].set(cornerLeftBL, (float) ((vBase2BL - vBezierOffset) - oyBL));
                break;
        }
    }
}
