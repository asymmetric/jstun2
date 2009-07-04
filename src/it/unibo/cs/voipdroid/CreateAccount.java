/* 
*  Copyright 2007, 2008, 2009 Luca Bonora, Luca Bedogni, Lorenzo Manacorda
*  
*  This file is part of VOIPDroid.
*
*  VOIPDroid is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  VOIPDroid is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with VOIPDroid.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.unibo.cs.voipdroid;

import it.unibo.cs.voipdroid.tools.AccountCreation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The CreateAccount class provide the layout for the creation of a new account, 
 * using Gizmo5 web services defined in the AccountCreation class.
 *
 */
public class CreateAccount extends Activity {

	public static String session = null;
	public static String account = null;
	public static String sessionId = null;
	public static String failure = null;
	public static ImageView captchaImageView = null;
	private ImageButton mConfirm;
	private EditText mFirstname;
	private EditText mLastname;
	private EditText mAddress;
	private EditText mEmail;
	private EditText mUsername;
	private EditText mPassword;
	private EditText mPasswordConfirm;
	private EditText mCaptcha;
	private CheckBox mCheck;
	private Boolean mPasswordValid = false;
	private Bitmap bitmap = null;

	private static final int setImageView = 0;
	private static final int popupFailure = 1;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case (KeyEvent.KEYCODE_DPAD_CENTER):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_DPAD_LEFT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_DPAD_RIGHT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_DPAD_UP):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_DPAD_DOWN):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_UNKNOWN):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_MENU):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SOFT_LEFT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SOFT_RIGHT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_BACK):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_CALL):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_CAMERA):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_ENDCALL):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_VOLUME_UP):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_VOLUME_DOWN):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_HOME):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SHIFT_LEFT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SHIFT_RIGHT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_ALT_LEFT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_ALT_RIGHT):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_EXPLORER):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_ENVELOPE):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SYM):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_ENTER):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_FOCUS):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_GRAVE):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_NOTIFICATION):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_POUND):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_POWER):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_SEARCH):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.KEYCODE_TAB):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.META_SHIFT_RIGHT_ON):
			return super.onKeyUp(keyCode, event);
		default: {
			String pwd = mPassword.getText().toString();
			String pwdConfirm = mPasswordConfirm.getText().toString();
			if ((!pwd.equals("")) && (!pwdConfirm.equals(""))
					&& (pwdConfirm.equals(pwd))) {
				mConfirm.setBackgroundResource(R.drawable.check_mark);
				mPasswordValid = true;
			} else {
				mConfirm.setBackgroundResource(android.R.drawable.ic_delete);
				mPasswordValid = false;
			}
		}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case (KeyEvent.KEYCODE_BACK): {
			Intent mIntent = new Intent();
			setResult(RESULT_CANCELED, mIntent);
			finish();
		};
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.account);
		setupUI();
		setupSession();
	}

	/**
	 * Setup a new session for the creation of the account
	 */
	private void setupSession() {
		/* Show progress dialog while getting session */
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Please wait while getting new session...");
		dialog.setIndeterminate(true);
		dialog.show();
		new Thread() {
			public void run() {
				session = AccountCreation.getSession(); //Gets a new session
				if (AccountCreation.readSession(session)) { //gets data from the downloaded session
					account = AccountCreation.getAccountUrl();
					sessionId = AccountCreation.getSessionId();

					Log.d("ACCOUNT", account);
					URL url = null;
					try {
						url = new URL(AccountCreation.getJpgUrl()); //Gets the image for captcha
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					
					if (!setRemoteImage(url)) {
						try {
							/* Jpg url not available, try png url */
							url = new URL(AccountCreation.getPngUrl());
							setRemoteImage(url);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}

					dialog.dismiss();
					Message m = new Message();
					m.what = CreateAccount.setImageView;
					CreateAccount.this.myViewUpdateHandler.sendMessage(m); //Update the ImageView
				} else {
					failure = AccountCreation.getFailure(); //Get the reason of the failure
					dialog.dismiss();
					Message m = new Message();
					m.what = CreateAccount.popupFailure;
					CreateAccount.this.myViewUpdateHandler.sendMessage(m);
				}
			}
		}.start();
	}

	/**
	 * Setup the user interface and the behaviour of the buttons in the screen
	 */
	private void setupUI() {
		mFirstname = (EditText) findViewById(R.id.firstName);
		mLastname = (EditText) findViewById(R.id.lastName);
		mAddress = (EditText) findViewById(R.id.address);
		mEmail = (EditText) findViewById(R.id.email);
		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mPasswordConfirm = (EditText) findViewById(R.id.passwordConfirm);
		captchaImageView = (ImageView) findViewById(R.id.captcha);
		mCaptcha = (EditText) findViewById(R.id.captchaText);
		mCheck = (CheckBox) findViewById(R.id.checkEULA);
		mConfirm = (ImageButton) findViewById(R.id.confirm);

		TextView terms = (TextView) findViewById(R.id.terms);
		//On terms click open the Gizmo5 licence web page
		terms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setAction("android.intent.action.VIEW");
				myIntent.addCategory("android.intent.category.BROWSABLE");
				Uri uri = Uri
						.parse("http://gizmo5.com/pc/fine-print/end-user-level-agreement/");
				myIntent.setData(uri);
				startActivity(myIntent);
			}
		});

		// Sets okButton onClick behaviour
		Button okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mPasswordValid
						&& !mUsername.getText().toString().equals("")
						&& !mEmail.getText().toString().equals("")) { 
					//Get the text on each field
					String captcha = mCaptcha.getText().toString();
					String firstname = mFirstname.getText().toString();
					String lastname = mLastname.getText().toString();
					String address = mAddress.getText().toString();
					String email = mEmail.getText().toString();
					String username = mUsername.getText().toString();
					String password = mPassword.getText().toString();
					String eula;
					if (mCheck.isChecked())
						eula = "1";
					else
						eula = "0";
					Log.v("RESULT", account + " " + sessionId + " " + captcha + " " + 
							firstname + " " + lastname + " " + address + " " + email + " " + 
							username + " " + password + " " + eula);
					String result = AccountCreation.getAccount(account,
							sessionId, captcha, firstname, lastname, address,
							email, username, password, eula);
					if (AccountCreation.readResponse(result)) {
						String message = AccountCreation.getMessageText();
						showPopup("Account Created", message, true);
					} else {
						failure = AccountCreation.getFailure();
						showPopup("Failure", failure, false);
					}
				} else
					showPopup("Missing informations",
							"Insert all the required informations", false);
			}
		});
		
		/**
		 * On cancel go back to the previous activity (Settings)
		 */
		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent mIntent = new Intent();
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});
	}

	/** Sets the Child-ImageView of this to the URL passed. 
	 * If IOexception, show drawable dunno */
	public Boolean setRemoteImage(URL aURL) {
		try {
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			bitmap = bm;
			return true;
		} catch (IOException e) {
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.dunno);
			/* Reset to 'Dunno' on any error. */
			//this.captchaImageView.setImageDrawable(getResources().getDrawable(
			// R.drawable.dunno));
		}
		return false;
	}

	/**
	 * Handle the message from the progress dialog threads
	 */
	Handler myViewUpdateHandler = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CreateAccount.setImageView:
				captchaImageView.setImageBitmap(bitmap);
				break;
			case CreateAccount.popupFailure:
				showPopup("Failure", failure, false);
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Shows a different popup in case of account creation successful or creation failure
	 * @param title the title of the popup
	 * @param message the message to show on it
	 * @param success the boolean for the account creation
	 */
	public void showPopup(final String title, String message,
			final Boolean success) {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.star_big_on)
				.setMessage(message).setTitle(title).setPositiveButton(
						R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (success == true) {
									String settings[] = new String[2];
									settings[0] = AccountCreation.getUsername();
									settings[1] = AccountCreation.getDomain();

									Bundle bundle = new Bundle();
									bundle.putStringArray("Settings", settings);
									Intent mIntent = new Intent();
									mIntent.putExtras(bundle);
									setResult(RESULT_OK, mIntent);
									finish();
								} else if (title.equals("Failure")) {
									setupSession();
								}
								/* User clicked OK so do some stuff */
							}
						}).show();
	}
}
