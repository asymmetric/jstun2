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

import java.io.IOException;
import java.net.InetAddress;

import it.unibo.cs.voipdroid.authentication.RegisterAgent;
import it.unibo.cs.voipdroid.authentication.RegisterAgentListener;
import it.unibo.cs.voipdroid.authentication.VOIPDroidRegAgListener;
import it.unibo.cs.voipdroid.databases.ProfileDbAdapter;
import it.unibo.cs.voipdroid.databases.SettingsDbAdapter;
import it.unibo.cs.voipdroid.stun.VOIPDroidSTUN;

import org.zoolu.sdp.AttributeField;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import com.jstun.core.attribute.MessageAttributeParsingException;
import com.jstun.core.header.MessageHeaderException;
import com.jstun.core.util.UtilityException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * VOIPDroid is the main activity of the application. It initialize the User
 * Interface and the SIP stack, then it allow to register the account and to
 * show it's contacts
 * */

public class VOIPDroid extends Activity {

	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_CONTACTS = Menu.FIRST + 1;
	private static final int MENU_REGISTER = Menu.FIRST + 2;
	private static final int MENU_UNREGISTER = Menu.FIRST + 3;
	private static final int ACTIVITY_SETTINGS = 0;
	private static final int ACTIVITY_CONTACTS = 1;
	private final int SENT = 1;
	private final int RECEIVED = 2;
	private static String nickname = null;
	private static String username = null;
	private String password = null;
	private String realm = null;
	private static String account = null;
	private static String contact_url;
	
	// Sip port as per RFC3261
	private static int port = 5060;
	Boolean register = false;
	public static Boolean firstContacts = true;
	private Boolean firstTime = true;

	Thread myRefreshThread = null;
	Thread mStunThread = null;
//	public static Thread myListenThread = null;

	static String sipState = "Disconnected";
	static String callState = "";
	static String host = "";
	static String incomingCall = "";
	static String sdp = "";
	public static short logged = 0;
	public static Boolean registration = false;

	RegisterAgent ra = null;
	RegisterAgentListener raListener = null;

	public static SipProvider sp = null;
	public static UserAgentProfile userProfile = null;

	static VOIPDroidListener listener = null;
//	private static InviteDialog id = null;
//	private static InviteDialog idlistener = null;
	private static int onCall = 0;

	private Long mRowIdSetting = null;
	private SettingsDbAdapter mSettings;

	private static final int SIPSTATE_CHANGED = 0;
	private static final int CALLSTATE_CHANGED = 1;
	private static final int INCOMING_CALL = 2;

	protected static String local_session = null;
	
	static Call call = null;
	static Call callListener = null;

	NotificationManager mNotificationManager;
	
	private boolean NATChecked = false;
	private boolean natted = false;
	private VOIPDroidSTUN st = null;
	private InetAddress publicAddr = null;
	
	private static String stunServer = "stun01.sipphone.com"; // default

//	private VOIPDroidRTP vrtp;
	
	
	/* "On" Methods */

	/**
	 * Called when the activity is firstly created. Sets and initialize the
	 * layout of the activity, the SIP stack and opens the settings DB
	 * */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Sets the layout of the activity
		setContentView(R.layout.main);

		createDirectory();

		if (icicle != null) {
			String[] settings = null;
			settings = icicle.getStringArray("Settings");
			setHost(settings[0]);
			setPort(Integer.valueOf(settings[1]));
			setContact_url(settings[2]);
			setSipState(settings[3]);
			firstTime = Boolean.valueOf(settings[4]);
		}
		// Initialize the thread for the refresh various states of the application
		this.myRefreshThread = new Thread(new secondCountDownRunner());
		this.myRefreshThread.start();

		// Initialize the SIP Stack
		if (firstTime) setSipStackParameters();
		// Initialize the layout
		setupUI();
		
		// Open the settings DB and call setupSettings() method
		mSettings = new SettingsDbAdapter(this);
		mSettings.open();
		setupSettings();
		
		// If the user chose to register on startup call setupRegister()
		if (register)
			setupRegister();

