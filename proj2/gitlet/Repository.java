package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        String currentParent = Commit.getCurrentCommit().getHash();
        Commit commit = new Commit(currentParent, message);
    }

    // if file found, removes file from CWD and index
    public static void rm(String filename) {
        Index index = Index.loadIndex(); // current staging directory
        Commit commit = Commit.getCurrentCommit(); // last commit
        // returns if file is not currently tracked either in the staging directory or already added as a past commit
        if (!commit.blob.containsKey(filename) && !index.getIndexMap().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        // removes file if staged and if tracked, stages for deletion
        index.removeFile(filename);

        // delete file from CWD
        File file = Utils.join(Repository.CWD, filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void log() {
        // get the commit HEAD is pointing to
        Commit commit = Commit.getCurrentCommit();
        // loop until no parents and print information
        while (true) {
            printCommitInfo(commit);
            // exits if first parent is null ie reaches sentinel commit
            if (commit.getParent().get(0) == null) {
                return;
            }
            List<String> parent = commit.getParent();
            commit = Commit.loadCommit(parent.get(0));
        }
    }
    private static void printCommitInfo(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getHash());
        // if has more than 1 parent, print merge specific info both parents
        if (commit.getParent().size() > 1) {
            List<String> parents = commit.getParent();
            String parent1 = parents.get(0);
            String parent2 = parents.get(1);
            System.out.println("Merge: " + parent1.substring(0, 6) + " " + parent2.substring(0, 6) + "/n");
        }

        System.out.println(commit.getTimeStamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    // prints out all commits. No order
    public static void globeLog() {
        List<String> listOfCommits = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        // iterate through commit list and
        for (String hash : listOfCommits) {
            printCommitInfo(Commit.loadCommit(hash));
        }
    }

    // prints out all commit IDS that have this commit message
    public static void find(String message) {
        List<String> listOfCommits = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        // iterate through commit list and
        for (String hash : listOfCommits) {
            Commit commit = Commit.loadCommit(hash);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getHash());
            }
        }
    }

    // prints out all current information of git at the time
    public static void status() {
        String currentBranch = Utils.readContentsAsString(Repository.HEAD_FILE);
        // print the branches. Current branch *
        List<String> branches = Utils.plainFilenamesIn(Repository.REF_DIR);
        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }

        System.out.println();
        System.out.println();

        // print currently staged files
        System.out.println("=== Staged Files ===");
        Index index = Index.loadIndex();
        Map<String, String> indexMap = index.getIndexMap();
        for (String file : indexMap.keySet()) {
            System.out.println(file);
        }

        System.out.println();
        System.out.println();

        // print out removed files
        System.out.println("=== Removed Files ===");
        Commit commit = Commit.getCurrentCommit();
        List<String> currentFiles =  Utils.plainFilenamesIn(Repository.CWD);
        // compare every previous blob file and see if it is still in CWD. If not, print deleted file
        for (String blob : commit.blob.keySet()) {
            if (!currentFiles.contains(blob)) {
                System.out.println(blob);
            }
        }
        System.out.println();
        System.out.println();

        /*
        * Tracked in the current commit, changed in the working directory, but not staged; or
        * Staged for addition, but with different contents than in the working directory; or
        * Staged for addition, but deleted in the working directory; or
        * Not staged for removal, but tracked in the current commit and deleted from the working directory.
        *  */

        System.out.println("=== Modifications Not Staged For Commit ===");
        //tracked and changed not stage
        Map<String, String> CWDmap = CWDtoSHA();
        for (String blob : commit.blob.keySet()) {
            // if file in previous commit and in CWD but different SHA and not staged
            if (inCWD(blob) && commit.getBlobSHA(blob).equals(CWDmap.get(blob))
            && !indexMap.containsKey(blob)) {
                System.out.print(blob + " (modified)/n");
            }
            //not staged to be removed, but tracked and deleted from CWD
            List<String> stagedRemoved = index.getStagedForRemoval();
            // not staged and not in CWD
            if (!stagedRemoved.contains(blob)  && !inCWD(blob)) {
                System.out.print(blob + " (deleted)/n");
            }

        }
        // staged for add but SHA is different in index and CWD
        for (String file : indexMap.keySet()) {
            if (inCWD(file) && !indexMap.get(file).equals(CWDmap.get(file))) {
                System.out.println(file +  " (modified)/n");
            }
            // staged add but deleted CWD
            if (!inCWD(file)) {
                System.out.println(file + " (deleted)/n");
            }
        }

        // print out files not tracked in commit but in cwd
        System.out.println("=== Untracked Files ===");
        for (String file : CWDmap.keySet()) {
            if (!commit.blob.containsKey(file)) {
                System.out.println(file);
            }
        }
        System.out.println();
        System.out.println();
    }


    // replaces files in CWD with checkout files. 1 of 3 options
    private static void checkoutSelector(String input) {
        // if string input matches branch name then
        List<String> branches =  Utils.plainFilenamesIn(REF_DIR);
        if (branches.contains(input)) {
            checkoutBranch(input);
        }
        else {
            checkoutFile(input);
        }
    }

    // replaces the file in CWD with the file from headcommit
    public static void checkoutFile(String filename) {

        Commit commit = Commit.getCurrentCommit();
        // if the previous commit has file, get hash of previous file
        //checkoutHelper(commit, filename);
    }

    // updates the file in CWD with a specific commitID
    public  static void checkoutCommitFile(String commitID, String filename) {
        Commit commit = Commit.loadCommit(commitID);
        checkoutHelper(commit, filename);
    }

    private static void checkoutHelper(Commit commit, String filename) {
        // check if file is tracked at the commit specified
        if (commit.blob.containsKey(filename)) {
            // get blob file location
            String previousFileSHA = commit.blob.get(filename);
            File blobPath = Utils.join(Repository.BLOBS_DIR, previousFileSHA);

            // load previous blob contents file
            // overwrite file in CWD with loaded content
            File currentFilePath = Utils.join(Repository.CWD, filename);
            if (!currentFilePath.exists()) {
                createNewFile(currentFilePath);
            }
            Utils.writeContents(currentFilePath, Utils.readContents(blobPath));
        }
    }

    // changes CWD to all files in that branch and make branch current branch HEAD
    public static void checkoutBranch(String branchName) {
        File branchFilePath = Utils.join(Repository.REF_DIR, branchName);
        String commitHash = Utils.readContentsAsString(branchFilePath); // gets the commit ID
        // load the commit of the specific branch
        Commit commit = Commit.loadCommit(commitHash);

        // add all blobs into the CWD
        for (String blob : commit.blob.keySet()) {
            checkoutHelper(commit, blob);
        }

        // change the HEAD to point to new branch
        Utils.writeContents(Repository.HEAD_FILE, branchFilePath.getPath());
    }

    // creates new branch at the current HEAD pointer location
    public static void branch(String branchName) {
        // create new file for branch
        File newBranchPath = Utils.join(REF_DIR, branchName);

        // check if branch already exist, exits if true
        if (newBranchPath.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }

        createNewFile(newBranchPath);
        // get the SHA-1 hash found at File Headpointer is directing to
        File currentBranchPath = new File(Utils.readContentsAsString(HEAD_FILE));
        String currentBranch = Utils.readContentsAsString(currentBranchPath);
        Utils.writeContents(newBranchPath, currentBranch);
    }

    public static void rmBranch(String branch) {
        File branchPath = Utils.join(REF_DIR, branch);
        if (!branchPath.exists()) {
            System.out.println("branch with that name does not exist.");
        }

        //safety check if HEAD and branch are the same. Aborts and exits
        else if (Branch.getHeadCommit().equals(Branch.getBranchCommit(branch))) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        // delete branch
        branchPath.delete();
    }

    // reset gets all tracked files from a specific commit. Removes untracked files. Moves Head pointer and branch to point to the commit
    public static void reset(String commitID) {
        //check if the commit exists
        Commit nextCommit = Commit.loadCommit(commitID); // Gets the commit we are attempting to move into
        if (nextCommit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        // get latest commit from current branch
        Commit currentBranchCommit = Commit.getCurrentCommit();
        List<String> CWD = Utils.plainFilenamesIn(Repository.CWD);
        //compare CWD against past commit. If r unadded files found, display warning and exit
        for (String file : CWD) {
            if (!currentBranchCommit.blob.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        // move current branch and HEAD to the specific commit
        String currentBranch = Branch.getCurrentBranchName();
        Branch.updateBranchPosition(Branch.getCurrentBranchName(), commitID);
        checkoutBranch(currentBranch);
    }

    public static void merge(String otherBranch) {
        // exit if have items staged
        Index index = Index.loadIndex();
        if (!index.getIndexMap().isEmpty()) {
            System.out.println("You have uncommitted changes.");
        }

        // exit if otherBranch == currentBranch
        else if (otherBranch == Branch.getCurrentBranchName()) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        // exit if otherBranch does not exist
        else if (!Branch.branchExist(otherBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        // get all files as a map from common ancestor, current branch and otherbranch
        String commonAncestor = Branch.latesCommonAncestor(otherBranch);
        Map<String, String> currentBranchFiles = Commit.loadCommit(Branch.getHeadCommit()).blob;
        Map<String, String> otherBranchFiles = Commit.loadCommit(Branch.getBranchCommit(otherBranch)).blob;
        Map<String, String> commonAncestorFiles = Commit.loadCommit(Branch.getBranchCommit(commonAncestor)).blob;

        // get a set of all files from the common ancestor and the two branches
        Set<String> allFiles = new HashSet<String>();
        allFiles.addAll(currentBranchFiles.keySet());
        allFiles.addAll(otherBranchFiles.keySet());
        allFiles.addAll(commonAncestorFiles.keySet());

        // appropriately modify working directory with necessary files for merge. Stage additions and deletions.

        File retrievedFile; // used to store files

        for (String file : allFiles) {
            // get all SHA values for each file
            String ancestorSHA = commonAncestorFiles.get(file);
            String currentBranchSHA = currentBranchFiles.get(file);
            String otherBranchSHA = otherBranchFiles.get(file);

            // if file is present at split, head and other
            if (ancestorSHA != null && currentBranchFiles != null && otherBranchFiles != null) {
                // if  modified in other but not in HEAD. Add content from other into working directory
                if (ancestorSHA.equals(currentBranchSHA) && !ancestorSHA.equals(otherBranchSHA)) {
                    // replacing CWD file with other file
                    retrievedFile = Utils.join(BLOBS_DIR, otherBranchSHA);
                    File fileToBeUpdated = Utils.join(CWD, file);

                    // retrieve file from blobs and overwrite the file with same name in CWD
                    Utils.writeContents(fileToBeUpdated, Utils.readContents(retrievedFile));
                    // stage the file
                    index.addToIndex(file);
                }
                // if modified in head but not other,
                else if (!ancestorSHA.equals(currentBranchSHA) && ancestorSHA.equals(otherBranchSHA)) {
                    // takes head change, do not add
                }
                // if modified in head and other, but they head and other are equal
                else if (!ancestorSHA.equals(currentBranchSHA) && !ancestorSHA.equals(otherBranchSHA)
                         && currentBranchSHA.equals(otherBranchSHA)) {
                    // do not merge. They are the same file. No need add
                }
                // if modified in head and other, but they head and other are not equal
                else if (!ancestorSHA.equals(currentBranchSHA) && !ancestorSHA.equals(otherBranchSHA)
                        && !currentBranchSHA.equals(otherBranchSHA)) {
                    // display merge conflict and exit
                    System.out.println("Merge conflict");
                }
            }
            // if file not in split, not in other but in head
            else if (ancestorSHA == null && currentBranchFiles != null && otherBranchFiles == null) {
                // already in branch do not need to change
            }
            // file not in split, not in current but in other
            else if (ancestorSHA == null && currentBranchFiles == null && otherBranchFiles != null) {
                // add file to index
                retrievedFile = Utils.join(BLOBS_DIR, otherBranchSHA);
                File fileToBeUpdated = Utils.join(CWD, file);

                // retrieve file from blobs and overwrite the file with same name in CWD
                Utils.writeContents(fileToBeUpdated, Utils.readContents(retrievedFile));
                // stage the file to the commit
                index.addToIndex(file);

            }

            // if file unmodified at split and current but not in other
            else if (ancestorSHA != null && currentBranchSHA != null && otherBranchSHA == null) {
                // stage file for removal
                rm(file);
            }
        }


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
            }
            else {
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

    // return a map of each blob and a hash
    public static Map<String, String> CWDtoSHA() {
        List<String> CWD = Utils.plainFilenamesIn(Repository.CWD);
        Map<String, String> CWDMap = new HashMap<String, String>();

        // Calculate every SHA value in CWD and add to CWDtoHASH map
        for (String file : CWD) {
            File filePath = Utils.join(Repository.CWD, file);
            String fileHash = Utils.sha1(Utils.readContents(filePath));
            CWDMap.put(file, fileHash);
        }
        return CWDMap;
    }


}
