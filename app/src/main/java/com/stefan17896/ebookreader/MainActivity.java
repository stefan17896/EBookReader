package com.stefan17896.ebookreader;

import androidx.appcompat.app.AppCompatActivity;

import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.Config;

import android.R.layout;
import android.R.raw;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class
MainActivity extends AppCompatActivity {
    FolioReader folioReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        folioReader = FolioReader.get();
        //folioReader.openBook(R.raw.book1);
        //folioReader.openBook("file:///android_asset/book1.epub");
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
