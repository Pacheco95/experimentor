package br.ufop.decom;

import br.ufop.decom.adapter.AdapterVarListToMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement
@XmlType(name = "experimentType", propOrder = {"experimentId", "vars", "tasks"})
@XmlAccessorType(XmlAccessType.NONE)
public class Experiment {

    @XmlAttribute(required = true)
    @XmlID
    @Getter @Setter
    private String experimentId;

    @XmlElementWrapper(required = true)
    @XmlElement(name = "task")
    @Getter @Setter
    private List<Task> tasks;

    @XmlJavaTypeAdapter(AdapterVarListToMap.class)
    private Map<String, String> vars;

    private static Logger LOGGER = Logger.getLogger(Experiment.class);

    public Experiment() {
        this("Unnamed", new ArrayList<>());
    }

    public Experiment(String experimentId, ArrayList<Task> tasks) {
        this.experimentId = experimentId;
        this.tasks = tasks;
        this.vars = new HashMap<>();
    }

    public Experiment(String experimentId, Task ... tasks) {
        this(experimentId, new ArrayList<>(Arrays.asList(tasks)));
    }

    public static Experiment loadFromFile(File configurationFile) throws JAXBException {
        LOGGER.debug(String.format("Unmarshalling \"%s\"", configurationFile.getAbsolutePath()));
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return  (Experiment) unmarshaller.unmarshal(configurationFile);
    }

    public void execute() {
        parseGlobalVars();
        registerObservers();
        LOGGER.debug(String.format("Starting experiment \"%s\"...", experimentId));

        CountDownLatch latch = new CountDownLatch(tasks.size());
        tasks.parallelStream().forEach(task -> task.setCountDownLatch(latch));
        tasks.parallelStream().filter(task -> task.getDependencies().isEmpty()).forEach(Task::execute);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. For all experiment task T
     *     For all dependency D in T
     *         Add T as a observer to D
     * 2. Start every tasks with no dependencies
     * */
    private void registerObservers() {
        tasks.forEach(task -> task.getDependencies().forEach(dependency -> {
            String message = String.format("Registering task \"%s\" as an observer to task \"%s\".", task.getTaskId(), dependency.getTaskId());
            LOGGER.debug(message);
            dependency.addObserver(task);
        }));
    }

    private void parseGlobalVars() {
        Pattern pattern = Pattern.compile("\\$\\((?<var>[a-zA-Z0-9-_]+)\\)");

        vars.forEach((varID, oldVarValue) -> {
            String newValue = replace(pattern, oldVarValue);
            vars.put(varID, newValue);
            LOGGER.debug(String.format("Parsing var <%s>: <%s> -> <%s>", varID, oldVarValue, newValue));
        });

        tasks.forEach(task -> {
            String newCommand = replace(pattern, task.getCommand());
            LOGGER.debug(String.format("Parsing task <%s>: <%s> -> <%s>", task.getTaskId(), task.getCommand(), newCommand));
            task.setCommand(newCommand);
        });
    }

    /**
     * Replaces all occurrences of {@code pattern} in {@code toReplace} to the current content of {@code pattern} in {@code vars} map.
     * */
    private String replace(Pattern pattern, String toReplace) {
        Matcher matcher = pattern.matcher(toReplace);

        StringBuilder newValue = new StringBuilder(toReplace);

        while (matcher.find()) {
            String var = matcher.group("var");
            if (!vars.containsKey(var))
                LOGGER.fatal(String.format("Global var \"%s\" does not exists.", var));
            newValue.replace(matcher.start(), matcher.end(), vars.get(var));
            // Lookup for new $(var) occurrences
            matcher = pattern.matcher(newValue);
        }
        return newValue.toString();
    }

    @Override
    public String toString() {
        try {
            JAXBContext context = JAXBContext.newInstance(Experiment.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(this, sw);
            return sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }
}
