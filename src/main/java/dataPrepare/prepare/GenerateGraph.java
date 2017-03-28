package dataPrepare.prepare;

import com.google.gson.Gson;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;

import java.util.*;

/**
 * Created by anna on 27.08.15.
 */
public class GenerateGraph {

    private static class Node{
        String name;
        Coordinate coordinate;
        List nodes;
        public Node(String name) {
            this.name = name;
            this.nodes = new LinkedList<>();
        }

        public void add(Node node){
            this.nodes.add(node);
        }
        public List<Node> get(){
            return nodes;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(Coordinate coordinate) {
            this.coordinate = coordinate;
        }
    }

    public static Graph generateTree(Integer k){
        Graph graph = new Graph();
        Host root =new Host(0, new Coordinate(0,0));
        graph.setHost(root);
        nodeToGraph(graph, root, k);
        return graph;
    }

    public static Graph generateTreeStatic(){
        Graph graph = new Graph();
        for (int i=0; i<50; i++){
            graph.setHost(new Host(1, new Coordinate(0,0)));
        }

        int[][] hostRelations = {
                {0,1},  {0,2},  {0,3}, {0,4}, {0, 5},
                {1,6}, {1,7}, {1,8}, {1,9}, {1,10}, {2, 11}, {2, 12},
                {2,13}, {2,14}, {2, 15}, {3, 16}, {3, 17}
        };

        for (int i=0; i<hostRelations.length; i++){
            graph.setRelation(
                    graph.getHosts().get(hostRelations[i][0]),
                    graph.getHosts().get(hostRelations[i][1])
            );
        }
        for (int i=18; i<50; i++){
            graph.setRelation(
                    graph.getHosts().get(new Random().nextInt(17)),
                    graph.getHosts().get(i)
            );
        }
        return graph;
    }

    public static Graph generateTreeSuperStatic(){
        Graph graph = new Graph();


        int[][] hostRelations = {
                {0,1},  {0,2},  {0,3}, {0,4}, {0, 5},
                {1,6}, {1,7}, {1,8}, {1,9}, {1,10}, {2, 11}, {2, 12},
                {2,13}, {2,14}, {2, 15}, {3, 16}, {3, 17}
        };

        for (int i=0; i<18; i++){
            Host host = new Host(1, new Coordinate(0,0));
            float m = 500f;
            switch (i){
                case 0:{m*=63;break;}
                case 1:{m*=19;break;}
                case 2:{m*=18;break;}
                case 3:{m*=14;break;}
                case 4:{m*=4;break;}
                case 5:{m*=8;break;}
                case 6:{m*=6;break;}
                case 7:{m*=3;break;}
                case 8:{m*=1;break;}
                case 9:{m*=2;break;}
                case 10:{m*=7;break;}
                case 11:{m*=3;break;}
                case 12:{m*=2;break;}
                case 13:{m*=2;break;}
                case 14:{m*=5;break;}
                case 15:{m*=6;break;}
                case 16:{m*=9;break;}
                case 17:{m*=5;break;}
            }

//            m = 2000f;
//            switch (i){
//                case 0:{m*=5;break;}
//                case 1:{m*=6;break;}
//                case 2:{m*=6;break;}
//                case 3:{m*=3;break;}
//            }
            //m = 2000f;
            host.addMetric("cellSize", m);
            graph.setHost(host);
        }

        for (int i=0; i<hostRelations.length; i++){
            graph.setRelation(
                    graph.getHosts().get(hostRelations[i][0]),
                    graph.getHosts().get(hostRelations[i][1])
            );
        }


        return graph;
    }

    private static void nodeToGraph(Graph graph, Host host, int number){
        List<Host> hosts = new LinkedList<>();
        for (int i=0; i<5; i++, number--){
            Host h = new Host(number, new Coordinate(0,0));
            hosts.add(h);
            graph.setHost(h);
        }
        for (Host host1 : hosts){
            graph.setRelation(host, host1);
        }
        for (Host h : hosts){
            if (number>0){
                nodeToGraph(graph, h, number);
            }

        }
    }

    public static Graph generateConstantGraph(){
        Graph graph = new Graph();

        float[][] hostPositions = {   {11,15}, {13,27}, {23,15}, {31,11}, {37,21}, {43,26}, {35,35}, {23,25}, {13,14}, {10,13}, {9,15},
                                    {10,17}, {11,25}, {10,27}, {11,29}, {14,30}, {15,28}, {21,13}, {23,13}, {29,11}, {30, 9}, {32,10},
                                    {33,12}, {31,13}, {30,13}, {38,19}, {39,21}, {39,23}, {37,23}, {36,22}, {42,25}, {43,24}, {45,25},
                                    {45,27}, {44,28}, {35,33}, {37,35}, {36,37}, {34,37}, {33,35}, {25,25}, {24,23}, {21,24}, {22,27}
        };

        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(
                    new Host(
                            1, new Coordinate(
                                hostPositions[i][0]*22,
                                hostPositions[i][1]*22
                            )
                    )
            );
        }

        int[][] hostRelations = {   {0,8},  {0,9},  {0,10}, {0,11}, {0, 1},
                                    {1,12}, {1,13}, {1,14}, {1,15}, {1,16}, {1, 7}, {1, 2},
                                    {2,17}, {2,18}, {2, 7}, {2, 3}, {2, 4},
                                    {3,19}, {3,20}, {3,21}, {3,22}, {3,23}, {3,24}, {3, 4},
                                    {4,25}, {4,26}, {4,27}, {4,28}, {4,29},
                                    {5,30}, {5,31}, {5,32}, {5,33}, {5,34}, {5, 6},
                                    {6,35}, {6,36}, {6,37}, {6,38}, {6,39}, {6, 7},
                                    {7,40}, {7,41}, {7,42}, {7,43}
        };

        for (int i=0; i<hostRelations.length; i++){
            graph.setRelation(
                    graph.getHosts().get(hostRelations[i][0]),
                    graph.getHosts().get(hostRelations[i][1])
            );
        }
        return graph;
    }
}
