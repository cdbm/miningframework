package app;

import buildManager.BuildGenerator;

import java.io.IOException;

public class StaticAnalysisMerge {

    String[] args;

    StaticAnalysisMerge(String[] args){
        this.args = args;
    }

    public void run() {
        BuildGenerator buildGenerator = new BuildGenerator();
        try {
            Process buildGeneration = buildGenerator.generateBuild();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
