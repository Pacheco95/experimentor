package br.ufop.decom;

import br.ufop.decom.adapter.AdapterDependencyListToArrayList;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"unused", "WeakerAccess"})
@XmlType(name = "taskType", propOrder = {"taskId", "command", "dependencies", "requirements"})
@XmlAccessorType(XmlAccessType.NONE)
public class Task implements Runnable {

    @XmlAttribute(required = true)
    @XmlID
    @Getter @Setter
    private String taskId;

    @XmlElement(required = true)
    @Getter @Setter
    private String command;

    /** Listen task standard output */
    private BufferedReader standardISReader;
    /** Listen task error output */
    private BufferedReader errorISReader;

    @XmlJavaTypeAdapter(AdapterDependencyListToArrayList.class)
    @Getter @Setter
    private List<Task> dependencies;

    @XmlElement
    @Getter @Setter
    private Requirements requirements;

    /** List of tasks waiting this task to end */
    @Getter @Setter
    private List<Task> observers;
    private static Logger LOGGER = Logger.getLogger(Task.class);
    @Getter @Setter
    private CountDownLatch experimentCountDownLatch;

    public Task() {
        this("Unnamed", "");
    }

    public Task(String taskId, String command) {
        this.taskId = taskId;
        this.command = command;
        this.dependencies = new ArrayList<>();
        this.requirements = new Requirements(1, 0, 0, 0);
        this.observers = new ArrayList<>();
    }

    @SuppressWarnings("WeakerAccess")
    public void execute() {
        new Thread(this).start();
    }

    @Override
    public synchronized void run() {
        try {
            LOGGER.debug(String.format("Running task: <%s>=<%s>", taskId, command));

            String[] cmd = new String[3];

            if (SystemUtils.IS_OS_LINUX) {
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
            } else if(SystemUtils.IS_OS_WINDOWS) {
                cmd[0] = "cmd.exe";
                cmd[1] = "/c";
            }

            cmd[2] = command;

            Process process = new ProcessBuilder(cmd).start();

            standardISReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorISReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            new Thread(this::listenStandardInputStream).start();
            new Thread(this::listenErrorInputStream).start();

            // TODO Stop if an error occurs

            process.waitFor();

            LOGGER.debug(String.format("Task <%s> terminated! Returned code: %d", taskId, process.exitValue()));

            // notify observers
            observers.forEach(observer -> observer.removeDependency(this));

            // notify the experiment
            experimentCountDownLatch.countDown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listenStandardInputStream() {
        try {
            String output;
            while ((output = standardISReader.readLine()) != null) System.out.println("\t" + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenErrorInputStream() {
        try {
            String output;
            while ((output = errorISReader.readLine()) != null) System.err.println("\t" + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void removeDependency(Task task) {
        dependencies.remove(task);
        if (dependencies.isEmpty())
            execute();
    }

    @Override
    public String toString() {
        return String.format("taskID=<%s> command=<%s>", taskId, command);
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

    public void setCountDownLatch(CountDownLatch latch) {
        this.experimentCountDownLatch = latch;
    }
}
