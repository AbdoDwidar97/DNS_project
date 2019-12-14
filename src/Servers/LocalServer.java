package Servers;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class LocalServer
{

    public static void main (String [] args) throws IOException
    {

        final int LOCAL_DNS_PORT = 9800;
        final int AUTH_DNS_PORT = 9803;

        DatagramSocket serverSocket = new DatagramSocket(LOCAL_DNS_PORT);

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
            if (name_request.equals("quit"))
            {
                System.out.println("Client is Disconnected.....");
                break;
            }
            String result = SearchForAddress(name_request);
            String serverdata = "";

            if (!result.equals("null"))
            {
                System.out.println("Client Requested : "+ name_request);
                System.out.println("URL : "+ name_request + "\nQuery type = A");
                System.out.println("IP Address : " + result);

                serverdata = "URL=" + name_request + " IP Address= " + result + " query type = A \nServer name : local DNS";

            }else
            {
                boolean isExist = false;
                for (int port = LOCAL_DNS_PORT + 1; port <= AUTH_DNS_PORT; port++)
                {

                    sendbuffer = name_request.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, IP,port);
                    serverSocket.send(sendPacket);

                    byte[] receivebuffer2 = new byte[1024];

                    recvdpkt = new DatagramPacket(receivebuffer2, receivebuffer2.length);
                    serverSocket.receive(recvdpkt);
                    String req = new String(recvdpkt.getData());
                    req = req.trim();

                    if (!req.equals("null"))
                    {
                        serverdata = req;
                        port = AUTH_DNS_PORT + 1;
                        isExist = true;
                    }
                }


                if (!isExist) serverdata = "request name not found!";
                System.out.println(serverdata);
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
            File myObj = new File("src/local_dns_table.txt");
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
