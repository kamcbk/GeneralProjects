package gitlite;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;

/** Initializes gitlite repository.
 *  @author Kevin Marroquin
 */

class Init {

    Init() {
        /**Creating new potential path.*/
        File git_dir = new File(".gitlite");
        if (!git_dir.exists()) {

            /**Making new directory.*/
            git_dir.mkdir();

            /**Making staging area directory.*/
            (new File(".gitlite/staging-area")).mkdir();

            /**Making all files folder.*/
            (new File(".gitlite/all-files-folder")).mkdir();

            /**Making all hashes folder.*/
            File commitDir = new File(".gitlite/commits");
            commitDir.mkdir();

            /**commit-history (keys/commits).*/
            File structureFile = Utils.join(git_dir, "structure");
            Utils.writeContents(structureFile);

            /**First commit and saving it.*/
            Instant ts = Instant.EPOCH; //1970 timestamp
            UnitCommit firstCommit = new UnitCommit("initial commit",
                    new HashMap<>(), null, ts, "master");
            File firstSha = Utils.join(commitDir, firstCommit.returnSha1ID());
            Utils.writeContents(firstSha);
            Utils.serialize(firstSha, firstCommit);

            /**Saving structure.*/
            Structure structure = new Structure(firstCommit);
            Utils.serialize(structureFile, structure);

        } else {
            System.out.println("A Gitlite version-control system " +
                    "already exists in the current directory.");
        }
    }

}
