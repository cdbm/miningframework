package buildManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BuildGenerator {

    public BuildGenerator() { }


    public Process generateBuild() throws IOException {
        System.out.println("==== GENERATING BUILD ====");
        Process proc  = Runtime.getRuntime().exec("gradlew.bat build");
        this.watchProcess(proc);
        return proc;
    }

    private void watchProcess(Process proc) throws IOException {
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }
}
