package com.gprovenz.gecofileorg.settings;

public interface Options {
    enum OnExistingFileAction { OVERWRITE, SKIP}

    enum Operation {MOVE, COPY}
}
