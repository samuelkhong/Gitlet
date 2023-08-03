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
    private List<String> stagedForRemoval;

    Index() {
        indexMap = new HashMap<String, String>();
        stagedForRemoval = new ArrayList<String>();
        saveIndex();
    }

    public void addToIndex(String fileName) {
        File CWDFilePath = Utils.join(Repository.CWD, fileName);
        // calc hash for CWD file
        String CWDfileSHA = Utils.sha1(Utils.readContents(CWDFilePath));
        Commit commit = Commit.getCurrentCommit(); // last commit

        // check if there was ever a previous version of this file before
        // if not create a new blob and store add to index
        List<String> currentBlobList = Utils.plainFilenamesIn(Repository.BLOBS_DIR);
        if (!currentBlobList.contains(CWDfileSHA)) {
            Blob blob = new Blob(fileName);
            indexMap.put(fileName, blob.getBlobHash());

        }

        // check if file was not in last commit but blob previously created
        // add to index
        else if (!commit.blob.containsKey(fileName)) {
            indexMap.put(fileName, CWDfileSHA);
        }

        // if found in commit, compare content
        else if (commit.blob.get(fileName) == CWDfileSHA) {
            // compare HASHes. If same, check if found in added to index and remove
            if (indexMap.containsKey(fileName)) {
                indexMap.remove(fileName);
            }
        }

        // if the file in CWD is added to index but has a different Hash than the one in index, update
        else if (indexMap.containsKey(fileName) && indexMap.get(fileName) != CWDfileSHA) {
            // update the index map with new hash
            indexMap.put(fileName, CWDfileSHA);
        }

        saveIndex();
    }

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
            Index index = new Index(); // after clearing index, repopulate with new index
        }
    }

    // removes file from index and updates index file in system
    public void removeFile(String filename) {
        if (indexMap.containsKey(filename)) {
            indexMap.remove(filename);
            saveIndex();
        }
        else {
            System.out.println("File not found in index");
        }
        // if file tracked by previous commit add it to the removal list
        Commit commit = Commit.getCurrentCommit();
        if (commit.blob.containsKey(filename)) {
            stagedForRemoval.add(filename);
            saveIndex();
        }

    }





}
