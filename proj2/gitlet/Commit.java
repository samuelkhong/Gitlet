package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  Commit object creates a snapshot of all files found in the current working directory that are
 *  tracked using gitlet.add(). The commit object consist of the metadata including the User, date
 *  of commit, String Hash of the parent commit and String message attached to the commit. The commit
 *  also stores String references to the SHA-1 hashses of obects that have been tracked.
 *
 *
 *  @author Samuel Khong
 */
public class Commit implements Serializable {

    private String hash;
    private String name = "samuel"; // name of the user hardcoded for now
    private String timeStamp; // date of the commit if first commit then use start date
    private List<String> parent; // SHA-1 value of previous commit
    private String message; // message associated with commit

    Map<String, String> blob = new HashMap<String, String>(); //Key:path, Value:SHA-1

    // Getters for the properties
    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public List<String> getParent() {
        return parent;
    }

    public String getMessage() {
        return message;
    }


    /* TODO: fill in the rest of this class. */
    public Commit(String parent, String message) {
        this.message = message;
        this.parent = new ArrayList<String>();
        this.parent.add(0, parent);

        long currentTimeMillis = System.currentTimeMillis();

        // Create a Date object using the current system time
        Date currentDate = new Date(currentTimeMillis);
        this.timeStamp = currentDate.toString();

        // if not the first commit ie has parents check for blobs
        if (this.parent.get(0) != null) {

            // load the previous commit
            Commit lastCommit = getCurrentCommit();
            // load current index, items staged and items to be deleted
            Index currentIndex = Index.loadIndex();
            Map<String, String> stagedToAdd = currentIndex.getIndexMap();
            List<String> stagedToDelete = currentIndex.getStagedForRemoval();

            // set last commit blobs to this commit's blobs
            this.blob = lastCommit.blob;

            // iterate through stage files to be added and creates and add blobs to current commit
            for (String file : stagedToAdd.keySet()) {
                Blob blob = new Blob(file);
                // add the newly created blob into the current blob list of the commit
                this.blob.put(file, blob.getBlobHash());
            }

            // iterate through items staged for removal and remove them from blob list
            for (String file : stagedToDelete) {
                blob.remove(file);
            }

            // clear out index after editing everything staged to add and delete
            Index.clearIndex();


            // create a SHA hash using all meta data found in commit
            String concatenatedBlobs =  sumBlobs(this.blob); // sums all the blobs into a large string
            this.hash = Utils.sha1(name, timeStamp, message, concatenatedBlobs, parent);
        }
        // first commit upon intialization. Creates sentinel commit
        else {
            this.timeStamp = new Date(0).toString(); // if first commit, sets default date
            this.hash = Utils.sha1(name, timeStamp);
            this.message = "intial commit";
        }

        // save the commit object
        this.saveCommit();

        // update the current branch so that it points to this commit
        String head = Utils.readContentsAsString(Repository.HEAD_FILE);
        File currentBranch = new File(head);
        Utils.writeContents(currentBranch, this.hash);
    }


    /* returns a concatenated string of all blob HASH values associated with a commit*/
    private String sumBlobs(Map<String, String> blobs) {
        // safety check not passing empty blobs
        if (blobs.isEmpty()) {
            return "";
        }

        StringBuilder concatenatedKeys = new StringBuilder();
        // iterate through all file and add the SHA hashes into one string
        for (String file : blobs.keySet()) {
            concatenatedKeys.append(blobs.get(file));
        }

        // return the concatenated string builder of hashes as a string
        return concatenatedKeys.toString();
    }


    // creates a file named commit hash with serialized commit object data in  .gitlet/objects/commtis
    public void saveCommit() {

        // create path to the commit
        File commitFile = Utils.join(Repository.COMMIT_DIR, this.hash); // name of file of specific dog
        Repository.createNewFile(commitFile);
        Utils.writeObject(commitFile, this);
    }

    // returns the commit file if specific commit hash matches commit input STring
    public static Commit loadCommit(String commitSHA1) {
        File SHAPath = Utils.join(Repository.COMMIT_DIR, commitSHA1);
        Commit retrievedCommit = null;
        // if the path to SHA hash of commit found, store in retrievedCommti object
        if (SHAPath.exists()) {
            Class<Commit> commitClass = Commit.class;
            retrievedCommit = Utils.readObject(SHAPath, commitClass);
        }
        else {
            System.out.println("Commit not found for SHA: " + commitSHA1);
        }
        return retrievedCommit;
    }

    // return the branch as a string HEADFILE points to
    private static String getPrevCommit() {
        // get branch to where the current HEAD is pointing to
        File file = new File(Utils.readContentsAsString(Repository.HEAD_FILE));
        return Utils.readContentsAsString(file);
    }
    public static Commit getCurrentCommit() {
        String latestCommitHash = getPrevCommit();
        return loadCommit(latestCommitHash);
    }

    public String getBlobSHA(String filename) {
        return this.blob.get(filename);
    }


}
