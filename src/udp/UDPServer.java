// Dependencies
import java.net.*;
import java.util.*;

class ClientInfo
{
  // Store client identifying information and connection times.
  String address;
  int port;
  String name;
  long connectTime;
  long lastSeen;

  // Constructor to initialize client info.
  ClientInfo(String address, int port, String name)
  {
    this.address = address;
    this.port = port;
    this.name = name;
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
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);

      InetAddress IPAddress = receivePacket.getAddress();
      int port = receivePacket.getPort(); 

      String clientKey = IPAddress.toString() + ":" + port;

      String usermsg = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

      // Check HashMap to see if client is new or existing.
      if (!clientMap.containsKey(clientKey)) 
      {
        // If new, construct new ClientInfo object and add to HashMap. 
        ClientInfo newClient = new ClientInfo(IPAddress.toString(), port, usermsg);
        clientMap.put(clientKey, newClient);
        // Console syntax to print new connection info. 
        System.out.println("NEW CLIENT CONNECTED: " + newClient.name);
        System.out.println("Address: " + clientKey);
        System.out.println("Connected at: " + new Date(newClient.connectTime));
        String confirmation = "You have connected to the server " + newClient.name + ", please send math equations.";
        sendData = confirmation.getBytes();

        // Create UDP packet with the 4 necessary components, then send.
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
        continue;
      } 

      // Update last seen time
      ClientInfo client = clientMap.get(clientKey);
      client.lastSeen = System.currentTimeMillis();
      
      System.out.println("MESSAGE FROM CLIENT (" + client.name + "): " + usermsg);

      if(usermsg.equalsIgnoreCase("quit")){
        System.out.println(client.name + " is disconnecting");
        clientMap.remove(clientKey);
        continue;
      }

      String servermsg = usermsg.toUpperCase();

      sendData = servermsg.getBytes();
      // Create UDP packet with the 4 necessary components, then send.
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

      serverSocket.send(sendPacket);
    }
  }
}
