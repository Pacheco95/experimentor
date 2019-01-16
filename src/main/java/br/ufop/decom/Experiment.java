package br.ufop.decom;

import br.ufop.decom.adapter.AdapterVarListToMap;
import javafx.util.Pair;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@XmlRootElement
@XmlType(name = "experimentType", propOrder = {"experimentId", "vars", "tasks"})
@XmlAccessorType(XmlAccessType.NONE)
public class Experiment {

    @XmlAttribute(required = true)
    @XmlID
    private String experimentId;

    @XmlElementWrapper(required = true)
    @XmlElement(name = "task")
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
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return  (Experiment) unmarshaller.unmarshal(configurationFile);
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Map<String, String> getVars() {
        return vars;
    }

    public void setVars(Map<String, String> vars) {
        this.vars = vars;
    }

    @SafeVarargs
    public final Experiment withGlobalVars(Pair<String, String>... vars) {
        for (Pair<String, String> p : vars)
            this.vars.put(p.getKey(), p.getValue());
        return this;
    }

    public Experiment withGlobalVars(ArrayList<Pair<String, String>> vars) {
        vars.forEach(pair -> this.vars.put(pair.getKey(), pair.getValue()));
        return this;
    }

    public Experiment withGlobalVars(Map<String, String> globalVars) {
        this.vars = globalVars;
        return this;
    }

    public void execute() {
        parseGlobalVars();
        registerObservers();
        LOGGER.debug(String.format("Starting experiment \"%s\"...", experimentId));
        tasks.stream().parallel().filter(task -> task.getDependencies().isEmpty()).forEach(Task::execute);

        new Scanner(System.in).nextLine();
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
        String pattern = "\\$\\((?<var>[a-zA-Z0-9-_]+)\\)";
        Pattern p = Pattern.compile(pattern);

        vars.forEach((id, value) -> {
            StringBuilder command = new StringBuilder(value);
            Matcher m = p.matcher(command);
            buildString(p, command, m);
            vars.put(id, command.toString());
            LOGGER.debug(String.format("Parsing var <%s>: <%s> -> <%s>", id, value, command.toString()));
        });

        tasks.forEach(task -> {
            StringBuilder command = new StringBuilder(task.getCommand());
            Matcher m = p.matcher(command);
            buildString(p, command, m);
            LOGGER.debug(String.format("Parsing task <%s>: <%s> -> <%s>", task.getTaskId(), task.getCommand(), command.toString()));
            task.setCommand(command.toString());
        });
    }

    private void buildString(Pattern p, StringBuilder command, Matcher m) {
        while (m.find()) {
            String var = m.group("var");
            if (!vars.containsKey(var))
                LOGGER.fatal(String.format("Global var \"%s\" does not exists.", var));
            command.replace(m.start(), m.end(), vars.get(var));
            m = p.matcher(command);
        }
    }

    @Override
    public String toString() {
        String result = "";
        try {
            JAXBContext context = JAXBContext.newInstance(Experiment.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(this, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return result;
    }
}
