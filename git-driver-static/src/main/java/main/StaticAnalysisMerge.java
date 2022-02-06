package main;

import buildManager.BuildGenerator;
import csvManager.CsvManager;
import gitManager.CollectedMergeMethodData;
import gitManager.CommitManager;
import gitManager.MergeManager;
import gitManager.ModifiedLinesManager;
import project.MergeCommit;
import project.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class StaticAnalysisMerge {

    String[] args;

    StaticAnalysisMerge(String[] args){
        this.args = args;
    }

    public void run() {
        //MergeManager mergeManager = new MergeManager();
        BuildGenerator buildGenerator = new BuildGenerator();
        CommitManager commitManager = new CommitManager(this.args);
        Project project = new Project("project", System.getProperty("user.dir"));
        ModifiedLinesManager modifiedLinesManager = new ModifiedLinesManager();


        try {

            MergeCommit mergeCommit = commitManager.buildMergeCommit();

            List<CollectedMergeMethodData> collectedMergeMethodDataList = modifiedLinesManager.collectData(project, mergeCommit);
            CsvManager csvManager = new CsvManager();
            csvManager.transformCollectedDataIntoCsv(collectedMergeMethodDataList);

            Process buildGeneration = buildGenerator.generateBuild();
            buildGeneration.waitFor();
            File buildJar = buildGenerator.getBuildJar();
            System.out.println("Build jar file: " + buildJar);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
