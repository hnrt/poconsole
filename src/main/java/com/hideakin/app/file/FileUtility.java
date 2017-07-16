/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.file;

import java.io.File;
import java.time.LocalDateTime;

public class FileUtility {

    private static final String BACKUP_EXT_LEADER = ".backup";

    public static String getTempPath(String basePath) {
        LocalDateTime dt = LocalDateTime.now();
        return String.format("%s.%d%02d%02d_%02d%02d%02d_%03d",
                basePath,
                dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                dt.getHour(), dt.getMinute(), dt.getSecond(), dt.getNano() / 1000_000);
    }

    public static String getBackupPath(String basePath) {
        int maxNum = 0;
        File baseFile = new File(basePath);
        String baseName = baseFile.getName();
        int baseNameLength = baseName.length();
        File parentFile = baseFile.getParentFile();
        String[] paths = parentFile.list();
        for (String next : paths) {
            if (next.startsWith(baseName)) {
                if (next.length() > baseNameLength) {
                    String tail = next.substring(baseNameLength);
                    if (tail.startsWith(BACKUP_EXT_LEADER)) {
                        try {
                            int n = Integer.parseUnsignedInt(tail.substring(BACKUP_EXT_LEADER.length()));
                            if (maxNum < n) {
                                maxNum = n;
                            }
                        } catch (NumberFormatException e) {
                            // just ignore this file
                        }
                    }
                }
            }
        }
        return String.format("%s%s%d", basePath, BACKUP_EXT_LEADER, maxNum + 1);        
    }

}
