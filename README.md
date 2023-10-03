# Gitlet
is an implementation of a version-control system. It was a project inspired by CS61B's Gitlet Project. Gitlet is executed through a command-line interface. 
It allows multiple developers to work on the same project simultaneously, managing different versions of the code and merging their changes seamlessly. 
Gitlet helps maintain a history of code revisions, making it easy to roll back to previous states and ensure a reliable and collaborative development process.

![Gitlet](https://raw.githubusercontent.com/samuelkhong/Gitlet/feature-branch/gitlet%20diagram.png)

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
| Command | Description |
| ------- | ----------- |
| `java gitlet.Main init` | To start a repository |
| `java gitlet.Main add [file name]` | To add a file |
| `java gitlet.Main commit [message]` | To make a commit |
| `java gitlet.Main rm [file name]` | To remove a file |
| `java gitlet.Main log` | To see the commit history |
| `java gitlet.Main global-log` | To see the global commit history |
| `java gitlet.Main find [commit message]` | To find commits that contain a given message |
| `java gitlet.Main status` | To see the status (current branch, staged files, removed files, modified not staged for files, and untracked files) |
| `java gitlet.Main checkout -- [file name]` | To checkout a file |
| `java gitlet.Main checkout [commit id] -- [file name]` | To checkout to a commit |
| `java gitlet.Main checkout [commit id] -- [file name]` | To checkout to a branch |
| `java gitlet.Main branch [branch name]` | To create a branch |
| `java gitlet.Main rm-branch [branch name]` | To remove a branch |
| `java gitlet.Main reset [commit id]` | To reset to a commit |
| `java gitlet.Main merge [branch name]` | To merge files from the given branch into the current branch |

# Acknowledgements
Thank you to the staff CS61B



