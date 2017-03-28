package dataPrepare.data;

import com.google.gson.Gson;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.Triangle;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.methods.Triangulate;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by guardeec on 04.10.16.
 */
public class TestD3 {



    public void force_OUTPUT(Voronoi voronoi, String json){
        Gson gson = new Gson();
        Map<String,List<Map<String, Object>>> paramsGraph = gson.fromJson(json, (Type) Map.class);
        int id=0;
        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.addMetric("coordinateId", id);
            id++;
        }

        List<Map<String, Object>> nodes = paramsGraph.get("nodes");
        for(Map<String, Object> node : nodes){
            String name = (String) node.get("name");
            for(Coordinate coordinate1 : voronoi.getDots()){
                if ((int)coordinate1.getMetric("coordinateId")==Integer.parseInt(name)){
                    coordinate1.setX(
                            Float.parseFloat(Double.toString((double)node.get("x")))
                    );
                    coordinate1.setY(
                            Float.parseFloat(Double.toString((double)node.get("y")))
                    );
                    coordinate1.addMetric("stopPolymorph", node.get("fixed"));
                }
            }



        }
    }

    private boolean checkOnFake(Voronoi voronoi, Edge edge, Graph graph){
        List<Polygon> polygonList = new LinkedList<>();
        for (Polygon polygon : voronoi.getPolygons()){
            if (polygon.getPoints().contains(edge.coordinate1) && polygon.getPoints().contains(edge.coordinate2)){
                polygonList.add(polygon);
            }
        }
        if (polygonList.size()>1){

            Host host1 = polygonList.get(0).getHost();
            Host host2 = polygonList.get(1).getHost();

            if (
                graph.getRelations().get(host1).contains(host2)
                ||
                graph.getRelations().get(host2).contains(host1)
            ){
                return false;
            }
        }
        if (polygonList.size()>2 || polygonList.size()==0){
            System.out.println("ERROR");
        }
        return true;
    }

    public String force_INPUT(Voronoi voronoi, Graph graph){
        ParamsGraph paramsGraph = new ParamsGraph();
        int id=0;
        for (Coordinate coordinate : voronoi.getDots()){
            if ((boolean)coordinate.getMetric("stopPolymorph")){
                coordinate.addMetric("fixed", true);
            }else {
                coordinate.addMetric("fixed", false);
            }
        }
        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.addMetric("coordinateId", id);
            paramsGraph.addNode(
                    Integer.toString(id),
                    coordinate.getX(),
                    coordinate.getY(),
                    (boolean)coordinate.getMetric("fixed")
            );
            id++;
        }

        List<Edge> edges = new LinkedList<>();
        for (Polygon polygon : voronoi.getPolygons()){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Edge edge = new Edge(polygon.getPoints().get(i), polygon.getPoints().get((i+1)%polygon.getPoints().size()));
                if (checkEdge(edge, edges)){
                    edges.add(edge);
                }
            }
        }
        for (Edge edge : edges){
            paramsGraph.addLink(
                    (int)edge.coordinate1.getMetric("coordinateId"),
                    (int) edge.getCoordinate2().getMetric("coordinateId"),
                    checkOnFake(voronoi, edge, graph)
            );
        }

        return paramsGraph.getJSON();
