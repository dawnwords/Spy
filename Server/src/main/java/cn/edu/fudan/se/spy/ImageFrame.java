package cn.edu.fudan.se.spy;

import org.jogamp.glg2d.GLG2DCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * A frame with an image panel
 */
@SuppressWarnings("serial")
public class ImageFrame extends JFrame {
    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;
    public ImagePanel panel;

    public ImageFrame() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        setTitle("Spy");
        setLocation((screenWidth - DEFAULT_WIDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        panel = new ImagePanel();
        panel.setSize(640, 480);
        panel.setLocation(0, 0);
        add(new GLG2DCanvas(panel));
    }
}
