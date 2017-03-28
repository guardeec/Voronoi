package dataPrepare.data.debug;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by guardeec on 21.03.17.
 */
public class DebugVoronoiData {
    private List<Edge> edges;

    public DebugVoronoiData() {
        this.edges = new LinkedList<>();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
