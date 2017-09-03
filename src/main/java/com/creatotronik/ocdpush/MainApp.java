package com.creatotronik.ocdpush;

import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.IOException;

public class MainApp {

    public static void main(String[] args) {
        String filename = args[0];
        String absPath = FilenameUtils.normalize(new File(filename).getAbsolutePath(), true);

        String host = "localhost";
        boolean useTftp = false;

        for(int ai=0; ai<args.length; ai++) {
            if (ai+1 < args.length && args[ai].equals("-h")) {
                host = args[++ai];
            }
            else if(args[ai].equals("-n")) {
                useTftp = true;
                // Doesn't seem to work on regular OpenOCD
            }
        }

        try {
            System.err.printf("Connect to: %s:4444\n", host);
            OpenOcdAccess openOcdAccess = new OpenOcdAccess(host);
            String start = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s\n", start);
            if(!start.contains("Open On-Chip Debugger")) {
                System.err.printf("Unexpected response");
                return;
            }

            openOcdAccess.write("reset halt");
            String postReset = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s\n", postReset);
            if(!postReset.contains("target halted due to debug-request")) {
                System.err.printf("Unexpected response");
                return;
            }

            ServeTftpFile server=null;
            if(useTftp) {
                server = new ServeTftpFile(filename);
                server.start();
            }

            openOcdAccess.write(String.format("flash write_image erase %s", useTftp ? ServeTftpFile.pathToAsset() : absPath));
            String postFlash = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s", postFlash);
            if(!postFlash.matches("(?s).+wrote \\d+ bytes from file.+")) {
                System.err.printf("Unexpected response\n");
                return;
            }

            if(useTftp) {
                server.stop();
            }

            openOcdAccess.write("reset");
            String postResetRun = openOcdAccess.readToPrompt();
            System.err.printf("Got: %s\n", postResetRun);

            openOcdAccess.close();
            System.err.printf("Done!\n");
        } catch (IOException e) {
            System.err.printf("Ëxception %s pushing file\n", e);
        }
    }
}
