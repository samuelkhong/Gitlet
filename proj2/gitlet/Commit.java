package gitlet;

// TODO: any imports you need here

import sun.nio.ch.Util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    String hash;
    String name; // name
    Date date; // date of the commit if first commit then use start date
    String parent; // SHA-1 value of previous commit

    Map<String, String> blob; //Key:path, Value:SHA-1

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    public Commit(String name, Date date, String parent, String message) {
        this.name = name;
        this.date = date;
        this.parent = parent;
        this.message = message;
        this.blob = new HashMap<String, String>();

        // if not the first commit ie has parents check for blobs
        if (this.parent != null) {

            // load the previous commit
            Commit previousCommmit = Commit.loadCommit(this.parent);
            // copy all blobs of the previous commit to this.commit
            this.blob.putAll(previousCommmit.blob);
            // iterate through index. For each file found, replace value in map with new hash

            // remove any files that were posed to be removed in the index

            // clear out index

            // create a SHA hash using all meta data found in commit
            String concatenatedBlobs =  sumBlobs(this.blob); // sums all the blobs into a large string
            this.hash = Utils.sha1(name, date.toString(), message, concatenatedBlobs, parent);
        }
        // first commit upon intialization. Creates sentinel commit
        else {
            this.date = new Date(0);
            this.hash = Utils.sha1(name, date.toString());
        }

        // save the commit object
        this.saveCommit();
    }


    /* returns a concatenated string of all blob HASH values associated with a commit*/
    private String sumBlobs(Map<String, String> blobs) {
        // safety check not passing empty blobs
        if (blobs.isEmpty()) {
            return "";
        }

        StringBuilder concatenatedKeys = new StringBuilder();
        // iterate through all key and pull out
        for (String file : blobs.keySet()) {
            concatenatedKeys.append(blobs.get(file));
        }

        return concatenatedKeys.toString();
    }

//    private Map<String, String> SHAtoCommit(String SHA) {
//        File commit = Utils.join(Repository.COMMIT_DIR, SHA);
//        if (commit.exists()) {
//            Class<Map> mapClass = Map.class;
//            return Utils.readObject(commit, mapClass);
//        }
//
//    }

    public void saveCommit() {

        // Create the directory path for commit files
        File commitDirectory = Repository.COMMIT_DIR;
        if (!commitDirectory.exists()) {
            if (!commitDirectory.mkdirs()) {
                System.out.println("Failed to create dog directory.");
                return;
            }
        }

        // calculate the hashValue of this commit
        //this.hash = Utils.sha1(name, date.toString(), parent)

        // create path to the breed of dog
        File commitFile = Utils.join(commitDirectory, this.hash); // name of file of specific dog

        try {
            // check if commit has been created before
            if (!commitFile.exists()) {
                if (commitFile.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create the file.");
                    return;
                }
            }

            // Write the commit object to the file
            Utils.writeObject(commitFile, this);
            System.out.println("commit saved successfully.");

        }


        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static Commit loadCommit(String commitSHA1) {
       //
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




}
