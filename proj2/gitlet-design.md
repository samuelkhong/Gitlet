# Gitlet Design Document

**Name**: Samuel Khong

## Classes and Data Structures


### Commit
This class stores metadata of the  commit and
keeps track of any current added files and parents
of the current commit

#### Instance Variables
* Message - contains the message of commit
* Date - date at which the commit was created. Assigned by the constructor
* Parent of current commit



### Repository
A library of functions for gitlet used by main. 
Used by main to select the correct Creates the necessary changes to the
./gitlet directory to store or retrieve files.

#### Instance Variables

1. FILE CWD - File pathway to the current working directory
2. FILE GITLET_DIR - File pathway to the gitlet folder

#### Functions
1. saveCommit() will save the current commit onto disk. Commit object is serialiazable and will be converted to
a stream of bits under the name of the Hash of the commit.
##### Init 





## Algorithms
#### Repository Class
1. init():  Checks if the current working directory has a /.gitlet directory.
   Creates the necessary folders and files for git to operate. 
  If current folder is not intialized, creates a new .gitlet directory and 2
  subdirectories "Objects", "Dirs". Objects stores all files including blobs, trees,
  and commits in respective subdirectories, Dirs stores the name of all branches.
  Creates the intial commit file and stores it into objects after hashing them. 
  Creates a subdirectory in Dir for master branch that points to the first commit.
  Creates a pointer "HEAD" in ./git that points to the master branch
2. commit(): Checks the index to see if all files in the working directory match the files added in 
the staging directory. Iterate through every hash in index to check /.git /object /objects / HASH exists.
If it does not, print 
error message " commit not posible xx.file... unmerged". If index matches with working directory, 
we will keep the loaded index file object saved to disk. We will then create a new tree object stores information
on each file in the index. We will then update the current branch to have the SHA-1 of the latest commit.
   1. Helper Functions makeTree 
      1. makeTree(): creates a tree object that stores the SHA-1 hashes for each blob.
      The tree will take the index object and previous commit hash as a parameter. 
      Returns a custom object called commitTree create a map of blob hashes and pathways from the index files.
      2. After adding all blob files from the index, it will see if there are any unchanged blob files from previous commits
      and  add the hashes and paths to the current commitTree. It will compare previous commit's blobs and see if objects 
      tracked at the current pathway are the same SHA hash. If it is, then it will add that hash to commitTree
   2. updateHeadBranch(): finds the current branch your head pointer is looking at and change the file found in the branch
   to be the current SHA-1 HASH of the latest commit.
## Persistence
#### Repository.add()
1. Write blobs to disks. After a file is added to the staging area we will need will
need to save the files to disk. We first will need to calculate the SHA-1 hash. We then create a blob
file using that SHA-1 Hash as the name of the file at that specific version. Using Utils.writeContents()
we can serialize and save the file as a stream of bits. The blog will be stored in the
objects folder in /.git/blobs

3. Writing updated index to disk. After a file is added into the staging area using for  example gitlet add file.txt,
we must update the index file in the /.git directory to include the SHA-1 hashes of the 
object. We can do this by creating a hashmap of blob hashes as keys and String pathway as map values. 
Once all hashes have been added, Using Utils.writeContents() we can serialize and overwrite the index file as a stream of bits. 
The blog will be stored as an index file in /.git/ objects / blobs
   1. 

    
