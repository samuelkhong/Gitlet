package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File DIR_DIR = join(GITLET_DIR, "dir");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File index = join(GITLET_DIR, "index");

    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "commitTree");


    /* TODO: fill in the rest of this class. */

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        if (!OBJECTS_DIR.exists()) {
            OBJECTS_DIR.mkdir();
        }


    }
}
