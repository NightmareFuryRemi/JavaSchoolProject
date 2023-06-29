/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

/**
 *
 * @author dzelazny
 */
import java.net.*;
import java.io.*;

public class EchoServerThread implements Runnable {
    protected Socket socket;
    private String clientId;

    public EchoServerThread(Socket clientSocket) {
        this.socket = clientSocket;
        this.clientId = Integer.toString(clientSocket.getPort());
    }

    public void run() {
        // Deklaracje zmiennych
        BufferedReader brinp = null;
        BufferedWriter bwout = null;
        String threadName = Thread.currentThread().getName();

        // Inicjalizacja strumieni
        try {
            brinp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bwout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(threadName + "| Błąd przy tworzeniu strumieni " + e);
            return;
        }

        String line = null;

        // Pętla główna
        try {
            while ((line = brinp.readLine()) != null) {
                System.out.println(threadName + "| Odczytano linię: " + line);

                // Badanie warunku zakończenia pracy
                if (line.equals("quit")) {
                    System.out.println(threadName + "| Zakończenie pracy z klientem: " + socket);
                    socket.close();
                    return;
                } else if (line.equals("stan")) {
                    double balance = readBalance();
                    bwout.write("Stan konta: " + balance + "\n");
                    bwout.flush();
                } else if (line.startsWith("wpłać ")) {
                    double amount = Double.parseDouble(line.substring(6));
                    if (amount > 0) {
                        double balance = readBalance();
                        balance += amount;
                        writeBalance(balance);
                        bwout.write("Wpłacono: " + amount + "\n");
                        bwout.flush();
                    } else {
                        bwout.write("Niepoprawna kwota\n");
                        bwout.flush();
                    }
                } else if (line.startsWith("wypłać ")) {
                    double amount = Double.parseDouble(line.substring(7));
                    double balance = readBalance();
                    if (balance >= amount) {
                        balance -= amount;
                        writeBalance(balance);
                        bwout.write("Wypłacono: " + amount + "\n");
                        bwout.flush();
                    } else {
                        bwout.write("Niepoprawna kwota\n");
                        bwout.flush();
                    }
                } else if (line.startsWith("przelej ")) {
                    String[] parts = line.substring(8).split(" ");
                    if (parts.length == 2) {
                        String recipientClientId = parts[0];
                        double transferAmount = Double.parseDouble(parts[1]);
                        double senderBalance = readBalance();
                        double recipientBalance = readBalance(recipientClientId);
                        if (senderBalance >= transferAmount && transferAmount > 0) {
                            senderBalance -= transferAmount;
                            recipientBalance += transferAmount;
                            writeBalance(senderBalance);
                            writeBalance(recipientBalance, recipientClientId);
                            bwout.write("Przelew wykonany\n");
                            bwout.flush();
                        } else {
                            bwout.write("Niewystarczające środki\n");
                            bwout.flush();
                        }
                    } else {
                        bwout.write("Niepoprawny format przelewu\n");
                        bwout.flush();
                    }
                } else {
                    bwout.write("Nieznane polecenie\n");
                    bwout.flush();
                }
            }
        } catch (IOException e) {
            System.out.println(threadName + "| Błąd wejścia-wyjścia: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(threadName + "| Błąd przy zamykaniu gniazda: " + e);
            }
        }
    }

    private double readBalance() throws IOException {
        double balance = 0.0;
        File file = new File(clientId + ".txt");
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            if ((line = br.readLine()) != null) {
                balance = Double.parseDouble(line);
            }
            br.close();
        } else {
            file.createNewFile();
        }
        return balance;
    }

    private void writeBalance(double balance) throws IOException {
        File file = new File(clientId + ".txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(Double.toString(balance));
        bw.close();
    }

    private double readBalance(String clientId) throws IOException {
        double balance = 0.0;
        File file = new File(clientId + ".txt");
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            if ((line = br.readLine()) != null) {
                balance = Double.parseDouble(line);
            }
            br.close();
        } else {
            file.createNewFile();
        }
        return balance;
    }

    private void writeBalance(double balance, String clientId) throws IOException {
        File file = new File(clientId + ".txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(Double.toString(balance));
        bw.close();
    }
}