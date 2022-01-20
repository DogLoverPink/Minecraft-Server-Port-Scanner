import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class App {
    public static String json;
    public static int countUp = 1;
    public static String startIP;
    public static int startPort;
    private static AtomicInteger counter = new AtomicInteger(-1);
    public static int limit;
    public static long start = System.nanoTime();
    public static String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
    public static PrintWriter writer;
    public static ExecutorService es = Executors.newCachedThreadPool();
    public static String mcServerInfo(String inputJson) { //This impletation might be added in a future update
        Matcher m = Pattern.compile(":\\{\"name\":\"([^)]+)\",\"p").matcher(json); //this extracts the server version, and player count from a json String
        String output = "";
        while(m.find()) {
        output = ("Version: "+m.group(1));  }
        if (!json.contains("\"sample\":[{")) {
        m = Pattern.compile(",\"online\":([^)]+)},\"ver").matcher(json);
        while(m.find()) {
        output = (output+" Online: "+m.group(1));  }
        } else {
        m = Pattern.compile(",\"online\":([^)]+),\"sample").matcher(json);
        while(m.find()) {
        output = (output+" Online: "+m.group(1));  } }
        m = Pattern.compile(":\\{\"max\":([^)]+),\"online").matcher(json);
        while(m.find()) {
        output = (output+"/"+m.group(1));  } 
        return output;
    }
    public static void mcHandShake(String inputIp, int inputPort) {
        boolean open = true;
        try {
            internalMCHandShake(inputIp, inputPort);
        } catch (IOException e) {
            open = false;
        } 
        if (open == false) {
                if (!GUI.onlyPrintOnlineServers) writer.println(inputIp+":"+inputPort+" is Offine!");
                if (GUI.onlyPrintOnlineServers) writer.print("");} //If I don't include this line (even tho it doesn't affect anything), it will mess with the Multithreading, IDK why.
        if (open == true) {
            writer.println(inputIp+":"+inputPort+" is Online!");
            
        }
        countUp++;
            System.out.println("Querying Server... "+(countUp-1)+"/"+limit);
            //if (countUp >= limit) {
                
                if ((countUp + countUp /20) >= limit) {
                long total = (System.nanoTime() - start) / 1000000;
                System.out.println("total time: " + total);
                writer.close();
                System.out.println("Created Log file \"Output Log "+timeStamp+"\"");
                System.out.print("Scanning Successful! Scanned ");
                GUI.f.add(GUI.exitLabel);
                GUI.thePanel.setVisible(false);
                try {
                    internalMCHandShake(inputIp, inputPort); // this is to wait a small bit, cause thread.sleep was giving issues
                } catch (IOException e) {}
                System.exit(0);
            }
    }
    /*------------------------------------------------------------------------------------------
     Hey! This code BELOW until the next similar comment bar, was copied of Stack Overflow! (my bad) linK: https://stackoverflow.com/questions/30768091/java-sending-handshake-packets-to-minecraft-server 
     ------------------------------------------------------------------------------------------*/
    public static void internalMCHandShake(String inputIp, int inputPort) throws IOException {
        String address = inputIp;
        int port = inputPort;
    
        InetSocketAddress host = new InetSocketAddress(address, port);
        Socket socket = new Socket();
        socket.setSoTimeout(5000);
        socket.connect(host, 3000);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());
        byte [] handshakeMessage = createHandshakeMessage(address, port);
    
        // C->S : Handshake State=1
        // send packet length and packet
        writeVarInt(output, handshakeMessage.length);
        output.write(handshakeMessage);
    
        // C->S : Request
        output.writeByte(0x01); //size is only 1
        output.writeByte(0x00); //packet id for ping
    
    
        // S->C : 
        int size = readVarInt(input); //HEY! if you remove this, its gonna break, so don't 
        int packetId = readVarInt(input);
    
        if (packetId == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
    
        if (packetId != 0x00) { //we want a status response
            socket.close();
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(input); //length of json string
    
        if (length == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
    
        if (length == 0) {
            socket.close();
            throw new IOException("Invalid string length.");
        }
    
        byte[] in = new byte[length];
        input.readFully(in);  //read json string
        json = new String(in);
        // C->S : Ping
        long now = System.currentTimeMillis();
        output.writeByte(0x09); //size of packet
        output.writeByte(0x01); //0x01 for ping
        output.writeLong(now); //time!?
    
        // S->C : Pong
        readVarInt(input);
        packetId = readVarInt(input);
        if (packetId == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }
    
        if (packetId != 0x01) {
            socket.close();
            throw new IOException("Invalid packetID");
            
            
        }
        long pingtime = input.readLong(); // HEY! if you remove this, its gonna break, so don't 
        socket.close();
    } 
    /*------------------------------------------------------------------------------------------
     Hey! This code ABOVE until the next similar comment bar, was copied of Stack Overflow! (my bad) link: https://stackoverflow.com/questions/30768091/java-sending-handshake-packets-to-minecraft-server 
     ------------------------------------------------------------------------------------------*/
    public static void startProcess() throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        counter = new AtomicInteger(-1);
            start = System.nanoTime();
         timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
         writer = new PrintWriter("Output Log "+timeStamp+".log", "UTF-8");
        Thread.sleep(1000);
        final CountDownLatch latch = new CountDownLatch(3);
        start = System.nanoTime();
        System.out.println("Confirmed, Scanning "+App.limit+" Servers on "+App.startIP+":"+App.startPort+" On "+GUI.scanSpeed+" Speed. Only print online Servers? "+GUI.onlyPrintOnlineServers);
        Runnable runnable = new Runnable() {
            public void run() {
                mcHandShake(App.startIP, (App.startPort + counter.incrementAndGet()));
                latch.countDown();
         }};
        Thread.sleep(500);
        for (double i = 0.0; i < limit; i++) {
            if (i % 100 == 0) {
                try {
                    if (GUI.scanSpeed.equals("Medium")) Thread.sleep(500);
                    if (GUI.scanSpeed.equals("Fast")) Thread.sleep(125);
                    if (GUI.scanSpeed.equals("Very Fast")) Thread.sleep(50);
                    if (GUI.scanSpeed.equals("Dangerous")) Thread.sleep(10);
                } catch (InterruptedException ie) {}}
                 es.submit(runnable);    
                 }
        
        latch.await();
        es.shutdown();
    }
    public static byte [] createHandshakeMessage(String host, int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    
        DataOutputStream handshake = new DataOutputStream(buffer);
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 4); //protocol version
        writeString(handshake, host, StandardCharsets.UTF_8);
        handshake.writeShort(port); //port
        writeVarInt(handshake, 1); //state (1 for handshake)
    
        return buffer.toByteArray();
    }
    
    public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte [] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    
    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
              out.writeByte(paramInt);
              return;
            }
    
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
    
    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

}
