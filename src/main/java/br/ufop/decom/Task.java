package br.ufop.decom;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Task implements Runnable {
    private String id;
    private String command;

    private BufferedReader standardISReader;
    private BufferedReader errorISReader;

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

    public void execute() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            LOGGER.info(String.format("Running task: %s=%s", id, command));
            Process process = Runtime.getRuntime().exec(command);
            standardISReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorISReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            new Thread(this::listenStandardInputStream).start();
            new Thread(this::listenErrorInputStream).start();

            // TODO Stop if an error occurs

            process.waitFor();

            LOGGER.info(String.format("Returned code of \"%s\": %d ", id, process.exitValue()));
            // notify observers
            observers.forEach(observer -> observer.updateDependency(this));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listenStandardInputStream() {
        try {
            String output;
            while ((output = standardISReader.readLine()) != null) {
                System.out.println(output);
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listenErrorInputStream() {
        try {
            String output;
            while ((output = errorISReader.readLine()) != null) {
                System.err.println(output);
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void updateDependency(Task task) {
        // TODO melhorar esta lógica
        terminatedDependencies++;
        if (terminatedDependencies == dependencies.size()) execute();
    }

    @Override
    public String toString() {
        return String.format("id=<%s> command=<%s>", id, command);
    }

    public void addDependency(Task task) {
        dependencies.add(task);
    }

    public Task withDependency(Task task) {
        addDependency(task);
        return this;
    }

    public Task withDependencies(Task ... tasks) {
        dependencies.addAll(Arrays.asList(tasks));
        return this;
    }

    public void addObserver(Task observer) {
        observers.add(observer);
    }

    public Task withObservers(Task ... observers) {
        this.observers.addAll(Arrays.asList(observers));
        return this;
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
}
