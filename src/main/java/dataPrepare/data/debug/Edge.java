package dataPrepare.data.debug;

import java.util.List;
import java.util.Map;

/**
 * Created by guardeec on 21.03.17.
 */
public class Edge {
    private Dot from;
    private List<Dot> to;

    public Edge(Dot from, List<Dot> to) {
        this.from = from;
        this.to = to;
    }

    public Dot getFrom() {
        return from;
    }

    public void setFrom(Dot from) {
        this.from = from;
    }

    public List<Dot> getTo() {
        return to;
    }

    public void setTo(List<Dot> to) {
        this.to = to;
    }
}
