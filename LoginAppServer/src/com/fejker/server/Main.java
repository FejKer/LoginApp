package com.fejker.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static ServerSocket serverSocket;
    static String username;
    static String password;
    static String ip;
    static boolean logged = false;
    static boolean register = false;
    static PrintWriter printWriter;
    static InputStreamReader inputStreamReader;
    static Socket socket;
    static ArrayList<Thread> threads = new ArrayList<>();
    static String message = null;

    public static List<Thread> getThreads() {
        return threads;
    }

    static {
        try {
            serverSocket = new ServerSocket(27000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void login() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.0.100:3306/messenger", "java", "pass");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts");
            while (rs.next()) {
                if(username.equals(rs.getString(2)) && password.equals(rs.getString(3))){
                    logged = true;
                    break;
                }

            }
            if(logged) {
                printWriter.println("Zalogowano.");
            }
            else {
                printWriter.println("Niepoprawne dane logowania.");
            }
            printWriter.println(logged);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void register() {
        boolean exists = false;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.0.100:3306/messenger", "java", "pass");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts");
            while (rs.next()) {
                if(username.equals(rs.getString(2))) {
                    printWriter.println("Konto o tej nazwie już istnieje.");
                    logged = false;
                    printWriter.println(logged);
                    exists = true;
                    break;
                }
            }
            if(!exists){
                stmt.executeUpdate("INSERT INTO accounts (username, password, register_ip) VALUES (\"" + username + "\", \"" + password + "\" , \"" + ip + "\" )");
                printWriter.println("Zarejestrowano.");
                logged = true;
                printWriter.println(logged);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        while(true) {
            try {
                socket = serverSocket.accept();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            clientHandler();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                System.out.println(t.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}

    private static void clientHandler() throws IOException {
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        SocketAddress getAddress = socket.getRemoteSocketAddress();
        ip = getAddress.toString();
        ip = ip.substring(1, ip.indexOf(":"));
        System.out.println("Klient połączony." + " Ip to: " + ip);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        if (bufferedReader.readLine().equals("true")) {
            register = true;
        } else {
            register = false;
        }
        System.out.println(register);
        username = bufferedReader.readLine();
        password = bufferedReader.readLine();
        System.out.println(username + password);

        if (!register) {
            login();
        }
        else {
            register();
        }

        if(logged) {
            /*try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.0.100:3306/messenger", "java", "wFNynV3k");
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select * from accounts");
                while (rs.next()) {
                    System.out.println("Wysyłanie danych.");
                    System.out.println(rs.getString(2));
                    printWriter.println(rs.getString(2));
                }
                } catch (SQLException e) {
                e.printStackTrace();
            }*/
            while(true) {
                message = bufferedReader.readLine();
                for(Thread thread : threads) {
                    if (!message.equals(null)) {
                        printWriter.println(message);
                    }
                }
            }
        }

    }

    }
