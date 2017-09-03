package com.creatotronik.ocdpush;

import org.anarres.tftp.protocol.resource.TftpData;
import org.anarres.tftp.protocol.resource.TftpDataProvider;
import org.anarres.tftp.protocol.resource.TftpFileChannelData;
import org.anarres.tftp.server.mina.TftpServer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.StandardOpenOption;

public class ServeTftpFile implements TftpDataProvider{

    public final short PORT = 69;
    private final String servedFilename;
    private TftpServer tftpServer;

    public ServeTftpFile(String filename) {
        this.servedFilename = filename;
    }

    public void start() throws IOException {
        tftpServer = new TftpServer(this, this.PORT);
        tftpServer.start();
    }

    public void stop() throws IOException {
        tftpServer.stop();
    }

    @CheckForNull
    @Override
    public TftpData open(@Nonnull String wantedFilename) throws IOException {
        System.out.printf("ocd requested file %s, serving %s", wantedFilename, servedFilename);

        FileChannel fileChannel = FileChannel.open(FileSystems.getDefault().getPath(servedFilename),
                StandardOpenOption.READ);
        return new TftpFileChannelData(fileChannel, (int)fileChannel.size());
    }

    public static String pathToAsset() throws UnknownHostException {
        return String.format("//tftp/%s/loadfile.hex", InetAddress.getLocalHost().getHostAddress());
    }
}
