// Dependencies
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /* 
      Define format equations must be sent in (sign num op sign num op ...)
      Valid Ops include: 
                + (Addition)
                - (Subtraction)
                * (Multiplication)
                / (Division)
                ^ (Exponentiation)
      Valid signs include:
                -     (Negative)
                Blank (Positive)
    */
    String eqFormat = "^-?\\d+(\\s*[-+*/^]\\s*-?\\d+)*$";

    while(true)
    {
      // Receive incoming UDP packet with its 2 components. 
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);

      // Get IP and port number of client.
      InetAddress IPAddress = receivePacket.getAddress();
      int port = receivePacket.getPort(); 

      // Create client key for hashmap lookup.
      String clientKey = IPAddress.toString() + ":" + port;

      String usermsg = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

      // Check HashMap to see if client is new or existing.
      if (!clientMap.containsKey(clientKey)) 
      {
        // If new, construct new ClientInfo object and add to HashMap.
        ClientInfo newClient = new ClientInfo(IPAddress.toString(), port, usermsg);
        clientMap.put(clientKey, newClient);
        // Console syntax to print new connection info and inform user of connection.
        System.out.println("NEW CLIENT CONNECTED: " + newClient.name);
        System.out.println("Address: " + clientKey);
        System.out.println("Connected at: " + new Date(newClient.connectTime));
        String confirmation = " You have connected to the server " + newClient.name + ", please send math equations.";
        sendData = confirmation.getBytes();

        // Create UDP packet with the 4 necessary components, then send.
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
        continue;
      } 

      // If client is not new, Update last seen time.
      ClientInfo client = clientMap.get(clientKey);
      client.lastSeen = System.currentTimeMillis();
      
      System.out.println("FROM CLIENT (" + client.name + "): " + usermsg);

      // Remove client from server hashmap if they send "quit".
      if(usermsg.equalsIgnoreCase("quit"))
      {
        System.out.println(client.name + " is disconnecting");
        clientMap.remove(clientKey);
        continue;
      }

      String servermsg = "";

      // Check if user eqaution is properly formated .
      try 
      {
        if(usermsg.matches(eqFormat))
        {
          // Takes equation pattern and uses matcher to split equation for parsing.
          Pattern pattern = Pattern.compile("-?\\d+|[+\\-*/^()]+");
          Matcher matcher = pattern.matcher(usermsg);

          int result = 0;
          // Reads first number in equation 
          if(matcher.find())
          {
            result = Integer.parseInt(matcher.group());
          }

          // Reads rest of equation after first number, then computes result.
          while (matcher.find()) 
          {
            String op = matcher.group();
            // Check the next operator.
            if(matcher.find())
            {
              int num2 = Integer.parseInt(matcher.group());

              if(op.equals("+"))
              {
                result += num2;
              }
              else if(op.equals("-"))
              {
                result -= num2;
              }
              else if(op.equals("/"))
              {
                result = (int) (result / num2);
              }
              else if(op.equals("*"))
              {
                result *= num2;
              }
              else if(op.equals("^"))
              {
                result = (int) Math.pow(result, num2);
              }
            }
          }
          servermsg = " Result: " + result;
        }
        else
        {
          servermsg = "Improper equation format, please try again";
        }
    }
    catch(Exception e)
    { // Check if equation is not properly formated but still passes matching check.
      servermsg = "Improper equation format, please try again";
    }

      sendData = servermsg.getBytes();
      // Create UDP packet with the 4 necessary components, then send.
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

      serverSocket.send(sendPacket);
    }
  }
}