		// TODO thread?
//		setupRTP();

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String settings[] = new String[5];
		settings[0] = getHost();
		settings[1] = String.valueOf(getPort());
		settings[2] = getContact_url();
		settings[3] = sipState;
		settings[4] = String.valueOf(firstTime);
		outState.putStringArray( "Settings" , settings);
		Log.v("SaveInstance","saved");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (registration) {
			TextView tv = (TextView) findViewById(R.id.sipState);
			tv.setText(sipState);
			raListener = new VOIPDroidRegAgListener();
			ra = new RegisterAgent(sp, account, contact_url, username, realm,
					password, raListener);
			ra.register();
			listener = new VOIPDroidListener();
		}
	}

	/* SETUP */
	
	/** Create a new Register Agent and Listener and register to the SIP Proxy */
	private void setupRegister() {
		if (getUsername() != null) {
			// Auth
			// VOIPDroidAuthentication.authenticate(sp);

			// New Register Agent listener
			raListener = new VOIPDroidRegAgListener();
			Log.i("SIPprovider", "ADDRESS VIA = " + sp.getViaAddress());
			Log.i("SIPprovider", "ADDRESS IF = " + sp.getInterfaceAddress());
			Log.i("SIPprovider", "ADDRESS PORT = " + String.valueOf(sp.getPort()));

			contact_url = "sip:" + getUsername() + "@" + sp.getViaAddress()
					+ ":" + String.valueOf(sp.getPort() + ";transport=udp");

			// Create a new RegisterAgent and register
			ra = new RegisterAgent(sp, account, contact_url, username, realm,
					password, raListener);
			Log.v("REGISTER", "Half");
			ra.register();
			registration = true;

		}
	}

	/**
	 * Load saved settings from DB If DB is empty open the Settings activity to
	 * add a new setting, or to create a new SIP account
	 * */
	private void setupSettings() {
		Cursor isSetting = mSettings.fetchAllSettings();

		if (isSetting.getCount() > 0) { // If Database is not empty load the
										// saved settings
			mRowIdSetting = new Long((long) 1);
			Cursor setting = mSettings.fetchSetting(mRowIdSetting);
			startManagingCursor(setting);

			setNickname(setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_NICKNAME)));
			setUsername(setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_USERNAME)));
			setPassword(setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_PASSWORD)));
			setRealm(setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_REGISTRAR)));
			account = "sip:" + getUsername() + "@" + getRealm();
			register = Boolean.valueOf(setting.getString((setting
					.getColumnIndex(SettingsDbAdapter.KEY_CHECKREG))));
			stunServer = setting.getString(setting.getColumnIndex(SettingsDbAdapter.KEY_STUN));
			mSettings.close();
		} else { // else open Settings activity
			Intent myIntent = new Intent();
			myIntent.setClass(this, Settings.class);
			this.startActivityForResult(myIntent, ACTIVITY_SETTINGS);
		}
	}

	private void createDirectory() { // TODO funziona?
		this.getFileStreamPath("").mkdirs();
		Log.v("CICCIO", "MARUZZELLA " + getFileStreamPath(""));
	}

	/**
	 * Create the User Interface of the activity Sets the layout and the
	 * behaviour of the buttons
	 * */
	private void setupUI() {
		// Load the call image to the button
		ImageButton btn = (ImageButton) findViewById(R.id.doCall);
		// Create and initialize a new InviteDialogListener()
		listener = new VOIPDroidListener();
		callListener = new Call(sp, account, contact_url, listener);
		callListener.setLocalSessionDescriptor(local_session);
		call = new Call(sp, account, contact_url, listener);
		call.setLocalSessionDescriptor(local_session);
		callListener.listen();
//		VOIPDroidInviteDialogListener vil = new VOIPDroidInviteDialogListener();
//		setInviteDialog(sp, vil);
//		setInviteDialogListener(sp, vil);
//		idlistener.listen();
		// Sets the behaviour of the call button
		btn.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View arg0) {
				// Do the call to to_url address (edittext on the screen)
				String to_url = ((EditText) findViewById(R.id.toUrl)).getText()
						.toString();

				/*
				 * if address is empty or client is not connected don't do calls
				 * TODO dire qualcosa all'utente nel caso ci siano problemi
				 */
				if (!(to_url.length() == 0) && (sipState == "Connected")) {
					Log.v("CALL", "starting the call to " + to_url);

					TextView tc = (TextView) findViewById(R.id.callStatus);
					tc.setText("Trying...");

					// Sets the call status
					setOnCall(SENT);

					call.call(to_url, getNickname() + "<" + account + ">" ,
							contact_url, local_session.toString());

//					id.invite(to_url, getNickname() + "<" + account + ">",
//							contact_url, local_session.toString());

					Log.v("INVITE", "FUORI dalla .invite()");
					// call.call(to_url);
				}
			}
		});
		// Sets the behaviour of the Stop call button
		btn = (ImageButton) findViewById(R.id.stopCall);
		btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				// Bisogna terminare la chiamata
				TextView tc = (TextView) findViewById(R.id.callStatus);
				if (call.isOnCall()) {
					Log.v("CALL", "stopped call");
					tc.setText("Stopping call..");
					call.bye();
				} else {
					call.cancel();
					((EditText) findViewById(R.id.toUrl)).setText("");
					tc.setText("");
				}
			}
		});
	}

	/** Init SipStack, SipProvider and SDP */
	private void setSipStackParameters() {
		SipStack.init();
		
		if (checkNat())
			sp = new SipProvider(publicAddr.getHostAddress(), port);
		else
			sp = new SipProvider(null); // TODO funziona?
		
		Log.v("SP",sp.toString());
		userProfile = new UserAgentProfile();
		initSessionDescriptor();
		addMediaDescriptor("audio", userProfile.audio_port,
				userProfile.audio_avp, userProfile.audio_codec,
				userProfile.audio_sample_rate);
		firstTime=false;
	}
	
	private boolean checkNat() {
		try {
			setNatted(getNatStatus());
			setNATChecked(true);
			return true;
		} catch (Exception e) {
			setNATChecked(false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("STUN Error")
				.setMessage(e.getMessage())
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int id) {
						d.dismiss();
					}
			});
			builder.create().show();
			return false;
		} finally { publicAddr = st.getPublicAddress(); }

	}
	
	private boolean getNatStatus() throws MessageAttributeParsingException, UtilityException, 
	IOException, MessageHeaderException {
		
		
		boolean b = false;
		int times = 2;
	
		// TODO sub-optimal solution
		while(!b && (times-- > 0) ) {
			st = new VOIPDroidSTUN();
			
			st.sendReq();
			
			b = st.getReply();
		}
		
		return b;
	}
	
	// TODO thread?
