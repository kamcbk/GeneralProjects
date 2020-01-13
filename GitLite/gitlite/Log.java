package gitlite;

/**Outputs a history of commits of the current branch.
 * @author Kevin Marroquin
 */

class Log {

    Log() {
        /**Loading structure and finding current head.*/
        Structure structure = (Structure) Utils.deserialize(".gitlite/structure");
        String head = structure.headsha1CurrBranch();

        /**Iterating until reach last commit, printing commit messages in the process.*/
        while (head != null) {
            UnitCommit currCommit = (UnitCommit) Utils.deserialize(".gitlite/commits/" + head);
            Utils.printMessage(currCommit);
            head = currCommit.returnParent();
        }
    }
}
