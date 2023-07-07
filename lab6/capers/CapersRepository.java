package capers;

import java.io.File;
import java.io.IOException;
import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, "capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    static final File DOG_FOLDER = Utils.join(CAPERS_FOLDER, "dogs");

    static final File STORY = Utils.join(CAPERS_FOLDER, "story.txt");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // if the folder CAPERS does not exist, create one.
        if (!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdir();
        }

        //Check if dog folder exit, if false then create a sub directory called dogs
        if (!DOG_FOLDER.exists()) {
            DOG_FOLDER.mkdir();
        }

        // check if story file is found in Capers directory, if not makes a new story
        if (!STORY.exists()) {
            try {
                if (STORY.createNewFile()) {
                    System.out.println("File created: ");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }



    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {

        String storySoFar = Utils.readContentsAsString(STORY); // load previous text found in story.txt
        String updatedStory;

        if (storySoFar.length() == 0) {
            updatedStory = text;
        }
        else {
            updatedStory = storySoFar + "\n" + text; // adds string text below storySoFar
        }

        Utils.writeContents(STORY, updatedStory); // updates story.txt
        System.out.println(updatedStory);


    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog dog = new Dog(name, breed, age);
        System.out.println(dog.toString());
        dog.saveDog();
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        String fileName = name + ".txt";
        File dogFile = Utils.join(".capers", "dogs", fileName);

        // if the name of the dog as a file exists, retrieve Dog
        if (dogFile.exists()) {
            Class<Dog> dogClass = Dog.class;
            Dog dog = Utils.readObject(dogFile, dogClass);

            dog.haveBirthday(); // iterates year by one year and prints birthday message
            dog.saveDog(); // overwrites dog files
        }
        else {
            System.out.println("dog not found");
        }


    }
}
