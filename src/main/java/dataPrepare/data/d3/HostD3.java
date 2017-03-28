package dataPrepare.data.d3;

/**
 * Created by guardeec on 13.03.17.
 */
public class HostD3 {
    private int id;
    private float x;
    private float y;
    private boolean fixed;

    public HostD3(int id, float x, float y, boolean fixed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.fixed = fixed;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
