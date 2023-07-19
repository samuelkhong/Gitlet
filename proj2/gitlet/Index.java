package gitlet;

import java.util.Map;

public class Index {


    private class IndexElement{
        private String fileName;
        private String hash;
        private String task;

        //
        IndexElement(String fileName, String task) {
            this.fileName = fileName;
            this.task = task;

        }
    }


}
