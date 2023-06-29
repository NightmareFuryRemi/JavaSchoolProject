/**
 *
 * @author dzelazny
 */
import java.net.*;
import java.io.*;

public class Client {

    public static void main(String args[]) {
        String host = "localhost";
        int port = 0;
        try {
            port = new Integer("6666").intValue();
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowy argument: port");
            System.exit(-1);
        }

        // Inicjalizacja gniazda klienckiego
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Nieznany host.");
            System.exit(-1);
        } catch (ConnectException e) {
            System.out.println("Połączenie odrzucone.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }
        System.out.println("Połączono z " + clientSocket);

        // Deklaracje zmiennych strumieniowych
        String line = null;
        BufferedReader brSockInp = null;
        BufferedReader brLocalInp = null;
        BufferedWriter bwout = null;

        // Utworzenie strumieni
        try {
            bwout = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            brSockInp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            brLocalInp = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Błąd przy tworzeniu strumieni: " + e);
            System.exit(-1);
        }

        // Pętla główna klienta
        try {
            while (true) {
                line = brLocalInp.readLine();
                if (line != null) {
                    System.out.println("Wysyłam: " + line);
                    bwout.write(line + "\n");
                    bwout.flush();

                    if (line.equals("quit")) {
                        System.out.println("Kończenie pracy...");
                        clientSocket.close();
                        System.exit(0);
                    } else if (line.equals("stan")) {
                        String response = brSockInp.readLine();
                        System.out.println(response);
                    } else if (line.startsWith("wpłać ") || line.startsWith("wypłać ") || line.startsWith("przelej ")) {
                        String response = brSockInp.readLine();
                        System.out.println(response);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }
    }
}