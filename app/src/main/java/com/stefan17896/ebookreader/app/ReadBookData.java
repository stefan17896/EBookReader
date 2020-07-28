package com.stefan17896.ebookreader.app;

import android.app.Application;
import android.os.FileUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.Activity;
import android.content.Context;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;


import java.util.zip.ZipInputStream;

public class ReadBookData {
    private static final String ns = null;

    public static List getBookData(File bookFile, boolean removeFiles)  {
        String book = bookFile.getName();
        File appFolder = new File("/storage/emulated/0/Android/data/com.stefan17896.ebookreader/files");
        File path = new File(appFolder + "/unpacked/" + book + "/");
        path.mkdirs();

        try {
            unzip(bookFile,path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        File metaInf = new File(path + "/META-INF/container.xml");

        List bookData = new ArrayList();

        try {
            InputStream inputStream = new FileInputStream(metaInf);
            bookData = bookData( path + "/" + findContentOpf(inputStream));
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
        }

        if(bookData.get(1) != null && bookData.get(0) != null) {
            File fullCoverPath = new File(path.toString() + "/" + bookData.get(1));
            File copyPath = new File(appFolder + "/" + md5((String) bookData.get(0)) + ".cov");
            if(!copyPath.exists())
            copyFile(fullCoverPath, copyPath );
        }
        if(path.isDirectory() && removeFiles){
            deleteRecursive(path);
        }
       return bookData;
    }
    public static String md5(String s) {
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
    private static void  deleteRecursive(File fileOrDirectory) {

        if(fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory() && fileOrDirectory.listFiles().length > 0)
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);
            fileOrDirectory.delete();
        }
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

    private static void copyFile(File inputPath, File outputPath) {

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

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
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

    /*Wrapper for development
    public static List getBookData()
    {
        File bookFile = new File("/storage/emulated/0/Download/book1.epub");
        return getBookData(bookFile);

    }*/


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

    //von stack overflow geklaut
    private static void unzip(File zipFile, File targetDirectory) throws IOException {
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
