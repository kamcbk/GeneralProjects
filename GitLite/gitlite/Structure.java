package gitlite;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**Complete structure of GitLite tracking and organization.
 * @author Kevin Marroquin
 */

class Structure implements Serializable {

    /**Defining and calling variables.*/

    /**Pointer to end of branch.*/
    private String branchSha1EndName;

    /**Pointer to head.*/
    private String headsha1CurrBranch;

    /**Name of current branch.*/
    private String currBranch;

    /**HashMap of branch name and head of branch from the user perspective.*/
    private HashMap<String, String> userBranches = new HashMap<>();

    /**HashMap of unique branch identifier and branch name on the system
     * perspective. Branches here are inaccessible to user.*/
    private HashMap<String, String> systemBranches = new HashMap<>();

    /**HashSet of removed files. Key: filePath, Value: fileName*/
    private HashSet<String> setToRemove = new HashSet<>();

    /**Tracked files staged to continuously commit.
     * Key: Path, Value: Sha1ID of file currently tracking.*/
    private HashMap<String, String> trackingFiles = new HashMap<>();

    /**Tracking staged files.*/
    private HashSet<String> trackingStaged = new HashSet<>();

    /**Returning branchSha1EndName.*/
    String branchSha1EndName() { return branchSha1EndName; }

    /**Returning headsha1CurrBranch.*/
    String headsha1CurrBranch() { return headsha1CurrBranch; }

    /**Returning currBranch.*/
    String currBranch() { return currBranch; }

    /**Return userBranches.*/
    HashMap<String, String> userBranches() { return userBranches; }

    /**Return systemBranches.*/
    HashMap<String, String> systemBranches() { return systemBranches; }

    /**Return setToRemove.*/
    HashSet<String> setToRemove() { return setToRemove; }

    /**Return trackingFiles.*/
    HashMap<String, String> trackingFiles() { return trackingFiles; }

    /**Return trackingStaged.*/
    HashSet<String> trackingStaged() { return trackingStaged; }



    ////////////BRANCH//////////////
    public boolean addBranch(String branchName, String shaCommit) {
        //Case whether branch name already exists in userBranch
        if (userBranches.containsKey(branchName)) {
            System.out.println("branch with that name already exists.");
            return false;
        } else {
            systemBranches.put("branch " + systemBranches.size(), branchName);
            userBranches.put(branchName, shaCommit);
            return true;
        }

    }

    public boolean rmBranch(String branchName) {
        if (!userBranches.containsKey(branchName)) {
            /**Case where branch name does not exist in userBranch.*/
            System.out.println("A branch with that name does not exist.");
            return false;
        } else if (currBranch.equals(branchName)) {
            /**Case if trying to remove branch you're currently on.*/
            System.out.println("Cannot remove the current branch.");
            return false;
        } else {
            userBranches.remove(branchName);
            return true;
        }
    }
    ////////////BRANCH//////////////



    public Structure(UnitCommit unitCommit, String branchName) {
        if (userBranches.containsKey(branchName)) {
            /**Error if branchName already exists in userBranches.*/
            System.out.println("A branch with that name already exists.");
        } else if (systemBranches.size() == 0) {
            /**Case where no branches exist.*/
            System.out.println("Have not initiated. " +
                    "Master branch does not exist");
        } else {
            addBranch(branchName, unitCommit.returnSha1ID());
        }

    }

    public void Structure(UnitCommit unitCommit, String branchName) {
        if (userBranches.containsKey(branchName)) {
            /**Error if branchName already exists in userBranches.*/
            System.out.println("A branch with that name already exists.");
        } else if (systemBranches.size() == 0) {
            /**Case where no branches exist.*/
            System.out.println("Have not initiated. " +
                    "Master branch does not exist");
        } else {
            addBranch(branchName, unitCommit.returnSha1ID());
        }

    }

