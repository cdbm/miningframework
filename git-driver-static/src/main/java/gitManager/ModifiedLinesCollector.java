/*package gitManager;

import groovy.lang.Tuple2;
import project.MergeCommit;
import project.Project;
import services.dataCollectors.modifiedLinesCollector.ModifiedLinesCollectorAbstract;
import services.dataCollectors.modifiedLinesCollector.ModifiedMethod;
import services.dataCollectors.modifiedLinesCollector.ModifiedMethodsHelper;
import util.TypeNameHelper;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static app.MiningFramework.arguments;

public class ModifiedLinesCollector extends ModifiedLinesCollectorAbstract {

    public ModifiedLinesCollector() {
        modifiedMethodsHelper = new ModifiedMethodsHelper("diffj.jar");
    }

    public void collectData(Project project, MergeCommit mergeCommit) {
        Set<String> mutuallyModifiedFiles = getFilesModifiedByBothParents(project, mergeCommit);

        for (String filePath : mutuallyModifiedFiles) {
            // get merge revision modified methods
            Set<ModifiedMethod> allModifiedMethods = modifiedMethodsHelper.getModifiedMethods(project, filePath, mergeCommit.getAncestorSHA(), mergeCommit.getSHA());
            // get methods modified by both left and right revisions
            Map<String, Tuple2<ModifiedMethod, ModifiedMethod>> mutuallyModifiedMethods = getMutuallyModifiedMethods(project, mergeCommit, filePath);

            boolean fileHasMutuallyModifiedMethods = !mutuallyModifiedMethods.isEmpty();
            if (fileHasMutuallyModifiedMethods) {
                // get file class name
                String className = TypeNameHelper.getFullyQualifiedName(project, filePath, mergeCommit.getAncestorSHA());

                // calling a data collector here because in this specific case we only need
                // revisions for the cases where there are mutually modified methods in this class
                //revisionsCollector.collectDataFromFile(project, mergeCommit, filePath);

                for (ModifiedMethod method : allModifiedMethods) {
                    // get left and right methods for the specific merge method
                    Tuple2<ModifiedMethod, ModifiedMethod> leftAndRightMethods = mutuallyModifiedMethods[method.getSignature()];
                    // if its null than this methods wasn't modified by both left and right

                    boolean methodWasModifiedByBothParents = leftAndRightMethods != null
                    // we loop in all methods and discard the cases that were not modified by both left and right
                    // instead of looping directly the mutually modified methods because its cheaper to do it like this
                    // because the other way we would have to search the all methods list for each iteration to get merge
                    // revision method
                    if (methodWasModifiedByBothParents) {
                        collectMethodData(leftAndRightMethods, method, project, mergeCommit, className)
                    }

                }

            }


        }
        println "${project.getName()} - ModifiedLinesCollector collection finished"
    }

    void createExperimentalDataFiles(String outputPath) {
        this.experimentalDataFile = new File(outputPath + "/data/results.csv")
        if (!experimentalDataFile.exists()) {
            this.experimentalDataFile << 'project;merge commit;className;method;left modifications;left deletions;right modifications;right deletions\n'
        }

        if (arguments.isPushCommandActive()) {
            this.experimentalDataFileWithLinks = new File("${outputPath}/data/result-links.csv");
        }
    }

    private synchronized void printResults(Project project, MergeCommit mergeCommit, String className, String modifiedDeclarationSignature,
                                           HashSet<Integer> leftAddedLines, HashSet<Integer> leftDeletedLines, HashSet<Integer> rightAddedLines,
                                           HashSet<Integer> rightDeletedLines) {

        experimentalDataFile << "${project.getName()};${mergeCommit.getSHA()};${className};${modifiedDeclarationSignature};${leftAddedLines};${leftDeletedLines};${rightAddedLines};${rightDeletedLines}\n"

        // Add links.
        if(arguments.isPushCommandActive())
            addLinks(project.getName(), mergeCommit.getSHA(), className, modifiedDeclarationSignature, leftAddedLines, leftDeletedLines, rightAddedLines, rightDeletedLines, arguments.getResultsRemoteRepositoryURL())

    }

}*/