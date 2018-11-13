import br.ufop.decom.Experiment;
import br.ufop.decom.Requirements;
import br.ufop.decom.Task;
import javafx.util.Pair;

@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection", "unused"})
public class Application {
    public static void compileTest() {

        Task util = new Task("compile-util", "cmd /c gcc \"$(dir)/util.c\" -c -o \"$(dir)/util.o\"");
        Task main = new Task("compile-main", "cmd /c gcc \"$(dir)/main.c\" -c -o \"$(dir)/main.o\"");
        Task link = new Task("link", "cmd /c gcc \"$(dir)/main.o\" \"$(dir)/util.o\" -o \"$(dir)/App\"").
                withDependencies(util, main).
                withRequirements(new Requirements(2, 100, 0, 5000));
        Task execute = new Task("execute", "cmd /c \"$(dir)/App\"").withDependencies(link);

        Experiment experiment = new Experiment("Compiling Test", util, main, link, execute)
                .withGlobalVars(new Pair<>("dir", "D:/IntelliJ Projects/Monografia/src/main/resources"));
        experiment.execute();
    }

    public static void cleanCompileTestFiles() {
        Pair<String, String> dir = new Pair<>("dir", "D:\\IntelliJ Projects\\Monografia\\src\\main\\resources");
        Pair<String, String> mainObj = new Pair<>("mainObj", "\"$(dir)\\main.o\"");
        Pair<String, String> utilObj = new Pair<>("utilObj", "\"$(dir)\\util.o\"");
        Pair<String, String> AppEXE = new Pair<>("App", "\"$(dir)\\App.exe\"");

        Task deleteMain = new Task("delete-main", "cmd /c del /f $(mainObj)");
        Task deleteUtil = new Task("delete-util", "cmd /c del /f $(utilObj)");
        Task deleteApp = new Task("delete-app", "cmd /c del /f $(App)");

        new Experiment("DateTime Test", deleteMain, deleteUtil, deleteApp).withGlobalVars(dir, mainObj, utilObj, AppEXE).execute();
    }


    @SuppressWarnings("SpellCheckingInspection")
    public static void dateTimeTest() {
        new Experiment("DateTime Test", new Task("dateTime", "cmd /c echo %date% %time%")).execute();
    }

    public static void main(String[] args) {
        //dateTimeTest();
        //compileTest();
        cleanCompileTestFiles();
    }
}
