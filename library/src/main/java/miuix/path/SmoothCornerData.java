package miuix.path;

public class SmoothCornerData {

    public final float width;
    public final float height;
    public final double smoothness;
    public CornerSegment topLeftCorner = null;
    public CornerSegment topRightCorner = null;
    public CornerSegment bottomRightCorner = null;
    public CornerSegment bottomLeftCorner = null;

    /**
     * 平滑圆角数据
     * 
     * @param width      宽度
     * @param height     高度
     * @param smoothness 平滑度 (0.0 - 1.0)
     */
    public SmoothCornerData(float width, float height, double smoothness) {
        this.width = width;
        this.height = height;
        this.smoothness = smoothness;
    }
}
