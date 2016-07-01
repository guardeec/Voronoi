package dataPrepare.prepare;

import dataPrepare.data.*;
import dataPrepare.voronoi.ConvexHull;
import dataPrepare.voronoi.Triangulate;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by guardeec on 26.04.16.
 */
public class GenereatePlanarGraphUsingTriangulation {

    public Graph generate(int frameSizeX, int frameSizeY, float coherence){
        Random random = new Random();
        Integer hostNumber;
        do {
            hostNumber = random.nextInt((frameSizeX+1)*(frameSizeY+1));
        }while (hostNumber<3);
        return generate(hostNumber, frameSizeX, frameSizeY, coherence);
    }

    public Graph generate(int hostNumber, int frameSizeX, int frameSizeY, float coherence){
        if (coherence<0 || coherence>1){
            throw new IllegalArgumentException("coherence must be between 0 and 1");
        }
        if (hostNumber<4){
            throw new IllegalArgumentException("Number of Hosts must be > 4");
        }
        if ((frameSizeX+1)*(frameSizeY+1)<hostNumber){
            throw new IllegalArgumentException("To much number of Hosts for field whith size X="+frameSizeX+" Y="+frameSizeY);
        }

        Random random = new Random();
        Graph graph = new Graph();

        //System.out.println("Nodes generation...");
        for (int i=0; i<hostNumber; i++){
            Coordinate coordinate;
            do {
               coordinate = new Coordinate(
                        random.nextInt(frameSizeX),
                        random.nextInt(frameSizeY)
                );
            } while (graphContainsDotWithSameCoordinates(coordinate, graph));
            graph.setHost(new Host(1, coordinate));
        }
        //System.out.println("Links generation...");
        graph = ConvexHull.make(graph);
        //System.out.println("    Making ConvexHull...");
        List<Triangle> triangles = Triangulate.make(graph);
        //System.out.println("    Making Triangulation...");
        for (TriangleDotsImpl triangle : triangles){
            graph.setRelation(
                    graph.getHosts().get(triangle.getFirstDot()),
                    graph.getHosts().get(triangle.getSecondDot())
            );
            graph.setRelation(
                    graph.getHosts().get(triangle.getSecondDot()),
                    graph.getHosts().get(triangle.getThirdDot())
            );
            graph.setRelation(
                    graph.getHosts().get(triangle.getThirdDot()),
                    graph.getHosts().get(triangle.getFirstDot())
            );
        }
        //System.out.println("Starting Rendering...");

        Graph clonedGraph = new Graph(graph);
        int counter = 0;
        int steps = (int) ((float) graph.getHostNumber()*1*coherence);
        while (counter<steps){
            clonedGraph.removeHost(clonedGraph.getHosts().get(random.nextInt(clonedGraph.getHostNumber())));
            if (checkConnectionInGraph(clonedGraph)){
                graph = new Graph(clonedGraph);
            }else {
                clonedGraph = new Graph(graph);
            }
            counter++;
        }
        return graph;
    }

    private boolean checkConnectionInGraph(Graph graph){
        Host startHost = graph.getHosts().get(0);
        List<Host> relatedHosts = graph.getRelations(startHost);
        startHost.setRadius(2);

        checkConnectionInGraphLoop(graph, relatedHosts);

        boolean graphIsConnected = true;
        for (Host host : graph.getHosts()){
            if (host.getRadius()<2){
                graphIsConnected = false;
            }
        }

        for (Host host : graph.getHosts()){
            host.setRadius(1);
        }
        return graphIsConnected;
    }

    private static void checkConnectionInGraphLoop(Graph graph, List<Host> relatedHosts){
        Iterator<Host> iterator = relatedHosts.iterator();
        while (iterator.hasNext()){
            Host host = iterator.next();
            if (host.getRadius()<2){
                host.setRadius(2);
                checkConnectionInGraphLoop(graph, graph.getRelations(host));
            }
        }
    }

    private boolean graphContainsDotWithSameCoordinates(Coordinate coordinate, Graph graph){
        boolean contains = false;
        for (Host host : graph.getHosts()){
            if (host.getCoordinate().equals(coordinate)){
                contains = true;
                break;
            }
        }
        return contains;
    }
}
