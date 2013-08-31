package com.insightfullogic.vertx.promises.codegen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class ParameterNames {

    private JsonObject config;

    public ParameterNames(String file) {
        load(file);
    }

    @SuppressWarnings("resource")
    private void load(String file) {
        InputStream resource = getClass().getResourceAsStream(file);
        try (Scanner scanner = new Scanner(resource).useDelimiter("\\A")) {
            String conf = scanner.next();
            config = new JsonObject(conf);
        }
    }
    
    public List<String> getNames(String className, String methodName) {
        JsonObject klass = config.getObject(className);
        if (klass == null)
            return null;
        
        JsonArray array = klass.getArray(methodName);
        if (array == null)
            return null;
        
        List<String> names = new ArrayList<>();
        Iterator<Object> it = array.iterator();
        while(it.hasNext()) {
            names.add((String) it.next());
        }
        return names;
    }

}
