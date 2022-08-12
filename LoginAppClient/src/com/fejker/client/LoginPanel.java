package com.fejker.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class LoginPanel {
    static String username;
    static String password;
    static boolean logged = false;
    static boolean register = false;
    public static Socket socket;
    static PrintWriter printWriter;
    static InputStreamReader inputStreamReader;
    RegisterPanel registerPanel = new RegisterPanel(socket);
    ClientPanel clientPanel = new ClientPanel(socket);

    private JPanel jpanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    static JFrame frame = new JFrame("Client");

    public LoginPanel() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                username = usernameField.getText().toLowerCase();
                password = passwordField.getText();
                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                try {
                    registerPanel.main(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login() throws IOException{
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        password = Integer.toString(password.hashCode());                   //definitely need to change this for security purposes
        printWriter.println(register);
        printWriter.println(username);
        printWriter.println(password);
        JOptionPane.showMessageDialog(null, bufferedReader.readLine());
        if (bufferedReader.readLine().equals("true")) {
            logged = true;
        }
        if (logged) {
            frame.dispose();
            ClientPanel clientPanel = new ClientPanel(socket);
            clientPanel.main(socket);
        } else {
            socket.close();
            connect();
        }
    }

    static void connect() {
        try {
            socket = new Socket("localhost", 27000);            //ip address of server that handles logins and registers
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connect();
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
        frame.setContentPane(new LoginPanel().jpanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(200,200);
        frame.pack();
        frame.setVisible(true);
    }
}
