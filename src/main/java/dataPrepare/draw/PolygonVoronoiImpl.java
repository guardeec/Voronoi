package dataPrepare.draw;

import java.util.List;

/**
 * Created by guardeec on 05.07.16.
 */
public interface PolygonVoronoiImpl {
    public List<CoordinateVoronoiImpl> getVDots();
    public void setVDots(List<CoordinateVoronoiImpl> dots);

    public void setSize(int size);
}
