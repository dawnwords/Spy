package cn.edu.fudan.se.spy;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Dawnwords on 2016/5/31.
 */
public class CommunicationService {
    private AtomicReference<Frame> frame;
    private String ip;
    private int port;
    private Socket socket;
    private DataOutputStream out;
    private ExecutorService threadPool;
    private Runnable resume, pause, execute;
    private volatile boolean isPause;

    public CommunicationService() {
        frame = new AtomicReference<Frame>();
        threadPool = Executors.newCachedThreadPool();
        resume = new Runnable() {
            @Override
            public void run() {
                isPause = false;
                if (socket == null || socket.isClosed()) {
                    try {
                        socket = new Socket(ip, port);
                        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                        threadPool.execute(execute);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(getClass().getName(), "resumeService");
            }
        };
        pause = new Runnable() {
            @Override
            public void run() {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignored) {
                    }
                    out = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                    socket = null;
                }
                isPause = true;
                Log.i(getClass().getName(), "pauseService");
            }
        };
        execute = new Runnable() {
            @Override
            public void run() {
                try {
                    Frame f = frame.get();
                    if (f != null && out != null) {
                        f.out(out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isPause) {
                    Log.i(getClass().getName(), "stop executing");
                } else {
                    threadPool.execute(execute);
                }
            }
        };
    }

    public CommunicationService setFrame(Frame frame) {
        this.frame.set(frame);
        return this;
    }

    public CommunicationService setServerAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
        return this;
    }

    public void resumeService() {
        if (ip == null || ip.length() == 0) return;
        threadPool.execute(resume);
    }

    public void pauseService() {
        threadPool.execute(pause);
    }

    public void destroyService() {
        isPause = true;
        threadPool.shutdown();
        Log.i(getClass().getName(), "destroyService");
    }
}
