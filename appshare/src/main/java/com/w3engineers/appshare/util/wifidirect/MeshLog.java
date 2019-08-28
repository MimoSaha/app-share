package com.w3engineers.appshare.util.wifidirect;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MeshLog {

    private static String TAG = "MeshLog";

    //Constant

   /* public static final String INFO = "(I)";
    public static final String WARNING = "(W)";
    public static final String ERROR = "(E)";
    public static final String SPECIAL = "(S)";*/

    public static final String INFO = "(I)";
    public static final String WARNING = "(W)";
    public static final String ERROR = "(E)";
    public static final String SPECIAL = "(S)";


    public static void clearLog() {
        writeText("", false);
    }

    public static void p(String msg) {
        p(TAG, msg);
        writeText(msg, true);
    }

    public static void o(String msg) {
        e(TAG, msg);
        writeText(msg, true);
    }

    public static void k(String msg) {
        e(TAG, msg);
        writeText(msg, true);
    }

/*
    public static void mm(String msg) {
        String m = SPECIAL.concat(" ").concat(msg);
        v(TAG, m);
        writeText(m, true);
    }
*/


    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void p(String tag, String msg) {
        Log.e(tag, msg);
    }



    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void v(String msg) {
        String m = SPECIAL.concat(" ").concat(msg);
        v(TAG, m);
        writeText(m, true);
    }

    public static void mm(String msg) {
        String m = SPECIAL.concat(" ").concat(msg);
        e(TAG, m);
        writeText(m, true);
    }

    public static void i(String msg) {
        String m = INFO.concat(" ").concat(msg);
        i(TAG, m);
        writeText(m, true);
    }

    public static void e(String msg) {
        String m = ERROR.concat(" ").concat(msg);
        e(TAG, msg);
        writeText(m, true);
    }

    public static void w(String msg) {
        String m = WARNING.concat(" ").concat(msg);
        e(TAG, msg);
        writeText(m, true);
    }

    public static void s(String msg) {
        String m = SPECIAL.concat(" ").concat(msg);
    }


    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    private static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    private static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    private static void d(String msg) {
        d(TAG, msg);
        writeText(msg, true);
    }

    private static void writeText(String text, boolean isAppend) {

         /*if (SharedPref.readBoolean(Constant.KEY_DEBUG)){
             DebugOverlay.with(MeshApp.getContext()).log(text);
         }*/

        Intent intent = new Intent("com.w3engineers.meshrnd.DEBUG_MESSAGE");
        intent.putExtra("value", text);
       // MeshApp.getContext().sendBroadcast(intent);

       /* try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() +
                    "/MeshRnD");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "Debug.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fOut = new FileOutputStream(file, isAppend);

            OutputStreamWriter osw = new
                    OutputStreamWriter(fOut);

//---write the string to the file---
            osw.write("\n" + text);
            //  osw.append(text)
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }

    private static void clearText() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() +
                    "/MeshRnD");
            File file = new File(directory, "Debug.txt");
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void n(String msg) {
        e(TAG, msg);
        writeText(msg, true);
    }

    public static void sp(String msg) {
        i(TAG, msg);
        writeText(msg, true);
    }
}
