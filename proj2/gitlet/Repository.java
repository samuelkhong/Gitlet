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
 *  @author Samuel Khong
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
    /** File that stores a hashmap of index objects as a stream of bits. Represents
     *  the current staging directory. Files that are added to this will be tracked
     *  and stored as blobs. If files that are currently tracked are commanded by
     *  gitlet to be removed, will be removed from the index. helper functions will
     *  also remove the files from the current working directory and will no
     *  */

    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    /** subdirectory of the objects folder. Stores blobs written to disk. blobs are
     *  blob files named by SHA-1 value determined by content*/
    public static final File COMMIT_DIR = join(OBJECTS_DIR, "commits");
    /** subdirectory of the objects folder. stores commit objects written to disk
     *  as a stream of bits/
     */

    public static final File MASTER_FILE = join(REF_DIR, "master");
    /**
     * File that stores the intial branch called master. Each branch file will hold
     * the string written to disk as bits of the most recent commit hash of that branch. */


    /** Found in /.gitlet Holds the path to the currently selected branch as a string*/


    /* TODO: fill in the rest of this class. */

    /* checks if ./gitlet exists. If not, creates ./gitlet and all subdirectories.
    *  sets up sentinel commit and sets HEAD pointer and Master branch*/
    public static void init() {
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

            // create the intial index file
            Index index = new Index();

            // set head to master branch and save it as string
            String HEAD_PTR = MASTER_FILE.getPath();
            //System.out.println(HEAD_PTR);
            // save the HEAD path as a file.
            Utils.writeContents(Repository.HEAD_FILE, HEAD_PTR);

            // create sentinel commit and save it
            Commit sentinel = new Commit(null, null);
            sentinel.saveCommit();

        }

        // if init is called while the /.gitlet directory already exist
        else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");

        }
    }

    // Adds new and modified files into the staging directory (Index File). Creates a new blob object for each new file
    public static void add(String fileName) {
        // check to see if this file is found in the current working directory

        Index index = Index.loadIndex();
        index.addToIndex(fileName);
    }

    //  dss
    public static void commit(String message) {
        String currentParent = Utils.readContentsAsString(Repository.HEAD_FILE);
        Commit commit = new Commit(currentParent, message);

    }


    // will create file at pathway. If file already exists will delete and replace it with empty file
    // reduces the logic needed in try and catch files to a simplified form
    public static void createNewFile(File filePath) {

        // deletes if file with same name  found at filePath
        if (filePath.exists()) {
            if (!filePath.delete()) {
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
        }
    }

    // returns true if a passed on filename is actually a file in the current working directory
    public static boolean inCWD(String file)  {
        File filePath = Utils.join(Repository.CWD, file); //Path to file if in CWD
        return (filePath.exists());
    }






}
