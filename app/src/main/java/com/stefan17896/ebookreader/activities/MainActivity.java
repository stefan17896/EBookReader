package com.stefan17896.ebookreader.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.Config;
import com.stefan17896.ebookreader.R;
import com.stefan17896.ebookreader.app.ReadBookData;

import android.R.layout;
import android.R.raw;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class
MainActivity extends AppCompatActivity {
    FolioReader folioReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readBookData();

        folioReader = FolioReader.get();
        //folioReader.openBook(R.raw.book1);
        //folioReader.openBook("file:///android_asset/book1.epub");
    }
    public void readBookData(){
        String bookName = "book1.epub";
        File appFolder = new File("/storage/emulated/0/Android/data/com.stefan17896.ebookreader");
        appFolder.mkdirs();


        List bookData = new ArrayList();
        bookData = ReadBookData.getBookData();
    }
    public void test(View view){
        //folioReader.openBook(R.raw.book1);
        test2(view);
    }
    public void test2(View view){
        //folioReader.openBook("file:///android_asset/book1.epub");
        folioReader.openBook(R.raw.book1);
    }
}
