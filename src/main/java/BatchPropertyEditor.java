import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BatchPropertyEditor {

    public static int numberOfFilesModified = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to the Bulk Property File Editor!");
        System.out.println("To get started, please enter the absolute path of your folder to search in (NOT case sensitive) (ex: C:\\Users\\Dave\\someFolder):");
        System.out.println("NOTE: forward slashes have undefined behavior. Please follow the windows filesystem convention of using backslashes.");
        String userFolder = reader.readLine();

        while (!userFolder.matches("^(C|c):\\\\(.+ *\\\\?)+$")) {
            System.out.println("Invalid path entered. Please enter a valid absolute path of your folder to search in (ex: C:\\Users\\Dave\\someFolder):");
            userFolder = reader.readLine();
        }
        if (userFolder.charAt(userFolder.length() -1) == '\\')
            userFolder = userFolder.substring(0, userFolder.length() - 1);

        // Create a File object for the directory
        File userDirectory = new File(userFolder);
        // Check if the provided path is a directory
        if (!userDirectory.isDirectory()) {
            System.out.println("The directory path you provided was not found. Please rerun the program and try again.");
            System.exit(0);
        }

        System.out.println();
        System.out.println("Please enter the common name of the property files or a regular expression to match by (ex: user.properties to modify files called user.properties or .+.properties to modify files ending with .properties):");
        String propertyFileRegex = reader.readLine();

        System.out.println();
        System.out.println("There are two file modification modes:");
        System.out.println("1. Setting the value of a given key. This option means you must specify a key and a value. For example, you could specify a key 'username' and a value 'dave', then each property file containing the key 'username' would have its value set to 'dave'.");
        System.out.println("2. Setting a value given the old value. This option means you must specify an old value and a new value. For example, you could specify an old value 'dave' and a new value 'david', then each property file containing the old value 'dave' would have its value set to 'david'.");
        System.out.println("NOTE: option 2 can be dangerous if a file has multiple keys with the same value, as they will all be replaced. Be cautious with this option.");
        System.out.println("Please enter the modification mode you would like to use (1 or 2):");
        String option = reader.readLine();
        while (!option.equals("1") && !option.equals("2")) {
            System.out.println("Invalid option entered. Please enter either 1 or 2:");
            option = reader.readLine();
        }

        String propKey = "";
        String oldPropValue = "";
        System.out.println();

        switch(option)
        {
            case "1" -> {
                System.out.println("Please enter the key to replace the value for (ex: username):");
                propKey = reader.readLine();
            }
            case "2" -> {
                System.out.println("Please enter the old property value to replace (ex: dave):");
                oldPropValue = reader.readLine();
            }
        }

        System.out.println();
        System.out.println("Please enter the new property value to set (ex: david):");
        String propNewValue = reader.readLine();
        System.out.println();

        if (option.equals("1"))
            replacePropertyRecursive(userDirectory, propertyFileRegex, propKey, propNewValue, true);
        else
            replacePropertyRecursive(userDirectory, propertyFileRegex, oldPropValue, propNewValue, false);

        System.out.println();
        System.out.println("Modification complete. Successfully modified " + numberOfFilesModified + " files. Please check one of the property files to make sure the property was set correctly.");
    }

    private static void replacePropertyRecursive(File directory, String propFileName, String propKeyOrOldValue, String newPropValue, boolean replaceByKey) {
        // Get all files and subdirectories in the current directory
        File[] files = directory.listFiles();

        if (files != null && !directory.getName().equals(".git")) {
            for (File file : files) {
                // If it's a directory, then recursively search in that directory
                if (file.isDirectory()) {
                    replacePropertyRecursive(file, propFileName, propKeyOrOldValue, newPropValue, replaceByKey);
                } else {
                    // Check if the current file matches the desired file name
                    if (file.getName().matches(propFileName)) {
                        try {
                            if (replaceByKey)
                                replaceTextInFile(file, propKeyOrOldValue + " *= *(.+)", propKeyOrOldValue + "=" + newPropValue);
                            else replaceTextInFile(file, propKeyOrOldValue, newPropValue);
                        } catch (IOException e) {
                            System.out.println("Unknown error occurred writing to file. Please restart the program and try again.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    public static void replaceTextInFile(File file, String oldText, String newText) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        Charset charset = StandardCharsets.UTF_8;
        String oldContent = Files.readString(path, charset);
        String newContent = oldContent.replaceAll(oldText, newText);
        if (!oldContent.equals(newContent)) {
            numberOfFilesModified++;
            System.out.println("Modified file: " + file.getAbsolutePath());
        }
        Files.writeString(path, newContent, charset);
    }
}