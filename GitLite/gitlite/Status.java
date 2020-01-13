package gitlite;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**Layout of status should represent the following:
 * Start:
 * === Branches ===
 * *master
 * other-branch
 *
 * === Staged Files ===
 * wug.txt
 * wug2.txt
 *
 * === Removed Files ===
 * goodbye.txt
 *
 * === Modifications Not Staged For Commit ===
 * junk.txt (deleted)
 * wug3.txt (modified)
 *
 * === Untracked Files ===
 * random.stuff
 * ---End---
 *
 * @author Kevin Marroquin
 */

class Status {

    Status() {

        /**Loading structure.*/
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        String currBranch = structure.currBranch();

        /**Loading currCommit.*/
        UnitCommit currCommit = (UnitCommit)
                Utils.deserialize(".gitlite/commits/" +
                        structure.headsha1CurrBranch());


        /**Getting branches ordered.*/
        HashMap<String, String> branchs = structure.userBranches();
        ArrayList<String> branchList = new ArrayList<>(branchs.keySet());
        Collections.sort(branchList);

        /**Gathering files to stage. Creating variable to contain values.*/
        ArrayList<String> stagingFileName = new ArrayList<>();

        /**Adding staged files and reordering them.*/
        for (String staged: structure.trackingStaged()) {
            stagingFileName.add(staged);
        }
        Collections.sort(stagingFileName);


        /**Getting a list of removed files ordered*/
        ArrayList<String> untrackedFiles = new ArrayList<>(structure.setToRemove());
        Collections.sort(untrackedFiles);

        /*****PRINTING STATUS.******/

        /**Printing branches.*/
        System.out.println("=== Branches ===");
        for (String branch: branchList) {
            if (branch.equals(currBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }

        /**Printing staged area.*/
        System.out.println();
        System.out.println("=== Staged Files ===");

        for (String staged: stagingFileName) {
            if (!untrackedFiles.contains(staged))
                System.out.println(staged);
        }

        /**Printing removed files.*/
        System.out.println();
        System.out.println("=== Removed Files ===");

        for (Object removed: untrackedFiles) {
            System.out.println(removed.toString());
        }

        /**Printing modifications and untracked files. Extra
         * credit and not going to fill them.
         *
         * A file in the working directory is "modified but not staged" if it is
         *
         * 1.Tracked in the current commit, changed in the working directory, but not staged; or
         * 2.Staged for addition, but with different contents than in the working directory; or
         * 3.Staged for addition, but deleted in the working directory; or
         * 4.Not staged for removal, but tracked in the current commit and deleted from the
         *      working directory.
         *
         * */
        System.out.println();
        System.out.println(
                "=== Modifications Not Staged For Commit ===");

        /**Variable to keep track of files to print.*/
        ArrayList<String> modFiles = new ArrayList<>();

        //1.Tracked in the current commit, changed in the working directory, but not staged
        //4.Not staged for removal, but tracked in the current commit and deleted from the
        //  working directory.
        for (String file: structure.trackingFiles().keySet()) {
            if (currCommit.returnFiles().containsKey(file)
                    && !structure.trackingStaged().contains(file)) {
                //Getting commit file copy
                byte[] commitFile = Utils.readContents(
                        ".gitlite/all-files-folder/"
                        + currCommit.returnFiles().get(file));
                //Getting working directory contents
                byte[] dirFile = new byte[0];
                try {
                    dirFile = Utils.readContents(file);
                } catch (IllegalArgumentException iae) {
                    continue;
                } finally {
                    if (!Arrays.equals(commitFile, dirFile)
                        && (new File(file).exists())) {
                        modFiles.add(file + " (tracked but not staged)");
                    }
                    if (!structure.setToRemove().contains(file)
                            && !(new File(file)).exists()) {
                        modFiles.add(file + " (deleted but tracked)");
                    }
                }
            }
        }
        //2.Staged for addition, but with different contents than in the working directory
        //3.Staged for addition, but deleted in the working directory
        for (String file: structure.trackingStaged()) {
            byte[] stagedFile = Utils.readContents(
                    ".gitlite/staging-area/" + file);
            //Getting working directory contents
            byte[] dirFile = new byte[0];
            try {
                dirFile = Utils.readContents(file);
            } catch (IllegalArgumentException iae) {
                continue;
            } finally {
                if (!Arrays.equals(stagedFile, dirFile)
                        && (new File(file)).exists()) {
                    modFiles.add(file + " (staged an earlier version)");
                }
                if (!Arrays.equals(stagedFile, dirFile)
                        && !(new File(file)).exists()) {
                    modFiles.add(file + " (staged a deleted file)");
                }
            }
        }

        /**Sorting and printing files.*/
        Collections.sort(modFiles);
        for (String file: modFiles) {
            System.out.println(file);
        }

        System.out.println();
        System.out.println("=== Untracked Files ===");
        ArrayList<String> dirFiles = new ArrayList<>();
        File[] allFiles = (new File(".")).listFiles();
        for (File file: allFiles) {
            String fileName = file.getName();
            if (!structure.trackingStaged().contains(fileName)
                && !structure.trackingFiles().containsKey(fileName)
                && !file.isDirectory()) {
                dirFiles.add(fileName);
            }
        }
        Collections.sort(dirFiles);
        for (String file: dirFiles) {
            if (!file.startsWith(".")) {
                System.out.println(file);
            }
        }
        System.out.println();
    }
}
