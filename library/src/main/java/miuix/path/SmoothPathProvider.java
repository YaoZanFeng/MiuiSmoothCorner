package miuix.path;

import android.graphics.RectF;
import androidx.annotation.Nullable;

public final class SmoothPathProvider {

    public static final float SMOOTH_FACTOR = 0.46f;
    public static final double SMOOTH_FACTOR_DOUBLE = 0.46000000834465027d;

    /**
     * 根据矩形区域和四角半径数组，计算并返回完整的平滑圆角数据。
     *
     * @param bounds     整体矩形区域
     * @param radii      8个半径值
     * @param smoothness 平滑度 (0.0 - 1.0)
     * @param offsetX    水平偏移量（内边距）
     * @param offsetY    垂直偏移量（内边距）
     * @return 完整的 {@link SmoothCornerData}，若 radii 为 null 则返回 null
     */
    @Nullable
    public static SmoothCornerData computeSmoothCorners(RectF bounds, float[] radii, double smoothness,
                                                        float offsetX, float offsetY) {
        if (radii == null) {
            return null;
        }

        float rectWidth  = bounds.width();
        float rectHeight = bounds.height();
        SmoothCornerData cornerData = new SmoothCornerData(rectWidth, rectHeight, smoothness);

        float[] safeRadii = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        for (int i = 0; i < Math.min(8, radii.length); i++) {
            if (!Float.isNaN(radii[i])) {
                safeRadii[i] = radii[i];
            }
        }

        float tlRadiusX = safeRadii[0];
        float tlRadiusY = safeRadii[1];
        float trRadiusX = safeRadii[2];
        float trRadiusY = safeRadii[3];
        float brRadiusX = safeRadii[4];
        float brRadiusY = safeRadii[5];
        float blRadiusX = safeRadii[6];
        float blRadiusY = safeRadii[7];

        //水平方向约束
        float sumTopWidth = tlRadiusX + trRadiusX;
        if (sumTopWidth > rectWidth) {
            tlRadiusX = (tlRadiusX * rectWidth) / sumTopWidth;
            trRadiusX = (trRadiusX * rectWidth) / sumTopWidth;
        }
        float clampedTrX = trRadiusX;

        //垂直方向约束
        float sumLeftHeight = trRadiusY + brRadiusY;
        if (sumLeftHeight > rectHeight) {
            trRadiusY = (trRadiusY * rectHeight) / sumLeftHeight;
            brRadiusY = (brRadiusY * rectHeight) / sumLeftHeight;
        }
        float clampedTrY = trRadiusY;
        float clampedBrY = brRadiusY;

        //水平方向约束
        float sumBottomWidth = brRadiusX + blRadiusX;
        if (sumBottomWidth > rectWidth) {
            brRadiusX = (brRadiusX * rectWidth) / sumBottomWidth;
            blRadiusX = (rectWidth * blRadiusX) / sumBottomWidth;
        }
        float clampedBrX = brRadiusX;

        //垂直方向约束
        float sumRightHeight = blRadiusY + tlRadiusY;
        if (sumRightHeight > rectHeight) {
            blRadiusY = (blRadiusY * rectHeight) / sumRightHeight;
            tlRadiusY = (rectHeight * tlRadiusY) / sumRightHeight;
        }
        float clampedBlY = blRadiusY;

        //初始化四角并计算几何数据
        if (cornerData.topLeftCorner == null) cornerData.topLeftCorner = new CornerSegment();
        if (cornerData.topRightCorner == null) cornerData.topRightCorner = new CornerSegment();
        if (cornerData.bottomRightCorner == null) cornerData.bottomRightCorner = new CornerSegment();
        if (cornerData.bottomLeftCorner  == null) cornerData.bottomLeftCorner = new CornerSegment();

        // 每个角取 x/y 半径中的较小值，保证对称性
        cornerData.topLeftCorner.compute(Math.min(tlRadiusX, tlRadiusY), bounds, offsetX, offsetY, smoothness, CornerPosition.TOP_LEFT);
        cornerData.topRightCorner.compute(Math.min(clampedTrX, clampedTrY), bounds, offsetX, offsetY, smoothness, CornerPosition.TOP_RIGHT);
        cornerData.bottomRightCorner.compute(Math.min(clampedBrX, clampedBrY), bounds, offsetX, offsetY, smoothness, CornerPosition.BOTTOM_RIGHT);
        cornerData.bottomLeftCorner .compute(Math.min(blRadiusX, clampedBlY), bounds, offsetX, offsetY, smoothness, CornerPosition.BOTTOM_LEFT);

        return cornerData;
    }

    /**
     * 判断垂直方向上半径是否受到矩形高度的约束（即两倍半径+平滑延伸超过了可用高度）。
     *
     * @param availableHeight 可用高度
     * @param radius1         第一个角的半径
     * @param radius2         第二个角的半径
     * @param smoothness      平滑度 (0.0 - 1.0)
     * @param smoothFactor    平滑系数
     * @return 若高度不足则返回 true
     */
    public static boolean isHeightConstrained(float availableHeight, float radius1, float radius2,
                                              double smoothness, float smoothFactor) {
        return availableHeight <= ((smoothness * smoothFactor) + 1.0d) * (radius1 + radius2);
    }

    /**
     * 判断水平方向上半径是否受到矩形宽度的约束（即两倍半径+平滑延伸超过了可用宽度）。
     *
     * @param availableWidth 可用宽度
     * @param radius1        第一个角的半径
     * @param radius2        第二个角的半径
     * @param smoothness     平滑度 (0.0 - 1.0)
     * @param smoothFactor   平滑系数
     * @return 若宽度不足则返回 true
     */
    public static boolean isWidthConstrained(float availableWidth, float radius1, float radius2,
                                             double smoothness, float smoothFactor) {
        return availableWidth <= ((smoothness * smoothFactor) + 1.0d) * (radius1 + radius2);
    }

    /**
     * 将弧度转换为角度。
     *
     * @param radians 弧度值
     * @return 对应的角度值
     */
    public static double radiansToDegrees(double radians) {
        return (radians * 180.0d) / Math.PI;
    }
}
