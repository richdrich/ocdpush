package com.creatotronik.ocdpush;

import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.IOException;

public class MainApp {

    public static void main(String[] args) {
        String filename = args[0];
        String absPath = FilenameUtils.normalize(new File(filename).getAbsolutePath(), true);

        try {
            OpenOcdAccess openOcdAccess = new OpenOcdAccess();
            String start = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s", start);
            if(!start.contains("Open On-Chip Debugger")) {
                System.err.printf("Unexpected response");
                return;
            }

            openOcdAccess.write("reset halt");
            String postReset = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s", postReset);
            if(!postReset.contains("target halted due to debug-request")) {
                System.err.printf("Unexpected response");
                return;
            }

            openOcdAccess.write(String.format("flash write_image erase %s", absPath));
            String postFlash = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s", postFlash);
            if(!postFlash.matches("(?s).+wrote \\d+ bytes from file.+")) {
                System.err.printf("Unexpected response");
                return;
            }

            openOcdAccess.write("reset");
            String postResetRun = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s", postResetRun);
            System.err.printf("Done!");
        } catch (IOException e) {
            System.err.printf("Ëxception %s pushing file", e);
        }
    }
}
