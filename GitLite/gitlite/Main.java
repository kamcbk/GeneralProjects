package gitlite;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Kevin Marroquin
 */
public class Main {

    /** Usage: java gitlite.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        //Case for no arguments
        if (args.length == 0) {
            System.out.println("No arguments given!");
        } else {
            new Main(args);
        }
    }

    Main(String[] args) {
        //Seeing if any key commands are given. Otherwise, throw error.
        String command = args[0];
        switch (command) {
            case "init":
                new Init();
                break;
            case "add":
                if (numArg(1, "", args)) {
                    new Add(args[1]);
                }
                break;
            case "commit":
                if (numArg(1, "", args)) {
                    if (args[1] == null | args[1].equals("")) {
                        System.out.println("Please enter a commit message.");
                    } else {
                        new Commit(args[1]);
                    }
                }
                break;
            case "rm":
                if (numArg(1, "", args)) {
                    new Remove(args[1]);
                }
                break;
            case "log": //TODO: Merge addition
                if (numArg(0, "", args)) {
                    new Log();
                }
                break;
            case "global-log":
                if (numArg(0, "", args)) {
                    new GlobalLog();
                }
                break;
            case "find":
                if (numArg(1, "", args)) {
                    new Find(args[1]);
                }
                break;
            case "status":
                if (numArg(0, "", args)) {
                    new Status();
                }
                break;
            case "checkout": //TODO: Test
                if (args.length > 4) {
                    System.out.println("Illegal number of " +
                            "arguments for command");
                    break;
                }
                /**Case 1.*/
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    new Checkout(args[2], false);
                } else if (args.length == 4) {
                    /**Case 2.*/
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    new Checkout(args[1], args[3]);
                } else if (args.length == 2) {
                    /**Case 3.*/
                    new Checkout(args[1], true);
                } else {
                    System.out.println("No checkout occurred. " +
                            "Please input additional arguments.");
                }
                break;
            case "branch":
                if (numArg(1, "", args)) {
                    new AddBranch(args[1]);
                }
                break;
            case "rm-branch":
                if (numArg(1, "", args)) {
                    new RmBranch(args[1]);
                }
                break;
            case "reset": //TODO: Test
                if (numArg(1, "", args)) {
                    new Reset(args[1]);
                }
                break;
            default:
                System.out.println("'" + command + "' " +
                        "is not a gitlite command!");
        }
    }

    /**Number of arguments after initial command. Allows for custom error.*/
    private boolean numArg(int num, String custoErr, String... args) {
        if (args.length - 1 == num) {
            return true;
        } else if (custoErr.length() > 0) {
            System.out.println(custoErr);
        } else {
            System.out.println("Illegal number of arguments for command");
        }
        return false;
    }
}
