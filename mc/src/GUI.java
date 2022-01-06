import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.awt.*;
import java.util.regex.Pattern;    
public class GUI    
{   public static JFrame f= new JFrame("MC Scanner");  
    public static JLabel ipLabel = new JLabel("IP:");
    public static JLabel portLabel = new JLabel("Start Port:");  
    public static JLabel speedLabel = new JLabel("Scan Speed:");
    public static JLabel amountLabel = new JLabel("AMT To Scan:"); 
    public static JPanel thePanel = new JPanel();
    public static JTextField portBox = new JTextField("Enter Port"); 
    public static JTextField ipBox = new JTextField("Enter Server IP"); 
    public static JTextField amountBox = new JTextField("AMT To Scan");
    public static JCheckBox onlineCB = new JCheckBox("Only Print Online Servers?"); 
    public static String scanSpeed = "Medium";
    public static JProgressBar scanProgress = new JProgressBar();
    public static boolean onlyPrintOnlineServers = true;
     GUI(){      
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
        thePanel.setLayout(null);   
        f.add(thePanel);
        ipLabel.setBounds(5,5, 200,20);   
        ipLabel.setSize(100,20);   
        amountLabel.setBounds(160,5, 100,20);    
        amountBox.setBounds(160,27, 80,20);
        amountBox.setForeground(Color.lightGray);
        portLabel.setBounds(5,27, 100,20);     
        speedLabel.setBounds(5,62, 100,17);  
        scanProgress.setBounds(5,84, 231, 15);
        scanProgress.setValue(100);
        scanProgress.setToolTipText("Waiting For Start");
        scanProgress.setString("Idle");
        scanProgress.setStringPainted(true);
        onlineCB.setBounds(0,44, 190,19);    
        onlineCB.setSelected(true); 
        onlineCB.setFocusable(false);
        ipBox.setBounds(25,5, 130,20);    
        ipBox.setCaretColor(Color.BLACK);
        ipBox.setToolTipText("Enter The IP to Scan!"); 
        portBox.setBounds(65,27, 90,20);     
        portBox.setToolTipText("Enter The Port to Start Scanning On!");
        String[] options = { "Medium","Fast", "Very Fast", "Dangerous"};
        JComboBox<String> speedDropDown = new JComboBox<String>(options);
        speedDropDown.setFocusable(false);
        speedDropDown.setSelectedItem("Fast");
        speedDropDown.setBounds(78,63, 85,16); 
        JButton startButton = new JButton("Go!");
        startButton.setBounds(181,55, 55,25);
        startButton.setFocusable(false); 
        portBox.setText("Enter Start Port"); portBox.setForeground(Color.LIGHT_GRAY);   
        ipBox.setText("Enter Server IP"); ipBox.setForeground(Color.LIGHT_GRAY);
        onlineCB.setOpaque(false);
        thePanel.add(scanProgress);thePanel.add(amountBox); thePanel.add(amountLabel);thePanel.add(startButton);thePanel.add(speedLabel);thePanel.add(speedDropDown);thePanel.add(onlineCB); thePanel.add(ipLabel); thePanel.add(ipBox); thePanel.add(portBox);thePanel.add(portLabel);
        onlineCB.addItemListener(new ItemListener() {    
             public void itemStateChanged(ItemEvent e) {                      
          }});   
          startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                    if (startButton.getText().equals("X")) {
                        long total = (System.nanoTime() - App.start) / 1000000;
                        System.out.println("total time: " + total);
                        App.writer.close();
                        System.out.println("Created Log file \"Output Log "+App.timeStamp+"\"");
                        System.out.print("Scanning Successful! Scanned ");
                        System.exit(0);
                    }
                    if (startButton.getText().equals("Go!")) {
                    boolean allGood = true;
                     if (ipBox.getText().equals("") ||ipBox.getText().equals("Enter Server IP")) {ipBox.setBackground(Color.pink); allGood = false;}
                     if (portBox.getText().equals("")||portBox.getText().equals("Enter Start Port")) {portBox.setBackground(Color.pink); allGood = false;} 
                     if (portBox.getText().matches(".*[a-zA-Z]+.*") || portBox.getText().length() > 5 || ((!portBox.getText().equals("") || !portBox.getText().equals("Enter Start Port"))&&Integer.parseInt(portBox.getText()) > 65535)) {
                         portBox.setBackground(Color.pink);allGood = false;}
                    if (amountBox.getText().matches(".*[a-zA-Z]+.*") || amountBox.getText().length() > 7) {
                        amountBox.setBackground(Color.pink);allGood = false;}
                    Pattern wrongChars = Pattern.compile("[^a-zA-Z0-9]"+"."+"[^a-zA-Z0-9]"+"."+"[^a-zA-Z0-9]"+".");
                    boolean illegalChars = wrongChars.matcher(ipBox.getText()).find();
                     if (illegalChars == true || !ipBox.getText().contains(".")) {
                         ipBox.setBackground(Color.pink);allGood = false;}
                    if (allGood == true) {
                    System.out.println("You Did It!");
                    if (onlineCB.isSelected() == false) onlyPrintOnlineServers = false;
                    scanSpeed = (String) speedDropDown.getSelectedItem();
                    App.startIP = ipBox.getText();
                    App.startPort = Integer.parseInt(portBox.getText());
                    App.limit = Integer.parseInt(amountBox.getText());
                    System.out.println("Scanning "+App.limit+" Servers on "+App.startIP+":"+App.startPort+" On "+scanSpeed+" Speed. Only print online Servers? "+onlyPrintOnlineServers);
                    startButton.setText("X");
                    ipBox.setEnabled(false);portBox.setEnabled(false);onlineCB.setEnabled(false);amountBox.setEnabled(false);speedDropDown.setEnabled(false);
                    try {
                        scanProgress.setString("Working...");
                        App.startProcess();
                    } catch (FileNotFoundException | UnsupportedEncodingException | InterruptedException e1) {
                        e1.printStackTrace();
                    }}}}});    
        ipBox.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (ipBox.getText().equals("Enter Server IP")) {ipBox.setText("");}
                ipBox.setForeground(Color.black);
                if (ipBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
                }
            @Override
            public void focusLost(FocusEvent e) { 
                if (ipBox.getText().equals("")) {ipBox.setText("Enter Server IP"); ipBox.setForeground(Color.LIGHT_GRAY);}
                if (ipBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
        }});
        portBox.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (portBox.getText().equals("Enter Start Port")) {portBox.setText("");}
                portBox.setForeground(Color.black);
                if (portBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
                }

            @Override
            public void focusLost(FocusEvent e) { 
                if (portBox.getText().equals("")) {portBox.setText("Enter Start Port"); portBox.setForeground(Color.LIGHT_GRAY);}
                if (portBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
        }});
        amountBox.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (amountBox.getText().equals("AMT To Scan")) {amountBox.setText("");}
                amountBox.setForeground(Color.black);
                if (amountBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
                }

            @Override
            public void focusLost(FocusEvent e) { 
                if (amountBox.getText().equals("")) {amountBox.setText("AMT To Scan"); amountBox.setForeground(Color.LIGHT_GRAY);}
                if (amountBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}
        }});
        
    ipBox.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {}
        public void keyTyped(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {
            if (ipBox.getBackground() == Color.pink) {ipBox.setBackground(Color.white);portBox.setBackground(Color.white);amountBox.setBackground(Color.white);}}});
        portBox.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {}
         public void keyTyped(KeyEvent e) {} 
         public void keyPressed(KeyEvent e) {
            if (portBox.getBackground() == Color.pink) {portBox.setBackground(Color.white);ipBox.setBackground(Color.white);amountBox.setBackground(Color.white);}}});
        amountBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {} 
            public void keyPressed(KeyEvent e) {
                if (amountBox.getBackground() == Color.pink) {portBox.setBackground(Color.white);ipBox.setBackground(Color.white);amountBox.setBackground(Color.white);}}}); 
        f.setSize(255,120); 
        f.setResizable(false); 
        Image icon = Toolkit.getDefaultToolkit().getImage("src\\icon.png");  
        f.setIconImage(icon);
        f.setVisible(true);
        thePanel.setVisible(true);
        f.setLocationRelativeTo(null);  
         
     }    
public static void main(String args[])    
{    
    new GUI();    
}}   