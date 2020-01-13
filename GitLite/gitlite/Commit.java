package gitlite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

/**Creates a new savepoint of changes. Saves files
 * in .gitlite repository.
 * @author Kevin Marroquin
 */

class Commit {

    /**Make a new commit. Inputs a MSG*/
    Commit(String msg) {

        /**Loading current commit.*/
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        String currCommitName = structure.headsha1CurrBranch(); //current commit
        UnitCommit currCommit = (UnitCommit) Utils.deserialize(
                new File(".gitlite/commits/" + currCommitName));


        /**Making path for files in staging area.*/
        File stagingAreaPath = new File(".gitlite/staging-area/");


        /**Error: Nothing to commit if:
         *  1. No files in staging area
         *  2. No files set to remove
         */
        if (stagingAreaPath.listFiles().length == 0
                && structure.setToRemove().size() == 0) {
            throw new GitliteException("No changes added to the commit.");
        }



        /**Creating UnitFiles for all files in staging area.
         * Adding them to future commit.*/
        String sha1ID;
        for (File file: stagingAreaPath.listFiles()) {
            sha1ID = Utils.sha1(file.getName() +
                    String.valueOf(System.currentTimeMillis()));
            //Tracks file
            structure.trackFile(file.getName(), sha1ID);
            try {
                //Copying file to all-files-folder
                File storedFile = new File(".gitlite/all-files-folder/"
                        + sha1ID);
                Files.copy(Paths.get(file.getPath()),
                        Paths.get(storedFile.getPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new GitliteException("Something went wrong with copying files!");
            }
            //Deletes file from staging area
            file.delete();
        }

        /**Tracking files from previous commit to new commit.*/
        for (String filename: structure.setToRemove()) {
            structure.removeTracked(filename);
        }

        /**Constructing new commit*/
        UnitCommit newCommit = new UnitCommit(msg, structure.trackingFiles(),
                currCommit.returnSha1ID(), Instant.now(), structure.currBranch());

        //Update structure with new commit and changing head.
        structure.updateCurrBranch(newCommit.returnSha1ID());
        structure.Structure(newCommit);

        //Drop all rm files
        structure.emptyRemovedFiles();
        structure.emptyStagingFiles();
        Utils.serialize(".gitlite/structure", structure);

        //Add current commit to commits folder
        File newCommitFile = new File(".gitlite/commits/" + newCommit.returnSha1ID());
        Utils.writeContents(newCommitFile);
        Utils.serialize(newCommitFile, newCommit);
    }
}
