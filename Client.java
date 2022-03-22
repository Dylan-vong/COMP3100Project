import java.net.*;
import java.io.*;

class Client {
    public static void main (String args[]) {
        Socket s = null; //Create new socket
        try{
            int port = 50000; //Sets the server port to 50000
            s = new Socket("localhost", port); //Initialises the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Reads the data from input
            DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Writes output to server
            
            //Handshake Protocol
            out.write(("HELO\n").getBytes()); //Client sends "HELO" to server
            String data = in.readLine();
            System.out.println("Received: "+ data) ; // Server receives "HELO"
            out.write(("AUTH Dylan\n").getBytes()); //Authentication
            String msg = in.readLine();
            System.out.println("Received: "+ msg) ; // Server receives Authentication
        }
        catch (UnknownHostException e){
            System.out.println("Sock:"+e.getMessage());
        }
        catch (EOFException e){
            System.out.println("EOF:"+e.getMessage());
        }
        catch (IOException e){
            System.out.println("IO:"+e.getMessage());
        }
        if(s!=null) try {
            s.close(); //Ends connection to server if there are no more inputs
        }
            catch (IOException e){
                System.out.println("close:"+e.getMessage());
            }
        }
    }
