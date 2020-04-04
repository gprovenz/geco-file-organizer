package com.gprovenz;

import com.drew.imaging.ImageProcessingException;
import com.gprovenz.photoor.reader.FileInfo;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {


        FileInfo fileInfo = FileInfo.getInstance(new File("c:\\temp\\IMG_1743_mod.JPG"));

        System.out.println(fileInfo);
    }
}
