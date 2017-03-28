package dataPrepare.data.graphviz;

import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guardeec on 14.03.17.
 */
public class MakePlanar {

    private static void parseLine(Graph graph, String line){
        String[] parts = line.split(" ");
        if (parts[0].equals("node")){
            Host host = findHost(graph, parts[1]);
            if (host != null) {
                host.getCoordinate().setX(Float.parseFloat(parts[2]));
                host.getCoordinate().setY(Float.parseFloat(parts[3]));
            }
        }
    }

    private static Host findHost(Graph graph, String name){
        for (Host host : graph.getHosts()){
            if (host.getMetrics().get("graphVizId").equals(name)){
                return host;
            }
        }
        return null;
    }

    private static String run(Graph graph){
        String result = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec("dot graphViz.dot -T plain-ext");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = null;
            while ((line=in.readLine())!=null){
                parseLine(graph, line);
                result+=line;
            }

            while ((line=err.readLine())!=null){
                result+=line;

            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List make(Graph graph, String type){
        //создаём файл для записи
        File file = new File("graphViz.dot");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //записываем файл
        String input = setInput(graph, type);
        try {
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            out.write(input);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String res = run(graph);

        return null;
    }

    public static String setInput(Graph graph, String type) {
        for (int i=0; i<graph.getHostNumber(); i++){
            graph.getHosts().get(i).addMetric("graphVizId", Integer.toString(i));
        }
        String input = "graph{ splines=\"line\"; layout=\""+type+"\";";
        Set<Host> usedHosts = new HashSet<>();
        for (Host from : graph.getHosts()){

            for (Host to : graph.getRelations().get(from)){
                //System.out.println(!usedHosts.contains(to));
                //if (!usedHosts.contains(to)){
                    input += from.getMetrics().get("graphVizId") + " -- "+to.getMetrics().get("graphVizId") +";";
                //}
            }
            usedHosts.add(from);
        }
        input += "}";
        return input;
    }



}
