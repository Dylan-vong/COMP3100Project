import java.net.*;
import java.io.*;

class Client {
    public static void main (String args[]) {
        Socket s = null;
        try{
            int port = 50000; //Sets the server port to 50000
            s = new Socket("localhost", port); //Initialises the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream( s.getOutputStream());
            out.write(("HELO\n").getBytes()); //HELO
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
            s.close();}
            catch (IOException e){
                System.out.println("close:"+e.getMessage());
            }
        }
    }
