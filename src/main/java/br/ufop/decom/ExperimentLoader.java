package br.ufop.decom;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExperimentLoader {

    @SuppressWarnings("SuspiciousMethodCalls")
    public static Experiment loadFromFile(File filePath) throws Exception {

        String jsonString = new String(Files.readAllBytes(Paths.get(filePath.toURI())), Charset.forName("UTF-8"));
        Object parsedJson = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);

        // Load experiment name
        String experimentName = JsonPath.read(parsedJson, "$.experiment.name");

        // Load global vars
        JSONArray vars = JsonPath.read(parsedJson, "$.experiment.vars");
        Map<String, String> globalVars = new HashMap<>();

        vars.forEach(v -> {
            LinkedHashMap var = (LinkedHashMap) v;
            String id = (String) var.get("id");
            String value = (String) var.get("value");
            globalVars.put(id, value);
        });

        // Load tasks
        JSONArray tasks = JsonPath.read(parsedJson, "$.experiment.tasks");
        Map<String, Task> tasksHMap = new HashMap<>();

        tasks.forEach(t -> {
            LinkedHashMap task = (LinkedHashMap) t;
            String id = (String) task.get("id");
            String command = (String) task.get("command");

            int cores = (int) ((Map) task.get("requirements")).get("cores");
            int ram = (int) ((Map) task.get("requirements")).get("ram");
            int storage = (int) ((Map) task.get("requirements")).get("storage");
            int timeout = (int) ((Map) task.get("requirements")).get("timeout");

            Task newTask = new Task(id, command);
            newTask.setRequirements(new Requirements(cores, ram, storage, timeout));

            tasksHMap.put(id, newTask);

            //String query = String.format("$.experiment.tasks[?(@.id == '%s')]", id);
        });

        // Assign dependencies
        tasks.forEach(t -> {
            LinkedHashMap task = (LinkedHashMap) t;
            Task current = tasksHMap.get(task.get("id"));

            JSONArray dependencies = (JSONArray) task.get("dependencies");

            dependencies.forEach(dep -> {
                Task dependency = tasksHMap.get(dep);
                current.addDependency(dependency);
            });
        });

        Experiment experiment = new Experiment(experimentName, new ArrayList<>(tasksHMap.values()));
        experiment.withGlobalVars(globalVars);

        return experiment;
    }
}
