package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
    /** ./gitlet directory used to store directories and files needed for gitlet*/
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** subdirectory of ./gitlet stores all objects tracked by gitlet
     *  has subdirectories for blobs and for commit objects*/
    public static final File REF_DIR = join(GITLET_DIR, "dir");
    /** subdirectory which stores branches. Branch Files ie master will be files
     *  which store the SHA-1 hash of commit files*/
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    /** Found in ./gitlet HEAD_FILE represents a pointer to the current branch
     *  you are working on. Can also point ot specific commits. Holds the path
     *  as a string towards the commit you are working on (typically the branch)
     *  HEADS that are to specific commits are called detached head states*/
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    /** File that stores a hashmap of index objects as a bit[] on disk. Represents
     *  the current staging directory. Files that are added to this will be tracked
     *  and stored as blobs. If files that are currently tracked are commanded by
     *  gitlet to be removed, will be removed from the index. helper functions will
     *  also remove the files from the current working directory and will no longer be
     *  trakced*/

    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");

    public static final File MASTER_FILE = join(REF_DIR, "master");

    private String HEAD_PTR;


    /* TODO: fill in the rest of this class. */

    /* checks if ./gitlet exists. If not, creates ./gitlet and all subdirectories.*/
    public void init() {
        // if current working directory does not have ./gitlet make directories
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            OBJECTS_DIR.mkdir();
            REF_DIR.mkdir();
            BLOBS_DIR.mkdir();
            COMMIT_DIR.mkdir();

            // create instance files MASTER and INDEX and HEAD
            createNewFile(MASTER_FILE); // master branch will not point to anything until first commit
            createNewFile(INDEX_FILE); // index file is initally empty
            createNewFile(HEAD_FILE); // will initally point to the master branch

            // create sentinel commit and save it
            Commit sentinel = new Commit("Samuel", null, null, null );
            sentinel.saveCommit();


            // set head to master branch and save it as string
            HEAD_PTR = MASTER_FILE.getPath().toString();


            // save the HEAD path as a file.
            saveHead();
        }


    }


    private void saveHead() {
        Utils.writeObject(HEAD_FILE, HEAD_PTR);

    }
    // will create file at pathway. If file already exists will delete and replace it with empty file
    private void createNewFile(File filePath) {

        if (filePath.exists()) {
            if (!filePath.delete()) {
                System.out.println("Failed to delete the existing Master file.");
                // Handle the error as necessary.
            }
        }

        try {
            if (filePath.createNewFile()) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("Failed to create the file.");
                // Handle the error as necessary.
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            // Handle the exception as necessary.
        }
    }

}
