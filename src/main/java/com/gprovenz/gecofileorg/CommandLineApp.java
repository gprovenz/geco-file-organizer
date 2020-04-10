package com.gprovenz.gecofileorg;

import com.gprovenz.gecofileorg.actions.FileCopier;
import com.gprovenz.gecofileorg.actions.FileMover;
import com.gprovenz.gecofileorg.settings.Options;
import com.gprovenz.gecofileorg.settings.Settings;
import com.gprovenz.gecofileorg.settings.SettingsReader;

import java.io.File;
import java.io.IOException;

/**
 * The main class that accepts command line arguments
 */
public class CommandLineApp {

    public static void main(String[] args ) throws IOException, InterruptedException {
        System.out.println("Geco File Organizer v. 1.0 beta");
        if (args.length<1) {
            System.out.println("Please specify settings file.");
            System.exit(1);
        }
        Settings settings = SettingsReader.read(new File(args[0]));
        execCommand(settings);
    }

    protected static void execCommand(Settings settings) throws IOException, InterruptedException {
        if (settings.getOperation()== Options.Operation.MOVE) {
            FileMover mover = new FileMover(settings);
            mover.moveAllFiles();
        } else if (settings.getOperation()==Options.Operation.COPY) {
            FileCopier copier = new FileCopier(settings);
            copier.copyAllFiles();
        } else {
            throw new IllegalArgumentException("Invalid operation: " + settings.getOperation());
        }
    }

}
