package dataPrepare.data.d3;

/**
 * Created by guardeec on 13.03.17.
 */
public class LinkD3 {
    private int source;
    private int target;

    public LinkD3(int source, int target) {
        this.source = source;
        this.target = target;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }
}
