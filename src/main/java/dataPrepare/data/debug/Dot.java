package dataPrepare.data.debug;

/**
 * Created by guardeec on 21.03.17.
 */
public class Dot {
    private float x;
    private float y;
    private boolean blocked;
    private int c;

    public Dot(float x, float y, boolean blocked, int c) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
        this.c = c;
    }
    public Dot(float x, float y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
}
