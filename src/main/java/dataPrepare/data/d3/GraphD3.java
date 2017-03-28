package dataPrepare.data.d3;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by guardeec on 13.03.17.
 */
public class GraphD3 {
    private List<HostD3> nodes;
    private List<LinkD3> links;

    public GraphD3() {
        this.nodes = new LinkedList<>();
        this.links = new LinkedList<>();
    }

    public void addNode(int id, float x, float y, boolean fixed){
        if (!graphContainsNode(id)){
            nodes.add(new HostD3(id, x, y, fixed));
        }
    }

    public void addLink(int from, int to){
        if (graphContainsNode(from) && graphContainsNode(to) && !graphContainsLink(from, to)){
            links.add(new LinkD3(from, to));
        }
    }

    public int getHost(float x, float y){
        for (HostD3 hostD3 : nodes){
            if (hostD3.getX()==x && hostD3.getY()==y){
                return hostD3.getId();
            }
        }
        return -1;
    }

    public String getJSON(){
        return new Gson().toJson(this);
    }

    private boolean graphContainsNode(int id){
        for (HostD3 host : nodes){
            if (host.getId()==id){
                return true;
            }
        }
        return false;
    }

    private boolean graphContainsLink(int from, int to){
        for (LinkD3 linkD3 : links){
            if ((linkD3.getSource()==from || linkD3.getTarget()==from) && (linkD3.getSource()==to || linkD3.getTarget()==to)){
                return true;
            }
        }
        return false;
    }
}
