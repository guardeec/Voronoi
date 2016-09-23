package dataPrepare.data.triangulation;

import dataPrepare.data.graph.Host;

/**
 * Created by guardeec on 28.06.16.
 */
public interface TriangleVoronoiImpl {
    public Host getFirstHost();
    public Host getSecondHost();
    public Host getThirdHost();
    public void setFirstHost(Host host);
    public void setSecondHost(Host host);
    public void setThirdHost(Host host);

}
