import java.net.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
            String helo = in.readLine(); //Client receives "HELO" success
            System.out.println("Received: " + helo); 

            String username = System.getProperty("user.name"); //Gets system username and saves to a string
            out.write(("AUTH " + username + "\n").getBytes()); //Authentication
            String auth = in.readLine(); //Client receives Authentication success
            System.out.println("Received: " + auth); 
            //End Handshake Protocol

            out.write(("REDY\n").getBytes()); //Client signals server for job
            String redy = in.readLine(); //Client receives next job
            System.out.println("Received: " + redy);
            while(redy != "NONE"){
                if(redy.contains("JOBN")) {
                    
                    String[] jobs = redy.split(" "); //Splits jobs by spaces
                    out.write(("GETS Capable " + jobs[4] + " " + jobs[5] + " " + jobs[6] + "\n").getBytes()); //Client sends server for GETS Capable command
                    String gets = in.readLine(); //Client receives all capable of servers available

                    String[] serverData = gets.split(" ");
                    List<String> serverList = new ArrayList<String>();
                    out.write(("OK\n").getBytes()); //Client sends server validation
                    String ok = in.readLine(); //Client receives server information
                    System.out.println("Received: " + ok);
                    for (int i = 0; i < Integer.valueOf(serverData[1]); i++) {
                        ok = in.readLine();
                        serverList.add(ok);
                        System.out.println(ok);
                        out.write("OK\n".getBytes());
                    }
                    String first = serverList.get(0);
                    String[] servers = first.split(" "); 

                    out.write(("OK\n").getBytes()); //Client sends server another validation
                    String ok2 = in.readLine(); //Client receives "."
                    System.out.println("Received: " + ok2); 
        
                    out.write(("SCHD " + jobs[2] + " " + servers[0] + " " + servers[1] + "\n").getBytes()); //Client sends server command to schedule a job
                    //out.write(("SCHD " + jobs[2] + " small 0\n").getBytes());
                    String schd = in.readLine(); //Client receives confirmation that job has been scheduled
                    System.out.println("Received: " + schd);
                }
                out.write(("QUIT\n").getBytes()); //Client sends command to quit/disconnect
                String quit = in.readLine(); //Client receives server's quit message
                System.out.println("Received: " + quit + "\n"); 
                s.close(); //Disconnects from server
            }
            

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
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("IO:"+e.getMessage());
        }

        if(s!= null) try {
            s.close(); //Ends connection to server
        }
        catch (IOException e){
            System.out.println("EXIT:"+e.getMessage());
        }
    }
}

class parse {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        //Initialise array based on data extracted from parser
        List<Server> serverList = ServerParseXML();

        //Print server ArrayList *For testing purposes only*
        //System.out.println(serverList.toString().substring(1, serverList.toString().length()-1));
    }

    private static List<Server> ServerParseXML() throws ParserConfigurationException, SAXException, IOException {
        //Initialize empty ServerList
        List<Server> serverList = new ArrayList<Server>();
        Server server = null;
      
        //Create DOM Parser for ds-sim servers
        DocumentBuilderFactory serverFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder serverXML = serverFactory.newDocumentBuilder();
        Document dsServer = serverXML.parse(new File("/home/ubuntu/ds-sim/src/pre-compiled/ds-system.xml")); //File path to servers
        dsServer.getDocumentElement().normalize(); //Normalise data to enable sorting
        NodeList sList = dsServer.getElementsByTagName("server");  //Reads elements that are in "server"
        for (int list = 0; list < sList.getLength(); list++) { //Iterate to extract all servers in file
            Node sNode = sList.item(list); //Sets node to current list in loop
            if (sNode.getNodeType() == Node.ELEMENT_NODE) { //Checks if current node item is valid
                Element serverInfo = (Element) sNode;
                server = new Server(); //Create a new server list

                //Extract server elements from file
                server.setServerType(serverInfo.getAttribute("type"));
                server.setServerLimit(Integer.parseInt(serverInfo.getAttribute("limit")));
                server.setServerBootupTime(Integer.parseInt(serverInfo.getAttribute("bootupTime")));
                server.setServerHourlyRate(Double.parseDouble(serverInfo.getAttribute("hourlyRate")));
                server.setServerCores(Integer.parseInt(serverInfo.getAttribute("cores")));
                server.setServerMemory(Integer.parseInt(serverInfo.getAttribute("memory")));
                server.setServerDisk(Integer.parseInt(serverInfo.getAttribute("disk")));

                serverList.add(server); //Add list to array
            }
        }
        return serverList;
    }
}

class Server { 
    //Initialises data types for server arraylist
    String type;
    int limit;
    int bootupTime;
    double hourlyRate;
    int core;
    int memory;
    int disk;
 
    public String toString() { 
        //Returns extracted server elements into a string
        return "Server [type:" + type + ", bootupTime:" + bootupTime + ", hourlyRate:" + hourlyRate + 
        ", core:" + core+ ", memory:" + memory + ", disk:" + disk + "]\n";
    }
 
    //Stores extracted server elements into above variables
    public String getServerType() {
        return type;
    }
    public void setServerType(String type) {
        this.type = type;
    }
 
    public int getServerLimit() {
        return limit;
    }
    public void setServerLimit(int limit) {
        this.limit = limit;
    }
 
    public int getServerBootupTime() {
        return bootupTime;
    }
    public void setServerBootupTime(int bootupTime) {
        this.bootupTime = bootupTime;
    }
 
    public double getServerHourlyRate() {
        return hourlyRate;
    }
    public void setServerHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
 
    public int getServerCores() {
        return core;
    }
    public void setServerCores(int core) {
        this.core = core;
    }
 
    public int getServerMemory() {
        return memory;
    }
    public void setServerMemory(int memory) {
        this.memory = memory;
    }
 
    public int getServerDisk() {
        return disk;
    }
    public void setServerDisk(int disk) {
        this.disk = disk;
    }
}

//Sorting algorithm to return largest server by number of cores
class SortByCore implements Comparator<Server> {
    public int compare(Server a, Server b){
        return a.core - b.core;
    }
}