//	private void setupRTP() {
//		try {
//			// TODO unmagic
//			vrtp = new VOIPDroidRTP(publicAddr, 16384, 16385);
//		} catch (SocketException e) {
//			Log.e("CALL", e.getMessage());
//		}
//	}

	/* "On" Methods */

	/**
	 * Sets Options on Menu, adding new menus and icons to the options
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		MenuItem menuS = menu.add(0, MENU_SETTINGS, 0, "Settings");
		menuS.setIcon(android.R.drawable.ic_menu_preferences);
		MenuItem menuC = menu.add(0, MENU_CONTACTS, 0, "Contacts");
		menuC.setIcon(R.drawable.contatto2);
		MenuItem menuR = menu.add(0, MENU_REGISTER, 0, "Login");
		menuR.setIcon(android.R.drawable.ic_input_add);
		MenuItem menuU = menu.add(0, MENU_UNREGISTER, 0, "Logout");
		menuU.setIcon(android.R.drawable.ic_delete);
		Log.v("MENU", "Create");
		return result;
	}

	/**
	 * When menu is opened set visible the login button if client is not
	 * connected, else show the disconnect button
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v("MENU", "Prepare");
		if (logged == 0 || logged == 2) {
			Log.v("MENU", "Prepare1");
			menu.findItem(MENU_REGISTER).setVisible(true);
			menu.findItem(MENU_UNREGISTER).setVisible(false);
		} else {
			menu.findItem(MENU_REGISTER).setVisible(false);
			menu.findItem(MENU_UNREGISTER).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/** Implement the behaviour of the menus. */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent myIntent = new Intent();

		switch (item.getItemId()) {
		// Open settings activity
		case MENU_SETTINGS:
			Log.v("MENU", "MARUZZELLA SETTINGS");
			mSettings = new SettingsDbAdapter(this);
			mSettings.open();
			myIntent.setClass(this, Settings.class);
			if (mRowIdSetting != null) {
				myIntent.putExtra(SettingsDbAdapter.KEY_ROWID, mRowIdSetting);
			}
			mSettings.close();
			// mSettings.close();
			this.startActivityForResult(myIntent, ACTIVITY_SETTINGS);
			break;
		// Open contacts activity
		case MENU_CONTACTS:
			Log.v("MENU", "CONTACTS");
			myIntent.setClass(this, Contacts.class);
			this.startActivityForResult(myIntent, ACTIVITY_CONTACTS);
			break;
		// Register to SIP proxy
		case MENU_REGISTER:
			Log.v("MENU", "REGISTER");
			setupRegister();
			break;
		// Unregister to SIP proxy
		case MENU_UNREGISTER:
			Log.v("MENU", "UNREGISTER");
			ra.unregister();
			registration = false;
			// this.myRefreshThread.stop();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/** Set what to do when contacts or settings activities finish */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();

		switch (requestCode) {
			case ACTIVITY_SETTINGS: {
				if (extras != null) {
					mSettings.open();
					mRowIdSetting = extras.getLong(SettingsDbAdapter.KEY_ROWID);
					setupSettings();
					if (register)
						setupRegister();
				}
				break;
			}
			case ACTIVITY_CONTACTS: {
				if (extras != null) {
					String sipAddress = extras
							.getString(ProfileDbAdapter.KEY_SIP_URI);
					EditText Text = (EditText) findViewById(R.id.toUrl);
					Text.setText(sipAddress);
				}
				break;
			}
		}
	}

	/* HANDLER */

	Handler VOIPDroidUpdateHandler = new Handler() {
		/** Gets called on every message that is received */
		// @Override
		public void handleMessage(Message msg) {
			TextView tv;
			switch (msg.what) {
			case SIPSTATE_CHANGED:
				tv = (TextView) findViewById(R.id.sipState);
				tv.setText(sipState);
				break;
			case CALLSTATE_CHANGED:
				tv = (TextView) findViewById(R.id.callStatus);
				tv.setText(tv.getText().toString() + " - " + callState);
				break;
			case INCOMING_CALL:
				Log.v("POPUP", "CIAOCIAO");
				setOnCall(RECEIVED);
				showPopUp(incomingCall);
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * Show a PopUp when a new incoming call is coming
	 * 
	 * @param String
	 *            incomingCall, the sender of the call
	 * */
	private void showPopUp(String incomingCall) {

		// Show a notification in the system
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		setDefault(Notification.DEFAULT_ALL, incomingCall);

		callListener.ring();
		// Create the popup dialog
		new AlertDialog.Builder(this).setIcon(android.R.drawable.star_big_on)
				.setMessage(incomingCall).setTitle(R.string.incomingCall)
				.setPositiveButton(R.string.incomingCallAccept,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Accettazione della chiamata
								Log.v("CALL", "ACCEPT");
								callListener.accept(VOIPDroid.incomingCall, sdp);
//								idlistener.accept(VOIPDroid.incomingCall, sdp);
//								dialog.dismiss();
							}
						}).setNegativeButton(R.string.incomingCallDecline,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Mandare il bye
								if (callListener.isOnCall()) {
									Log.v("CALL", "BYE");
									callListener.bye();
									dialog.dismiss();
								} else {
									Log.v("CALL", "REFUSE");
									callListener.refuse();
//									idlistener.refuse(603, SipResponses
//											.reasonOf(603));
									VOIPDroid.setCall(VOIPDroid.callListener, VOIPDroid.getSp(),VOIPDroid.getListener());
									VOIPDroid.getCallListener().listen();
									dialog.dismiss();
								}
							}
						}).show();
	}

	class secondCountDownRunner implements Runnable {
		// @Override
		public void run() {
			String previousState = sipState;
			String previousCallState = callState;
			String previousCall = incomingCall;
			while (!Thread.currentThread().isInterrupted()) {
				if (previousState != sipState) {
					previousState = sipState;
					Message m = new Message();
					m.what = SIPSTATE_CHANGED;
					VOIPDroid.this.VOIPDroidUpdateHandler.sendMessage(m);
				} else if (previousCallState != callState) {
					previousCallState = callState;
					Message m = new Message();
					m.what = CALLSTATE_CHANGED;
					Log.v("INVITE", "CHANGING CALL STATE to " + callState);
					VOIPDroid.this.VOIPDroidUpdateHandler.sendMessage(m);
				} else if (previousCall != incomingCall) {
					// A call happened!
					previousCall = incomingCall;
					Message m = new Message();
					m.what = INCOMING_CALL;
					VOIPDroid.this.VOIPDroidUpdateHandler.sendMessage(m);
				} else {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private void setDefault(int defaults, CharSequence incomingCall) {

		// This method sets the defaults on the notification before posting it.

		// This is who should be launched if the user selects our notification.
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, VOIPDroid.class), 0);

		CharSequence text = incomingCall;

		final Notification notification = new Notification(
				android.R.drawable.star_big_on, // the icon for the status bar
				text, // the text to display in the ticker
				5000); // the timestamp for the notification

		notification.setLatestEventInfo(this, // the context to use
				getText(R.string.incomingCall),
				// the title for the notification
				text, // the details to display in the notification
				contentIntent); // the contentIntent (see above)

		notification.defaults = defaults;

		mNotificationManager.notify(R.layout.main, notification); // notification
	}

	/* SETTERS AND GETTERS */
	/** Gets the local SDP */
	public static String getSessionDescriptor() {
		return local_session;
	}

	/** Sets the local SDP */
	public void setSessionDescriptor(String sdp) {
		local_session = sdp;
	}

	/** Inits the local SDP (no media spec) */
	public static void initSessionDescriptor() {
		SessionDescriptor sdp = new SessionDescriptor(getUsername(), getHost());

		local_session = sdp.toString();
	}

	/** Adds a media to the SDP */
	public static void addMediaDescriptor(String media, int port, int avp,
			String codec, int rate) {

		if (local_session == null) {
			initSessionDescriptor();
		}

		SessionDescriptor sdp = new SessionDescriptor(local_session);

		String attr_param = String.valueOf(avp);

		if (codec != null) {
			attr_param += " " + codec + "/" + rate;
		}
		sdp.addMedia(new MediaField(media, port, 0, "RTP/AVP", String
				.valueOf(avp)), new AttributeField("rtpmap", attr_param));

		local_session = sdp.toString();
	}
	
	// deprecated
//	class NatStatusChecker implements Runnable {
//
//		public void run() {
//			boolean natStatus = getNatStatus();
//			setNatted(natStatus);
//			if (natStatus) {
//				publicAddr = st.getPublicAddress();
////				sp.setViaAddress(public_address);
//				// TODO race condition ?
////				sp.halt();
////				sp = new VOIPDroidSipProvider(public_address, port);
//			}
//			// da qui in poi conosciamo l'ip pubblico
//			setNATChecked(true);
//			if (register)
//				setupRegister();
//		}		
//		
//	}
	


	/** Sets the sipState to state */
	public static void setSipState(String state) {
		sipState = state;
		Log.v("ACTIONS", "CHANGING TELEPHONE STATE TO " + state);
	}

	/** Sets the callState to state */
	public static void setCallState(String state) {
		callState = state;
		Log.v("ACTIONS", "CHANGING CALL STATE TO " + state);
	}

	/**
	 * Gets the nickname of the account
	 * 
	 * @return the nickname
	 * */
	public static String getNickname() {
		return nickname;
	}

	/**
	 * Sets the nickname of the account
	 * 
	 * @param nickname
	 *            the nickname to set
	 **/
	// TODO make static
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Gets the username of the account
	 * 
	 * @return the username
	 */
	public static String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the account
	 * 
	 * @param name the username to set
	 */
	public void setUsername(String name) {
		username = name;
	}

	/**
	 * Sets the password of the account
	 * 
	 * @param password
	 *            the account password
	 * */
	private void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the realm of the account
	 * 
	 * @return the realm
	 * */
	public String getRealm() {
		return realm;
	}

	/**
	 * Sets the realm of the account
	 * 
	 * @param the
	 *            realm of the account
	 * */
	public void setRealm(String realm) {
		this.realm = realm;
	}

	/**
	 * Gets the contact url
	 * 
	 * @return the contact url
	 * */
	public String getContact_url() {
		return contact_url;
	}

	/**
	 * Sets the contact url to contactUrl
	 * 
	 * @param contactUrl
	 *            the contact url to set
	 * */
	public static void setContact_url(String contactUrl) {
		contact_url = contactUrl;
	}

	/**
	 * Gets the SipProvider
	 * 
	 * @return the SipProvider
	 * */
	public static SipProvider getSp() {
		return sp;
	}

	/**
	 * Sets the SipProvider to sp
	 * 
	 * @param sp
	 *            the new SipProvider
	 * */
	public void setSp(SipProvider sp) {
		VOIPDroid.sp = sp;
	}

	/**
	 * Gets the UserAgentProfile
	 * 
	 * @return the User Agent Profile
	 * */
	public UserAgentProfile getUserProfile() {
		return userProfile;
	}

	/**
	 * Sets the UserAgentProfile to userProfile
	 * 
	 * @param profile
	 *            the user agent profile
	 */
	public void setUserProfile(UserAgentProfile profile) {
		userProfile = profile;
	}

	/**
	 * Returns the VOIPDroidListener
	 * 
	 * @return listener
	 */
	public static VOIPDroidListener getListener() {
		return listener;
	}

	/**
	 * Sets the VOIPDroidListener
	 * 
	 * @param listener
	 *            the new VOIPDroidListener
	 */
	public void setListener(VOIPDroidListener Vlistener) {
		listener = Vlistener;
	}
	
	public Call getCall() {
		return call;
	}
	
	public static void setCall(Call call, SipProvider sp, VOIPDroidListener cl) {
		call = new Call(sp,account,contact_url,cl);
		call.setLocalSessionDescriptor(local_session);
	}
	
	public static Call getCallListener() {
		return callListener;
	}
	
/*	*//**
	 * Returns the InviteDialog
	 * 
	 * @return id the Invite Dialog
	 *//*
	public InviteDialog getInviteDialog() {
		return id;
	}

	*//**
	 * Returns the InviteDialogListener
	 * 
	 * @return idlistener the Invite Dialog Listener
	 *//*
	public static InviteDialog getInviteDialogListener() {
		return idlistener;
	}

	*//**
	 * Creates a new InviteDialogListener
	 * 
	 * @param sp
	 *            the SipProvider
	 * @param idl
	 *            the InviteDialogListener
	 *//*
	public static void setInviteDialogListener(SipProvider sp,
			InviteDialogListener idl) {
		idlistener = new InviteDialog(sp, idl);
	}

	*//**
	 * Creates a new InviteDialog
	 * 
	 * @param sp
	 *            the SipProvider
	 * @param idl
	 *            the InviteDialogListener
	 *//*
	public static void setInviteDialog(SipProvider sp, InviteDialogListener idl) {
		VOIPDroid.id = new InviteDialog(sp, idl);
	}*/

	/**
	 * Gets the host address
	 * 
	 * @return host
	 */
	public static String getHost() {
		return host;
	}

	/**
	 * Sets the host address
	 * 
	 * @param hostAddress
	 */
	public static void setHost(String hostAddress) {
		host = hostAddress;
	}

	/**
	 * Gets the port address
	 * 
	 * @return port the port address
	 */
	public static int getPort() {
		return port;
	}

	/**
	 * Sets the port address
	 * 
	 * @param port
	 *            the port address
	 */
	public static void setPort(int port) {
		VOIPDroid.port = port;
	}

	/**
	 * Sets the incoming call state for the popup
	 * 
	 * @param caller
	 *            the address of the caller
	 * @param sdp
	 *            the SDP
	 */
	public static void incomingCall(NameAddress caller, String sdp) {
		// Show popup
		incomingCall = caller.toString();
		VOIPDroid.sdp = sdp;
		Log.v("POPUP", incomingCall);
	}

	/**
	 * Gets the onCall state
	 * 
	 * @return onCall
	 */
	public static int getOnCall() {
		return onCall;
	}

	/**
	 * Sets the onCall state
	 * 
	 * @param onCall
	 */
	public static void setOnCall(int onCall) {
		VOIPDroid.onCall = onCall;
	}

	public boolean isNATChecked() {
		return NATChecked;
	}

	private void setNATChecked(boolean b) {
		this.NATChecked = b;
	}
	
	public boolean isNatted() {
		return natted;
	}
	
	private void setNatted(boolean natStatus) {
		natted = natStatus;
	}
	
	public static String getStunServer() {
		return stunServer;
	}

/*	*//**
	 * Override the android's onKeyDown method Disconnect from client on Back
	 * key pressed
	 *//*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case (KeyEvent.KEYCODE_BACK): {
			ra.unregister();
			this.myRefreshThread.stop();
			break;
		}
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
		return true;
	}*/
}