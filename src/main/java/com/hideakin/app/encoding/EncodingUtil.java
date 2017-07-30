/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.encoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

public class EncodingUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16BE = "UTF-16BE";
    public static final String UTF_16LE = "UTF-16LE";
    public static final String SHIFT_JIS = "Shift_JIS";
    public static final String EUC_JP = "EUC-JP";
    private static final int CAPACITY = 1024;
    private static final CharsetDecoder[] decoders = new CharsetDecoder[] {
            Charset.forName(UTF_8).newDecoder(),
            Charset.forName(SHIFT_JIS).newDecoder(),
            Charset.forName(EUC_JP).newDecoder(),
            Charset.forName(UTF_16BE).newDecoder(),
            Charset.forName(UTF_16LE).newDecoder(),
    };
    private static final int INDEX_GROUP1_MIN = 0;
    private static final int INDEX_GROUP1_MAX = INDEX_GROUP1_MIN + 2;
    private static final int INDEX_GROUP2_MIN = INDEX_GROUP1_MAX + 1;
    private static final int INDEX_UTF_16BE = INDEX_GROUP2_MIN + 0;
    private static final int INDEX_UTF_16LE = INDEX_GROUP2_MIN + 1;
    private static final int INDEX_GROUP2_MAX = INDEX_GROUP2_MIN + 1;
    private static final boolean DEBUG = false;

    public static String examine(String path) {
        try (InputStream inStream = new FileInputStream(new File(path))) {
            byte[] bom = new byte[3];
            int len = inStream.read(bom);
            String encoding = EncodingUtil.parseBom(bom, len);
            if (encoding == null) {
                ByteBuffer ibuf1 = ByteBuffer.allocate(CAPACITY);
                ibuf1.put(bom, 0, len);
                ByteBuffer ibuf2 = ByteBuffer.allocate(CAPACITY);
                ibuf2.put(bom, 0, len);
                CharBuffer obuf = CharBuffer.allocate(CAPACITY);
                boolean[] results = new boolean[decoders.length];
                Arrays.fill(results, true);
                int[] zeros = new int[2];
                Arrays.fill(zeros, 0);
                boolean onlyAscii = true;
                int b = -1;
                for (;;) {
                    int c = inStream.read();
                    if (c < 0 || ibuf1.position() == CAPACITY) {
                        ibuf1.flip();
                        encoding = check1(ibuf1, obuf, true, results);
                        if (encoding != null) {
                            break;
                        }
                        if ((ibuf2.position() & 1) == 0) {
                            ibuf2.flip();
                            encoding = check2(ibuf2, obuf, true, results, zeros);
                            if (encoding != null) {
                                break;
                            }
                        }
                        encoding = getFirst(results);
                        if (encoding != null) {
                            break;
                        }
                        encoding = UTF_8;
                        break;
                    } else if (c > 127) {
                        onlyAscii = false;
                    }
                    ibuf1.put((byte)c);
                    ibuf2.put((byte)c);
                    if (0 < c && c < 32 && !onlyAscii) {
                        ibuf1.flip();
                        encoding = check1(ibuf1, obuf, false, results);
                        if (encoding != null) {
                            break;
                        }
                        ibuf1.clear();
                        onlyAscii = true;
                    }
                    if ((ibuf2.position() & 1) == 0 && ((b == 0 && 0 < c && c < 32) || (0 < b && b < 32 && c == 0))) {
                        ibuf2.flip();
                        encoding = check2(ibuf2, obuf, false, results, zeros);
                        if (encoding != null) {
                            break;
                        }
                        ibuf2.clear();
                        c = -1;
                    }
                    b = c;
                }
            }
            return encoding;
        } catch (IOException e) {
            return UTF_8;
        }
    }

    private static String parseBom(byte[] buf, int len) {
        if (DEBUG) {
            System.out.println("parseBom(" + len + ")");
        }
        if (len >= 3) {
            if (buf[0] == (byte) 0xEF && buf[1] == (byte) 0xBB && buf[2] == (byte) 0xBF) {
                return UTF_8;
            }
        }
        if (len >= 2) {
            if (buf[0] == (byte) 0xFE && buf[1] == (byte) 0xFF) {
                return UTF_16BE;
            } else if (buf[0] == (byte) 0xFF && buf[1] == (byte) 0xFE) {
                return UTF_16LE;
            }
        }
        return null;
    }

    private static String check1(ByteBuffer in, CharBuffer out, boolean endOfInput, boolean[] results) {
        int valid = 0;
        if (in.limit() > 0) {
            for (int index = INDEX_GROUP1_MIN; index <= INDEX_GROUP1_MAX; index++) {
                if (results[index]) {
                    CharsetDecoder decoder = decoders[index].reset();
                    in.position(0);
                    out.clear();
                    CoderResult cr = decoder.decode(in, out, endOfInput);
                    if (DEBUG) {
                        System.out.println(decoders[index].getClass().getName() + ": " + cr.toString());
                    }
                    if (cr.isError()) {
                        results[index] = false;
                    } else {
                        valid++;
                    }
                }
            }
            for (int index = INDEX_GROUP2_MIN; index <= INDEX_GROUP2_MAX; index++) {
                if (results[index]) {
                    valid++;
                }
            }
            int zeros = 0;
            in.position(0);
            while (in.position() < in.limit()) {
                if (in.get() == 0) {
                    zeros++;
                }
            }
            if (zeros > 1) {
                // a single occurrence of zero is ignored.
                if (DEBUG) {
                    System.out.println("Zeros: " + zeros);
                }
                for (int index = INDEX_GROUP1_MIN; index <= INDEX_GROUP1_MAX; index++) {
                    if (results[index]) {
                        results[index] = false;
                        valid--;
                    }
                }
            }
        } else {
            for (int index = 0; index < decoders.length; index++) {
                if (results[index]) {
                    valid++;
                }
            }
        }
        return valid == 1 ? getFirst(results) : null;
    }


    private static String check2(ByteBuffer in, CharBuffer out, boolean endOfInput, boolean[] results, int[] zeros) {
        int valid = 0;
        if (in.limit() > 0) {
            for (int index = INDEX_GROUP1_MIN; index <= INDEX_GROUP1_MAX; index++) {
                if (results[index]) {
                    valid++;
                }
            }
            for (int index = INDEX_GROUP2_MIN; index <= INDEX_GROUP2_MAX; index++) {
                if (results[index]) {
                    CharsetDecoder decoder = decoders[index].reset();
                    in.position(0);
                    out.clear();
                    CoderResult cr = decoder.decode(in, out, endOfInput);
                    if (DEBUG) {
                        System.out.println(decoders[index].getClass().getName() + ": " + cr.toString());
                    }
                    if (cr.isError()) {
                        results[index] = false;
                    } else {
                        valid++;
                    }
                }
            }
            if (endOfInput && (in.limit() & 1) != 0) {
                for (int index = INDEX_GROUP2_MIN; index <= INDEX_GROUP2_MAX; index++) {
                    if (results[index]) {
                        results[index] = false;
                        valid--;
                    }
                }
            } else if (results[INDEX_UTF_16BE] && results[INDEX_UTF_16LE]) {
                int n = in.limit() / 2;
                in.position(0);
                for (int i = 0; i < n; i++) {
                    if (in.get() == 0) {
                        zeros[0]++;
                    }
                    if (in.get() == 0) {
                        zeros[1]++;
                    }
                }
            }
        } else {
            for (int index = 0; index < decoders.length; index++) {
                if (results[index]) {
                    valid++;
                }
            }
        }
        if (endOfInput && results[INDEX_UTF_16BE] && results[INDEX_UTF_16LE]) {
            if (DEBUG) {
                System.out.println("Hi-zeros: BE=" + zeros[0] + " LE=" + zeros[1]);
            }
            if (zeros[0] < zeros[1]) {
                results[INDEX_UTF_16BE] = false; // exclude UTF-16BE
                valid--;
            } else if (zeros[0] > zeros[1]) {
                results[INDEX_UTF_16LE] = false; // exclude UTF-16LE
                valid--;
            }
        }
        return valid == 1 ? getFirst(results) : null;
    }

    private static String getFirst(boolean[] results) {
        for (int index = 0; index < decoders.length; index++) {
            if (results[index]) {
                return decoders[index].charset().name();
            }
        }
        return null;
    }

}
