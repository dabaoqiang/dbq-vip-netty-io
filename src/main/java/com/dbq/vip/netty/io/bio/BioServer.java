package com.dbq.vip.netty.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xq
 */
public class BioServer {

    // 服务端网络IO模型封装对象
    ServerSocket serverSocket;

    // 服务器s
    public BioServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("BIO服务以启动，监听端口是:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始监听，并处理逻辑
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            System.out.println(client.getPort());
            //读数据
            InputStream is = client.getInputStream();
            byte[] buff = new byte[1024];
            int read = is.read(buff);
            if (read > 0) {
                String msg = new String(buff, 0, read);
                System.out.println("收到：" + msg);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new BioServer(8080).listen();
    }


}
