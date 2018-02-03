package com.phasemaps.azio.phasemapsbeacon.res;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DELL on 6/9/2017.
 */

public class PhaseLogger {
    File log;
    String filepath = "Phase Maps";
    String filename = "log.txt";

    public PhaseLogger(Context context) {
        // create or open file if exists
        log = new File(context.getExternalFilesDir(filepath), filename);
    }

    public void write(String entry) {
        try {
            // writers in append and auto flush mode
            FileOutputStream fos = new FileOutputStream(log, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            // current timestamp
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            // log given entry with timestamp
            String str = timeStamp + " " + entry;
            fos.write(str.getBytes());
            osw.append("\n");
            osw.flush();
            osw.close();
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
