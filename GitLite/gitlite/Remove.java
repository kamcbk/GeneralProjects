package gitlite;

/**Class to unstage files in structure.
 * @author Kevin Marroquin.
 */

class Remove {
    Remove(String path) {
        /**Loading structure for usage.*/
        Structure structure = (Structure)
                Utils.deserialize(".gitlite/structure");
        structure.untrackFile(path);
        Utils.serialize(".gitlite/structure", structure);
    }
}
