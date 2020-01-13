package gitlite;

import java.time.Instant;
import java.io.Serializable;
import java.util.HashMap;

/**All Information contained in a commit.
 *
 * @author Kevin Marroquin
 */

class UnitCommit implements Serializable {


    /**String of a SHA-1 key for commit.*/
    private String sha1ID;

    /**String of commit message.*/
    private String commitMsg;

    /**List of UnitFiles contained in a commit,
     * tracked files.
     * e.g. Key: gitlite/test.txt, Value: 3KT8
     * e.g. Key: Path, Value: sha1ID*/
    private HashMap<String, String> files;

    /**Timestamp of commit.*/
    private Instant timestamp;

    /**String of SHA-1 key for parent commit.*/
    private String parentSha1ID;

    /**String of commit's current branch.*/
    private String currBranch;

    //Creating a UnitCommit

    /**Inputs for creating a UnitCommit*/
    public UnitCommit(String commitMsg, HashMap<String, String> files, String parentSha1ID,
                      Instant timestamp, String currBranch) {
        this.commitMsg = commitMsg;
        this.files = files;
        this.parentSha1ID = parentSha1ID;
        this.timestamp = timestamp;
        this.sha1ID = Utils.sha1(this.timestamp.toString());
        this.currBranch = currBranch;
    }

    /**Reassign parentSha1ID to a new ID*/
    public void reassignParent(String sha1ID) {
        this.parentSha1ID = sha1ID;
    }

    /**Returns the Sha1ID of UnitCommit.*/
    public String returnSha1ID() {
        return this.sha1ID;
    }

    /**Returns the commitMsg of UnitCommit.*/
    public String returnCommitMsg() {
        return this.commitMsg;
    }

    /**Returns the Files HashMap history of UnitCommit.*/
    public HashMap<String, String> returnFiles() {
        return this.files;
    }

    /**Returns the timestamp of UnitCommit*/
    public Instant returnTimeStamp() {
        return this.timestamp;
    }

    /**Returns parent Sha1ID of UnitCommit.*/
    public String returnParent() {
        return this.parentSha1ID;
    }

    /**Returns current branch.*/
    public String returnCurrBranch() {
        return this.currBranch;
    }
}
