package org.example.formulaeditor.io;

import com.google.gson.Gson;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.network.NetworkSerializer;
import org.example.formulaeditor.network.WorkbookSyncDTO;

import java.io.*;

public class WorkbookFileIO {

    // Reuse the SyncDTO to save and load Json files
    public static void save(Workbook workbook, String filePath) throws IOException {
        var dto = NetworkSerializer.toSyncDTO(workbook);
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(dto, writer);
        }
    }

    public static Workbook load(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return new Workbook();
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            var dto = gson.fromJson(reader, WorkbookSyncDTO.class);
            return NetworkSerializer.fromSyncDTO(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return new Workbook();
        }
    }

    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
}
