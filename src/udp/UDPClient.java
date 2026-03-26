import java.io.*;
import java.net.*;

class UDPClient
{
  public static void main(String args[]) throws Exception
  {
    // Startup notification in console
    System.out.println("Client is running!");
    // Buffers & sockets
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    DatagramSocket clientSocket = new DatagramSocket();

    // Read input from user
    BufferedReader inFromUser =
      new BufferedReader(new InputStreamReader(System.in));

    // Convert user input to bytes for sending.
    String sentence = inFromUser.readLine();
    sendData = sentence.getBytes();

    InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

    // Creates UDP packet with the 4 necessary components, then sends
    DatagramPacket sendPacket =
      new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

    clientSocket.send(sendPacket);

    // Prepare to receive UDP packet with the 2 components, then receives
    DatagramPacket receivePacket =
      new DatagramPacket(receiveData, receiveData.length);

    clientSocket.receive(receivePacket);

    // Convert received data to string
    String modifiedSentence =
        new String(receivePacket.getData());

    // Print server response, then close socket.
    System.out.println("FROM SERVER:" + modifiedSentence);
    clientSocket.close();
  }
}
