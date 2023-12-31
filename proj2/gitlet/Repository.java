package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 * Stores a library of functions used Main() to manage a gitlet repository
 * as well as helper functions used by the Repository class.
 *
 *  @author Samuel Khong
 */
public class Repository {
    /**
     *
     * A list of Pathways to different commonly used Files and Directories used in Gitlet
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
            Commit sentinel = new Commit(null, null, null);
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

    // Creates a commit object as well as saves an files staged for addtion and Removes files from CWD staged for deletion
    public static void commit(String message) {
        // check if anything is staged to add or delete
        Index index = Index.loadIndex();
        if (index.getIndexMap().isEmpty() && index.getStagedForRemoval().isEmpty()) {
                System.out.println("No changes added to the commit.");
                return;
        }
        // check if we have a commit message
        else if (message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }

        // get previous commit hash and create a new commit object
        String currentParent = Commit.getCurrentCommit().getHash();
        Commit commit = new  Commit(currentParent, null, message);
    }

    //removes file from CWD and stages them for deletion
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

    // prints a list of all past commits leading from the current commit of HEAD
    // follows the first parent of commits
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
            // Move the current commit to the first parent.
            commit = Commit.loadCommit(commit.getParent().get(0));
        }
    }

    // helper function for log(). Helps prints commit Information
    private static void printCommitInfo(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getHash());
        // if has more than 1 parent, print merge specific info both parents
        if (commit.getParent().get(1) != null) {
            List<String> parents = commit.getParent();
            String parent1 = parents.get(0);
            String parent2 = parents.get(1);
            System.out.println("Merge: " + parent1.substring(0, 6) + " " + parent2.substring(0, 6));
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
        String currentBranch = Branch.getCurrentBranchName();
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

        // go to staged for removal and iterate through index.stagedForRemovals and pritn each file
        for (String removedFile : index.getStagedForRemoval()) {
            System.out.println(removedFile);
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
        Commit commit = Commit.getCurrentCommit();

        // iterate through each blob from the last commit
        for (String blob : commit.blob.keySet()) {
            // if file in previous commit and in CWD but different SHA and not staged
            if (inCWD(blob) && !commit.getBlobSHA(blob).equals(CWDmap.get(blob))
            && !indexMap.containsKey(blob)) {
                System.out.print(blob + " (modified)");
            }
            // if tracked in last commit, not staged to be removed,  and deleted from CWD
            List<String> stagedRemoved = index.getStagedForRemoval();
            // not staged and not in CWD
            if (commit.blob.containsKey(blob) && !stagedRemoved.contains(blob)  && !inCWD(blob)) {
                System.out.print(blob + " (deleted)");
            }

        }

        // iterate thorugh staged items assume every item is staged since iterating through set of staged files
        for (String file : indexMap.keySet()) {

            // staged to add but file is deleted from CWD
            if (inCWD(file) ) {
                // prints modified if CWD and staging directory HASH is different
                if (!indexMap.get(file).equals(CWDmap.get(file))) {
                    System.out.println(file +  " (modified)");
                }

            }
            // staged add but deleted CWD
            else {
                System.out.println(file + " (deleted)");
            }
        }

        System.out.println();
        System.out.println();

        // print out files not tracked in commit but in cwd
        System.out.println("=== Untracked Files ===");
        for (String file : CWDmap.keySet()) {
            // check if file is tracked in last commit or staged for addition in index
            // print if neither
            if (!commit.blob.containsKey(file) && !indexMap.containsKey(file)) {
                System.out.println(file);
            }
        }
        System.out.println();
        System.out.println();
    }

    // replaces the file in CWD with the file from headcommit
    public static void checkoutFile(String filename) {

        Commit commit = Commit.getCurrentCommit();
        // if the previous commit has file, get hash of previous file
        checkoutHelper(commit, filename);
    }

    // updates the file in CWD with a specific commitID version of a file
    public  static void checkoutCommitFile(String commitID, String filename) {

        List<String> listOfCommits = Utils.plainFilenamesIn(COMMIT_DIR);
        // check if commit ID exists. If fails, print error message. exits function
        if (!listOfCommits.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit commit = Commit.loadCommit(commitID);
        checkoutHelper(commit, filename);
    }

    // helper function used by
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
        // print error if file not found in that commit
        else {
            System.out.println("File does not exist in that commit.");
        }
    }

    // changes CWD to all files in that branch and make branch current branch HEAD
    public static void checkoutBranch(String branchName) {
        // check if current Branch exists. If false print error and exit
        List<String> branchList = Utils.plainFilenamesIn(REF_DIR);
         if (!branchList.contains(branchName)) {
             System.out.println("No such branch exists.");
             return;
         }

        // if found, check if input branch is the current Branch with HEAD ptr. If so, print error message. Exit
        else if (Branch.getCurrentBranchName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        File branchFilePath = Utils.join(Repository.REF_DIR, branchName);
        String commitHash = Utils.readContentsAsString(branchFilePath); // gets the commit ID

        // load the commit of the specific branch
        branchCheckoutHelper(commitHash);

        // change the HEAD to point to new branch
        Utils.writeContents(Repository.HEAD_FILE, branchFilePath.getPath());
    }

    // assist in Branch checkout. Checks if any currently track file is up to date and committed. Removes any files in
    // the CWD that aren't part of the commit being checked out and clears the stagign area.
    private  static void branchCheckoutHelper(String commitHash) {

        // load the commit of the specific branch
        Commit commitToBeLoaded = Commit.loadCommit(commitHash); // commit to be loaded
        Commit lastCommit = Commit.getCurrentCommit(); // most recent commit in the current branch
        Map<String, String> CWDFiles = CWDtoSHA();

        // check if any files are overwritten when loading the new commit. If so, check if there is a
        // saved version of the file. If file is not currently tracked, print error message and exit."
        for (String file : lastCommit.blob.keySet()) {
            if (CWDFiles.containsKey(file) && !lastCommit.blob.get(file).equals(CWDFiles.get(file))) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");

                return;
            }
        }

        // delete any file that is not the  commit to be loaded
        for (String file : CWDFiles.keySet()) {
            // if file is not found, delete
            if (!commitToBeLoaded.blob.containsKey(file)) {
                File unwantedFile = Utils.join(CWD, file);
                unwantedFile.delete();
            }
        }
        // iterate through commitToBeLoaded's blobs and add them to CWD
        for (String blob : commitToBeLoaded.blob.keySet()) {
            checkoutHelper(commitToBeLoaded, blob);
        }


        // clear the index.
        Index.clearIndex();
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

    // removes a specific branch (deletes pointer to a commit). The commit associated with the branch will remain unchanged
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

        branchCheckoutHelper(commitID);
        // change update the branch to point to the current commit
        Branch.updateBranchPosition(Branch.getCurrentBranchName(), commitID);
    }

    // combines the most recent file between 2 branches into CWD and creates merge commit
    // if files are both modified in both branches, merge conflict will occur
    public static void merge(String otherBranch) {
        // exit if have items staged
        Index index = Index.loadIndex();
        if (!index.getIndexMap().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        // exit if otherBranch == currentBranch
        else if (otherBranch.equals(Branch.getCurrentBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        // exit if otherBranch does not exist
        else if (!Branch.branchExist(otherBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        // get all files as a map from common ancestor, current branch and otherbranch
        String commonAncestor = Branch.latestCommonAncestor(otherBranch); // gets the commit ID of the latest shared ancestor
        // no common ancestor found exit. Print error message
        if (commonAncestor == null) {
            System.out.println("no common ancestor");
            return;
        }

        Map<String, String> currentBranchFiles = Commit.loadCommit(Branch.getHeadCommit()).blob;
        Map<String, String> otherBranchFiles = Commit.loadCommit(Branch.getBranchCommit(otherBranch)).blob;
        Map<String, String> commonAncestorFiles = Commit.loadCommit(commonAncestor).blob;

        // get a set of all files from the common ancestor and the two branches
        Set<String> allFiles = new HashSet<String>();
        allFiles.addAll(currentBranchFiles.keySet());
        allFiles.addAll(otherBranchFiles.keySet());
        allFiles.addAll(commonAncestorFiles.keySet());

        // appropriately modify working directory with necessary files for merge. Stage additions and deletions.

        File retrievedFile; // used to store file pathways  to blobs of otherBranch
        for (String file : allFiles) {
            // get all SHA values for each file
            String ancestorSHA = commonAncestorFiles.get(file);
            System.out.println("Ancestor files: " + ancestorSHA);

            String currentBranchSHA = currentBranchFiles.get(file);
            System.out.println("HEAD files " + currentBranchSHA);

            String otherBranchSHA = otherBranchFiles.get(file);
            System.out.println("Other File" + otherBranchSHA);

            // if file is present at split, head and other
            if (ancestorSHA != null && currentBranchSHA != null && otherBranchSHA != null) {
                // if  modified in other but not in HEAD. Add content from other into working directory
                if (ancestorSHA.equals(currentBranchSHA) && !ancestorSHA.equals(otherBranchSHA)) {

                    // stage the file
                    index.addToIndex(file);
                    // add file to CWD
                    checkoutCommitFile(Branch.getBranchCommit(otherBranch), file);
                }

                // if modified in head and other, but they head and other are not equal
                else if (!ancestorSHA.equals(currentBranchSHA) && !ancestorSHA.equals(otherBranchSHA)
                        && !currentBranchSHA.equals(otherBranchSHA)) {
                    // display merge conflict and exit
                    System.out.println("Merge conflict");

                    // get contents from HEAD file
                    File headFile = Utils.join(CWD, file);
                    String headFileContent = "";
                    if (headFile.exists()) {
                        headFileContent = Utils.readContentsAsString(headFile);
                    }

                    // get the file location for the other branch's file
                    File otherFile = new File(BLOBS_DIR, otherBranchFiles.get(file));
                    String otherFileContent = "";
                    if (otherFile.exists()) {
                        otherFileContent = Utils.readContentsAsString(otherFile);
                    }

                    String mergeConflictMessage = "<<<<<<< " + Branch.getCurrentBranchName() + '\n';
                    mergeConflictMessage += headFileContent + '\n' + "=======\n";
                    mergeConflictMessage += otherFileContent;
                    mergeConflictMessage += ">>>>>>>\n";

                    // create file and it to CWD with merge conflict message
                    createNewFile(headFile); // overwrites the CWD file or creates file if doesn't exist
                    Utils.writeContents(headFile, mergeConflictMessage);
                    // stage it for addtion
                    index.addToIndex(file);
                }
            }

            // file not in split, not in current but in other
            else if (ancestorSHA == null && currentBranchSHA == null && otherBranchSHA != null) {
                System.out.println("File not found in split or HEAD but added in other");

                // add file from otherBranch to index
                retrievedFile = Utils.join(BLOBS_DIR, otherBranchSHA);
                File fileToBeUpdated = Utils.join(CWD, file);

                // retrieve file from blobs and overwrite the file with same name in CWD
                Utils.writeContents(fileToBeUpdated, Utils.readContents(retrievedFile));
                // stage the file to the commit
                index.addToIndex(file);

                // load files to CWD
                checkoutCommitFile(Branch.getBranchCommit(otherBranch), file);
            }

            // if file unmodified at split and current but not in other
            else if (ancestorSHA == null && currentBranchSHA == null && ancestorSHA != otherBranchSHA) {
                // if the SHA is null, means the file was deleted in other
                // stage file for removal
                if (otherBranchSHA == null) {
                    rm(file);
                }
            }
        }

        // create a new merge commit
        String parent1 = Branch.getHeadCommit();
        String parent2 = Branch.getBranchCommit(otherBranch);
        Commit mergeCommit = new Commit(parent1, parent2,"Merged " + otherBranch + " into " + Branch.getCurrentBranchName() + ".");
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
        List<String> CWD = Utils.plainFilenamesIn(Repository.CWD); // all brna
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
