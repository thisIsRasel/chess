package com.example.rasel.myapplication;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by rasel on 5/6/18.
 */

public class MySocket {

    public static Socket socket;

    public MySocket(){

        try {

            //socket = IO.socket("http://192.168.56.1:3000");
            socket = IO.socket("http://domain.com:49500");
            //ifconfig vboxnet0 to get ipaddress if you run from genymotion

        } catch(URISyntaxException ex) {

            System.out.println("Something went wrong");
        }
    }

    public void connect() {

        socket.connect();
    }

}
