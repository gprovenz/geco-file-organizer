package com.gprovenz.gecofileorg.reader;

import com.gprovenz.gecofileorg.settings.Settings;

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

        String monthLong = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String monthShort = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

        String path = settings.getDestinationPathStructure()
                .replace("${file_type}", fileInfo.getFileType().get().getFileType())
                .replace("${year}", String.valueOf(year))
                .replace("${month}", String.format("%02d", month))
                .replace("${day}", String.format("%02d", day))
                .replace("${month_name}", String.valueOf(monthLong))
                .replace("${month_name_short}", String.valueOf(monthShort));

        return new File(new File(parent, path), fileInfo.getFileName());
    }
}
