# Gitlet
is an implementation of a version-control system. Gitlet is executed through a command-line interface. 
It allows multiple developers to work on the same project simultaneously, managing different versions of the code and merging their changes seamlessly. 
Gitlet helps maintain a history of code revisions, making it easy to roll back to previous states and ensure a reliable and collaborative development process.

# Features
Saving backups of directories of files. In gitlet, this is called committing, and the backups themselves are called commits.
Restoring a backup version of one or more files or entire commits. In gitlet, this is called checking out those files or that commit.
Viewing the history of your backups. In gitlet, you view this history in something called the log.
Maintaining related sequences of commits, called branches.
Merging changes made in one branch into another.

# Getting started
Clone the repository. Compile all the java classes in the gitlet subdirectory: javac gitlet/Main.java gitlet/Staging.java gitlet/Repo.java gitlet/Branch.java gitlet/Commit.java gitlet/Head.java gitlet/Status.java.
Move the gitlet folder into the desired directory you want to track. 

# Gitlet commands
To start a repo: java gitlet.Main init

To add a file: java gitlet.Main add [file name]

To make a commit: java gitlet.Main commit [message]

To remove a file: java gitlet.Main rm [file name]

To see the commit history: java gitlet.Main log

To see the global commit history: java gitlet.Main global-log

To find commits that contain a given message: java gitlet.Main find [commit message]

To see the status (current branch, staged files, removed files, modified not staged for files, and untracked files): java gitlet.Main status

To checkout a file: java gitlet.Main checkout -- [file name]

To checkout to a commit: java gitlet.Main checkout [commit id] -- [file name]

To checkout to a branch: java gitlet.Main checkout [commit id] -- [file name]

To create a branch: java gitlet.Main branch [branch name]

To remove a branch: java gitlet.Main rm-branch [branch name]

To reset to a commit: java gitlet.Main reset [commit id]

To merge files from the given branch into the current branch: **java gitlet.Main merge [branch name]**
