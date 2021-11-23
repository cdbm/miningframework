package gitManager;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class MergeManager {

    public MergeManager() {}

    public void merge(){
        final File localPath;
        try {
            Git git = Git.open(new File("./.git"));
            Repository repository = git.getRepository();

            ObjectId mergeBase = repository.resolve("teste");

            // perform the actual merge, here we disable FastForward to see the
            // actual merge-commit even though the merge is trivial
            MergeResult merge = git.merge().
                    include(mergeBase).
                    setCommit(true).
                    setFastForward(MergeCommand.FastForwardMode.NO_FF).
                    //setSquash(false).
                            setMessage("Merged changes").
                            call();
            System.out.println("Merge-Results for id: " + mergeBase + ": " + merge);
            for (Map.Entry<String,int[][]> entry : merge.getConflicts().entrySet()) {
                System.out.println("Key: " + entry.getKey());
                for(int[] arr : entry.getValue()) {
                    System.out.println("value: " + Arrays.toString(arr));
                }
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
