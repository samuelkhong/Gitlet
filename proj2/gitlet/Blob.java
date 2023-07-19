package gitlet;

import java.io.File;
import java.io.IOException;
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
        if (filePath.isFile()) {


            // save content of file as byte array
            content = Utils.readContents(filePath);

            // creates a hashValue used to identify blob from contents of blob
            this.hash = Utils.sha1(content);
            saveBlob();
        }
    }

    private void saveBlob() {

        // Check if Blob Directory exists if not exits

        if (!Repository.BLOBS_DIR.isDirectory()) {
            System.out.println("Error directory  /.gitlet/objects/blobs not found.");
            System.exit(1);
        }

        // create path to blob file
        File blobFile = Utils.join(Repository.BLOBS_DIR, this.hash); // name of file of specific dog

        try {
            // check if commit has been created before
            if (!blobFile.exists()) {
                if (blobFile.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create the file.");
                    return;
                }
            }

            // Write the blob object to the file
            Utils.writeObject(blobFile, this);
            System.out.println("commit saved successfully.");

        }


        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }


    }
}
