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
##### Init 





## Algorithms
#### Repository Class
1. init():  Checks if the current working directory has a /.gitlet directory.
   Creates the necessary folders and files for git to operate. If current folder is not intialized, creates a new .gitlet directory and 2
  subdirectories "Objects", "Dirs". Objects stores all files including blobs, trees,
  and commits in respective subdirectories, Dirs stores the name of all branches.
  Creates the intial commit file and stores it into objects after hashing them. 
  Creates a subdirectory in Dir for master branch that points to the first commit.
  Creates a pointer "HEAD" in ./git that points to the master branch

## Persistence
1. Write blobs to disks. After a file is added to the staging area we will need will
need to save the files to disk. We first will need to calculate the SHA-1 hash. We then create a blob
file using that SHA-1 Hash as the name of the file at that specific version. Using Utils.writeContents()
we can serialize and save the file as a stream of bits. The blog will be stored in the
objects folder in /.git/blobs

   1. Writing updated index to disk. After a file is added into the staging area using for  example gitlet add file.txt,
   we must update the index file in the /.git directory to include the SHA-1 hashes of the 
   object. We can do this by creating a hashmap of blob hashes as keys and String pathway as map values. 
   Once all hashes have been added, Using Utils.writeContents() we can serialize and overwrite the index file as a stream of bits. 
   The blog will be stored as an index file in /.git/
    
