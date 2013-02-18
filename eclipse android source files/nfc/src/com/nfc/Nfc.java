package com.nfc;
/*
 * NFC Integration Android
 * Main class
 * Kieron Langdon
 * 2012
 */
import java.io.*;

import com.nfc.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.view.View;


public class Nfc extends Activity {

/* Called when the activity is first created. */

	/* GUI controls*/
	EditText txtData;
	Button btnWriteSDFile;
	Button btnReadSDFile;
	Button btnClearScreen;
	Button btnClose;
	Button btnTransfer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	/* Binds GUI elements with controls*/
	
/* creates a textdata string with a prewritten message*/
	txtData = (EditText) findViewById(R.id.txtData);
	txtData.setHint("Enter Number to dial here...");
	
/*adds a listener to the writetofile button */
	btnWriteSDFile = (Button) findViewById(R.id.btnWriteSDFile);
	btnWriteSDFile.setOnClickListener(new OnClickListener() {

	public void onClick(View v) {
		/* write on SD card file with data from the text box*/
		try {
			File myFile = new File("/sdcard/mysdfile.txt");/*create a new file in that location*/
			myFile.createNewFile();//Initialise the constructor*/
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = 
			new OutputStreamWriter(fOut);
			myOutWriter.append(txtData.getText());/*create the stream to output to file*/
			myOutWriter.close();
			fOut.close();/*close the file*/
			Toast.makeText(getBaseContext(),
					"Done writing to SD card 'mysdfile.txt'",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}//End onClick
	}); //End btnWriteSDFile

		btnReadSDFile = (Button) findViewById(R.id.btnReadSDFile);
		btnReadSDFile.setOnClickListener(new OnClickListener() {

		public void onClick(View v) {
		try {
			File myFile = new File("/sdcard/mysdfile.txt");/*reads the file*/
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
			}
			txtData.setText(aBuffer);
			myReader.close();
			Toast.makeText(getBaseContext(),/*Dialog box with message*/
					"Done reading from SD card 'mysdfile.txt'",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		}/*End onClick*/
		}); /*End btnReadSDFile*/

		btnClearScreen = (Button) findViewById(R.id.btnClearScreen);
		btnClearScreen.setOnClickListener(new OnClickListener() {
		/*Pass the call details over using a bluetooth push*/
			public void onClick(View v){			
				File sourceFile = new File("/sdcard/sendio.txt"); 
				Intent intent = new Intent(); 
				intent.setAction(Intent.ACTION_SEND); 
				intent.setType("image/jpeg"); 
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sourceFile)); 
				startActivity(intent);
				
			}});
		btnTransfer = (Button) findViewById(R.id.btnTransfer);
		btnTransfer.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/* write on SD card file data in the text box*/
				try {
					File myFile = new File("/sdcard/mysdfile.txt");
					myFile.createNewFile();
					FileOutputStream fOut = new FileOutputStream(myFile);
					OutputStreamWriter myOutWriter = 
											new OutputStreamWriter(fOut);
					myOutWriter.append(txtData.getText());
					myOutWriter.close();
					fOut.close();
					Toast.makeText(getBaseContext(),/*Dialog box with message*/
							"Calling SIP contact now....",
							Toast.LENGTH_SHORT).show();
					/*Start new intent with number from textbox, this starts the SIP client which will passs the details to the next intent*/		
					Intent intent = new Intent();
					intent.setClassName("com.csipsimple", "com.csipsimple.ui.SipHome");
				    /*Start a new intent with the number entered from the textbox using the built in Android calling feature*/
					startActivity(intent);
					String url = ("tel:txtData.getText().toString()");
			        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
			        startActivity(callIntent);

				} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}); /*end of btnClose*/
	}/*end of onCreate*/
	@Override /*This next piece of code adds a dialog  box assign to a hardware button the device*/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	super.onKeyDown(keyCode, event);
	try {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
        	new AlertDialog.Builder(this)
            .setTitle("NFC Integration 2012")
            .setMessage("Designed by Kieron Langdon-Press Back to escape")
             .show();
        	//beep starts here
        	 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
             Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
             r.play();
                return true;
       
		}
    } catch (Exception e) {} 
			return false;
	    }

}/*End of class*/