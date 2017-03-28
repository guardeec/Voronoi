package dataPrepare.data.graph;

import com.google.gson.Gson;

import java.util.*;

/**
 * Created by anna on 27.08.15.
 */
public class Graph {
    private List<Host> nodes;
    private Map<Host, List<Host>> links;

    public Graph copy(){
        Graph copy = new Graph();
        for (Host host : this.nodes){
            copy.setHost(host);
        }
        for (Host host : this.nodes){
            for (Host rel : this.links.get(host)){
                copy.setRelation(host, rel);
            }
        }
        return copy;
    }

    public Graph() {
        this.nodes = new LinkedList<>();
        this.links = new HashMap<>();
    }
    public Graph(Graph graph){
        this.nodes = new LinkedList<>();
        this.links = new HashMap<>();
        for (Host host : graph.getHosts()){
            Host cloneHost = new Host(host);
            this.nodes.add(cloneHost);
            this.links.put(cloneHost, new LinkedList<Host>());
        }
        for (Map.Entry<Host, List<Host>> hostListEntry : graph.getRelations().entrySet()){
            Host clonedHost = null;
            for (Host host : this.nodes){
                if (hostListEntry.getKey().equals(host)){
                    clonedHost = host;
                    break;
                }
            }
            List<Host> relations = this.links.get(clonedHost);
            for (Host host : hostListEntry.getValue()){
                Host clonedRelationHost = null;
                for (Host host1 : this.nodes){
                    if (host1.equals(host)){
                        clonedRelationHost = host1;
                    }
                }
                relations.add(clonedRelationHost);
            }
        }
    }

    public void setHost(Host host){
        nodes.add(host);
        links.put(host, new LinkedList<Host>());
    }
    public List<Host> getHosts(){
        return nodes;
    }
    public void removeHost(Host host){
        Iterator<Map.Entry<Host, List<Host>>> hostListEntryIterator = links.entrySet().iterator();
        while (hostListEntryIterator.hasNext()){
            Map.Entry<Host, List<Host>> hostListEntry = hostListEntryIterator.next();
            Iterator<Host> linkedHostsIterator = hostListEntry.getValue().iterator();
            while (linkedHostsIterator.hasNext()){
                Host linkedHost = linkedHostsIterator.next();
                if (linkedHost.equals(host)){
                    linkedHostsIterator.remove();
                }
            }
            if (hostListEntry.getKey().equals(host)){
                hostListEntryIterator.remove();
            }
        }
        nodes.remove(host);
    }

    public void setRelations(Host hostFrom, List<Host> hostsTo){
        if (nodes.contains(hostFrom) && nodes.containsAll(hostsTo)){
            for (Host host : hostsTo){
                setRelation(hostFrom, host);
            }

        }else {
            throw new IllegalArgumentException("Argument(HOST) is not exist");
        }


    }
    public void setRelation(Host hostFrom, Host hostTo){
        if (nodes.contains(hostFrom) && nodes.contains(hostTo)){
            if (!links.get(hostFrom).contains(hostTo)){
                links.get(hostFrom).add(hostTo);
            }
            if (!links.get(hostTo).contains(hostFrom)){
                links.get(hostTo).add(hostFrom);
            }
        }else {
            throw new IllegalArgumentException("Argument(HOST) is not exist");
        }
    }

    public Map<Host, List<Host>> getRelations(){
        return this.links;
    }
    public List<Host> getRelations(Host host){
        if (nodes.contains(host)){
            return links.get(host);
        }else {
            throw new IllegalArgumentException("Host not found");
        }

    }

    public Integer getHostNumber(){
        return nodes.size();
    }
    public Integer getRelationNumber(){
        int counter =0;
        for (Host host : nodes){
            counter += links.get(host).size();
        }
        return counter;
    }

    public String getGraphJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public List<List<Host>> getEdges(){
        List<List<Host>> edges = new ArrayList<>();
        for (Host hostFrom : nodes){
            for (Host hostTo : links.get(hostFrom)){
                boolean contains = false;
                for (List<Host> edge : edges){
                    if ((edge.get(0).equals(hostFrom) && edge.get(1).equals(hostTo))
                            ||
                            (edge.get(1).equals(hostFrom) && edge.get(0).equals(hostTo))
                            ){
                        contains=true;
                    }
                }
                if (!contains){
                    List<Host> edge = new LinkedList<>();
                    edge.add(hostFrom);
                    edge.add(hostTo);
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    public boolean checkOnPlanar(){
        List<List<Host>> edges = getEdges();
        for (int i=0; i<edges.size(); i++){
            for (int q=i; q<edges.size(); q++){
                if (
                        edges.get(i)!=edges.get(q)
                                &&
                                edges.get(i).get(0)!=edges.get(i).get(1)
                                &&
                                edges.get(i).get(0)!=edges.get(q).get(0)
                                &&
                                edges.get(i).get(0)!=edges.get(q).get(1)
                                &&
                                edges.get(i).get(1)!=edges.get(q).get(0)
                                &&
                                edges.get(i).get(1)!=edges.get(q).get(1)
                                &&
                                edges.get(q).get(0)!=edges.get(q).get(1)
                        ) {
                    if (IsLinePartsIntersected(
                            edges.get(i).get(0).getCoordinate(),
                            edges.get(i).get(1).getCoordinate(),
                            edges.get(q).get(0).getCoordinate(),
                            edges.get(q).get(1).getCoordinate()
                    )) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private boolean IsLinePartsIntersected(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {
        double common = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());

        if (common == 0) return false;

        double rH = (a.getY() - c.getY())*(d.getX() - c.getX()) - (a.getX() - c.getX())*(d.getY() - c.getY());
        double sH = (a.getY() - c.getY())*(b.getX() - a.getX()) - (a.getX() - c.getX())*(b.getY() - a.getY());

        double r = rH / common;
        double s = sH / common;

        if (r >= 0 && r <= 1 && s >= 0 && s <= 1)
            return true;
        else
            return false;
    }
}
