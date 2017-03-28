package dataPrepare.methods;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by guardeec on 20.03.17.
 */
public class HamiltonianCycle {
    private int V, pathCount;
    private int[] path;
    private int[][] graph;
    public int[] findHamiltonianCycle(int[][] g) {
        V = g.length;
        path = new int[V];
        Arrays.fill(path, -1);
        graph = g;
        try {
            path[0] = 0;
            pathCount = 1;
            solve(0);
            return null;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return path;
        }
    }
    private void solve(int vertex) throws Exception {
        if (graph[vertex][0] == 1 && pathCount == V){throw new Exception("Solution found");}
        if (pathCount == V){return;}
        for (int v = 0; v < V; v++) {
            if (graph[vertex][v] == 1 ) {
                path[pathCount++] = v;
                graph[vertex][v] = 0;
                graph[v][vertex] = 0;
                if (!isPresent(v)){solve(v);}
                graph[vertex][v] = 1;
                graph[v][vertex] = 1;
                path[--pathCount] = -1;
            }
        }
    }
    private boolean isPresent(int v) {
        for (int i = 0; i < pathCount - 1; i++){
            if (path[i] == v){return true;}
        }
        return false;
    }


//    public static void main (String[] args) {
//        int[][] graph = {
//                {0,1,1,0,0,1,0},
//                {1,0,1,0,0,0,0},
//                {1,1,0,1,0,1,0},
//                {0,0,1,0,1,1,0},
//                {0,0,0,1,0,0,1},
//                {1,0,1,1,0,0,1},
//                {0,0,0,0,1,1,0}
//        };
//        int h[] = new HamiltonianCycle().findHamiltonianCycle(graph);
//        for (int a : h) {
//            System.out.println(a);
//        }
//    }

}
