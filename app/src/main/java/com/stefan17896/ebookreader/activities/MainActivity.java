package com.stefan17896.ebookreader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.folioreader.FolioReader;
import com.stefan17896.ebookreader.R;
import com.stefan17896.ebookreader.app.ReadBookData;
import com.stefan17896.ebookreader.app.Storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    FolioReader folioReader;
    public File fileDir;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String bookName = "book1";

        File fileDir = new File(String.valueOf(getExternalFilesDir(null)));
        fileDir.mkdirs();

        File book = new File("/storage/emulated/0/Download/book1.epub");



        openBook(book);


        //folioReader.openBook(R.raw.book1);
        //folioReader.openBook("file:///android_asset/book1.epub");
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        openIntent(intent);
    }

    void openIntent(Intent intent) {
        if(intent == null)
            return;


    }
    public void openBook(File inFile){

        String bookFileName = inFile.toString().substring(inFile.toString().lastIndexOf("/")+1);
        List bookData = new ArrayList();
        bookData = ReadBookData.getBookData(inFile, true);

        String cover = (String) bookData.get(1);
        String title = (String) bookData.get(0);
        String titleHash = md5(title);

        File fileDir = new File(String.valueOf(getExternalFilesDir(null)));
        String fileName =  titleHash + inFile.toString().substring(inFile.toString().lastIndexOf("."));
        File bookFile = new File(fileDir + "/" + fileName);
        if(!bookFile.exists()){
            copyFile(inFile.toString(), bookFile.toString());
        }


        Storage.Book book = new Storage.Book(fileName, cover, title, 0) ;

        folioReader = FolioReader.get();
        folioReader.openBook(bookFile.toString());

    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void test(View view){
        //folioReader.openBook(R.raw.book1);
        test2(view);
    }
    public void test2(View view){
        //folioReader.openBook("file:///android_asset/book1.epub");
        //folioReader.openBook(R.raw.book1);
        Log.e("error", "click!");
    }
    private void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
}
