package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

// Library of functions that helps return the apropriate Branch or commit and helps
// bundle manual manipulation of writing and retrieving Branches from program to disk.
public class Branch {

    // returns the commit SHA of the current Branch
    public static String getHeadCommit() {
        String currentBranch = Utils.readContentsAsString(Repository.HEAD_FILE);
        File currentBranchPath = new File(currentBranch);
        String currentCommit = Utils.readContentsAsString(currentBranchPath);

        return currentCommit;
    }

    // return the commit ID of branchName
    public static String getBranchCommit(String branchName) {
        File branch = Utils.join(Repository.REF_DIR, branchName);
        if (!branch.exists()) {
            return "Branch not found";
        }

        // return the Commit ID to that branch
        return Utils.readContentsAsString(branch);

    }

    // moves the headPointer to another branch
    public static void changeBranch(String branchName) {
        File branch = Utils.join(Repository.REF_DIR, branchName);
        if (branch.exists()) {
            Utils.writeContents(Repository.HEAD_FILE, branch.getPath());
        }
    }

    // moves the current branch to a specific commit
    public static void updateBranchPosition(String branchName, String commitID) {
        File commit = Utils.join(Repository.COMMIT_DIR, commitID);
        File branch = Utils.join(Repository.REF_DIR, branchName);
        if (commit.exists() && branch.exists()) {
            Utils.writeContents(branch, commitID);
        }
    }

    // returns the name of the curent branch that has HEAD pointed to it
    public static String getCurrentBranchName() {
        File file = new File (Utils.readContentsAsString(Repository.HEAD_FILE));
        return file.getName();
    }

    // returns the earliest shared commit between the current branch and the other branch
    public static String latestCommonAncestor(String otherBranch) {

        Set<String> ancestors = new HashSet<String>();
        String node = getHeadCommit(); // gets the current commitID of the latest node in the branch
        Commit currentBranch = Commit.loadCommit(node);
        Commit commitPTR = currentBranch;

        // iterate through all commits until reaching root commit
        // store all commits from current branch into Set
        while (commitPTR != null) {
            ancestors.add(commitPTR.getHash());
            for (String parent : commitPTR.getParent()) {
                commitPTR = Commit.loadCommit(parent);
            }
        }
        // Traverse the commit history of the given branch to find the split point
        String otherBranchID = Branch.getBranchCommit(otherBranch);
        Commit otherBranchPTR = Commit.loadCommit(otherBranchID);
        while (otherBranchPTR != null) {
            if (ancestors.contains(otherBranchPTR.getHash())) {
                return otherBranchPTR.getHash(); // Found the split point
            }
            for (String parent : otherBranchPTR.getParent()) {
                otherBranchPTR= Commit.loadCommit(parent);
            }
        }

        return null; // No split point found
    }

    // pritns true of branch exists
    public static Boolean branchExist(String branch) {
        File file = Utils.join(Repository.REF_DIR, branch);
        if (file.exists()) {
            return  true;
        }
        return false;
    }
}
