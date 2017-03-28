package dataPrepare.data.voronoi;

import dataPrepare.data.graph.Coordinate;

/**
 * Created by Guardeec on 31.03.16.
 */
public class Attractor {
    private Coordinate from;
    private Coordinate to;

    public Attractor(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }
    public boolean isAttractor(Coordinate from, Coordinate to){
        return (this.from==from || this.to==from) && (this.from==to || this.to==to);
    }
    public Coordinate getFrom() {
        return from;
    }
    public Coordinate getTo() {
        return to;
    }
}
