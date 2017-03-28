package dataPrepare.methods;

import dataPrepare.data.graph.Coordinate;

import java.util.*;

/**
 * Created by guardeec on 20.07.16.
 */
public class Dejkstra {
    private class Vertex {
        final private String id;
        final private String name;


        public Vertex(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex other = (Vertex) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return name;
        }

    }
    private class Edge {
        private final String id;
        private final Vertex source;
        private final Vertex destination;
        private final int weight;

        public Edge(String id, Vertex source, Vertex destination, int weight) {
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        public String getId() {
            return id;
        }

        public Vertex getDestination() {
            return destination;
        }

        public Vertex getSource() {
            return source;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return source + " " + destination;
        }
    }
    private class Graph {
        private final List<Vertex> vertexes;
        private final List<Edge> edges;

        public Graph(List<Vertex> vertexes, List<Edge> edges) {
            this.vertexes = vertexes;
            this.edges = edges;
        }

        public List<Vertex> getVertexes() {
            return vertexes;
        }

        public List<Edge> getEdges() {
            return edges;
        }



    }

    private final List<Vertex> nodes;
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;

    public Dejkstra(List<Coordinate> nodes, List<List<Coordinate>> edges) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<Vertex>();
        for (Coordinate coordinate : nodes){
            Vertex vertex = new Vertex(coordinate.toString(), coordinate.toString());
            this.nodes.add(vertex);
        }
        this.edges = new ArrayList<Edge>();
        for (List<Coordinate> edge : edges){
            Edge edge1 = new Edge(
                    edge.get(0).toString()+edge.get(1).toString(),
                    getVertexById(edge.get(0).toString()),
                    getVertexById(edge.get(1).toString()),
                    lengthOfEdge(edge)
            );
            Edge edge2 = new Edge(
                    edge.get(1).toString()+edge.get(0).toString(),
                    getVertexById(edge.get(1).toString()),
                    getVertexById(edge.get(0).toString()),
                    lengthOfEdge(edge)
            );
            this.edges.add(edge1);
            this.edges.add(edge2);
        }
    }
    private Vertex getVertexById(String id){
        for (Vertex vertex : nodes){
            if (vertex.getId().equals(id)){
                return vertex;
            }
        }
        return null;
    }
    private Coordinate getCoordinateByVertex(Vertex vertex, List<Coordinate> coordinates){
        for (Coordinate coordinate : coordinates){
            if (vertex.getId().equals(coordinate.toString())){
                return coordinate;
            }
        }
        return null;
    }
    private int lengthOfEdge(List<Coordinate> edge){
        return (int) Math.sqrt( Math.pow(edge.get(0).getX()-edge.get(1).getX(), 2) + Math.pow(edge.get(0).getY()-edge.get(1).getY(), 2));
    }

    public void execute(Coordinate sourceId) {
        Vertex source = getVertexById(sourceId.toString());
        settledNodes = new HashSet<Vertex>();
        unSettledNodes = new HashSet<Vertex>();
        distance = new HashMap<Vertex, Integer>();
        predecessors = new HashMap<Vertex, Vertex>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && !isSettled(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public List<Coordinate> getPath(Coordinate targetCoordinate, List<Coordinate> coordinates) {
        Vertex target = getVertexById(targetCoordinate.toString());

        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }

        // Put it into the correct order
        Collections.reverse(path);
        List<Coordinate> coordinatePath = new LinkedList<>();
        for (Vertex vertex : path){
            coordinatePath.add(getCoordinateByVertex(vertex, coordinates));
        }

        return coordinatePath;
    }

}
