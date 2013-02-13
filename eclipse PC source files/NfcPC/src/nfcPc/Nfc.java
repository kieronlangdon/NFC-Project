package nfcPc;
/*
 * NFC Integration PC
 * Main class
 * Kieron Langdon
 * 2012
 */
import java.io.*;
import java.io.ObjectOutputStream.PutField;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;


import java.awt.image.*;

public class Nfc extends JPanel implements ActionListener {
	
    protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
    static Process child1;
    JTextArea bottomTextArea;
    JButton jbutton1 = new JButton("Transfer Call");       
    JButton jbutton2 = new JButton("Call");
    JButton jbutton3 = new JButton("Read from file");
    /*constructor*/
    public Nfc() {
       super(new GridBagLayout());

       textField = new JTextField();
       JLabel label = new JLabel("Add SIP number here:");
       JPanel panel = new JPanel();
      // ImageIcon icon = new ImageIcon("image.jpeg");
       ImageIcon icon = new ImageIcon(getClass().getResource("/image.jpeg"));
       JLabel label1 = new JLabel();
       jbutton1.addActionListener(this);
       jbutton2.addActionListener(this);
       jbutton3.addActionListener(this);
       textField.addActionListener(this);
       textField.addMouseListener(
    		new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					textField.setText(""); /*set blank textbox on click*/
				
				}
				
				public void mousePressed(MouseEvent e) {	
				}
				
				public void mouseReleased(MouseEvent e) {
				}
				
				public void mouseEntered(MouseEvent e) {
				}
				
				public void mouseExited(MouseEvent e) {
				}
    	}
       );

       //Add Components to this panel.
       GridBagConstraints c = new GridBagConstraints();
       c.gridwidth = GridBagConstraints.REMAINDER;
       c.fill = GridBagConstraints.HORIZONTAL;
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 1.0;
       c.weighty = 1.0;
       add(label);
       add(textField, c);
       add(jbutton1);
       add(jbutton2);
       add(jbutton3);
       add(panel);
       panel.add(label1);
       label1.setIcon(icon);
        

       setVisible(true); 
    }

    public void actionPerformed(ActionEvent evt) {
    	
       String text = textField.getText();
       try{
    	   if (evt.getSource() == jbutton1){
    		 //transfer call functionality,this calls another class in the package
    		   Bluetooth h=new Bluetooth();
    	        h.main(null);
    	        textField.setText("Working......");//message while waiting
    	   }
    	   else if (evt.getSource() == jbutton2){
    		   String command = "usr/bin/xterm";//execute the terminal
    		   /*start a new process for the SIP client with the details given*/
    	       child1 = Runtime.getRuntime().exec("ekiga -c sip:"+text+"@46.22.128.231 ");
    	       FileWriter outFile = new FileWriter("file.txt");//write the details to file
    	       PrintWriter out = new PrintWriter(outFile);
    	       out.println(text);
    	       out.close();//close the file
    	   }
    	   else if (evt.getSource() == jbutton3){
    		   //read from file
    		   String input;
    		   BufferedReader in = new BufferedReader(new FileReader("file.txt"));
    		   while((input = in.readLine()) != null){
    		       //set the textfield to the file output
    			   textField.setText(input);
    		   }
    		   in.close();
    	   }
	       
       }
       catch(IOException e){
           e.printStackTrace();
       } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
       textField.selectAll();
    }

    private static void createAndShowGUI() {
       //Create and set up the window.
       final JFrame frame = new JFrame("NFC Client");
          
       //Add contents to the window.
       frame.add(new Nfc());

       //Display the window.
       frame.pack();
       frame.setVisible(true);
       frame.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
       
       //Anonymous class to kill ekiga, what happens when user closes the JFrame.
       WindowListener windowListener = new WindowAdapter()
       {
	       // anonymous WindowAdapter class
	       public void windowClosing( WindowEvent w ){
	    	   frame.setVisible( false );
	           frame.dispose();
	           
	           try{
		           Process child2 = Runtime.getRuntime().exec("pkill -x ekiga");
		       }catch(IOException e){
		    	   
		       };
		       
	           //Close the JFrame
	           frame.setVisible( false );
	           frame.dispose();
	       } // end windowClosing
       };// end anonymous class
       frame.addWindowListener( windowListener );
    }

    public static void main(String[] args) {
       //Schedule a job for the event dispatch thread:
       //Create and show this application's GUI.
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               createAndShowGUI();
           }
       });
    }
}