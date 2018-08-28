package com.aliouswang.practice.olympic.socket;

import com.aliouswang.practice.olympic.util.L;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private int port = 8000;
    private ServerSocket serverSocket;

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        L.d("Echo server is started!");
    }

    public String echo(String msg) {
        return "echo:" + msg;
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }

    public void service() {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                L.d("New connection accespted " + socket.getInetAddress() + ":" + socket.getPort());
                BufferedReader br = getReader(socket);
                PrintWriter pw = getWriter(socket);
                String msg = null;
                while ((msg = br.readLine()) != null) {
                    L.d(msg);
                    pw.println(echo(msg));
                    if (msg.equals("bye")) {
                        break;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (socket != null) socket.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new EchoServer().service();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
