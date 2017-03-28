package dataPrepare.methods;

import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.Triangle;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
    1.генерация входного файла
    2.запуск triangle
    3.парсинг ответа
    подробнее о triangle - triangle berkley
     */
public class Triangulate {

    private static boolean checkOnUnic(Graph graph){
        List<Integer> x = new LinkedList<>();
        List<Integer> y = new LinkedList<>();
        for (Host host : graph.getHosts()){
            if (x.contains((int)host.getCoordinate().getX())){
                host.getCoordinate().setX(host.getCoordinate().getX()+1);
                return false;
            }
            if (y.contains((int)host.getCoordinate().getY())){
                host.getCoordinate().setY(host.getCoordinate().getY()+1);
                return false;
            }
            x.add((int) host.getCoordinate().getX());
            y.add((int) host.getCoordinate().getX());
        }
        return true;
    }

    /*
    генерация входного файла
     */
    private static String setInput(Graph graph){


        /*
        входной файл состоит их пяти кортежей
         */

        //заполняем первый кортеж
        String nodesParameters = "";
        nodesParameters+= graph.getHostNumber()+ " 2 0 1\n";

        //заполняем второй кортеж
        String nodes = "";
        for (int i=0, nodeNumber=1; i<graph.getHostNumber(); i++, nodeNumber++){
            nodes+= nodeNumber+" "
                    +(int)graph.getHosts().get(i).getCoordinate().getX()+" "
                    +(int)graph.getHosts().get(i).getCoordinate().getY()+" "
                    +nodeNumber+"\n";
        }
        nodes+="\n";

        //заполняем третий кортеж
        String linksParameters="";
        linksParameters+= graph.getRelationNumber()+" 1\n";

        //заполняем четвёртый кортеж
        String links="";
        for (int i=0, nodeNumber=0, linkNumber=1; i<graph.getHostNumber(); i++){
            nodeNumber=i+1;
            List<Host> relations = graph.getRelations(graph.getHosts().get(i));
            if(relations.size()>0){
                for (int q=0; q<relations.size(); q++){
                    if(nodeNumber!=graph.getHosts().indexOf(relations.get(q))+1){
                        links+=linkNumber+" "+nodeNumber+" "+(graph.getHosts().indexOf(relations.get(q))+1)+" 1\n";
                        linkNumber++;
                    }
                }
            }

        }
        links+="\n";

        //заполняем пятый кортеж
        String endfile="0";

        String output = nodesParameters
                +nodes
                +linksParameters
                +links
                +endfile;

        return output;
    }

    public static ArrayList make(Graph graph){
        //создаём файл для записи
        File file = new File("graph.poly");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //записываем файл
        String input = setInput(graph);
        try {
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            out.write(input);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Выполняем модуль
        Process p;
        try {
            p = Runtime.getRuntime().exec("./triangle -b graph.poly");
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //считываем триангуляцию
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("graph.1.ele")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        List<String> lines = new ArrayList<String>();
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String [] linesAsArray = lines.toArray(new String[lines.size()]); //парсим в тругольники
        ArrayList<Triangle> triangles = new ArrayList<>();
        for (int i=1; i<linesAsArray.length-1; i++){
            String phrase = linesAsArray[i];
            String delims = "[ ]+";
            String[] tokens = phrase.split(delims);

            triangles.add(
                    new Triangle(
                            Integer.parseInt(tokens[tokens.length-3]) - 1,
                            Integer.parseInt(tokens[tokens.length-2]) - 1,
                            Integer.parseInt(tokens[tokens.length-1]) - 1
                    )
            );
        }
        return triangles;
    }


//
//    public static Graph addToGraph(Graph graph, ArrayList<Triangle> triangles){
//        //добавим набор треугольников в граф
//        for (int i=0; i<triangles.size(); i++){
//            graph.setRelations( triangles.get(i).getFirstDot(),
//                                triangles.get(i).getSecondDot()
//            );
//            graph.setRelations( triangles.get(i).getSecondDot(),
//                                triangles.get(i).getThirdDot()
//            );
//            graph.setRelations( triangles.get(i).getThirdDot(),
//                                triangles.get(i).getFirstDot()
//            );
//        }
//        return graph;
//    }

}