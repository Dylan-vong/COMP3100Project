/* Dylan Vongsouvanh - 45956987 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Client {
    public static void main (String args[]) {
        Socket s = null; //Create new socket
        try{
            int port = 50000; //Sets the server port to 50000
            s = new Socket("localhost", port); //Initialises the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //Reads the data from server
            DataOutputStream out = new DataOutputStream(s.getOutputStream()); //Writes output to server
            
            //Begin Handshake Protocol
            out.write(("HELO\n").getBytes()); //Client sends "HELO" to server
            String reply = in.readLine(); //Client receives "HELO" success
            System.out.println("Received: " + reply); 

            String username = System.getProperty("user.name"); //Gets system username and saves to a string
            out.write(("AUTH " + username + "\n").getBytes()); //Authentication
            reply = in.readLine(); //Client receives Authentication success
            System.out.println("Received: " + reply); 
            //End Handshake Protocol

            while(!reply.equals("NONE")) { //Checks if there are any jobs available
                out.write(("REDY\n").getBytes()); //Client signals server for job
                reply = in.readLine(); //Client receives next job
                System.out.println("Received: " + reply);

                if(reply.contains("JOBN")) { //Checks if the server's response contains a job
                    String[] jobs = reply.split(" "); //Splits jobs by spaces
                    out.write(("GETS Capable " + jobs[4] + " " + jobs[5] + " " + jobs[6] + "\n").getBytes()); //Client sends server for GETS Capable command
                    reply = in.readLine(); //Client receives all capable of servers available

                    String[] serverData = reply.split(" "); //Splits the server information
                    List<String> serverList = new ArrayList<String>(); //Create a new arraylist for servers

                    out.write(("OK\n").getBytes()); //Client sends server validation/receive server list

                    for(int i = 0; i < Integer.valueOf(serverData[1]); i++) { //Iterates over all servers to parse them
                        reply = in.readLine(); //Client reads server list
                        serverList.add(reply); //Add the current server into the arraylist
                        System.out.println(reply);
                    }

                    String last = serverList.get(serverList.size()-1); //Gets the last server in the list
                    String[] servers = last.split(" "); //Splits the server details by spaces

                    out.write(("OK\n").getBytes()); //Client sends server another validation
                    reply = in.readLine(); //Client receives "."
                    System.out.println("Received: " + reply); 
                    
                    out.write(("SCHD " + jobs[2] + " " + servers[0] + " " + servers[1] + "\n").getBytes()); //Client sends server command to schedule a job
                    reply = in.readLine(); //Client receives confirmation that job has been scheduled
                    System.out.println("Received: " + reply);
                } 
            }            
            out.write(("QUIT\n").getBytes()); //Client sends command to quit/disconnect
            reply = in.readLine(); //Client receives server's quit message
            System.out.println("Received: " + reply + "\n"); 
            s.close(); //Disconnects from server    
        }

        catch (UnknownHostException e){ //IP of server is incorrect or cannot be connected to
            System.out.println("Host:"+e.getMessage());
        }
        catch (EOFException e){ //End of File
            System.out.println("EOF:"+e.getMessage());
        }
        catch (IOException e){ //An error has occurred
            System.out.println("IO:"+e.getMessage());
        }
        catch(ArrayIndexOutOfBoundsException e) { //Array items exceeds the storage allocated
            System.out.println("IO:"+e.getMessage());
        }

        if(s != null) try { //The socket stops sending messages to the client
            s.close(); //Ends connection to server
        }
        catch (IOException e){
            System.out.println("EXIT:"+e.getMessage());
        }
    }
}