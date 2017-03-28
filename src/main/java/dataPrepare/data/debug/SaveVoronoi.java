package dataPrepare.data.debug;

import com.google.gson.Gson;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.voronoi.Voronoi;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guardeec on 30.09.16.
 */
public class SaveVoronoi {
    private static SaveVoronoi ourInstance = new SaveVoronoi();

    public static SaveVoronoi getInstance() {
        return ourInstance;
    }

    private SaveVoronoi() {
    }

    PrintWriter out;
    String previosStatement = "";
    String fileName = "";

    public void startSaving(String fileName){
        this.fileName = fileName;
//        File file = new File(fileName);
//        if (file.exists()){
//            file.delete();
//        }
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
           // out.print("[");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveStatement(Voronoi voronoi){
        DebugVoronoiData debugVoronoiData = parse(voronoi);
        String statement = new Gson().toJson(debugVoronoiData);
        if (!previosStatement.equals(statement)){
         //   out.print(statement+",");
            out.print(statement+"\n");
            previosStatement=statement;
        }
        endSaving();
        startSaving(fileName);
    }
    public void endSaving(){
       // out.print("[]]");
        out.close();
    }

    public static String read(String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
        exists(fileName);

        try {
            BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
    }

    private DebugVoronoiData parse(Voronoi voronoi){
        DebugVoronoiData debugVoronoiData = new DebugVoronoiData();

        Map<Host, Dot> HD = new HashMap<>();
        Graph graph = voronoi.voronoiLikeAGraph(voronoi);
        for (Host host : graph.getHosts()){
            float x = host.getCoordinate().getX();
            float y = host.getCoordinate().getY();
            if (Float.isNaN(x) || Float.isNaN(y)){
                x=0; y=0;
            }
            Dot dot = new Dot(x, y, (boolean) host.getCoordinate().getMetric("stopPolymorph"), (int) host.getCoordinate().getMetric("c"));
            HD.put(host, dot);
        }
        List<Edge> edges = new LinkedList<>();
        for (Host host : graph.getHosts()){
            List<Host> related = graph.getRelations().get(host);
            List<Dot> relatedDots = related.stream().map(HD::get).collect(Collectors.toCollection(LinkedList::new));
            Dot dot = HD.get(host);
            Edge edge = new Edge(dot, relatedDots);
            edges.add(edge);
        }
        debugVoronoiData.setEdges(edges);
        return debugVoronoiData;
    }

}
