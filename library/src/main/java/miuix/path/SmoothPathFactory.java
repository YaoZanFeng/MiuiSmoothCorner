package miuix.path;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

public class SmoothPathFactory {

    /**
     * 根据 8 个半径值生成平滑圆角路径
     *
     * @param bounds     整体矩形区域
     * @param radii      8个半径值
     * @param smoothness 平滑度 (0.0 - 1.0)
     * @return 生成的平滑路径
     */
    public static Path createSmoothPath(RectF bounds, float[] radii, double smoothness) {
        SmoothCornerData data = SmoothPathProvider.computeSmoothCorners(bounds, radii, smoothness, 0, 0);
        
        if (data == null) return new Path();

        Path path = new Path();
        
        CornerSegment tl = data.topLeftCorner;
        CornerSegment tr = data.topRightCorner;
        CornerSegment br = data.bottomRightCorner;
        CornerSegment bl = data.bottomLeftCorner;

        float tlSweep = 90f - (float)Math.toDegrees(tl.hAngle) - (float)Math.toDegrees(tl.vAngle);
        float trSweep = 90f - (float)Math.toDegrees(tr.hAngle) - (float)Math.toDegrees(tr.vAngle);
        float brSweep = 90f - (float)Math.toDegrees(br.hAngle) - (float)Math.toDegrees(br.vAngle);
        float blSweep = 90f - (float)Math.toDegrees(bl.hAngle) - (float)Math.toDegrees(bl.vAngle);

        path.moveTo(tl.hControlPoints[3].x, tl.hControlPoints[3].y);

        path.lineTo(tr.hControlPoints[0].x, tr.hControlPoints[0].y);
        cubicTo(path, tr.hControlPoints[1], tr.hControlPoints[2], tr.hControlPoints[3]);
        path.arcTo(tr.bounds, 270f + (float)Math.toDegrees(tr.hAngle), trSweep, false);
        cubicTo(path, tr.vControlPoints[1], tr.vControlPoints[2], tr.vControlPoints[3]);

        path.lineTo(br.vControlPoints[0].x, br.vControlPoints[0].y);
        cubicTo(path, br.vControlPoints[1], br.vControlPoints[2], br.vControlPoints[3]);
        path.arcTo(br.bounds, (float)Math.toDegrees(br.vAngle), brSweep, false);
        cubicTo(path, br.hControlPoints[1], br.hControlPoints[2], br.hControlPoints[3]);

        path.lineTo(bl.hControlPoints[0].x, bl.hControlPoints[0].y);
        cubicTo(path, bl.hControlPoints[1], bl.hControlPoints[2], bl.hControlPoints[3]);
        path.arcTo(bl.bounds, 90f + (float)Math.toDegrees(bl.hAngle), blSweep, false);
        cubicTo(path, bl.vControlPoints[1], bl.vControlPoints[2], bl.vControlPoints[3]);

        path.lineTo(tl.vControlPoints[0].x, tl.vControlPoints[0].y);
        cubicTo(path, tl.vControlPoints[1], tl.vControlPoints[2], tl.vControlPoints[3]);
        path.arcTo(tl.bounds, 180f + (float)Math.toDegrees(tl.vAngle), tlSweep, false);
        cubicTo(path, tl.hControlPoints[1], tl.hControlPoints[2], tl.hControlPoints[3]);

        path.close();
        return path;
    }

    private static void cubicTo(Path path, PointF p1, PointF p2, PointF p3) {
        path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }
}
