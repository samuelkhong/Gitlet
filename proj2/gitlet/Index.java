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

    Index() {
        indexMap = new HashMap<String, String>();
        saveIndex();
    }

    public void addToIndex(String fileName) {
        // check if file exist. If doesn't exits
        File file = Utils.join(Repository.CWD, fileName);
        if (!file.isFile()) {
            return;
        }
        // calculated CWD version of file's hash and see if any blobs with hash exist.
        // convert file to bit array and then calculate sha-1 hash

        String latestFileHash = Utils.sha1(Utils.readContents(file));
        List<String> currentBlobList = Utils.plainFilenamesIn(Repository.BLOBS_DIR);

        // if current Hash is not found, adds the modified file into index and adds updated blob
        // Either means that a currently tracked file is modified or new file added
        if (!currentBlobList.contains(latestFileHash)) {
            Blob blob = new Blob(fileName);
            indexMap.put(fileName, blob.getBlobHash());
            saveIndex();
        }
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

    // clears the index file and replaces it with an empty file in the CWD named index
    public static void clearIndex() {
        if (Repository.INDEX_FILE.exists()) {
            Repository.INDEX_FILE.delete();
            Repository.createNewFile(Repository.INDEX_FILE);
            Index index = new Index();
        }
    }





}
