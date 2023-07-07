package capers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
public class Dog implements Serializable {

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = Utils.join(CapersRepository.CWD, "Dog"); // TODO (hint: look at the `join`
                                         //      function in Utils)

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        // TODO (hint: look at the Utils file)
        String fileName = name + ".txt";
        File dogFile = Utils.join(".capers", "dogs", fileName);

            // if the name of the dog as a file exists, retrieve Dog
            if (dogFile.exists()) {
                Class<Dog> dogClass = Dog.class;
                return Utils.readObject(dogFile, dogClass);
            }
            else {
                System.out.println("file not found");
            }





        return null;
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        // TODO (hint: don't forget dog names are unique)

        // Create the directory path for dog files
        File dogDirectory = new File(".capers/dogs");
        if (!dogDirectory.exists()) {
            if (!dogDirectory.mkdirs()) {
                System.out.println("Failed to create dog directory.");
                return;
            }
        }

        // create path to the breed of dog
        File dogFile = Utils.join(".capers", "dogs", this.name + ".txt"); // name of file of specific dog

        try {
            if (!dogFile.exists()) {
                if (dogFile.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create the file.");
                    return;
                }
            }

            // Write the dog object to the file
            Utils.writeObject(dogFile, this);
            System.out.println("Dog saved successfully.");

        }


        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
