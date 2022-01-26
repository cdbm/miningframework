package main;

import arguments.ArgsParser;
import buildManager.BuildGenerator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import gitManager.CollectedMergeMethodData;
import gitManager.CommitManager;
import gitManager.MergeManager;
import gitManager.ModifiedLinesManager;
import project.MergeCommit;
import project.Project;
import app.MiningFramework;
import arguments.ArgsParser;
import arguments.Arguments;
import util.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StaticAnalysisMerge {

    String[] args;

    StaticAnalysisMerge(String[] args){
        this.args = args;
    }

    public void run() {
        ArgsParser argsParser = new ArgsParser();
        Arguments appArguments = argsParser.parse(new String[0]);


        MergeManager mergeManager = new MergeManager();
        BuildGenerator buildGenerator = new BuildGenerator();
        CommitManager commitManager = new CommitManager(this.args);
        Project project = new Project("projetct", System.getProperty("user.dir"));
        ModifiedLinesManager modifiedLinesManager = new ModifiedLinesManager();


        try {

            MergeCommit mergeCommit = commitManager.buildMergeCommit();
            List<CollectedMergeMethodData> collectedMergeMethodDataList = modifiedLinesManager.collectData(project, mergeCommit);


            Process buildGeneration = buildGenerator.generateBuild();
            buildGeneration.waitFor();
            File buildJar = buildGenerator.getBuildJar();
            System.out.println("Build jar file: " + buildJar);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
