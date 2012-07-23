/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tjenwellens.timetracker.file;

import android.content.Context;
import android.util.Log;
import java.io.*;

/**
 *
 * @author Tjen
 */
public class FileHandler
{

    public static void writeFile(Context context, String fileName, String contents)
    {
        try { // catches IOException below
//            final String TESTSTRING = "Hello Android";
            // ##### Write a file to the disk #####
            /* We have to use the openFileOutput()-method the ActivityContext provides, to protect your file from others and 
             * This is done for security-reasons. 
             * We chose MODE_WORLD_READABLE, because we have nothing to hide in our file */
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(contents);
            // ensure that everything is really written out and close
            osw.flush();
            osw.close();
        } catch (IOException io) {
        }
    }

    public static String readFile(Context context, String fileName, int length)
    {
        String returnString = null;
//        final String TESTSTRING = "Hello Android";
        try {
            // ##### Read the file back in #####
            // We have to use the openFileInput()-method the ActivityContext provides. 
            // Again for security reasons with openFileInput(...)
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            // Prepare a char-Array that will hold the chars we read back in.
            char[] inputBuffer = new char[length];
            // Fill the Buffer with data from the file
            isr.read(inputBuffer);
            // Transform the chars to a String
            returnString = new String(inputBuffer);
            // Check if we read back the same chars that we had written out
//            boolean isTheSame = TESTSTRING.equals(readString);
            // WOHOO lets Celebrate =)
//            Log.i("File Reading stuff", "success = " + isTheSame);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return returnString;
    }

    public static void deleteFile(Context context, String fileName)
    {
        context.deleteFile(fileName);
    }
}
