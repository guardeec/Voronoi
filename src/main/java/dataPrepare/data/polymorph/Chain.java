package dataPrepare.data.polymorph;

import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.methods.HamiltonianCycle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by guardeec on 20.03.17.
 */
public class Chain {
    /*
    //
    //// Ф-ИИ ДЛЯ ОБХОДА ДИАГРАММЫ
    //
     */
    public List<Polygon> getChain(Voronoi voronoi){
        List<List<Polygon>> layers = getLayers(voronoi.getPolygons().stream().collect(Collectors.toCollection(LinkedList::new)));
        List<Graph> graphs = getLayersAsGraph(layers);
        makeChain(graphs);
        List<Polygon> sortedPolygons = voronoi.getPolygons().stream().collect(Collectors.toCollection(LinkedList::new));
        sortedPolygons.sort((o1, o2) -> {
            int o1Id=(int)o1.getHost().getMetrics().get("cycleId"),
                    o2Id=(int)o2.getHost().getMetrics().get("cycleId"),
                    o1Order=(int)o1.getHost().getMetrics().get("cycleOrder"),
                    o2Order=(int)o2.getHost().getMetrics().get("cycleOrder");
            if (o1Id>o2Id){return -1;}
            if (o1Id<o2Id){return 1;}
            if (o1Id==o2Id){
                if (o1Order>o2Order){return -1;}
                if (o1Order<o2Order){return 1;}
            }
            return 0;
        });
        return sortedPolygons;
    }
    //находит гамильтоновы пути и расставляет порядок обхода в соответсвии с путями
    private void makeChain(List<Graph> graphs){
        Map<Graph, List<Graph>> graphSlices = new HashMap<>();
        int cycleId = 0;
        for (Graph graph : graphs){
            System.out.println(1);
            int[][] graphMatrix = new int[graph.getHostNumber()][graph.getHostNumber()];
            for (int i=0; i<graph.getHostNumber(); i++){for (int q=0; q<graph.getHostNumber(); q++){graphMatrix[i][q]=0;}}
            for (int i=0; i<graph.getHostNumber(); i++){graph.getHosts().get(i).addMetric("hamiltonId", i);}
            for (Host from : graph.getHosts()){
                for (Host to : graph.getRelations().get(from)){
                    graphMatrix[(int)from.getMetrics().get("hamiltonId")][(int)to.getMetrics().get("hamiltonId")]=1;
                }
            }
            int[] path = new HamiltonianCycle().findHamiltonianCycle(graphMatrix);

            if (path==null){
                while (path==null){
                    int[][] graphMatrixNew = new int[graphMatrix[0].length+1][graphMatrix[0].length+1];
                    for (int i=0; i<graphMatrix.length; i++){
                        System.arraycopy(graphMatrix[i], 0, graphMatrixNew[i], 0, graphMatrix[0].length);
                    }
                    for (int i=0; i<graphMatrix.length+1; i++){
                        if (i<graph.getHostNumber()-1){
                            graphMatrixNew[i][graphMatrix.length]=1;
                            graphMatrixNew[graphMatrix.length][i]=1;
                        }else {
                            graphMatrixNew[i][graphMatrix.length]=0;
                            graphMatrixNew[graphMatrix.length][i]=0;
                        }
                    }
                    graphMatrix=graphMatrixNew;
                    path = new HamiltonianCycle().findHamiltonianCycle(graphMatrix);
                }
                List<int[]> pathes = new LinkedList<>();
                List<Integer> cutId = new LinkedList<>();


                for (int i = 0; i<path.length; i++){if (path[i]>=graph.getHostNumber()){cutId.add(i);}}
                cutId.add(path.length);
                for (int i=0, pointer=0; i<cutId.size(); i++){
                    int p[] = Arrays.copyOfRange(path, pointer, cutId.get(i));
                    pointer=cutId.get(i)+1;
                    pathes.add(p);
                }

                for (int[] slice : pathes){
                    int cycleOrder = 0;
                    for (int index : slice){
                        for (Host host : graph.getHosts()){
                            if ((int)host.getMetrics().get("hamiltonId")==index){
                                host.addMetric("cycleOrder", cycleOrder);
                                host.addMetric("cycleId", cycleId);
                                cycleOrder++;
                                break;
                            }
                        }
                    }
                    cycleId++;
                }
            }else {

                for (Host host : graph.getHosts()){
                    host.addMetric("cycleId", cycleId);
                    int cycleOrder = 0;
                    for (int i=0; i<path.length; i++){if (path[i]==(int)host.getMetrics().get("hamiltonId")){cycleOrder=i; break;}}
                    host.addMetric("cycleOrder", cycleOrder);
                }
                cycleId++;
            }
        }
    }
    //превращает слои в граф
    private List<Graph> getLayersAsGraph(List<List<Polygon>> layers ){
        List<Graph> layersAsGraphs = new LinkedList<>();
        for (List<Polygon> layer : layers){
            Graph graph = new Graph();
            for (Polygon polygon : layer){
                graph.setHost(polygon.getHost());
            }
            for (Polygon polygon : layer){
                List<Polygon> linkedHosts = getNearestPolygons(polygon, layer);
                for (Polygon linked : linkedHosts){
                    graph.setRelation(polygon.getHost(), linked.getHost());
                }
            }
            layersAsGraphs.add(graph);
        }
        return layersAsGraphs;
    }
    //возвращает список соседних полигонов
    private List<Polygon> getNearestPolygons(Polygon target, List<Polygon> polygons){
        List<Polygon> nearestPolygons = new LinkedList<>();
        List<Coordinate[]> edgesTarget = getEdges(target);
        for (Polygon polygon : polygons){
            List<Coordinate[]> edgesPolygon = getEdges(polygon);
            OUT: for (Coordinate[] edgeTarget : edgesTarget){
                for (Coordinate[] edgePolygon : edgesPolygon){
                    if (equalEdges(edgeTarget, edgePolygon)){
                        nearestPolygons.add(polygon);
                        break OUT;
                    }
                }
            }
        }
        return nearestPolygons;
    }
    //возвращает слои
    private List<List<Polygon>> getLayers(List<Polygon> polygons){
        List<List<Polygon>> layers = new LinkedList<>();
        while (polygons.size()>0){
            List<Polygon> CH = getConvexPolygons(polygons);
            layers.add(CH);
            polygons.removeAll(CH);
        }
        return layers;
    }
    //возвращает все полигоны принадлежащие оболочке
    private List<Polygon> getConvexPolygons(List<Polygon> polygons){
        return polygons.stream().filter(p -> isPolygonConvex(polygons, p)).collect(Collectors.toCollection(LinkedList::new));
    }
    //возращает все ребра полигона
    private List<Coordinate[]> getEdges(Polygon polygon){
        List<Coordinate[]> edges = new LinkedList<>();
        for (int i=0; i<polygon.getPoints().size(); i++){
            Coordinate[] edge = {polygon.getPoints().get(i), polygon.getPoints().get((i+1)%polygon.getPoints().size())};
            edges.add(edge);
        }
        return edges;
    }
    //проверка принадлежности полигона к оболочке
    private boolean isPolygonConvex(List<Polygon> polygons, Polygon polygon){
        List<Coordinate[]> edges = getEdges(polygon), anotherEdges = new LinkedList<>();
        polygons.stream().filter(p -> p != polygon).forEach(p -> anotherEdges.addAll(getEdges(p)));
        Iterator<Coordinate[]> iterator = edges.iterator();
        while (iterator.hasNext()){
            Coordinate[] e1 = iterator.next();
            for (Coordinate[] e2 : anotherEdges){if (equalEdges(e1, e2)){iterator.remove();break;}}
        }
        return edges.size()!=0;
    }
    //сравнение ребер
    private boolean equalEdges(Coordinate[] edge1, Coordinate[] edge2){return (edge1[0]==edge2[0] || edge1[0]==edge2[1]) && (edge1[1]==edge2[0] || edge1[1]==edge2[1]);}
}
