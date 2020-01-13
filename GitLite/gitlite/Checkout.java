package gitlite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

/**Looks into prior commit history, either file-wise or
 * through a prior branch.
 * @author Kevin Marroquin
 */

class Checkout {

    //Case 1 and 3: Only filename OR branchname
    Checkout(String name, boolean isBranch) {
        //Case 3
        if (isBranch) {
            newBranch(name);
        } else { //Case 1
            currCommitFiles(name);
        }
    }
    //Case 2: filename and commitID
    Checkout(String commitID, String filename) {
        priorCommitFiles(commitID, filename);
    }

    /**Return the files that existed in a previous commit*/
    public static void priorCommitFiles(String commitID, String filename) {

        //Loading prior commit
        UnitCommit priorCommit =
                (UnitCommit) Utils.deserialize(".gitlite/commits/" + commitID);

        //Gathering Sha1ID of file of most previous commit
        String currFileSha1 = null;
        if (!(priorCommit == null)) {
            currFileSha1 = priorCommit.returnFiles().get(filename);
        }

        if (priorCommit == null) {
            //Error for loading commit
            System.out.print("No commit with that id exists.");
        } else if (currFileSha1 == null || currFileSha1.isEmpty()) {
            //Error for loading Sha1ID
            System.out.print("File does not exist in that commit.");
        } else {
            //Replace directory file with prior commit file
            try {
                //Copying file to all-files-folder
                File currFile = new File(filename);
                File pastFile = new File(".gitlite/all-files-folder/" + currFileSha1);
                Files.copy(Paths.get(pastFile.getPath()),
                        Paths.get(currFile.getPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new GitliteException("Something went wrong with copying files!");
            }
        }
    }

    /**Return the files from a prior filename*/
    public static void currCommitFiles(String filename) {
        Structure structure = (Structure) Utils.deserialize(".gitlite/structure");
        priorCommitFiles(structure.branchSha1EndName(), filename);
    }

    /**Loads and rewrites all files in a commit at the head of a given branch to
     * a working directory.
     */
    public static void newBranch(String branchname) {
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");


        if (!structure.userBranches().containsKey(branchname)) {
            /**Check if branchname exists in our structure.*/
            System.out.print("No such branch exists.");

        } else if (structure.currBranch().equals(branchname)) {
            /**Check if branch is the current branch.*/
            System.out.print("No need to checkout the current branch.");

        } else {
            /**Creating frame for branches operations.*/

            /**Gathering branch names.*/
            String oldBranchHead = structure.branchSha1EndName();
            String newBranchHead = structure.userBranches().get(branchname);

            /**Loading old commit.*/
            UnitCommit oldCommit = (UnitCommit)
                    Utils.deserialize(
                            ".gitlite/commits/" + oldBranchHead);

            /**Loading new commit.*/
            UnitCommit newCommit = (UnitCommit)
                    Utils.deserialize(
                            ".gitlite/commits/" + newBranchHead);


            /**Creating list of files in current directory, new commit,
             * old commit, and staging area.*/


            /**Loading all old and new files.*/
            HashMap<String, String> oldFiles = oldCommit.returnFiles();
            HashMap<String, String> newFiles = newCommit.returnFiles();

            /**Adding/replacing new files.*/
            for (String newfile : newFiles.keySet()) {
                Utils.writeContents(new File(newfile),
                        Utils.readContents(
                                ".gitlite/all-files-folder/"
                                        + newFiles.get(newfile)));
            }

            /**Deleting old untracked files.*/
            for (String oldfile : oldFiles.keySet()) {
                if (!newFiles.containsKey(oldfile)) {
                    //delete oldCF from dir
                    (new File(oldfile)).delete();
                }
            }

            /**Clearing staging area and changing
             * structure information to match files.*/
            Utils.clearStagingArea();
            structure.changebranch(branchname);
            structure.changeTrackingFiles(newCommit.returnFiles());
            Utils.serialize(".gitlite/structure", structure);
        }
    }
}
