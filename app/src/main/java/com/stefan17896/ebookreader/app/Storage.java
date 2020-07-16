package com.stefan17896.ebookreader.app;

import java.io.File;

public class Storage {
public static class Book{
    public final String filename;
    public final String coverFile;
    public final String title;
    public final int progress;

    public Book(String filename, String coverFile, String title, int progress) {
        this.filename = filename;
        this.coverFile = coverFile;
        this.title = title;
        this.progress = progress;
    }


}

}
