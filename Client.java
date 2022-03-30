import java.net.*;
import java.io.*;

import java.util.ArrayList;
import java.util.List;

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
            String helo = in.readLine();
            System.out.println("Received: " + helo); //Client receives "HELO" success
            out.write(("AUTH Dylan\n").getBytes()); //Authentication
            String auth = in.readLine();
            System.out.println("Received: " + auth); //Client receives Authentication success
            //End Handshake Protocol

            out.write(("REDY\n").getBytes()); //Client signals server for job
            String redy = in.readLine();
            System.out.println("Received: " + redy); //Client receives next job

            out.write(("GETS All\n").getBytes()); //Client sends server for GET command
            String get = in.readLine();
            System.out.println("Received: " + get); //Client receives number of servers available

            out.write(("OK\n").getBytes()); //Client sends server validation
            String ok = in.readLine();
            System.out.println("Received: " + ok); //Client receives list of servers

            // out.write(("OK\n").getBytes()); //Client sends server another validation
            // String ok2 = in.readLine();
            // System.out.println("Received: " + ok2); //Client receives "."

        }
        catch (UnknownHostException e){ //IP of server is incorrect or cannot be connected to
            System.out.println("Sock:"+e.getMessage());
        }
        catch (EOFException e){ //End of File
            System.out.println("EOF:"+e.getMessage());
        }
        catch (IOException e){ //An error has occurred
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

class parse {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        //Initialise array based on data extracted from parser
        List<Server> serverList = ServerParseXML();
        System.out.println(serverList.toString().substring(1, serverList.toString().length()-1));
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
 
    public String toString() { //Returns extracted server elements into a string
        return "Server [type:" + type + ", bootupTime:" + bootupTime + ", hourlyRate:" + hourlyRate + ", core:" + core + ", memory:" + memory + ", disk:" + disk + "]\n";
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