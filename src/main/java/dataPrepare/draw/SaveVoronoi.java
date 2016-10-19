package dataPrepare.draw;

import com.google.gson.Gson;
import dataPrepare.data.voronoi.*;

import java.io.*;
import java.util.List;

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
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
           // out.print("[");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void saveStatement(List<dataPrepare.data.voronoi.Polygon> polygons){
        String statement = new Gson().toJson(polygons);
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

}
