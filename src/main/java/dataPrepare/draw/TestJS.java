package dataPrepare.draw;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;

/**
 * Created by guardeec on 04.07.16.
 */
public class TestJS {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            FileReader reader = new FileReader("d3.js");
            engine.eval(reader);
            reader = new FileReader("forceGraph.js");
            engine.eval(reader);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }



}
