package cn.edu.fudan.se.spy;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dawnwords on 2016/5/31.
 */
public class ServerThread extends Thread {
    private ServerSocket ss;
    private ImagePanel panel;

    public ServerThread(ImagePanel panel) {
        this.panel = panel;
        try {
            this.ss = new ServerSocket(6000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Socket client = null;
            try {
                client = ss.accept();
                System.out.println("connected:" + client.getInetAddress());
                DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                while (!isInterrupted()) {
                    final BufferedImage image = YvuUtil.yuv2jpeg(in);
                    if (image != null) {
                        repaintImage(image);
                    }
                }
            } catch (EOFException e) {
                if (client != null) {
                    System.out.println("disconnected:" + client.getInetAddress());
                    try {
                        client.close();
                    } catch (IOException ignored) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void repaintImage(final BufferedImage image) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.setImage(image);
                panel.repaint();
            }
        });
    }
}
