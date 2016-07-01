package dataPrepare.data;

import java.util.List;

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
