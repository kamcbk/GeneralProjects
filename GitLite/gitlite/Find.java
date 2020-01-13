package gitlite;

import java.io.File;

/**Finds and returns a sha1ID containing the commit messege
 * searched for.
 * @author Kevin Marroquin
 */

class Find {

    Find(String commitMsg) {
        //Loading list of files
        File commitPath = new File(".gitlite/commits");
        File[] listOfFiles = commitPath.listFiles();

        //Keeping a counter to test whether commitMsg exists in structure
        boolean hasMsg = false;
        for (File file: listOfFiles) {
            UnitCommit commit = (UnitCommit) Utils.deserialize(file);
            if (commit.returnCommitMsg().equals(commitMsg)) {
                System.out.println(commit.returnSha1ID());
                hasMsg = true;
            }
        }
        //Error-ing if commit message is not contained in previous commit messages
        if (!hasMsg) {
            System.out.println("Found no commit with that message.");
        }
    }
}
