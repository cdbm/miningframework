package main;

import arguments.ArgsParser;
import buildManager.BuildGenerator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import gitManager.CommitManager;
import gitManager.MergeManager;
import project.MergeCommit;
import project.Project;
import app.MiningFramework;
import arguments.ArgsParser;
import arguments.Arguments;
import util.FileManager;

import java.io.File;
import java.io.IOException;

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


        try {
            Class injectorClass = appArguments.getInjector();
            Injector injector = Guice.createInjector((com.google.inject.Module) injectorClass.newInstance());

            MergeCommit mergeCommit = commitManager.buildMergeCommit();
            System.out.println(mergeCommit.getSHA());
            System.out.println(mergeCommit.getRightSHA());

            MiningFramework framework = injector.getInstance(MiningFramework.class);
            framework.setArguments(appArguments);

            FileManager.createOutputFiles(appArguments.getOutputPath(), appArguments.isPushCommandActive());
            framework.collectModifiedLines(project, mergeCommit);

            Process buildGeneration = buildGenerator.generateBuild();
            buildGeneration.waitFor();
            File buildJar = buildGenerator.getBuildJar();
            System.out.println("Build jar file: " + buildJar);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
