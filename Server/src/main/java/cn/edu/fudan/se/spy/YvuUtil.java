package cn.edu.fudan.se.spy;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * Created by Dawnwords on 2016/5/31.
 */
public class YvuUtil {
    private static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    public static BufferedImage yuv2jpeg(DataInputStream in) throws IOException {
        byte[] data = new byte[in.readInt()];
        int mWidth = in.readInt();
        int mHeight = in.readInt();
        in.readFully(data);

        int[] mIntArray = new int[mWidth * mHeight];
        decodeYUV420SP(mIntArray, data, mWidth, mHeight);
        BufferedImage result = new BufferedImage(mWidth, mHeight, TYPE_INT_RGB);
        result.setRGB(0, 0, mWidth, mHeight, mIntArray, 0, mWidth);
        return result;
    }
}
