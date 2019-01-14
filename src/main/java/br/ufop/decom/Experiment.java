package br.ufop.decom;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Experiment {
    private String name;
    private ArrayList<Task> tasks;
    private Map<String, String> globalVars;
    private static Logger LOGGER = Logger.getLogger(Experiment.class);

    public Experiment(String name, ArrayList<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
        this.globalVars = new HashMap<>();
    }

    public Experiment(String name, Task ... tasks) {
        this(name, new ArrayList<>(Arrays.asList(tasks)));
    }

    public Map<String, String> getGlobalVars() {
        return globalVars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * 1. For all experiment task T
     *     For all dependency D in T
     *         Add T as a observer to D
     * 2. Start every tasks with no dependencies
     * */
    public void execute() {
        parseGlobalVars();
        showInfo();
        registerObservers();

        System.err.println();
        LOGGER.info(String.format("Starting experiment \"%s\"", name));

        tasks.stream().filter(task -> task.getDependencies().isEmpty()).forEach(Task::execute);
    }

    private void registerObservers() {
        tasks.forEach(task -> task.getDependencies().forEach(dependency -> {
            String message = String.format("Adding task \"%s\" as an observer to task \"%s\".", task.getId(), dependency.getId());
            LOGGER.info(message);
            dependency.addObserver(task);
        }));
    }

    private void showInfo() {
        LOGGER.info("Experiment name: " + name + "\n");
        globalVars.forEach((key, value) -> LOGGER.info(String.format("Global var:\n\tname: %s\n\tvalue: %s", key, value)));
        System.err.println();
        tasks.forEach(task -> LOGGER.info(String.format("Task:\n\tid: %s\n\tcommand: %s", task.getId(), task.getCommand())));
        System.err.println();
    }

    private void parseGlobalVars() {
        String pattern = "\\$\\((?<var>[a-zA-Z0-9-_]+)\\)";
        Pattern p = Pattern.compile(pattern);

        globalVars.forEach((id, value) -> {
            StringBuilder command = new StringBuilder(value);
            Matcher m = p.matcher(command);
            buildString(p, command, m);
            globalVars.put(id, command.toString());
        });

        tasks.forEach(task -> {
            StringBuilder command = new StringBuilder(task.getCommand());
            Matcher m = p.matcher(command);
            buildString(p, command, m);
            task.setCommand(command.toString());
        });
    }

    private void buildString(Pattern p, StringBuilder command, Matcher m) {
        while (m.find()) {
            String var = m.group("var");
            if (!globalVars.containsKey(var))
                LOGGER.fatal(String.format("Global var \"%s\" does not exists.", var));
            command.replace(m.start(), m.end(), globalVars.get(var));
            m = p.matcher(command);
        }
    }

    @SafeVarargs
    public final Experiment withGlobalVars(Pair<String, String>... vars) {
        for (Pair<String, String> p : vars)
            globalVars.put(p.getKey(), p.getValue());
        return this;
    }

    public Experiment withGlobalVars(ArrayList<Pair<String, String>> vars) {
        vars.forEach(pair -> globalVars.put(pair.getKey(), pair.getValue()));
        return this;
    }

    public Experiment withGlobalVars(Map<String, String> globalVars) {
        this.globalVars = globalVars;
        return this;
    }
}
