package com.gprovenz.photoor.reader;

import com.gprovenz.photoor.settings.Settings;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class PathBuilder {
    public static File buildDestPath(Settings settings, File parent, FileInfo fileInfo) {
        Calendar c = Calendar.getInstance();
        c.setTime(fileInfo.getCreationDate());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        String monthShort = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String monthLong = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        String path = String.format("%s/%s/%02d-%s-%s/%02d-%s-%s/",
                fileInfo.getMediaType(), year, month, monthLong, year, day, monthShort, year);

        return new File(new File(parent, path), fileInfo.getFileName());
    }
}
