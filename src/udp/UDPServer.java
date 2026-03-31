// Dependencies
import java.io.*;
import java.net.*;
import java.util.*;

class ClientInfo
{
  // Store client identifying information and connection times.
  String address;
  int port;
  long connectTime;
  long lastSeen;

  // Constructor to initialize client info.
  ClientInfo(String address, int port)
  {
    this.address = address;
    this.port = port;
    this.connectTime = System.currentTimeMillis();
    this.lastSeen = this.connectTime;
  }

  // Function to calculate duration of connection in seconds.
  long getDuration()
  {
    return (lastSeen - connectTime) / 1000;
  }
}

class UDPServer 
{
  public static void main(String args[]) throws Exception
  {
    // HashMap to store client info. Key is "IP:Port" string, giving the corresponding ClientInfo object. 
    Map<String, ClientInfo> clientMap = new HashMap<>();
    // Socket to listen on port 9876.
    DatagramSocket serverSocket = new DatagramSocket(9876);

    byte[] receiveData = new byte[1024];
    byte[] sendData  = new byte[1024];
    System.out.println("SERVER is running:");

    while(true)
    {

      DatagramPacket receivePacket =
        new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);

      String sentence = new String(receivePacket.getData());
      System.out.println("MESSAGE FROM CLIENT:" + sentence);

      InetAddress IPAddress = receivePacket.getAddress();
      int port = receivePacket.getPort();

      String clientKey = IPAddress.toString() + ":" + port;
      // Check HashMap to see if client is new or existing. 
      if (!clientMap.containsKey(clientKey)) 
      {
        // If new, construct new ClientInfo object and add to HashMap. 
        ClientInfo newClient = new ClientInfo(IPAddress.toString(), port);
        clientMap.put(clientKey, newClient);
        // Console syntax to print new connection info. 
        System.out.println("NEW CLIENT CONNECTED:");
        System.out.println("Address: " + clientKey);
        System.out.println("Connected at: " + new Date(newClient.connectTime));
      } 
      else 
      {
        // Update last seen time
        ClientInfo client = clientMap.get(clientKey);
        client.lastSeen = System.currentTimeMillis();
      }

      String capitalizedSentence = sentence.toUpperCase();

      sendData = capitalizedSentence.getBytes();
      // Create UDP packet with the 4 necessary components, then send.
      DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, port);

      serverSocket.send(sendPacket);
    }
  }
}
