package dataPrepare.draw;

import dataPrepare.*;
import dataPrepare.data.voronoi.Voronoi;

/**
 * Created by guardeec on 13.07.16.
 */
public class ThreadAnimation implements Runnable {
    @Override
    public void run() {
        Voronoi voronoi = new Voronoi(dataPrepare.Test2.getInstance().getGraph(), 500, 500);
    }
}
