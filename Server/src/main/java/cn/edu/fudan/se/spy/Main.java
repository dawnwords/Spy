package cn.edu.fudan.se.spy;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        final ImageFrame frame = new ImageFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        new ServerThread(frame.panel).start();
    }

}

