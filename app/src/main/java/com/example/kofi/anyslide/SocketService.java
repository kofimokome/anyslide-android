package com.example.kofi.anyslide;

import android.content.SharedPreferences;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;

public class SocketService {

    private static Socket socket;
    private static boolean isSocketInitialised = false;

    public static boolean isUserSetup = false;


    public static void initialiseSocket() {
        if (!isIsSocketInitialised()) {
            {
                try {
                    socket = IO.socket("https://anyslide.herokuapp.com/");
                    socket.connect();
                    isSocketInitialised = true;
                } catch (URISyntaxException e) {
                    System.out.println("socket did not initialise");
                }
            }
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static boolean isIsSocketInitialised() {
        return isSocketInitialised;
    }

    public static void disconnect(){
        socket.disconnect();
        isSocketInitialised = false;
    }
}
