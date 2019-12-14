package Servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client
{
    public static void main (String [] args) throws IOException
    {
        BufferedReader clientRead = new BufferedReader(new InputStreamReader(System.in));

        InetAddress IP = InetAddress.getByName("localhost");
        DatagramSocket clientSocket = new DatagramSocket();
        while(true)
        {
            byte[] sendbuffer;
            byte[] receivebuffer = new byte[1024];

            System.out.print("\nEnter name / Enter quit to exit : ");
            String clientData = clientRead.readLine();
            sendbuffer = clientData.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendbuffer, sendbuffer.length, IP, 9800);
            clientSocket.send(sendPacket);

            if(clientData.equals("quit"))
            {
                System.out.println("Connection ended by client");
                break;
            }

            DatagramPacket receivePacket =
                    new DatagramPacket(receivebuffer, receivebuffer.length);
            clientSocket.receive(receivePacket);
            String serverData = new String(receivePacket.getData());
            serverData = serverData.trim();
            System.out.print("\nReply from Server: " + serverData);

        }
        clientSocket.close();

    }
}