//
//        Graph graph = voronoi.voronoiLikeAGraph(voronoi);
//        List<Triangle> triangles = Triangulate.make(graph);
//
//        for (Triangle triangle : triangles){
//            triangle.setFirstHost(graph.getHosts().get(triangle.getFirstDot()));
//            triangle.setSecondHost(graph.getHosts().get(triangle.getSecondDot()));
//            triangle.setThirdHost(graph.getHosts().get(triangle.getThirdDot()));
//            Edge edge1 = new Edge(
//                    triangle.getFirstHost().getCoordinate(),
//                    triangle.getSecondHost().getCoordinate()
//            );
//            Edge edge2 = new Edge(
//                    triangle.getSecondHost().getCoordinate(),
//                    triangle.getThirdHost().getCoordinate()
//            );
//            Edge edge3 = new Edge(
//                    triangle.getThirdHost().getCoordinate(),
//                    triangle.getFirstHost().getCoordinate()
//            );
//            if (checkEdge(edge1, edges)){
//                paramsGraph.addLink(
//                        (int)edge1.coordinate1.getMetric("coordinateId"),
//                        (int)edge1.coordinate2.getMetric("coordinateId"),
//                        true);
//            }
//            if (checkEdge(edge2, edges)){
//                paramsGraph.addLink(
//                        (int)edge2.coordinate1.getMetric("coordinateId"),
//                        (int)edge2.coordinate2.getMetric("coordinateId"),
//                        true);
//            }
//            if (checkEdge(edge3, edges)){
//                paramsGraph.addLink(
//                        (int)edge3.coordinate1.getMetric("coordinateId"),
//                        (int)edge3.coordinate2.getMetric("coordinateId"),
//                        true);
//            }
//        }
//        System.out.println(paramsGraph.getJSON());
    }


    public void force_INPUT(Graph graph){
        ParamsGraph paramsGraph = new ParamsGraph();

        Map<Host, Integer> ids = new HashMap<>();
        int i=0;
        for (Host host : graph.getHosts()){
            ids.put(host, i);
            paramsGraph.addNode(
                    ((Integer)host.getMetrics().get("id")).toString(),
                    host.getCoordinate().getX(),
                    host.getCoordinate().getY(),
                    false
            );
            i++;
        }

        for(Host host : graph.getHosts()){
            List<Host> hostsTo = graph.getRelations(host);
            for (Host hostTo:hostsTo){
                paramsGraph.addLink(
                        ids.get(host),
                        ids.get(hostTo),
                        false
                );
            }
        }
        System.out.println(paramsGraph.getJSON());


    }

    public void force_OUTPUT(Graph graph, String json){
        Gson gson = new Gson();
        Map<String,List<Map<String, Object>>> paramsGraph = gson.fromJson(json, (Type) Map.class);
        List<Map<String, Object>> nodes = paramsGraph.get("nodes");
        for(Map<String, Object> node : nodes){
            String name = (String) node.get("name");
            for(Host host : graph.getHosts()){
                if ((int)host.getMetrics().get("id")==Integer.parseInt(name)){
                    host.getCoordinate().setX(
                            Float.parseFloat(Double.toString((double)node.get("x")))
                    );
                    host.getCoordinate().setY(
                            Float.parseFloat(Double.toString((double)node.get("y")))
                    );
                }
            }
        }
    }



    private boolean checkEdge(Edge edge, List<Edge> edges){
        for (Edge edge1 : edges){
            if (
            (edge1.getCoordinate1()==edge.getCoordinate1() && edge1.getCoordinate2()==edge.getCoordinate2())
            ||
            (edge1.getCoordinate2()==edge.getCoordinate1() && edge1.getCoordinate1()==edge.getCoordinate2())
            ){
                return false;
            }
        }
        return true;
    }

    private class Edge{
        Coordinate coordinate1;
        Coordinate coordinate2;

        public Edge(Coordinate coordinate1, Coordinate coordinate2) {
            this.coordinate1 = coordinate1;
            this.coordinate2 = coordinate2;
        }

        public Coordinate getCoordinate1() {
            return coordinate1;
        }

        public Coordinate getCoordinate2() {
            return coordinate2;
        }
    }


    public class ParamsGraph {
        public Map<String, List> graph;

        public void setGraph(Map<String, List> graph) {
            this.graph = graph;
        }

        /*
                В конструкторе инициализуются списки нодов и линков
                 */
        public ParamsGraph() {
            graph = new LinkedHashMap<String, List>();
            List<Map> nodes = new LinkedList<Map>();
            List<Map> links = new LinkedList<Map>();
            graph.put("nodes", nodes);
            graph.put("links", links);
        }

        /*
        Добавление ноды
         */
        //нужно определииь типы нодов
        public void addNode(String name, float x, float y, boolean stop){
            Map<String, Object> node = new LinkedHashMap<String, Object>();
            node.put("name", name);         //имя ноды
            node.put("x", x);
            node.put("y", y);
            node.put("fixed", stop);
            graph.get("nodes").add(node);
        }

        /*
        Добавление Линков
         */
        public void addLink(int source, int target, boolean fake){
            Map<String, Object> link = new LinkedHashMap<String, Object>();
            link.put("source", source);     //имя ноды - сорс линка
            link.put("target", target);     //имя ноды - таргет линка
            link.put("fake", fake);
            graph.get("links").add(link);
        }

        //получить JSON представление инстанса
        //необходимо для парсинга даты в html файле
        public String getJSON(){
            Gson gson = new Gson();
            return gson.toJson(graph);
        }
    }
    
}
