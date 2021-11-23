package app;

import buildManager.BuildGenerator;
import gitManager.MergeManager;

import java.io.File;
import java.io.IOException;

public class StaticAnalysisMerge {

    String[] args;

    StaticAnalysisMerge(String[] args){
        this.args = args;
    }

    public void run() {
        MergeManager mergeManager = new MergeManager();
        BuildGenerator buildGenerator = new BuildGenerator();
        try {
            //mergeManager.merge();
            Process buildGeneration = buildGenerator.generateBuild();
            buildGeneration.waitFor();
            File buildJar = buildGenerator.getBuildJar();
            System.out.println("Build jar file: " + buildJar);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
