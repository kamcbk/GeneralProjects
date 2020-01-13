package gitlite;

/** General exception indicating a Gitlite error.
 * @author Kevin Marroquin
 */
class GitliteException extends RuntimeException {

    /** A GitliteException MSG as its message. */
    GitliteException(String msg) {
        super(msg);
    }
}
