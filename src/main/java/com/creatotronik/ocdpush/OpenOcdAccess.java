package com.creatotronik.ocdpush;

import java.io.*;
import java.net.Socket;

public class OpenOcdAccess extends Socket {

    private final BufferedReader inputReader;
    private final BufferedWriter outputWriter;

    public OpenOcdAccess() throws IOException {
        super("localhost", 4444);

        inputReader = new BufferedReader(
                new InputStreamReader(getInputStream()));
        outputWriter = new BufferedWriter(new OutputStreamWriter(getOutputStream()));
    }

    public String readToPrompt() throws IOException {
        String buffer="";

        while(!buffer.endsWith("\r> ")) {
            char ch = (char) inputReader.read();
            if(ch < 0) {
                return buffer;
            }
            buffer += ch;
        }

        return buffer;
    }

    public void write(String command) throws IOException {
        outputWriter.write(command + "\n\r");
        outputWriter.flush();
    }
}
