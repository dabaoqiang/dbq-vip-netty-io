package com.dbq.vip.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author xq
 * @desc
 */
public class NIOServerDemo {

    private int port;

    // 轮询器 大堂经理
    private Selector selector;

    // 缓冲区 Buffer 等候区
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    // 初始化
    public NIOServerDemo(int port) {

        // 开门营业
        try {
            this.port = port;
            ServerSocketChannel server = ServerSocketChannel.open();
            //告诉地址
            server.bind(new InetSocketAddress(this.port));
            // 升级版本,NIO模型默认采用阻塞式
            server.configureBlocking(false);
            // 开始接客
            selector = Selector.open();
            // 准备接受所有的请求 准备号翻牌子
            server.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println("listen on :" + this.port + ".");
        try {
            // 轮询主线程
            while (true) {
                // 大堂经理交叫号
                selector.select();
                // 每次都拿到所有号子
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // 不断的迭代，就叫轮询
                // 同步体现在这里，因为每次只能拿到一个key、每次只能处理一种状态
                Iterator<SelectionKey> iter = selectionKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    // 每一个key代表一种状态
                    // 每一个号对应一种业务
                    // 数据就绪，数据可读，数据可写等等
                    process(key);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(SelectionKey key) throws IOException {
        //就绪
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            // 设置为非阻塞
            channel.configureBlocking(false);
            // 当数据准备就绪时候，将状态改为可读
            channel.register(selector, SelectionKey.OP_READ);
        }
        // 是否可读
        else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            int len = channel.read(buffer);
            if (len > 0) {
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                key = channel.register(selector, SelectionKey.OP_WRITE);
                // 在key上携带一个附加，一会再写出去
                key.attach(content);
                System.out.println("读取内容：" + content);
            }
            // 是否可写
        } else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            String content = (String) key.attachment();
            channel.write(ByteBuffer.wrap(("输出：" + content).getBytes()));
            channel.close();
        }

    }


    public static void main(String[] args) {
        new NIOServerDemo(8080).listen();

    }
}
