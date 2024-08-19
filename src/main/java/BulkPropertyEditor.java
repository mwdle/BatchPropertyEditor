import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class BulkPropertyEditor {

    private static Charset charset = StandardCharsets.UTF_8;

    private static int numberOfFilesModified = 0;

    private static HashMap<Path, String> backup = new HashMap<>();

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to the Bulk Property File Editor!");
        System.out.println("To get started, please enter the absolute path to your folder of AFT's (ex: C:\\Users\\u1339709\\folderToSearchForPropFilesIn):");
        System.out.println("NOTE: Please follow the windows filesystem convention of using backslashes in your file paths.");
        String folderPath = reader.readLine();

        while (!folderPath.matches("^[a-zA-Z]:\\\\.*$")) {
            System.out.println("Invalid path entered. Please enter a valid absolute path to your folder of AFT's (ex: C:\\Users\\u1339709\\folderToSearchForPropFilesIn):");
            folderPath = reader.readLine();
        }
        if (folderPath.charAt(folderPath.length() -1) == '\\')
            folderPath = folderPath.substring(0, folderPath.length() - 1);

        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.out.println("The directory path you provided was not found. Please rerun the program and try again.");
            System.exit(0);
        }

        System.out.println();
        System.out.println("Please enter the common name of the property files (Supports regex entry such as: .*\\.properties (to match any file ending in .properties) or aft.properties (to match any file with exact name 'aft.properties')):");
        String propertyFileName = reader.readLine();

        System.out.println();
        System.out.println("There are two file modification modes:");
        System.out.println("1. Setting the value of a given key. This option means you must specify a key and a value. For example, you could specify a key 'db.password' and a value 'myDbPassword', then each property file containing the key 'db.password' would have its value set to 'myDbPassword'.");
        System.out.println("2. Setting a value given the old value. This option means you must specify an old value and a new value. For example, you could specify an old value 'myOldDbPassword' and a new value 'myNewDbPassword', then each property file containing the old value 'myOldDbPassword' would have that value replaced with 'myNewDbPassword'.");
        System.out.println("IMPORTANT: Be cautious using option 2 - it can be 'dangerous' if the value you are modifying exists more than once in the same property file, since the program replaces all instances of the value in the file.");
        System.out.println("Please enter the modification mode you would like to use (1 or 2):");
        String option = reader.readLine();
        while (!option.equals("1") && !option.equals("2")) {
            System.out.println("Invalid option entered. Please enter either 1 or 2:");
            option = reader.readLine();
        }

        String propKey = "";
        String oldPropValue = "";
        System.out.println();

        if (option.equals("1")) {
            System.out.println("Please enter the key to replace the value for (ex: db.password):");
            propKey = reader.readLine();
        }
        else {
            System.out.println("Please enter the old property value to replace (ex: myOldDbPassword):");
            oldPropValue = reader.readLine();
        }

        System.out.println();
        System.out.println("Please enter the new property value to set (ex: myNewDbPassword):");
        String propNewValue = reader.readLine();
        System.out.println();

        if (option.equals("1")) replacePropertyRecursive(folder, propertyFileName, propKey, propNewValue, true);
        else replacePropertyRecursive(folder, propertyFileName, oldPropValue, propNewValue, false);

        System.out.println();
        System.out.println("Modification complete. Successfully modified " + numberOfFilesModified + " files.");
        System.out.println("Please check one of the property files to make sure the property was set correctly.");
        System.out.println("If you notice an issue in any file, enter either \"u\" or \"undo\" to undo all changes. Otherwise, press any other key to exit the program.");
        option = reader.readLine();
        if (option.equalsIgnoreCase("undo") || option.equalsIgnoreCase("u")) {
            backup.forEach((path, content) -> {
                try {
                    Files.writeString(path, content, charset);
                } catch (IOException e) {
                    System.out.println("Unknown error undoing changes to:" + path);
                    System.exit(0);
                }
                System.out.println("Reverted file: " + path + " to its original state.");
            });
            System.out.println("All changes have been successfully reverted.");
        }
        System.out.println("All done! Exiting . . .");
    }

    private static void replacePropertyRecursive(File folder, String propFileName, String propKeyOrOldValue, String newPropValue, boolean replaceByKey) {
        // Get all files and subdirectories in the current directory
        File[] files = folder.listFiles();

        if (files != null && !folder.getName().equals(".git")) {
            for (File file : files) {
                // If it's a directory, then recursively search in that directory
                if (file.isDirectory()) {
                    replacePropertyRecursive(file, propFileName, propKeyOrOldValue, newPropValue, replaceByKey);
                } else {
                    // Check if the current file matches the desired file name
                    if (file.getName().matches(propFileName)) {
                        try {
                            if (replaceByKey)
                                replaceTextInFile(file, propKeyOrOldValue + " *=.*", propKeyOrOldValue + "=" + newPropValue);
                            else replaceTextInFile(file, "= *" + propKeyOrOldValue, "=" + newPropValue);
                        } catch (IOException e) {
                            System.out.println("Unknown error occurred writing to:" + file.getName() + ". Please restart the program and try again.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
    }

    public static void replaceTextInFile(File textFile, String oldText, String newText) throws IOException {
        Path path = Paths.get(textFile.getAbsolutePath());
        String oldContent = Files.readString(path, charset);
        String newContent = oldContent.replaceAll(oldText, newText);
        if (!oldContent.equals(newContent)) {
            numberOfFilesModified++;
            System.out.println("Modified file: " + textFile.getAbsolutePath());
            backup.put(path, oldContent);
        }
        Files.writeString(path, newContent, charset);
    }
}