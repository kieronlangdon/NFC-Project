package nfcPc;
/*
 * NFC Integration PC
 * Bluetooth class
 * Kieron Langdon
 * 2012
 * This file uses hcitool on Ubuntu to complete the OBEX push (bluetooth)
 */
import java.util.*;
import java.io.*;

public class Bluetooth {

	/*
	 * @param cmd
	 *            the command to execute
	 * @return an array of 2 vectors representing the output stream from the
	 *         command and the error stream 
	 */
	public static Vector[] executeLinuxShellCmd(String cmd) {
		Vector in = new Vector();
		Vector err = new Vector();
		try {
			// Execute the command
			Process proc = Runtime.getRuntime().exec(cmd);
			// Output stream to the command - not used
			DataOutputStream dos = new DataOutputStream(proc.getOutputStream());
			// Input stream from the command
			DataInputStream dis = new DataInputStream(proc.getInputStream());
			// Error stream from the command
			DataInputStream eis = new DataInputStream(proc.getErrorStream());
			boolean cont = true;
			// Loop at least once
			do {
				// If there is some input grab it
				while (dis.available() > 0) {
					in.add(dis.readLine());
				}
				// If there are some errors grab them
				while (eis.available() > 0) {
					err.add(eis.readLine());
				}
				// See if the command has finished executing
				try {
					// Attempting to get the exit code throws an exeception if
					// proc hasn't exited yet
					int code = proc.exitValue();
					cont = false;
				} catch (IllegalThreadStateException itse) {
				}
				// Wait
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
			} while (cont);
			// Clean up
			dos.flush();
			dos.close();
			dis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		// Return what there is
		return new Vector[] { in, err };
	}

	private static final String DNE_ERROR = "Device is not available: Success";

	private static final String STARTING_SCAN = "Scanning ...";

	/**
	 * Method to return a list of discoverable devices in range
	 * 
	 * @return list of BTDevice objects representing the devices found.
	 */
	public static Vector getBTDevicesInRange() {
		// Execute the scan command
		Vector v[] = executeLinuxShellCmd("hcitool scan");
		// Check for errors
		if (v[1].size() > 0) {
			System.out.println("there are errors ");
			// Go through the errors
			for (Enumeration e = v[1].elements(); e.hasMoreElements();) {
				String error = (String) e.nextElement();
				System.out.println("->" + error);
				// If there are errors
				if (error.equalsIgnoreCase(DNE_ERROR)) {
					System.out.println("ERROR: Device not avaliable");
					return null;
				}
			}
		}
		// Vector to return
		Vector devices = new Vector();
		// Check through the output
		if (v[0].size() > 0) {
			Enumeration e = v[0].elements();
			// Check if the scan started
			if (((String) e.nextElement()).equalsIgnoreCase(STARTING_SCAN)) {
				System.out.println("Scan started...");
				// Go through rest of the output
				for (; e.hasMoreElements();) {
					// Break up the lines
					StringTokenizer st = new StringTokenizer((String) e
							.nextElement());
					// If there are BT addr & name then add them
					if (st.countTokens() == 2) {
						devices
								.add(new BTDevice(st.nextToken(), st
										.nextToken()));
					}
					// In case  didn't get a name
					else if (st.countTokens() == 1) {
						devices
								.add(new BTDevice(st.nextToken(), st
										.nextToken()));
					}
					// System.out.println("->"+line);
				}
				return devices;
			}
		}
		return devices;

	}
	
	private static final String PUSH_FINISHED = "pushed";
	
	public static boolean OBEXPush(String BT_Addr, String file) {
		if (!new File(file).exists()) {
			System.out.println("ERROR: Cannot find file: "+file);
			return false;
		}
		
		//Make sure to release all rfcomm bindings 
		String cmd = "sudo rfcomm release all";
		System.out.println(cmd);
		Vector v[] = executeLinuxShellCmd(cmd);
		// Check for errors
		if (v[1].size() > 0) {
			System.out.println("there are errors ");
			// Go through the errors
			for (Enumeration e = v[1].elements(); e.hasMoreElements();) {
				String error = (String) e.nextElement();
				System.out.println("->" + error);
			}
			return false;
		}
		
		//Attempt to perform the RFCOMM bind.
		cmd = "sudo rfcomm bind /dev/rfcomm0 "+BT_Addr+"  9";//9 is default rfcomm bind
		System.out.println(cmd);
		v = executeLinuxShellCmd(cmd);
		// Check for errors
		if (v[1].size() > 0) {
			System.out.println("there are errors ");
			// Go through the errors
			for (Enumeration e = v[1].elements(); e.hasMoreElements();) {
				String error = (String) e.nextElement();
				System.out.println("->" + error);
			}
			return false;
		}
		// Vector to return
		Vector devices = new Vector();
		// Check through the output
		if (v[0].size() == 0) {
			

			String to_file = file;
			if (file.lastIndexOf("/") > -1) {
				to_file = file.substring(file.lastIndexOf("/")+1);
			}
			else if (file.lastIndexOf("\\") > -1) {
				to_file = file.substring(file.lastIndexOf("\\")+1);
			}
			cmd = "sudo  ussp-push /dev/rfcomm0 "+file+" "+to_file;
			System.out.println(cmd);
			v = executeLinuxShellCmd(cmd);
			//Check for errors
			if (v[1].size() > 0) {
				System.out.println("there are errors ");
				// Go through the errors
				for (Enumeration e = v[1].elements(); e.hasMoreElements();) {
					String error = (String) e.nextElement();
					System.out.println("->" + error);
				}
				return false;
			}
			if (v[0].size() > 0) {
				System.out.println("Looks good so far ");
				if (!((String)v[0].elementAt(v[0].size()-1)).endsWith(PUSH_FINISHED)) {
					System.out.println("ERROR: Push Failed - timeout?");
					System.out.println("->"+v[0].elementAt(v[0].size()-1));
					return false;
				} else {
					System.out.println("Push Completed!");
				}
			}
			
			//Release the rfcomm binding
			cmd = "sudo rfcomm release /dev/rfcomm0";
			System.out.println(cmd);
			v = executeLinuxShellCmd(cmd);
			// Check for errors
			if (v[1].size() > 0) {
				System.out.println("there are errors ");
				// Go through the errors
				for (Enumeration e = v[1].elements(); e.hasMoreElements();) {
					String error = (String) e.nextElement();
					System.out.println("->" + error);
				}
				return false;
			}	
		}
		return true;
	}
//main class for the file push
	public static void main(String args[]) throws Exception {
		Vector v = getBTDevicesInRange();// get the device
		for (Enumeration e = v.elements();e.hasMoreElements();) {
			BTDevice dev = (BTDevice) e.nextElement();
			System.out.println("GOT: "+dev.BT_ID+":"+dev.Friendly_Name);//give output to user
			OBEXPush(dev.BT_ID,"file.txt");// push the file to device
		}
    }
}
//this class is the temporary store the bluetooth device's details
class BTDevice {
    public String BT_ID = null;

    public String Friendly_Name = null;

    public BTDevice(String id, String fname) {
        BT_ID = id;
        Friendly_Name = fname;
    }
}

