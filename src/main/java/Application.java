import br.ufop.decom.Experiment;
import br.ufop.decom.ExperimentLoader;

import java.io.File;

public class Application {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: <file_path>");
            System.exit(1);
        }

        File experimentFile = new File(args[0]);
        try {
            Experiment experiment = ExperimentLoader.loadFromFile(experimentFile);
            experiment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
