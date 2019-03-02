import br.ufop.decom.Experiment;

import javax.xml.bind.JAXBException;
import java.io.File;

public class Application {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: Xperimentor <file_path>");
            System.exit(1);
        }

        try {
            File file = new File(args[0]);
            if(!file.exists()) {
                System.err.println(String.format("File \"%s\" does not exists", file.getAbsolutePath()));
                System.exit(1);
            }
            Experiment.loadFromFile(file).execute();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
