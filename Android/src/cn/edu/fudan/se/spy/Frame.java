package cn.edu.fudan.se.spy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Dawnwords on 2016/5/31.
 */
public class Frame implements Serializable {
    private int width, height;
    private byte[] data;

    public Frame(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public void out(DataOutputStream out) throws IOException {
        out.writeInt(data.length);
        out.writeInt(width);
        out.writeInt(height);
        out.write(data);
        out.flush();
    }

}