    /**Creating or modifying to current structure*/
    public Structure(UnitCommit unitCommit) {
        //Adding unitCommit to dump unitCommits
        if (systemBranches.size() > 0) {
            /**Selecting head branch and adding new unitCommit.*/
            unitCommit.reassignParent(headsha1CurrBranch);
            branchSha1EndName = unitCommit.returnSha1ID();
            headsha1CurrBranch = unitCommit.returnSha1ID();

        } else {
            /**Initial commit.*/
            addBranch("master", unitCommit.returnSha1ID());
            currBranch = "master";
            headsha1CurrBranch = unitCommit.returnSha1ID();
            branchSha1EndName = unitCommit.returnSha1ID();


        }
    }
    /**Creating or modifying to current structure*/
    public void Structure(UnitCommit unitCommit) {
        //Adding unitCommit to dump unitCommits
        if (systemBranches.size() > 0) {
            /**Selecting head branch and adding new unitCommit.*/
            unitCommit.reassignParent(headsha1CurrBranch);
            branchSha1EndName = unitCommit.returnSha1ID();
            headsha1CurrBranch = unitCommit.returnSha1ID();
            trackingFiles = unitCommit.returnFiles();

        } else {
            /**Initial commit.*/
            addBranch("master", unitCommit.returnSha1ID());
            currBranch = "master";
            headsha1CurrBranch = unitCommit.returnSha1ID();
            branchSha1EndName = unitCommit.returnSha1ID();



        }
    }

    /**Update current head with a previous commit.*/
    public void updateCurrHead(String sha1ID) {
        headsha1CurrBranch = sha1ID;
    }

    /**Update current branch with new commit information.*/
    public void updateCurrBranch(String sha1ID) {
        userBranches.put(currBranch, sha1ID);
    }

    /**Sets a tracked or staged file into an untracked file*/
    public void untrackFile(String path) {
        //Cases: in staging area, in not staging area
        //Cases: Tracking, not tracking
        //Cases: setToRemove contains tracking file, doesn't contain

        //Case rm untracks file already untracked
        if (setToRemove.contains(path)) {
            throw new GitliteException("No reason to remove the file.");
        }
        //Case file is currently in staging area
        File stagedFile = new File(".gitlite/staging-area/" + path);
        if (stagedFile.exists()) {
            stagedFile.delete();
        }
        //Removing tracked staged file from trackingStaged
        trackingStaged.remove(path);

        //Adding path to setToRemove to remove in commit
        //if path is in current commit
        if (trackingFiles.containsKey(path)){
            setToRemove.add(path);
        }
    }

    /**Begins tracking a staged file.*/
    public void trackStagedFile(String filename) {
        trackingStaged.add(filename);
    }

    /**Begins tracking a file.*/
    public void trackFile(String filename, String sha1ID) {
        trackingFiles.put(filename, sha1ID);
    }

    /**Remove a tracked file.*/
    public void removeTracked(String filename) {
        trackingFiles.remove(filename);
    }

    /**Remove file from untrackedFile.*/
    public void removeUntrackedFile(String filename) {
        setToRemove.remove(filename);
    }

    /**Resets setToRemove to an empty set*/
    public void emptyRemovedFiles() {
        setToRemove = new HashSet<>();
    }

    /**Resets staging area.*/
    public void emptyStagingFiles() {
        trackingStaged = new HashSet<>();
    }


    /**Change head of current branch to branchname. Fails to check
     * potential errors; */
    public void changebranch(String branchname) {
        currBranch = branchname;
        branchUpdate(branchname);

    }

    /**Replaces information from another commit onto this structure.*/
    private void branchUpdate(String branchname) {
        //Updating sha1 ID
        String shaRevert = userBranches.get(branchname);
        headsha1CurrBranch = shaRevert;
        branchSha1EndName = shaRevert;

        //Clearing staging and removed files
        emptyStagingFiles();
        emptyRemovedFiles();
    }

    /**Replaces tracking files in a structure with files.
     * Key: Path, Value: Sha1ID of file currently tracking.*/
    public void changeTrackingFiles(HashMap<String, String> files) {
        trackingFiles = files;
    }

}
