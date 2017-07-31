// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.encoding;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;

public class EncodingUtilTest {

    private static final String TEMP_PREFIX = "EncodingUtilTest_";
    private static final String TEMP_SUFFIX = ".tmp";

    @Test
    public void bomUtf8() throws IOException {
        final byte[] data = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 0x31, 0x32, 0x33, 0x0A };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("bomUtf8: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void bomUtf16Be() throws IOException {
        final byte[] data = { (byte) 0xFE, (byte) 0xFF, 0x00, 0x31, 0x00, 0x32, 0x00, 0x33, 0x00, 0x0A };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("bomUtf16Be: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16BE", encoding);
    }

    @Test
    public void bomUtf16Le() throws IOException {
        final byte[] data = { (byte) 0xFF, (byte) 0xFE, 0x31, 0x00, 0x32, 0x00, 0x33, 0x00, 0x0A, 0x00 };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("bomUtf16Le: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16LE", encoding);
    }

    @Test
    public void utf16Be() throws IOException {
        final byte[] data = { 0x00, 0x31, 0x00, 0x32, 0x00, 0x33 };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("utf16Be: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16BE", encoding);
    }

    @Test
    public void utf16Le() throws IOException {
        final byte[] data = { 0x31, 0x00, 0x32, 0x00, 0x33, 0x00 };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("utf16Le: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16LE", encoding);
    }

    @Test
    public void utf16BeFollowedByLf() throws IOException {
        final byte[] data = { 0x4F, 0x4F, (byte) 0x99, (byte) 0x99, 0x00, 0x0A, 0x00, 0x41, 0x00, 0x42, 0x00, 0x43 };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("utf16BeFollowedByLf: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16BE", encoding);
    }

    @Test
    public void utf16LeFollowedByLf() throws IOException {
        final byte[] data = { 0x4F, 0x4F, (byte) 0x99, (byte) 0x99, 0x0A, 0x00, 0x41, 0x00, 0x42, 0x00, 0x43, 0x00 };
        File temp = createFile(data);
        String encoding = EncodingUtil.examine(temp.getPath());
        System.out.println("utf16LeFollowedByLf: " + temp.getPath() + ": " + encoding);
        temp.delete();
        assertEquals("UTF-16LE", encoding);
    }

    private File createFile(byte[] data) throws IOException {
        File temp = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
        FileOutputStream out = new FileOutputStream(temp);
        out.write(data);
        out.close();
        return temp;        
    }

}
