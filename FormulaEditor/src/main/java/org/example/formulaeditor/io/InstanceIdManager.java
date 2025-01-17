package org.example.formulaeditor.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class InstanceIdManager {
    private static final String filePath = "src/main/resources/instanceId.txt";

    public static String getInstanceId() {
        File file = new File(filePath);
        if (file.exists()) {
            // try reading ID from file
            try {
                return Files.readString(Paths.get(filePath)).trim();
            } catch (IOException e) {
                e.printStackTrace();
                // create new on error
                return createInstanceId();
            }
        } else {
            // create new on first run
            return createInstanceId();
        }
    }

    private static String createInstanceId() {
        // Generate a random UUID
        String newId = UUID.randomUUID().toString();

        // write id to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(newId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newId;
    }
}
