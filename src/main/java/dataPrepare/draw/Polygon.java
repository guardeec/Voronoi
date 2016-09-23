package dataPrepare.draw;

import java.util.List;

/**
 * Created by guardeec on 05.07.16.
 */
public class Polygon implements PolygonVoronoiImpl{
    List listOfDots;
    List length;
    List straight;
    int size;


    public List<CoordinateVoronoiImpl> getPDots() {
        return listOfDots;
    }

    public void setPDots(List<CoordinateVoronoiImpl> dots, List<Integer> length, List<Integer> straight) {
        this.listOfDots = dots;
        this.length = length;
        this.straight = straight;
    }


    public int getSize() {
        return size;
    }

    @Override
    public List<CoordinateVoronoiImpl> getVDots() {
        return listOfDots;
    }

    @Override
    public void setVDots(List<CoordinateVoronoiImpl> dots) {
        this.listOfDots = dots;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }
}
