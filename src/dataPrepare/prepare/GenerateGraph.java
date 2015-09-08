package dataPrepare.prepare;

import dataPrepare.data.Graph;

import java.util.Random;

/**
 * Created by anna on 27.08.15.
 */
public class GenerateGraph {

    /*
    генерация рандомного НЕПЛАНАРНОГО графа
     */
    public static Graph generate(int nodes, int frameSizeX, int frameSizeY){

        Graph graph = new Graph();

        //заносим хосты
        for (int i=0; i<nodes; i++){
            Random random = new Random();
            int x = random.nextInt(frameSizeX);
            int y = random.nextInt(frameSizeY);
            graph.setHost(x, y);
        }

        //заносим отношения
        for (int i=0; i<graph.getHostsNumber(); i++){
            Random random = new Random();
            int relationsNumber = random.nextInt(graph.getHostsNumber());

            for (int q=0; q<relationsNumber; q++){
                int toId = random.nextInt(graph.getHostsNumber());
                graph.setRelations(i, toId);
            }
        }

        return graph;
    }

    /*
    статично заданные вариации графа
     |
     |
     |
    \./
     */

    public static Graph generateConstantGraph(){
        Graph graph = new Graph();

        int[][] hostPositions = {   {11,15}, {13,27}, {23,15}, {31,11}, {37,21}, {43,26}, {35,35}, {23,25}, {13,14}, {10,13}, {9,15},
                                    {10,17}, {11,25}, {10,27}, {11,29}, {14,30}, {15,28}, {21,13}, {23,13}, {29,11}, {30, 9}, {32,10},
                                    {33,12}, {31,13}, {30,13}, {38,19}, {39,21}, {39,23}, {37,23}, {36,22}, {42,25}, {43,24}, {45,25},
                                    {45,27}, {44,28}, {35,33}, {37,35}, {36,37}, {34,37}, {33,35}, {25,25}, {24,23}, {21,24}, {22,27}
        };

        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
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
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }

        return graph;
    }

    public static Graph generateStar(){
        Graph graph = new Graph();
        int[][] hostPositions = {   {5,9}, {7,10}, {7,7}, {5,6}, {3,7}, {2,9}, {3,11}, {5,11}
        };
        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
        }
        int[][] hostRelations = {   {0,1},  {0,2},  {0,3}, {0,4}, {0, 5},
                {0,6}, {0,7}

        };
        for (int i=0; i<hostRelations.length; i++){
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }
        return graph;
    }

    public static Graph generateRing(){
        Graph graph = new Graph();
        int[][] hostPositions = {   {1,1}, {0,2}, {1,3}, {2,2}
        };
        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
        }
        int[][] hostRelations = {   {0,1},  {1,2},  {2,3}, {3,0}
        };
        for (int i=0; i<hostRelations.length; i++){
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }
        return graph;
    }

    public static Graph generateTriangulatedGraph(){
        Graph graph = new Graph();
        int[][] hostPositions = {   {3,7}, {3,9}, {5,11}, {7,10}, {6,7}, {5,9}
        };
        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
        }
        int[][] hostRelations = {   {0,1},  {1,2},  {2,3}, {3,4}, {4, 0},
                {0,5},  {1,5},  {2,5}, {3,5}, {4, 5},
        };
        for (int i=0; i<hostRelations.length; i++){
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }
        return graph;
    }

    public static Graph generateLine(){
        Graph graph = new Graph();
        int[][] hostPositions = {   {4,12}, {7,8}, {11,11}, {14,7}, {16,10}
        };
        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
        }
        int[][] hostRelations = {   {0,1},  {1,2},  {2,3}, {3,4}
        };
        for (int i=0; i<hostRelations.length; i++){
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }
        return graph;
    }

    public static Graph generateVertex(){
        Graph graph = new Graph();
        int[][] hostPositions = {   {8,10}, {6,9}, {5,11}, {7,13}, {9,12}, {7,10}, {6,11}, {8,12}, {7,11}
        };
        for (int i=0; i<hostPositions.length; i++){
            graph.setHost(hostPositions[i][0], hostPositions[i][1]);
        }
        int[][] hostRelations = {   {0,1},  {1,2},  {2,3}, {3,4}, {4, 5}, {5,6}, {6,7}, {7,8}
        };
        for (int i=0; i<hostRelations.length; i++){
            graph.setRelations(hostRelations[i][0], hostRelations[i][1]);
        }
        return graph;
    }


}
