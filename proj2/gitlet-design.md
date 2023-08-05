# Gitlet Design Document

**Name**: Samuel Khong

## Classes and Data Structures

### Blob
Stores the hash and file content of a tracked file as a bit array
#### Instanec Variables
* String hash: holds the SHA-1 hash for the file's content
* Byte[] content: stores the file's content as a stream of bits

### Commit
This class stores metadata of the  commit and
keeps track of any current added files and parents
of the current commit.

#### Instance Variables
* String Message - contains the message of commit
* String Date - date at which the commit was created. Assigned by the constructor
* String Parent of current commit
* Map <String, String> blob: Key path, Value: SHA-1 hash

#### Specs
* Uses the sha-1 hash to return a string hash of commit
  * sha-1 hash takes in strings and bitarray. For commits, takes in meta data
  of commit including name, date, commitMessage and blobs. Since blobs are a map of key values,
  will combine Helper to a string varible. 

##### Helper Funcitons
* String blobSum: returns a string of concatenated blob hash values as a single string. 
  * input: Map<String, String> blob
* void saveCommit(): serializes the commit object to bits and saves it to disk
* void loadCommit(): returns the correct input object given the commit hash
  * input: String hash



### Index:
Stores all files currently tracked ready for commit.
#### Instance Varaibles:
* Map<String, String> indexmap: stores filenames and the blob hashes used for staging





### Repository
A library of functions for gitlet used by main. 
Used by main to select the correct Creates the necessary changes to the
./gitlet directory to store or retrieve files.

#### Instance Variables
1. FILE CWD - File pathway to the current working directory
2. FILE GITLET_DIR - File pathway to the gitlet folder


### Algorithms

###  init():  
Checks if the current working directory has a /.gitlet directory.
Creates the necessary folders and files for git to operate.
If current folder is not intialized, creates a new .gitlet directory and 2
subdirectories "Objects", "Dirs". Objects stores all files including blobs,
and commits in respective subdirectories, Dirs stores the name of all branches.
Creates the intial commit file and stores it into objects after hashing them.
Creates a subdirectory in Dir for master branch that points to the first commit.
Creates a file  "HEAD" in ./git Writes the string of the current path to current branch
Creates an empty index file in /.gitlet
###  Commit(): Checks the index to see if all files in the working directory match the files added in
the staging directory. Creates a new commit object using commit constructor adds metadata and adds all files in
index to blob before clearing index

##### Instance Variables
1. String Message - user inputed message about updated string 
2. String parent - String pointer to commit hash


### add():
Adds specified file with String filename into the index if not previously modified or unadded
#### input variable
* String filename
##### Specs
* takes filename and first check if the filename is found in CWD
* Creates a hash of the current file. Compares it to list of blob hashes
* if new hash not found, creates a blob and adds filename and hash to IndexMap

### Rm():
Removes the files with String filename from the CWD and from the index file
#### input variable
* String filename: 

#### specs
* checks to see if filename is a file in the current working directory using Repository.inCWD()
* deletes file with the same filename in CWD
* loads current index in index object
* Using getIndexMap() gets current index map and uses indexMap.remove("filename" to remove file from index
* updates index by doing saveIndex()

### Log():
Prints out all past commits within the same branch. Displays the commit hash, Date and message
#### specs 
* load the most recent commit using loadcommit() and getMostRecent()
* recursively iterates to each parent until first commit. 
  * if merged, prints shortened for both parents
* prints out commit from most recent to first

## Persistence

#### Repository.add()
1. Write blobs to disks. After a file is added to the staging area we will need will
need to save the files to disk. We first will need to calculate the SHA-1 hash. We then create a blob
file using that SHA-1 Hash as the name of the file at that specific version. Using Utils.writeContents()
we can serialize and save the file as a stream of bits. The blog will be stored in the
objects folder in /.git/blobs

2. Writing updated index to disk. After a file is added into the staging area using for  example gitlet add file.txt,
we must update the index file in the /.git directory to include the SHA-1 hashes of the 
object. We can do this by creating a hashmap of blob hashes as keys and String pathway as map values. 
Once all hashes have been added, Using Utils.writeContents() we can serialize and overwrite the index file as a stream of bits. 
The blog will be stored as an index file in /.git/ objects / blobs
3. Writing commits to disk. Since the class is serializable after object is created, use saveCommit() to convert
to stream of bits saved in the ./gitlet/objects/commit folder. Named after the SHA-1 Hash of the commit. 
4. Writing Master branch to disk. Write String to bits using Utils.writeContents
5. Writing Head to disk. Using Utils.

    
