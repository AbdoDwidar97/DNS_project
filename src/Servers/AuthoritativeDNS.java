package Servers;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class AuthoritativeDNS
{

    public static void main (String [] args) throws IOException
    {
        DatagramSocket serverSocket = new DatagramSocket(9803);

        while(true)
        {
            byte[] receivebuffer = new byte[1024];
            byte[] sendbuffer;

            DatagramPacket recvdpkt = new DatagramPacket(receivebuffer, receivebuffer.length);
            serverSocket.receive(recvdpkt);

            InetAddress IP = recvdpkt.getAddress();
            int portno = recvdpkt.getPort();
            String name_request = new String(recvdpkt.getData());
            name_request = name_request.trim();

            String result = SearchForAddress(name_request);
            String serverdata = "";

            if (!result.equals("null"))
            {
                System.out.println("Client Requested : "+ name_request);
                System.out.println("URL : "+ name_request + "\nQuery type = A");
                System.out.println("IP Address : " + result);
                System.out.println("Query type : NS");
                System.out.println("Found record on authoritative DNS servers:\nName: authoritative_dns_table.txt");
                System.out.println("IP : localhost");

                serverdata = "URL=" + name_request + " IP Address= " + result +
                        " query type = A, NS \nServer name : authoritative DNS" +
                        "Authoritative answer :" + "\nName : authoritative_dns_table.txt" +
                        "\nIP= localhost";

            }else
            {
                serverdata = "null";
            }


            sendbuffer = serverdata.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, IP,portno);
            serverSocket.send(sendPacket);

            if(serverdata.equalsIgnoreCase("quit"))
            {
                System.out.println("connection ended by server");
                break;
            }

        }
        serverSocket.close();
    }

    private static String SearchForAddress(String name_request)
    {
        String res = "null";

        try
        {
            File myObj = new File("src/authoritative_dns_table.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                String data = myReader.nextLine();
                String [] dataRes = data.split(" ");
                if (dataRes[0].equals(name_request))
                {
                    return dataRes[1];
                }
            }
            myReader.close();

        } catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return res;
    }

}
