// Dependencies
import java.io.*;
import java.net.*;

class UDPClient
{
  public static void main(String args[]) throws Exception
  {
    // Startup notification in console.
    System.out.println("Client is running!");
    // Buffers & sockets
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    DatagramSocket clientSocket = new DatagramSocket();

    // Prompt user for name and read, convert to bytes for sending.
    System.out.println("Enter name:");

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    String name = inFromUser.readLine();

    sendData = name.getBytes();
    
    InetAddress IPAddress = InetAddress.getByName("localhost");

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

    clientSocket.send(sendPacket);

    while(true)
    {
      // Prepare to receive UDP packet with the 2 components, then receives.
      DatagramPacket receivePacket =
        new DatagramPacket(receiveData, receiveData.length);

      clientSocket.receive(receivePacket);

      // Convert received data to string.
      String solution = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

      // Print server response, then close socket.
      System.out.println("FROM SERVER:" + solution);
      
      // Read input from user.
      inFromUser = new BufferedReader(new InputStreamReader(System.in));

      // Convert user input to bytes for sending.
      String equation = inFromUser.readLine();

      sendData = equation.getBytes();

      // Creates UDP packet with the 4 necessary components, then sends.
      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

      clientSocket.send(sendPacket);

      if(equation.toLowerCase().startsWith("quit"))
      {
        break;
      }
    }
    clientSocket.close();
  }
}
