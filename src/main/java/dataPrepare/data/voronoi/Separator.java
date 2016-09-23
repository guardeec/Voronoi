package dataPrepare.data.voronoi;

import dataPrepare.data.graph.Coordinate;

/**
 * Created by Guardeec on 31.03.16.
 */
public class Separator {
    private Coordinate from;
    private Coordinate to;

    public Separator(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }
    public Coordinate getFrom() {
        return from;
    }
    public Coordinate getTo() {
        return to;
    }
}
