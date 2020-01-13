package gitlite;

import java.io.File;

/**Provides a list of all commits ever made.
 * @author Kevin Marroquin
 */

class GlobalLog {

    GlobalLog() {
        /**Loading all files in commit file.*/
        File commitPath = new File(".gitlite/commits");
        File[] listOfFiles = commitPath.listFiles();

        /**Printing all commits.*/
        for (int i = 0; listOfFiles.length > i; i++) {
            UnitCommit commit =
                    (UnitCommit) Utils.deserialize(listOfFiles[i]);
            Utils.printMessage(commit);
        }
    }
}
