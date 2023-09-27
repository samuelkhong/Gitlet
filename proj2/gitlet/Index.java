package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Index serves as a list of files which need ot be tracked. Any files added to index will be saved as
// blobs to the system.
// used by gitlet.add() Class holds a map of IndexElements. Key: file name; Value: Blob
// saves a map of files needed to be added. Creates a blob object that saves currently added files
// Used by commit to know what files need to be recorded in a commit.
public class Index implements Serializable {

    private Map<String, String> indexMap; // key: fileName Value:SHA-1 of blob
    private List<String> stagedForRemoval; // list of files. Will remove file from being tracked in next commit

    Index() {
        indexMap = new HashMap<String, String>();
        stagedForRemoval = new ArrayList<String>();
        saveIndex();
    }

    public void addToIndex(String fileName) {
        File CWDFilePath = Utils.join(Repository.CWD, fileName);
        if (!CWDFilePath.exists()) {
            System.out.println("File not found in current working directory.");
            return;
        }


        // calc hash for CWD file
        String CWDfileSHA = Utils.sha1(Utils.readContents(CWDFilePath));
        Commit commit = Commit.getCurrentCommit(); // last commit

        // Check to make sure you don't add same exact file multiple times
        if (indexMap.containsKey(fileName) && indexMap.get(fileName).equals(CWDfileSHA)) {
            System.out.println("File already added");
            return;
        }

        // check if file was previously commited
        if (commit.blob.containsKey(fileName)) {
            // removes blob if the smae
            if (commit.blob.get(fileName).equals(CWDfileSHA)) {
                System.out.println("File removed since same as previous commit");
                indexMap.remove(fileName);
            }
            // if file in previous commit but doesn't have same HASH ie MODIFIED FILE then add it to index list
            else {
                System.out.println("file in previous commit but doesn't have same HASH");
                indexMap.put(fileName, CWDfileSHA);
            }
        }

        // if file not in previous commit and not already added to index, then can stage to index for additon
        else {
            indexMap.put(fileName, CWDfileSHA);
            System.out.println("file not in previous commit and not already added to index");
        }

        saveIndex();
    }

    // writes an index object as a stream of bits to disk In the INDEX file found in CWD
    private void saveIndex() {
        Utils.writeObject(Repository.INDEX_FILE, this);
    }

    // returns the Index Object from disk to java progrma
    public static Index loadIndex() {
        Class<Index> indexClass = Index.class;
        return Utils.readObject(Repository.INDEX_FILE, indexClass);
    }

    public Map<String, String> getIndexMap() {
        return indexMap;
    }
    public List<String> getStagedForRemoval() {
        return stagedForRemoval;
    }

    // clears the index file and replaces it with an empty file in the CWD named index
    public static void clearIndex() {
        if (Repository.INDEX_FILE.exists()) {
            Repository.INDEX_FILE.delete();
            Repository.createNewFile(Repository.INDEX_FILE);
            Index index = new Index(); // after clearing index, repopulate with new index and saves it
        }
    }

    // removes file from items staged to be added and updates index file in system
    public void removeFile(String filename) {
        // if files tracked by previous commit add it to the removal list
        // stages files for removal on the next commit so no longer tracked
        Commit commit = Commit.getCurrentCommit();
        if (commit.blob.containsKey(filename)) {
            stagedForRemoval.add(filename);
        }

        // removes file if it is currently staged
        if (indexMap.containsKey(filename)) {
            indexMap.remove(filename);
        }
        saveIndex();
    }





}
