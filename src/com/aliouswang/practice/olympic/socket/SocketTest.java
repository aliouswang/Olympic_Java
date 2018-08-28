package com.aliouswang.practice.olympic.socket;

import com.aliouswang.practice.olympic.util.L;

import java.io.IOException;
import java.net.*;

public class SocketTest {

    public static void main(String[] args) {
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress("localhost", 8000);
        try {
            socket.connect(socketAddress, 6000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InetAddress address = InetAddress.getByName("www.baidu.com");
            L.d(address.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
