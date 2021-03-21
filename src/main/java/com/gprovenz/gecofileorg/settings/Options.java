package com.gprovenz.gecofileorg.settings;

public interface Options {
    enum OnExistingFileAction { OVERWRITE, SKIP, RENAME}

    enum Operation {MOVE, COPY}

    enum CompareMode {CONTENT, DATE}

}
