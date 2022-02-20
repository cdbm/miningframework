package main;

import buildManager.BuildGenerator;
import csvManager.CsvManager;
import gitManager.CollectedMergeMethodData;
import gitManager.CommitManager;
import gitManager.MergeManager;
import gitManager.ModifiedLinesManager;
import project.MergeCommit;
import project.Project;
import services.outputProcessors.GenerateSootInputFilesOutputProcessor;
import services.outputProcessors.soot.RunSootAnalysisOutputProcessor;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        String miningPath = this.args[5];
        System.out.println("Mining path: " + miningPath);

        try {

            MergeCommit mergeCommit = commitManager.buildMergeCommit();

            Process buildGeneration = buildGenerator.generateBuild();
            buildGeneration.waitFor();

            if(buildGeneration.exitValue() != 0) {
                System.out.println("Could not generate a valid build");
                return;
            }

            File buildJar = buildGenerator.getBuildJar();

            File dest = new File(miningPath + "/files/project/" + mergeCommit.getSHA() + "/original-without-dependencies/merge/build.jar");
            FileUtils.copyFile(buildJar, dest);

            List<CollectedMergeMethodData> collectedMergeMethodDataList = modifiedLinesManager.collectData(project, mergeCommit);
            CsvManager csvManager = new CsvManager();
            csvManager.transformCollectedDataIntoCsv(collectedMergeMethodDataList, miningPath);
            GenerateSootInputFilesOutputProcessor generateSootInputFilesOutputProcessor = new GenerateSootInputFilesOutputProcessor();
            generateSootInputFilesOutputProcessor.convertToSootScript(miningPath);

            for(CollectedMergeMethodData data : collectedMergeMethodDataList) {
                File left = new File(miningPath + "/files/"+ data.getProject().getName() + "/" + mergeCommit.getSHA() + "/changed-methods/" + data.getClassName() +"/" + data.getMethodSignature() + "/left-right-lines.csv");
                File right = new File(miningPath + "/files/"+ data.getProject().getName() + "/" + mergeCommit.getSHA() + "/changed-methods/" + data.getClassName() +"/" + data.getMethodSignature() + "/right-left-lines.csv");

                csvManager.trimBlankLines(left);
                csvManager.trimBlankLines(right);
            }


            RunSootAnalysisOutputProcessor runSootAnalysisOutputProcessor = new RunSootAnalysisOutputProcessor();
            runSootAnalysisOutputProcessor.executeAllAnalyses(miningPath);

            //System.out.println("Build jar file: " + buildJar);
        } catch (IOException /*| InterruptedException e*/e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
