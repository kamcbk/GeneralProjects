package gitlite;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.Arrays;

/**Adds file to gitlite staging area. Several case scenarios may occur:
 * Cases:
 * 1. New staging area file
 *   a. Tracked
 *   b. Untracked
 * 2. Updated staging area file
 * 3. Same staging area file (do nothing)
 * 4. Same commit file (Do not stage.
 *      Delete file from staging area if existing)
 *
 * Error cases:
 * 1.Staging files does not exist
 * 2.No gitlite repository made
 *
 *  @author Kevin Marroquin
 */

class Add {

    /**Adding files to repository.*/
    Add(String path) {

        /**Loading files to test whether to run Add command.*/

        /**File set to stage.*/
        File to_stage = new File(path);

        /**File name.*/
        String filename = to_stage.getName();

        /**Directory of .gitlite.*/
        File dirExists = new File(".gitlite");

        /**Staging area.*/
        File stagingArea = Utils.join(dirExists, "staging-area");

        /*******ERROR CASES********/
        if (!dirExists.exists()) {
            /**Test to see if .gitlite directory exists.*/
            System.out.println("Could not find .gitlite folder " +
                    "in current directory\n" + "Hint: gitlite init " +
                    "initializes a gitlite repository");

        } else if (!to_stage.exists()) {
        /**Test to see if file exists.*/
        System.out.println("File does not exist.");
        } else {

            /*******POSSIBLE CASES********/

            /**Loading a new file that doesn't exist or is not in path.*/
            byte[] toStageFile = Utils.readContents(to_stage);
            if (toStageFile.equals(null)) {
                throw new GitliteException("File does not exist.");
            }

            /**Loading current commit file.*/
            Structure structure = (Structure)
                    Utils.deserialize(".gitlite/structure");
            String currCommitName = structure.headsha1CurrBranch();
            UnitCommit currCommit = (UnitCommit) Utils.deserialize(
                    new File(".gitlite/commits/" + currCommitName));

            /**Loading current file from commit file (if exists in past commit).*/
            byte[] currInfo = new byte[0];
            String commitPathFile = currCommit.returnFiles().get(path);
            File currFile = new File(
                    ".gitlite/all-files-folder/" + commitPathFile);
            if (currFile.exists()) {
                currInfo = Utils.readContents(currFile);

            }

            /**Removing file from setToRemove if it's in set.
             * This will eliminate the block in setToRemove.*/
            structure.removeUntrackedFile(path);

            /**Creating File for file in staging area (if applicable).*/
            File stagingAreaFile = new File("");
            if (commitPathFile != null) {
                stagingAreaFile = Utils.join(
                        new File(".gitlite/staging-area/"), path);
            }

            //Case path file is the same as last committed file.
            //Remove file from staging area (4)
            if (Arrays.equals(toStageFile, currInfo)) {
                //In case there was already a file in stagingArea
                Utils.join(stagingArea, commitPathFile).delete();

            //Searching for file if previously existed in staging area (2 & 3)
            } else if (stagingAreaFile.exists()) {

                //Load staging-file file version
                byte[] stagedFile = Utils.readContents(stagingAreaFile);

                //Staging area file is not the same as new file
                if (!Arrays.equals(toStageFile, stagedFile)) {
                    try {

                        Files.copy(Paths.get(path),
                                Paths.get(".gitlite/staging-area/" + path),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ioe) {
                        throw new GitliteException(
                                "Something went wrong with copying files!");
                    }
                }

            } else {
                //Staging area file is not the same as new file
                //Tracking file staged if not currently tracked (1)
                if (!structure.trackingFiles().containsKey(path)) {
                    structure.trackStagedFile(path);
                }
                try {
                    //Creating new file in staging-area
                    Files.copy(Paths.get(path),
                            Paths.get(".gitlite/staging-area/" + path),
                            StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ioe) {
                    throw new GitliteException(
                            "Something went wrong with copying files!");
                }
            }
            /**Saving structure.*/
            Utils.serialize(".gitlite/structure", structure);
        }
    }
}
