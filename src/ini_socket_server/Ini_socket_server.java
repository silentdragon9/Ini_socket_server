/*
This program will communicate using socket 8080 and listening SNMP trap from client (Ini_socket_client.java).

Name: Mohammad Ariff Bin Idris
ID: 2017430762
Subject: Test 3 ITT786
Dateline: 18 November 2018
*/
package ini_socket_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;   
import java.net.InetAddress;   
import java.util.Vector;   
import org.snmp4j.AbstractTarget;   
import org.snmp4j.CommunityTarget;   
import org.snmp4j.PDU;   
import org.snmp4j.Snmp;   
import org.snmp4j.TransportMapping;   
import org.snmp4j.event.ResponseEvent;   
import org.snmp4j.mp.SnmpConstants;   
import org.snmp4j.smi.Address;   
import org.snmp4j.smi.GenericAddress;   
import org.snmp4j.smi.OID;   
import org.snmp4j.smi.OctetString;   
import org.snmp4j.smi.UdpAddress;   
import org.snmp4j.smi.VariableBinding;   
import org.snmp4j.transport.DefaultUdpTransportMapping;   

public class Ini_socket_server {

    private static final long MEGABYTE = 1048576; //1024 x 1024 = 1048576

    public static long bytesToMegabytes(long memory) {
        return memory / MEGABYTE;
    }
    public static void main(String[] args) {
       
        
        
         try
        {
            ServerSocket ser = new ServerSocket(8080);
            Socket sock = ser.accept();
            
            Address targetAddress = GenericAddress.parse("udp:127.0.0.1/8080");   
            TransportMapping transport = new DefaultUdpTransportMapping();   
            Snmp snmp = new Snmp(transport);   
            transport.listen();   
               
            CommunityTarget target = new CommunityTarget();   
            target.setCommunity(new OctetString("public")); 
            target.setAddress(targetAddress);  
            target.setRetries(2);  
            target.setTimeout(5000);  
            target.setVersion(1);
            
            PDU request = new PDU();   
            request.setType(PDU.GET); 
            request.add(new VariableBinding(new OID(".1.3.6.1.2.1.25.2.2.0")));//OID total RAM kByte/sec
            System.out.println("Request UDP:" + request);                  
            ResponseEvent respEvt = snmp.send(request, target); 
            
            
            
            if (respEvt != null && respEvt.getResponse()!=null)   
            {   
                Vector <VariableBinding> revBindings = respEvt.getResponse().getVariableBindings();   
                for (int i=0; i<revBindings.size();i++)   
                {   
                    VariableBinding vbs = revBindings.elementAt(i);   
                    System.out.println("\n" + vbs.getOid()+":"+vbs.getVariable()+" bytes");   
                    
                    
                }   
            }   
             
            // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Total memory is: " + runtime.totalMemory()+ " bytes");
        System.out.println("Total memory is: "+ runtime.totalMemory()/1048576 + " Megabytes");
        System.out.println("Used memory is: " + memory + " bytes");
        System.out.println("Used memory is: "+ bytesToMegabytes(memory) + " Megabytes");
            
            BufferedReader ed = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String tmp = ed.readLine();
            System.out.print("I Received :"+tmp);
            
            PrintStream pr = new PrintStream(sock.getOutputStream());
            String str = "Total RAM will be calculate at server";
            pr.println(str);
            
        }catch(IOException ex)
        {ex.printStackTrace();   
        }
        
    }
    
}
