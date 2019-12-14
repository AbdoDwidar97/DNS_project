package Servers;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TLD_DNS
{
    public static void main (String [] args) throws IOException
    {
        DatagramSocket serverSocket = new DatagramSocket(9802);

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
                String [] resu = result.split(" ");

                System.out.println("Client Requested : "+ name_request);
                System.out.println("URL : "+ name_request + "\nQuery type = A");
                System.out.println("IP Address : "+ resu[0]);
                System.out.println("Query type : CName");
                System.out.println("IP Address : "+ resu[0]);
                System.out.println("Canonical name : " + resu[1] + "\nAliases : " + name_request);


                serverdata = "URL=" + name_request + " IP Address= " + resu[0] +
                             " query type = A, CName \nServer name : TLD DNS" +
                             "\nCanonical name : " + resu[1] + "\nAliases : " + name_request;

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
            File myObj = new File("src/tld_dns_table.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                String data = myReader.nextLine();
                String [] dataRes = data.split(" ");
                if (dataRes[0].equals(name_request))
                {
                    return dataRes[1] + " " + dataRes[2];
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
