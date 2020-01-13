package gitlite;

import java.io.File;
import java.util.HashMap;

/**Resets current working directory to a previous commits.
 * @author Kevin Marroquin
 */
class Reset {

    Reset(String commitID) {
        /**Loading structure.*/
        Structure structure =
                (Structure) Utils.deserialize(".gitlite/structure");

        /**Running no commitID error.*/
        File commitDir = new File(".gitlite/commits/");
        if (!Utils.join(commitDir, commitID).exists()) {
            System.out.println("No commit with that id exists.");
        } else {
            /**Loading most recent commit.*/
            UnitCommit currCommit = (UnitCommit)
                    Utils.deserialize(
                            Utils.join(commitDir, structure.headsha1CurrBranch()));

            /**Loading commit wishing to point branch to.*/
            UnitCommit newCommit = (UnitCommit)
                    Utils.deserialize(Utils.join(commitDir, commitID));

            /**Loading all old and new files.*/
            HashMap<String, String> currFiles = currCommit.returnFiles();
            HashMap<String, String> newFiles = newCommit.returnFiles();

            /**Adding/replacing new files.*/
            for (String newfile : newFiles.keySet()) {
                Utils.writeContents(new File(newfile),
                        Utils.readContents(
                                ".gitlite/all-files-folder/"
                                        + newFiles.get(newfile)));
            }

            /**Deleting old untracked files.*/
            for (String currfile : currFiles.keySet()) {
                if (!newFiles.containsKey(currfile)) {
                    //delete oldCF from dir
                    (new File(currfile)).delete();
                }
            }

            /**Checking if commit is in current branch. Changing otherwise.*/
            if (!structure.currBranch().equals(newCommit.returnCurrBranch())) {
                structure.changebranch(newCommit.returnCurrBranch());

            }

            /**Moving head to prior commit.*/
            structure.updateCurrBranch(newCommit.returnSha1ID());
            structure.updateCurrHead(newCommit.returnSha1ID());


            /**Changing structure information to match files.*/
            structure.changeTrackingFiles(newCommit.returnFiles());
            Utils.clearStagingArea();
            Utils.serialize(".gitlite/structure", structure);

        }
    }
}
