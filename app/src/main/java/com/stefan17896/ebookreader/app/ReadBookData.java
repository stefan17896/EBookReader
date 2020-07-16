package com.stefan17896.ebookreader.app;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadBookData {
    private static final String ns = null;

    public static List getBookData(File bookFile)  {
        String book = bookFile.getName();
        File appFolder = new File("/storage/emulated/0/Android/data/com.stefan17896.ebookreader");
        File path = new File("/storage/emulated/0/Android/data/com.stefan17896.ebookreader/" + book);
        path.mkdirs();

        try {
            unzip(bookFile,path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        File metaInf = new File(path + "/META-INF/container.xml");

        List bookdt = new ArrayList();

        try {
            InputStream inputStream = new FileInputStream(metaInf);
            bookdt = bookData( path + "/" + findContentOpf(inputStream));
            int halt = 0;
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
        }
       return bookdt;
    }
    public static class Entry {
        public final String href;
        public final String id;
        public final String mediaType;

        private Entry(String href, String id, String mediaType) {
            this.href = href;
            this.id = id;
            this.mediaType = mediaType;
        }
    }

    public static List bookData(String opfLocation) throws XmlPullParserException, IOException {
        String title;
        List<String> bookData = new ArrayList<>();
        bookData.add(null);
        bookData.add(null);

        List<Entry> entries = new ArrayList<>();
        InputStream in = new FileInputStream(opfLocation);
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, "package");
            String name = parser.getName();

            while (parser.next() != XmlPullParser.END_TAG) {
                name = parser.getName();
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                name = parser.getName();
                // Starts by looking for the entry tag
                //parser.require(XmlPullParser.START_TAG, ns, "metadata");
                while (parser.next() != XmlPullParser.END_TAG) {
                    name = parser.getName();
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    if (parser.getName().equals("dc:title")) {
                        if (parser.next() == XmlPullParser.TEXT) {
                            bookData.set(0,parser.getText());
                            parser.nextTag();
                            name = parser.getName();
                            break;
                        }
                    }
                }
            }
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "manifest");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if (parser.getName().equals("item")) {
                    String href = parser.getAttributeValue(null, "href");
                    String id = parser.getAttributeValue(null, "id");
                    String mediaType = parser.getAttributeValue(null, "media-type");
                    entries.add(new Entry(href, id, mediaType));
                    parser.nextTag();
                }
            }

            int size = entries.size();
            for (int i = 0; i < size; i++) {
                Entry z = entries.get(i);
                if(z.id.equals("cover")){
                 bookData.set(1, z.href);
                }
            }

        } finally {
            in.close();

        }
    return bookData;
    }

    //Wrapper for development
    public static List getBookData()
    {
        File bookFile = new File("/storage/emulated/0/Download/book1.epub");
        return getBookData(bookFile);

    }

    public static String findContentOpf(InputStream in) throws XmlPullParserException, IOException {
        String fullPath = "";
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, "container");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                // Starts by looking for the entry tag
                    parser.require(XmlPullParser.START_TAG, ns, "rootfiles");
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        fullPath = parser.getAttributeValue(null, "full-path");
                    }
            }
            return fullPath;

        } finally {
            in.close();
        }
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (((ZipEntry) ze).isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }
}
