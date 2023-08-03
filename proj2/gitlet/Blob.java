package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    //private String fileName;
    private String hash;
    private byte[] content;

    // intializes blob and hashes content. Calls saveBlob to save hashed content under SHA1 hash in
    // the /objects/blobs

    Blob(String fileName) {
        //this.fileName = fileName;
        File filePath = Utils.join(Repository.CWD, fileName);
        // check if current file is actually found in the working directory if found, calc blob hash and saves blob in system
        if (Repository.inCWD(fileName)) {

            // convert content of file as byte array
            content = Utils.readContents(filePath);

            // creates a hashValue used to identify blob from contents of blob
            this.hash = Utils.sha1(content);
            saveBlob();
        }
    }

    // returns the hash associated with the blob
    public String getBlobHash() {
        return hash;
    }


    private void saveBlob() {
        // Check if Blob Directory exists if not exits
        if (!Repository.BLOBS_DIR.isDirectory()) {
            System.out.println("Error directory  /.gitlet/objects/blobs not found.");
            System.exit(1);
        }

        // create path to blob file
        File blobFile = Utils.join(Repository.BLOBS_DIR, this.hash); // name of file of specific dog

            // check if commit has been created before
            if (!blobFile.exists()) {
                Repository.createNewFile(blobFile);
            }

            // Write blob to the file as byte array
//            Utils.writeObject(blobFile, this);
            Utils.writeContents(blobFile, content);
            System.out.println("blob saved successfully.");
    }
}
