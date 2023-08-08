package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Branch {

    // returns the commit SHA of the current Branch
    public static String getHeadCommit() {
        String currentBranch = Utils.readContentsAsString(Repository.HEAD_FILE);
        File currentBranchPath = new File(currentBranch);
        String currentCommit = Utils.readContentsAsString(currentBranchPath);

        return currentCommit;
    }

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
    public static String latesCommonAncestor(String otherBranch) {
        Set<String> currentBranchNodes = new HashSet<String>();
        String node = getHeadCommit(); // gets the current commitID of the latest node in the branch
        Commit commit = Commit.loadCommit(node);

        // iterate through all commits until reaching root commit
        // store all commits from current branch into Set
        while (commit.getParent().get(0) != null) {
            currentBranchNodes.add(commit.getHash());
            commit = Commit.loadCommit(commit.getParent().get(0)); // recursively advance by parent commit
        }

        Commit branch2 = Commit.loadCommit(otherBranch);
        // iterate through branch2 until root node.
        // check if current branch nodes contains branch 2 hash. If not go to parent node of branch2
        while (!currentBranchNodes.contains(branch2.getHash())) {
            branch2 = Commit.loadCommit(branch2.getParent().get(0));
        }
        // once shared Hash found, return most common ancestor
        return  branch2.getHash();
    }

    public static Boolean branchExist(String branch) {
        File file = Utils.join(Repository.REF_DIR, branch);
        if (file.exists()) {
            return  true;
        }
        return false;
    }
}
