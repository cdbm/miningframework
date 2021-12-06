package main;

import buildManager.BuildGenerator;
import gitManager.CommitManager;
import gitManager.MergeManager;
import project.MergeCommit;
import project.Project;
import services.dataCollectors.modifiedLinesCollector.ModifiedLinesCollector;

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
        CommitManager commitManager = new CommitManager(this.args);
        ModifiedLinesCollector modifiedLinesCollector = new ModifiedLinesCollector();
        Project project = new Project("projetct", System.getProperty("user.dir"));

        try {
            MergeCommit mergeCommit = commitManager.buildMergeCommit();
            System.out.println(mergeCommit.getSHA());
            System.out.println(mergeCommit.getRightSHA());
            modifiedLinesCollector.collectData(project, mergeCommit);
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
