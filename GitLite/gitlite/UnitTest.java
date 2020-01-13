package gitlite;

import ucb.junit.textui;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlite package.
 *  @author Kevin Marroquin
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }


    /*******INIT*******/

    /**
     * A test to determine whether Init initializes a new git repository.
     * Also determines whether Init has already been initialized.
     */
    @Test
    public void initInitializeTest() {

        File gitliteDir = new File(".gitlite");

        //Testing to see if new .gitlite folder was created
        newInit();
        assertEquals(true, gitliteDir.exists());

        //Testing to see if initializing a second time fails
        new Init();

        //Deleting newly made repository
        deleteRepos();

    }

    /**
     * Determine if there exists an initial commit
     */
    @Test
    public void initCommitTest() {

        File gitliteDir = new File(".gitlite");

        //Testing to see if new .gitlite folder was created
        newInit();
        assertEquals(true, gitliteDir.exists());

        //Loading structure
        Structure structure = (Structure) Utils.deserialize(
                Utils.join(gitliteDir, "structure"));

        //Checking if initial properties are exact
        assertEquals("master", structure.currBranch());
        assertEquals("eac21c9edbfbbc4fda8719ba8aafe6ee3ad2137d",
                structure.headsha1CurrBranch());

        //Deleting newly made repository
        deleteRepos();

    }

    /**
     * Create a new gitlite repository.
     */
    private void newInit() {
        //Checking if current directory contains a .gitlite folder.
        if (new File(".gitlite").exists()) {
            throw new GitliteException("A current gitlite directory current " +
                    "exists. Aborting further commands");
        }
        new Init();

    }

    /**
     * Deletes a newly created gitlite repository.
     */
    private void deleteRepos() {
        //Checking if current directory contains a .gitlite folder.
        File gitliteDir = new File(".gitlite");
        if (!gitliteDir.exists()) {
            throw new GitliteException("Gitlet directory does not exist, " +
                    "can not delete non-existing file.");
        }

        //Deleting files
        recursiveDelete(gitliteDir);

        //Checking if gitlite repository exists
        assertEquals(false, gitliteDir.exists());

    }

    /**
     * Recursive function to delete all files in a file path.
     */
    private static void recursiveDelete(File file) {
        //To end the recursive loop
        if (!file.exists())
            return;

        //If directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //Call delete to delete files and empty directory
        file.delete();
    }


    /*******ADD*******/

    /**
     * Testing whether files are being added appropriately. Adds two files.
     * Changes one file and re-adds them to staging area. Attempts to re-add
     * an already staged file.
     */
    @Test
    public void addFilesTest() {
        //Adding files
        addFiles();

        //Checking if files were added
        File stagingAreaDir = new File(".gitlite/staging-area");
        File[] files = stagingAreaDir.listFiles();

        //Testing if files were added
        assertEquals(2, files.length);

        //Loading files, reversing their contents, and re-adding files
        String contents0 = Utils.readContentsAsString(files[0]);
        String contents1 = Utils.readContentsAsString(files[1]);

        Utils.writeContents(new File("bob.txt"), "This is not bob.");
        Utils.writeContents(new File("blob.txt"), "This is bob.");

        new Add("bob.txt");
        new Add("blob.txt");

        assertEquals(contents0, Utils.readContentsAsString(files[1]));
        assertEquals(contents1, Utils.readContentsAsString(files[0]));


        //Tries to add the same file again
        new Add("bob.txt");

        //Deleting gitlite repository and newly created files
        deleteRepos();
        deleteNewFiles();

    }

    /**
     * Test error cases for add.
     */
    @Test
    public void addFilesErrTest() {
        //Adding a file without a gitlite repository
        new Add("bob.txt");

        newInit();

        //Adding a file that doesn't exist
        new Add("bob.txt");

        //Assert that no files exist in staging area
        File stagingAreaDir = new File(".gitlite/staging-area");
        File[] files = stagingAreaDir.listFiles();
        assertEquals(0, files.length);

        //Deleting gitlite
        deleteRepos();
    }


    /**
     * Adding files to staging area.
     */
    private void addFiles() {
        //Begin new gitlite session and making new files
        newInit();
        makeNewFiles();

        //Adding files
        new Add("bob.txt");
        new Add("blob.txt");

    }


    /**
     * Making any type of file, given a filename
     * and it's contents (as strings).
     */
    private void makeAnyFile(String filename, String contents) {
        try {
            File file = new File(filename);

            if (file.createNewFile()) {
            } else {
                System.out.println("Error, file already exists.");
            }

            //Writing contents
            Utils.writeContents(file, contents);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Deleting any type of file, given a filename
     * and it's contents (as strings).
     */
    public void deleteAnyFile(String filename) {
        File file = new File(filename);
        file.delete();

    }

    /**
     * Adding bob.txt to staging area.
     */
    private void addBob() {
        //Testing if init exists. Initializes init if not
        if (!new File(".gitlite").exists()) {
            new Init();
        }
        makeAnyFile("bob.txt", "This is bob.");
        new Add("bob.txt");
    }

    /**
     * Adding blob.txt to staging area.
     */
    private void addBlob() {
        //Testing if init exists. Initializes init if not
        if (!new File(".gitlite").exists()) {
            new Init();
        }
        makeAnyFile("blob.txt", "This is not bob.");
        new Add("blob.txt");
    }


    /**
     * Create two small files to test.
     */
    private void makeNewFiles() {
        try {
            File file0 = new File("bob.txt");
            File file1 = new File("blob.txt");

            if (file0.createNewFile() && file1.createNewFile()) {
                System.out.println("Success in creating files!");
            } else {
                System.out.println("Error, file already exists.");
            }

            Utils.writeContents(file0, "This is bob.");
            Utils.writeContents(file1, "This is not bob.");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Deleting two files created by makeNewFiles().
     */
    private void deleteNewFiles() {
        File file0 = new File("bob.txt");
        File file1 = new File("blob.txt");

        //Determining if files exist and deletes them
        if (file0.exists() && file1.exists()) {
            file0.delete();
            file1.delete();
            System.out.println("Success in deleting files!");
        } else {
            System.out.println("Error, files may or may not exist");
        }
    }


    /*******COMMIT*******/

    /**
     * Commit two files.
     */
    @Test
    public void commitFilesTest() {
        //Loading and committing two basic files
        commitSimple("Committing two files.");

        //Testing whether number of files are in appropriate areas
        assertEquals(0,
                new File(".gitlite/staging-area").listFiles().length);
        assertEquals(2,
                new File(".gitlite/all-files-folder").listFiles().length);
        assertEquals(2,
                new File(".gitlite/commits").listFiles().length);


        //Deleting gitlite repository and newly created files
        deleteRepos();
        deleteNewFiles();
    }

    @Test
    public void commitBobThenBlobTest() {
        addBob();
        new Commit("Committing bob.txt");

        //Testing whether number of files are in appropriate areas
        assertEquals(0,
                new File(".gitlite/staging-area").listFiles().length);
        assertEquals(1,
                new File(".gitlite/all-files-folder").listFiles().length);
        assertEquals(2,
                new File(".gitlite/commits").listFiles().length);

        addBlob();
        new Commit("Committing blob.txt");

        //Testing whether number of files are in appropriate areas
        assertEquals(0,
                new File(".gitlite/staging-area").listFiles().length);
        assertEquals(2,
                new File(".gitlite/all-files-folder").listFiles().length);
        assertEquals(3,
                new File(".gitlite/commits").listFiles().length);

        //Deleting gitlite repository and newly created files
        deleteNewFiles();
        deleteRepos();

    }

    /**
     * Committing two simple files with msg as it's input.
     */
    private void commitSimple(String msg) {
        addFiles();
        new Commit(msg);

    }

    /*******STATUS*******/

    /**
     * Testing status with just init.
     */
    @Test
    public void initStatusTest() {
        newInit();
        new Status();
        deleteRepos();

    }

    /**
     * Testing status with add.
     */
    @Test
    public void addStatusTest() {
        addFiles();
        new Status();
        deleteRepos();
        deleteNewFiles();
    }

    @Test
    public void rmStatusTest() {
        newInit();
        addBob();
        new Remove("bob.txt");
        new Status();
        deleteRepos();
        deleteAnyFile("bob.txt");
    }

    /*******REMOVE*******/


    /**
     * Testing whether remove stops tracking files.
     */
    @Test
    public void addRmTest() {
        addFiles();
        new Remove("bob.txt");
        new Status();
        deleteRepos();
        deleteNewFiles();
    }

    /**
     * Test whether removing after a commit moves files to "Removed Files".
     */
    @Test
    public void addCommitRmTest() {
        commitSimple("Testing addCommitRmTest");
        new Remove("bob.txt");
        new Status();
        System.out.println();
        new Commit("Removed bob.txt");
        new Status();
        deleteRepos();
        deleteNewFiles();
    }

    /**
     * Test whether adding the same file after a commit displays in status.
     */
    @Test
    public void commitRmAddTest() {
        commitSimple("Testing addCommitRmTest");
        new Remove("bob.txt");
        new Status();
        System.out.println();
        new Add("bob.txt");
        new Status();
        deleteRepos();
        deleteNewFiles();
    }


    /*******LOG AND GLOABLLOG*******/


    /**
     * Test whether log gives a list of commands.
     */
    @Test
    public void logTest() {
        addBob();
        new Commit("Adding bob.");
        addBlob();
        new Commit("Adding blob.");
        new Log();

        //Deleting gitlite repository and newly created files
        deleteNewFiles();
        deleteRepos();

    }

    /**
     * Test whether global log gives a list of commands.
     */
    @Test
    public void globalLogTest() {
        addBob();
        new Commit("Adding bob.");
        addBlob();
        new Commit("Adding blob.");
        new GlobalLog();

        //Deleting gitlite repository and newly created files
        deleteNewFiles();
        deleteRepos();

    }

    /*******STATUS*******/

    @Test
    /**Testing if branches and files are alphabetical.*/
    public void orderStatusTest() {
        newInit();

        //Branches
        new AddBranch("reorder");
        new AddBranch("disorder");
        new AddBranch("madness");
        new AddBranch("sparta!!!");

        //Untracked files
        makeAnyFile("toxicity.txt", "How do you own disorder! Disorder!");
        makeAnyFile("byob.txt", "Why do presidents fight the war? " +
                "Why do they always send the poor");
        makeAnyFile("dreaming.txt", "For today we will take the body " +
                "parts and put them on the wall.");
        makeAnyFile("forest.txt", "Walk with me my little child through " +
                "the forest of denial.");
        makeAnyFile("spiders.txt", "Dreams are made winding through our hands!");
        makeAnyFile("thetawaves.txt", "Time feels like a midnight rush.");

        new Status();

        //Staged
        new Add("toxicity.txt");
        new Add("byob.txt");
        new Add("dreaming.txt");
        new Add("forest.txt");
        new Add("spiders.txt");
        new Add("thetawaves.txt");


        new Status();


        //Removed
        new Commit("Bring the system down!!");
        deleteAnyFile("thetawaves.txt");
        deleteAnyFile("forest.txt");
        deleteAnyFile("byob.txt");


        new Status();
        deleteAnyFile("spiders.txt");
        deleteAnyFile("toxicity.txt");
        deleteAnyFile("dreaming.txt");
        deleteRepos();
    }

    /**
     * File tracked and modified appears in status.
     */
    @Test
    public void statusMod1Test() {
        addBob();
        new Commit("Adding bob.");
        new Status();
        File file = new File("bob.txt");
        Utils.writeContents(file, "This is not bob.");
        new Status();
        deleteAnyFile("bob.txt");
        deleteRepos();
    }

    /**
     * File tracked and missing from working directory. Appears in status.
     */
    @Test
    public void statusMod4() {
        addBob();
        new Commit("Adding bob.");
        new Status();
        deleteAnyFile("bob.txt");
        System.out.println();
        new Status();
        deleteRepos();
    }

    /**
     * Staged for addition, but an earlier version.
     */
    @Test
    public void statusMod2() {
        addBob();
        File file = new File("bob.txt");
        Utils.writeContents(file, "This is not bob.");
        new Status();
        deleteRepos();
        deleteAnyFile("bob.txt");

    }

    /**
     * Staged for addition, but deleted from the working directory.
     */
    @Test
    public void statusMod3() {
        addBob();
        deleteAnyFile("bob.txt");
        new Status();
        deleteRepos();

    }

    /*******FIND*******/

    /**
     * Test one and multiple commits with FIND.
     */
    @Test
    public void findTest() {
        addBob();
        new Commit("Aww");
        new Find("Aww");
        System.out.println();

        addBlob();
        new Commit("Aww");
        new Find("Aww");
        System.out.println();

        File file0 = new File("bob.txt");
        Utils.writeContents(file0, "This is not blob.");
        new Add("bob.txt");
        new Commit("Nah");
        new Find("Aww");
        System.out.println();

        File file1 = new File("blob.txt");
        Utils.writeContents(file1, "This is blob.");
        new Add("blob.txt");
        new Commit("Aww");
        new Find("Aww");
        System.out.println();

        deleteRepos();
        deleteNewFiles();

    }

    /**
     * Error test for find.
     */
    @Test
    public void findErrTest() {
        addBob();
        new Commit("Aww");
        new Find("Nah");

        deleteRepos();
        deleteAnyFile("bob.txt");

    }

    /*******CHECKOUT*******/

    /**Testing first case for checkout.*/
    @Test
    public void checkoutCase1Test() {
        addBob();
        new Commit("Committing bob.");
        Utils.writeContents("bob.txt", "This is blob.");
        assertEquals(Utils.readContentsAsString("bob.txt"), "This is blob.");
        new Add("bob.txt");
        new Checkout("bob.txt", false);
        assertEquals(Utils.readContentsAsString("bob.txt"), "This is bob.");
        assertEquals(true,
                (new File(".gitlite/staging-area/bob.txt")).exists());
        new Commit("Changing bob.txt");
        assertEquals(Utils.readContentsAsString("bob.txt"), "This is bob.");
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        String bobSha1 = structure.trackingFiles().get("bob.txt");
        assertEquals("This is blob.",
                Utils.readContentsAsString(".gitlite/all-files-folder/" + bobSha1));

        deleteRepos();
        deleteAnyFile("bob.txt");

    }

    /**Testing first error case for checkout.*/
    @Test
    public void checkoutCase1ErrorTest() {
        newInit();
        new Checkout("bob.txt", false);
        addBob();
        new Commit("Adding bob.");
        new Checkout("blob.txt", false);

        deleteRepos();
        deleteAnyFile("bob.txt");

    }

    /**Testing second case for checkout.*/
    @Test
    public void checkoutCase2Test() {
        addBob();
        new Commit("Committing bob.");
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        String currCommitID = structure.headsha1CurrBranch();
        Utils.writeContents("bob.txt", "This is blob.");
        assertEquals(Utils.readContentsAsString("bob.txt"), "This is blob.");
        new Add("bob.txt");
        new Commit("Changing bob.txt");
        new Checkout(currCommitID, "bob.txt");
        assertEquals("This is bob.",
                Utils.readContentsAsString("bob.txt"));
        assertFalse((new File(".gitlite/staging-area/bob.txt")).exists());

        deleteRepos();
        deleteAnyFile("bob.txt");
    }

    /**Testing second error case for checkout.*/
    @Test
    public void checkoutCase2ErrorTest() {
        newInit();
        new Checkout("1234", "bob.txt");
        addBob();
        new Commit("Adding bob.");
        new Checkout("1234", "blob.txt");
        new Checkout("1234", "bob.txt");

        deleteRepos();
        deleteAnyFile("bob.txt");
    }

    /*******ADDBRANCH*******/

    /**
     * Testing basic branch assigning.
     */
    @Test
    public void basicBranch1Test() {
        newInit();
        new AddBranch("growth");
        addBlob();
        addBob();
        new Commit("Main commit on bob and blob.");
        new Checkout("growth", true);
        new Status();
        new Log();

        //Seeing if blob and bob exist
        assertEquals(false, (new File("bob.txt")).exists());
        assertEquals(false, (new File("blob.txt")).exists());

        new Checkout("master", true);
        new Status();
        new Log();

        //Loading structure
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        assertEquals(true, structure.trackingFiles().containsKey("bob.txt"));
        assertEquals(true, structure.trackingFiles().containsKey("blob.txt"));

        //Loading commit
        String currCommitName = structure.headsha1CurrBranch();
        UnitCommit currCommit = (UnitCommit) Utils.deserialize(
                new File(".gitlite/commits/" + currCommitName));
        assertEquals(true, currCommit.returnFiles().containsKey("bob.txt"));
        assertEquals(true, currCommit.returnFiles().containsKey("blob.txt"));

        //Seeing if blob and bob exist
        assertEquals(true, (new File("bob.txt")).exists());
        assertEquals(true, (new File("blob.txt")).exists());

        deleteNewFiles();
        deleteRepos();


    }

    /**
     * Testing basic branch assigning.
     */
    @Test
    public void basicBranch2Test() {
        newInit();
        new AddBranch("growth");
        addBlob();
        addBob();
        new Commit("Main commit on bob and blob.");
        deleteNewFiles();
        new Checkout("growth", true);

        //Seeing if blob and bob exist
        assertEquals(false, (new File("bob.txt")).exists());
        assertEquals(false, (new File("blob.txt")).exists());

        makeAnyFile("bob.txt", "This is not bob.");
        new Add("bob.txt");

        new Commit("Alternative commit!");
        assertEquals(false, (new File("blob.txt")).exists());
        assertEquals("This is not bob.",
                Utils.readContentsAsString("bob.txt"));

        new Checkout("master", true);
        assertEquals("This is bob.",
                Utils.readContentsAsString("bob.txt"));
        assertEquals("This is not bob.",
                Utils.readContentsAsString("blob.txt"));

        new Checkout("growth", true);
        assertEquals(false, (new File("blob.txt")).exists());
        assertEquals("This is not bob.",
                Utils.readContentsAsString("bob.txt"));

        deleteAnyFile("bob.txt");
        deleteRepos();


    }

    /*******RMBRANCH*******/

    /**
     * Basic remove branch.
     */
    @Test
    public void rmBranchSimpleTest() {
        newInit();
        makeAnyFile("bob.txt", "This is bob.");
        new Add("bob.txt");
        new Commit("Adding bob.");
        new AddBranch("newBranch");
        new Checkout("newBranch", true);
        makeAnyFile("blob.txt", "This is not bob.");
        new Add("blob.txt");
        new Commit("Adding blob.");
        new Checkout("master", true);
        new RmBranch("newBranch");
        new Checkout("newBranch", false);
        assertEquals(false, (new File("blob.txt")).exists());
        assertEquals(true, (new File("bob.txt")).exists());
        new Status();
        new Log();

        deleteAnyFile("bob.txt");
        deleteRepos();
    }

    /**
     * Test error cases for removing branches.
     */
     @Test
    public void rmBranchErrTest() {
         newInit();
         new AddBranch("Error");
         new Checkout("Error", true);
         makeAnyFile("bob.txt", "This is bob.");
         new Add("bob.txt");
         new Commit("Committing bob.");
         new RmBranch("Error");
         deleteAnyFile("bob.txt");
         new RmBranch("NonExisting");
         deleteRepos();
     }


    /*******RESET*******/

     /**Creating several scenarios for posssible reset
      * 1. Reset current commit
      * 2. Reset prior commit on the same branch
      * 3. Reset prior commit on a different branch
      */

     @Test
     public void resetTest() {

         /**Creating settings for master.*/
         newInit();
         new AddBranch("Toxicity");
         makeAnyFile("spiders.txt", "Dreams are made winding through our hands!");
         makeAnyFile("sugar.txt", "Who can believe you? Who can believe you? " +
                 "Let your mother pray.");
         new Add("spiders.txt");
         new Commit("Adding spiders");
         Structure structure = (Structure) Utils.deserialize(".gitlite/structure");
         String savept1 = structure.headsha1CurrBranch();
         new Add("sugar.txt");
         new Commit("Adding sugar");

         /**Creating settings for toxicity branch.*/
         makeAnyFile("toxicity.txt", "How do you own disorder! Disorder!");
         makeAnyFile("forest.txt", "Walk with me my little child through " +
                 "the forest of denial.");
         new Checkout("Toxicity", true);
         new Add("toxicity.txt");
         new Commit("Adding toxicity");
         structure = (Structure) Utils.deserialize(".gitlite/structure");
         String savept2 = structure.headsha1CurrBranch();
         new Add("forest.txt");
         new Commit("Adding forest");

         //Checking if resets current commit
         makeAnyFile("deerdance.txt", "Pushing little children with their " +
                 "fully automatics, they like to push the weak around.");
         new Add("deerdance.txt");
         Utils.writeContents("forest.txt", "Why can't you see that you are my child.");
         structure = (Structure) Utils.deserialize(".gitlite/structure");
         new Reset(structure.headsha1CurrBranch());
         assertEquals(0,
                 (new File(".gitlite/staging-area/")).listFiles().length);
         assertEquals(
                 "Walk with me my little child through the forest of denial.",
                 Utils.readContentsAsString("forest.txt"));

         //Checking if resets to past commit
         new Reset(savept2);
         structure = (Structure) Utils.deserialize(".gitlite/structure");
         assertEquals(1, structure.trackingFiles().size());
         assertEquals(savept2, structure.headsha1CurrBranch());
         assertEquals("Toxicity", structure.currBranch());

         //Checking if resets to past commit in different branch
         new Reset(savept1);
         structure = (Structure) Utils.deserialize(".gitlite/structure");
         assertEquals(1, structure.trackingFiles().size());
         assertEquals(savept1, structure.headsha1CurrBranch());
         assertEquals("master", structure.currBranch());
         makeAnyFile("know.txt", "You never think you know why!");
         new Add("know.txt");
         new Commit("Adding know");

         new Log();
         System.out.println("*************");
         new GlobalLog();

         deleteRepos();
         deleteAnyFile("sugar.txt");
         deleteAnyFile("spiders.txt");
         deleteAnyFile("know.txt");
         deleteAnyFile("deerdance.txt");
         deleteAnyFile("toxicity.txt");
         deleteAnyFile("forest.txt");

     }

     /**
     Checks that resetting to a nonexistant commit ID or with an untracked 
     file present prints the correct error.
     */
     @Test
     public void resetErrTest() {
         newInit();
         makeAnyFile("bob.txt", "This is bob.");
         new Add("bob.txt");
         new Commit("Adding bob.txt");
         makeAnyFile("blob.txt", "This is blob.");
         new Add("blob.txt");
         new Commit("Adding blob.txt");
         new Reset("1234");

         deleteRepos();
         deleteAnyFile("bob.txt");
         deleteAnyFile("blob.txt");
     }
}
