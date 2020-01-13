package gitlite;

/**Class for removing unhooking a branch from structure.
 * @author Kevin Marroquin
 */

class RmBranch {

    /**Remove a branch from user's perspective.*/
    RmBranch(String branchname) {
        /**Loading structure for usage.*/
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");

        if (structure.rmBranch(branchname)) {
            Utils.serialize(".gitlite/structure", structure);
        }
    }
}
