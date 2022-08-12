package com.fejker.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterPanel {

    static JFrame frame = new JFrame("Client");
    static String username;
    static String password;
    static String password_confirm;
    static boolean register = true;
    private JPanel jpanel;
    private JButton loginButton;
    private JButton registerButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField passwordconfField;
    static PrintWriter printWriter;
    static InputStreamReader inputStreamReader;


    public RegisterPanel(Socket socket) {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                username = usernameField.getText().toLowerCase();
                password = passwordField.getText();
                password_confirm = passwordconfField.getText();
                if (username.length() < 3) {
                    JOptionPane.showMessageDialog(null, "Username powinien mieć przynajmniej 4 znaki!");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        connect(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (username.contains("admin") || username.contains("root")) {
                    JOptionPane.showMessageDialog(null, "Username zawiera zakazany element.");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        connect(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!password.equals(password_confirm)) {
                    JOptionPane.showMessageDialog(null, "Hasła się nie zgadzają.");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        connect(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    password = Integer.toString(password.hashCode());
                    try {
                        register(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                register = false;
                LoginPanel loginPanel = new LoginPanel();
                loginPanel.main(null);
            }
        });
    }

    public static void main(Socket socket) throws IOException{
        frame.setContentPane(new RegisterPanel(socket).jpanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(200,200);
        frame.pack();
        frame.setVisible(true);
    }

    static void connect(Socket socket) throws IOException {
        try {
            socket = new Socket("localhost", 27000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void register(Socket socket) throws IOException {
        LoginPanel loginPanel = new LoginPanel();
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        printWriter.println(register);
        printWriter.println(username);
        printWriter.println(password);

        JOptionPane.showMessageDialog(null, bufferedReader.readLine());
        if (bufferedReader.readLine().equals("true")) {
            loginPanel.logged = true;
        }
        if(loginPanel.logged) {
            frame.dispose();
            ClientPanel clientPanel = new ClientPanel(socket);
            clientPanel.main(socket);
        } else {
            socket.close();
            connect(socket);
        }
    }

}