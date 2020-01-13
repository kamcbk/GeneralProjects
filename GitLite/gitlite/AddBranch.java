package gitlite;

/**Class for adding a new branch to structure.
 * @author Kevin Marroquin
 */

class AddBranch {

    /**Create a new branch name for structure.*/
    AddBranch(String branchname) {
        /**Loading structure for usage.*/
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");

        if (structure.addBranch(branchname, structure.headsha1CurrBranch())) {
            Utils.serialize(".gitlite/structure", structure);
        }
    }
}
