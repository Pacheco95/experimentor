package br.ufop.decom;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Task {
    private String id;
    private String command;
    private List<Task> dependencies;
    private Requirements requirements;
    private List<Task> observers;
    private static Logger LOGGER = Logger.getLogger(Task.class);
    private int terminatedDependencies;

    public Task(String id, String command) {
        this.id = id;
        this.command = command;
        this.dependencies = new ArrayList<>();
        this.requirements = new Requirements(1, 0, 0, 0);
        this.observers = new ArrayList<>();
    }

    public Task withDependency(Task task) {
        addDependency(task);
        return this;
    }

    public void addObserver(Task observer) {
        observers.add(observer);
    }

    public Task withDependencies(Task ... tasks) {
        dependencies.addAll(Arrays.asList(tasks));
        return this;
    }

    public Task withObservers(Task ... observers) {
        this.observers.addAll(Arrays.asList(observers));
        return this;
    }

    public void addDependency(Task task) {
        dependencies.add(task);
    }

    public Task withRequirements(Requirements requirements) {
        setRequirements(requirements);
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Task> dependencies) {
        this.dependencies = dependencies;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public void execute() throws IOException, InterruptedException {
        // TODO run in a separated threads
        System.err.println();
        LOGGER.info(String.format("Executing task %s", id));
        Process process = Runtime.getRuntime().exec(command);
        // TODO Check timeout requirement
        process.waitFor();

        // Check output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output;
        while ((output = reader.readLine()) != null)
            LOGGER.info("Output:\n" + output);

        // Check errors
        BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((output = reader.readLine()) != null)
            LOGGER.info("ERRORS: " + output);

        LOGGER.info("Return code: " + process.exitValue());
        // notify observers
        observers.forEach(observer -> observer.updateDependency(this));
    }

    private synchronized void updateDependency(Task task) {
        terminatedDependencies++;
        if (terminatedDependencies == dependencies.size()) {
            try {
                execute();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("id=<%s> command=<%s>", id, command);
    }
}
