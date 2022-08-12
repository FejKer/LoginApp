package com.fejker.client;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientPanel {
    private JPanel jpanel;
    private JTextField textField1;
    private JList usersList;
    static PrintWriter printWriter;
    static InputStreamReader inputStreamReader;
    static JFrame frame = new JFrame("Client");
    String[] users;
    String receive;

    public ClientPanel(Socket socket) {
        usersList.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    receive = bufferedReader.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public static void main(Socket socket) {
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setContentPane(new ClientPanel(socket).jpanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400,400);
        frame.pack();
        frame.setVisible(true);
    }

}
