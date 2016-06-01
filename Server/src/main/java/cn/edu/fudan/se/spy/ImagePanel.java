package cn.edu.fudan.se.spy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Dawnwords on 2016/5/31.
 */
public class ImagePanel extends JPanel {
    private AtomicReference<BufferedImage> image;

    public ImagePanel() {
        this.image = new AtomicReference<>();
    }

    public void setImage(BufferedImage image) {
        this.image.set(image);
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        BufferedImage image = this.image.get();
        if (image == null) return;
        g.drawImage(image, 0, 0, 640, 480, null);
    }
}
