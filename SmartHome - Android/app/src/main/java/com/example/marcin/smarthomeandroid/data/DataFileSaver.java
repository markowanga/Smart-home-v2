package com.example.marcin.smarthomeandroid.data;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Marcin on 18.04.2017.
 */

public class DataFileSaver {
    private static File getAlbumStorageDir(String fileName, Context context) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
    }

    public static void saveError(Context context, String data) {
        try {
            String fileName = "error.txt";
            File file = getAlbumStorageDir(fileName, context);
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new
                    BufferedWriter(fileWriter);
            bufferedWriter.write(data + "\n");
            bufferedWriter.write("--------------------------------\n");
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
