package it.unibo.cs.voipdroid;

import it.unibo.cs.voipdroid.databases.ContactsDbAdapter;
import it.unibo.cs.voipdroid.databases.ProfileDbAdapter;
import it.unibo.cs.voipdroid.databases.SettingsDbAdapter;
import it.unibo.cs.voipdroid.tools.ManageString;
import it.unibo.cs.voipdroid.tools.ParsedProfileDataSet;
import it.unibo.cs.voipdroid.tools.XMLContact;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Contacts provide the layout for the list of contacts and the methods to 
 * manage them using Gizmo5 web services and the sqlite database.
 *
 */
public class Contacts extends ListActivity {

	private static final int CONTACT_CREATE = 0;
	private static final int CONTACT_EDIT = 1;

	private static final int MENU_INSERT = Menu.FIRST;
	private static final int MENU_EDIT = Menu.FIRST + 1;
	private static final int MENU_DELETE = Menu.FIRST + 2;
	private static final int MENU_PROFILE = Menu.FIRST + 3;

	public static ContactsDbAdapter ContactmDbHelper;
	public static ProfileDbAdapter ProfilemDbHelper;

	private String username = null;
	private String pwd = null;
	private static final int fillC = 0;
	private static final int profileShow = 1;

	public View profileView;

	/**
	 * Override onKey methods, onKeyUp allow to search contacts on key press
	 */
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
			// case (KeyEvent.KEYCODE_CLEAR): return super.onKeyUp(keyCode,
			// event);
			// case (KeyEvent.KEYCODE_DEL): return super.onKeyUp(keyCode,
			// event);
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
			// case (KeyEvent.KEYCODE_BACKSLASH): return super.onKeyUp(keyCode,
			// event);
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
		case (KeyEvent.META_ALT_LEFT_ON):
			return super.onKeyUp(keyCode, event);
		case (KeyEvent.META_ALT_RIGHT_ON):
			return super.onKeyUp(keyCode, event);
			// case (KeyEvent.META_SHIFT_LEFT_ON): return super.onKeyUp(keyCode,
			// event);
			// case (KeyEvent.META_SHIFT_ON): return super.onKeyUp(keyCode,
			// event);
		case (KeyEvent.META_SHIFT_RIGHT_ON):
			return super.onKeyUp(keyCode, event);
			// case (KeyEvent.META_SYM_ON): return super.onKeyUp(keyCode,
			// event);
		default: {
			EditText Text = (EditText) findViewById(R.id.searchName);
			String name = Text.getText().toString(); // get the string in the
			// edittext
			fillContacts(ContactmDbHelper.fetchName(name)); // query to the db
			// and show the
			// contacts that match the string
		}
		}
		return true;
	}

	/**
	 * Override onKey methods, onKeyDown override Back key to finish the
	 * contacts activity returning to the main activity
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case (KeyEvent.KEYCODE_BACK): {

			ContactmDbHelper.close(); // Close contact DB
			ProfilemDbHelper.close(); // Close Profile DB
			Bundle bundle = new Bundle();
			bundle.putString("", "");

			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);

			setResult(RESULT_CANCELED, mIntent); // Return an empty Intent to
			// the main activity
			finish();
		}
			;
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	/**
	 * Called when activity is firstly created. Creates the contacts layout
	 * (Contacts list) and opens the databases to update the contacts from the
	 * web services, or shows a popup if settings are missing.
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.contact_list); // set contacts layout

		Log.v("LISTACONTATTI", "CONTACTS");

		SettingsDbAdapter mSettings = new SettingsDbAdapter(this);
		mSettings.open(); // Open settings database to get username and password
		Cursor isSetting = mSettings.fetchAllSettings();
		if (isSetting.getCount() > 0) {
			Long row = new Long((long) 1);
			Cursor setting = mSettings.fetchSetting(row);
			startManagingCursor(setting);

			username = setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_USERNAME));
			pwd = setting.getString(setting
					.getColumnIndex(SettingsDbAdapter.KEY_PASSWORD));
		}
		mSettings.close();
		Log.v("LISTACONTATTI", "Loaded Settings");
		// If username is null show a popup to add a new setting in settings
		// activity,
		// else open contacts and profile db and load contacts
		if (username != null) {
			ContactmDbHelper = new ContactsDbAdapter(this, username);
			ContactmDbHelper.open();

			ProfilemDbHelper = new ProfileDbAdapter(this, username);
			ProfilemDbHelper.open();

			Log.v("CONTATTI", "OPENED DBs");

			// If it's the first time to contacts to be opened download contacts
			// from webservices
			// else load them from database
			if (VOIPDroid.firstContacts) {
				VOIPDroid.firstContacts = false;
				final ProgressDialog dialog = new ProgressDialog(this); // Simple
				// progress
				// dialog
				dialog.setMessage("Please wait while loading...");
				dialog.setIndeterminate(true);
				dialog.show();
				new Thread() {
					public void run() {

						boolean result = false;
						// fetch the md5 checksum of the contact list on DB
						Cursor contactDbMD5 = ProfilemDbHelper
								.fetchMd5("ListOfAllContacts");
						startManagingCursor(contactDbMD5);
						// get the list of contacts MD5 from the web and compare
						// to the
						// MD5 on DB
						result = XMLContact.getContactsMD5(username, pwd);
						String md5 = XMLContact.getParsedContactMD5();
						// if the contact is present
						if (contactDbMD5.moveToFirst()) {
							// If MD5 are different, update the md5 on DB
							// and get the new list,
							// else show it from DB.
							// If md5 from DB is not present (first time it's
							// loaded), get the list from
							// Web Service, add the md5 to the DB and show
							// the list
							if (!md5.equals(contactDbMD5.getString(contactDbMD5
									.getColumnIndex(ProfileDbAdapter.KEY_MD5)))) {
								Log.v("MD5", "DIFFERENT");
								if (result)
									updateDbProfile(
											contactDbMD5
													.getLong(contactDbMD5
															.getColumnIndex(ProfileDbAdapter.KEY_ROWID)),
											md5);
								result = XMLContact.getContacts(username, pwd);
							}
						} else {
							Log.v("GETCONTACT", "FROM WS");
							if (result) {
								Log.v("GETCONTACT", "MD5");
								ProfilemDbHelper.createProfile(
										"ListOfAllContacts",
										"ListOfAllContacts", null, null, null,
										null, "ListOfAllContacts", null, null,
										null, null, md5);
							}
							result = XMLContact.getContacts(username, pwd);
						}
						Log.v("GETCONTACT", "DONE");
						dialog.dismiss(); // dismiss the dialog
						Log.v("DISMISS", "DONE");

						if (result) {
							Message m = new Message();
							m.what = Contacts.fillC;
							// fill contacts to the list, sending a message to
							// the handler
							Contacts.this.myViewUpdateHandler.sendMessage(m);
						}
					}
				}.start();
			} else { // Load contacts from the database
				fillContacts(ContactmDbHelper.fetchAllContacts());
			}
		} else { // open the popup if no account present
			Log.v("LISTACONTATTI", "ELSE");
			new AlertDialog.Builder(this).setIcon(
					android.R.drawable.star_big_on)
					.setTitle(R.string.noAccount).setMessage(
							R.string.noAccountMessage).setPositiveButton(
							R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									Bundle bundle = new Bundle();
									bundle.putString("", "");
									Intent mIntent = new Intent();
									mIntent.putExtras(bundle);

									setResult(RESULT_CANCELED, mIntent);
									finish();
								}
							}).show();
		}
	}

	/**
	 * Sets Options on Menu, adding new menus and icons to the options This
	 * options are : Insert contact Edit contact Delete contact Show profile
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		MenuItem menuInsert = menu.add(0, MENU_INSERT, 0, "Insert Contact");
		menuInsert.setIcon(R.drawable.contatto_in);
		MenuItem menuEdit = menu.add(0, MENU_EDIT, 0, "Edit Contact");
		menuEdit.setIcon(R.drawable.contatto_edit);
		MenuItem menuDel = menu.add(0, MENU_DELETE, 0, "Delete Contact");
		menuDel.setIcon(R.drawable.contatto_out);
		MenuItem menuProfile = menu.add(0, MENU_PROFILE, 0,
				"Get Contact Profile");
		menuProfile.setIcon(R.drawable.contatto_profile);
		return result;
	}

	/**
	 * Implement the behaviour of menus. If add contact menu is selected, go to
	 * ContactEdit activity with no data. If edit contact menu is selected, go to
	 * ContactEdit activity with selected contact data. If delete contact is
	 * selected, remove the contact from the database and from the web service
	 * If show profile is selected, show the profile of the contact, from db is
	 * present, else from web service
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		// Add contact selected
		case MENU_INSERT:
			createContact(); // Opens activity ContactEdit to a new contact
			return true;
			// Edit contact selected
			// Opens activity ContactEdit, loading the selected contact data
		case MENU_EDIT:
			if (getListView().getSelectedItemId() != ListView.INVALID_ROW_ID) {
				Intent i = new Intent(this, ContactEdit.class);
				i.putExtra(ContactsDbAdapter.KEY_ROWID, getListView()
						.getSelectedItemId());
				startActivityForResult(i, CONTACT_EDIT);
				return true;
			}
			// Delete contact selected
		case MENU_DELETE:
			if (getListView().getSelectedItemId() != ListView.INVALID_ROW_ID) {
				Cursor contact = ContactmDbHelper.fetchContact(getListView()
						.getSelectedItemId());
				startManagingCursor(contact); // fetch the contact and get it's
				// jid
				final String jid = contact.getString(contact
						.getColumnIndex(ContactsDbAdapter.KEY_JID));
				Log.v("REMOVE", jid);
				final ProgressDialog dialog = new ProgressDialog(this); // simple
				// progress
				// dialog
				dialog.setMessage("Please wait while loading...");
				dialog.setIndeterminate(true);
				dialog.show();
				new Thread() {
					public void run() {
						XMLContact.removeContact(username, pwd, jid); // remove with
																	// web service
						ContactmDbHelper.deleteContact(getListView()
								.getSelectedItemId());  // remove contact
														// from DB
						Cursor profile = ProfilemDbHelper
								.fetchName(ManageString.SubStringJid(jid)); // fetch the
																			// profile
																			// of the contact
																			// from DB
						startManagingCursor(profile);
						Log.v("DeleteProfile", String.valueOf(profile
								.getCount()));
						if (profile.moveToFirst()) {
							Long row = profile
									.getLong(profile
											.getColumnIndex(ProfileDbAdapter.KEY_ROWID));
							ProfilemDbHelper.deleteProfile(row); // remove its
																// profile from DB
						}
						
						// Update the MD5 of the contacts list
						Cursor contactDbMD5 = ProfilemDbHelper
								.fetchMd5("ListOfAllContacts");
						startManagingCursor(contactDbMD5);
						boolean result = XMLContact.getContactsMD5(username, pwd);
						String md5 = XMLContact.getParsedContactMD5();
						// if the contact is present
						if (contactDbMD5.moveToFirst()) {
							if (result)
								updateDbProfile(
										contactDbMD5
												.getLong(contactDbMD5
														.getColumnIndex(ProfileDbAdapter.KEY_ROWID)),
										md5);
						} else {
							if (result)
								ProfilemDbHelper.createProfile("ListOfAllContacts",
										"ListOfAllContacts", null, null, null,
										null, "ListOfAllContacts", null, null,
										null, null, md5);
						}
						
						dialog.dismiss();
						Log.v("DISMISS", "DONE");

						Message m = new Message();
						m.what = Contacts.fillC;
						Contacts.this.myViewUpdateHandler.sendMessage(m); // reload
						// contact
						// list
					}
				}.start();
				return true;
			}
			// show profile selected
		case MENU_PROFILE:
			if (getListView().getSelectedItemId() != ListView.INVALID_ROW_ID) {
				Cursor contact = ContactmDbHelper.fetchContact(getListView()
						.getSelectedItemId());
				startManagingCursor(contact); // fetch the contact and get it's
				// jid
				final String jid = contact.getString(contact
						.getColumnIndex(ContactsDbAdapter.KEY_JID));
				Log.v("PROFILE", jid);

				// Create a new layout inflater to show the profile, from DB if
				// present
				// else from web service
				LayoutInflater factory = LayoutInflater.from(this);
				profileView = factory.inflate(R.layout.alert_dialog_profile,
						null);
				// New progress dialog
				final ProgressDialog dialog = new ProgressDialog(this);
				dialog
						.setMessage("Please wait while retrieving profile informations...");
				dialog.setIndeterminate(true);
				dialog.show();
				new Thread() {
					public void run() {
						// fetch profile
						Cursor profileCursor = ProfilemDbHelper
								.fetchName(ManageString.SubStringJid(jid));
						// if the profile is present
						if (profileCursor.moveToFirst()) {
							// get profile MD5 from the web and compare to the
							// MD5 on DB
							XMLContact.getProfileMD5(username, pwd, jid);
							ParsedProfileDataSet profileMd5 = XMLContact
									.getParsedProfile();
							String md5 = profileMd5.getMd5();
							// If MD5 are different, update the profile on DB
							// and show it,
							// else get it from DB and show it.
							// If md5 from DB is not present (first time it's
							// loaded), get profile from
							// Web Service, add a new entry to the DB and show
							// it
							if (!md5
									.equals(profileCursor
											.getString(profileCursor
													.getColumnIndex(ProfileDbAdapter.KEY_MD5)))) {
								Log.v("MD5", "DIFFERENT");
								XMLContact.getProfile(username, pwd, jid);
								ParsedProfileDataSet profile = XMLContact
										.getParsedProfile();
								createProfileView(profileView, profile);
								updateDbProfile(
										profileCursor
												.getLong(profileCursor
														.getColumnIndex(ProfileDbAdapter.KEY_ROWID)),
										profile);
							} else {
								Log.v("PROFILE", "FROM DB");
								createProfileView(profileView, profileCursor);
							}
						} else {
							Log.v("PROFILO", "FROM WS");
							XMLContact.getProfile(username, pwd, jid);
							ParsedProfileDataSet profile = XMLContact
									.getParsedProfile();
							createProfileView(profileView, profile);
							createDbProfile(profile);
						}

						dialog.dismiss();
						Message m = new Message();
						m.what = Contacts.profileShow;
						Contacts.this.myViewUpdateHandler.sendMessage(m);
					}
				}.start();

				return true;
			}
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Fill the contacts list with contacts in the cursor from the DB
	 * 
	 * @param contact
	 *            Cursor that contains the contacts to show
	 */
	private void fillContacts(Cursor contact) {
		// Create the item list
		startManagingCursor(contact);

		String[] from = new String[] { ContactsDbAdapter.KEY_NAME, ContactsDbAdapter.KEY_JID };
		int[] to = new int[] { R.id.text1, R.id.text2 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter contactAdapter = new SimpleCursorAdapter(this,
				R.layout.contact_row, contact, from, to);
		setListAdapter(contactAdapter);
	}

	/**
	 * Show a popup with the profile using profileView, created from DB or Web
	 */
	private void showProfile() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.star_big_on)
				.setTitle(R.string.profile).setView(profileView)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								/* User clicked OK so do some stuff */
							}
						}).show();
	}

	/**
	 * Opens the activity to add a new contact
	 */
	private void createContact() {
		Intent i = new Intent(this, ContactEdit.class);
		startActivityForResult(i, CONTACT_CREATE);
	}

	/**
	 * Overridden OnListItemClick() method to send to the main activity the sip
	 * uri of the contact selected in the list. Sip_uri is present in the
	 * profile, so if we have profile's contact already in DB, we get it from
	 * there, else we get the profile from the web service.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor contactCursor = ContactmDbHelper.fetchAllContacts();
		contactCursor.moveToPosition(position);
		final String jid = contactCursor.getString(contactCursor
				.getColumnIndex(ContactsDbAdapter.KEY_JID));
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Please wait while retrieving sip uri...");
		dialog.setIndeterminate(true);
		dialog.show();
		new Thread() {
			public void run() {
				// Fetch the profile in the DB
				Cursor profileCursor = ProfilemDbHelper.fetchName(ManageString
						.SubStringJid(jid));
				Intent mIntent = new Intent();
				Bundle bundle = new Bundle();
				// If present get the string and add it to the intent
				if (profileCursor.moveToFirst()) {
					bundle
							.putString(
									ProfileDbAdapter.KEY_SIP_URI,
									profileCursor
											.getString(profileCursor
													.getColumnIndex(ProfileDbAdapter.KEY_SIP_URI)));
					Log.v("SIPURI", profileCursor.getString(profileCursor
							.getColumnIndex(ProfileDbAdapter.KEY_SIP_URI)));
				} else {
					// Else get profile from the web, add a new entry to the db
					// and add it to the intent
					XMLContact.getProfile(username, pwd, jid);
					ParsedProfileDataSet profile = XMLContact
							.getParsedProfile();
					createDbProfile(profile);
					bundle.putString(ProfileDbAdapter.KEY_SIP_URI, profile
							.getSip_uri());
				}
				ContactmDbHelper.close(); // Close databases
				ProfilemDbHelper.close();

				dialog.dismiss(); // Dismiss dialog

				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent); // Finish activity
				finish();
			}
		}.start();
	}

	/**
	 * Overridden method to reload contacts when a new contact has been created
	 * or a selected contact has been edited
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (resultCode) {
		case RESULT_OK: {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("Please wait while loading...");
			dialog.setIndeterminate(true);
			dialog.show();
			new Thread() {
				public void run() {
					// fetch the md5 checksum of the contact list on DB
					Cursor contactDbMD5 = ProfilemDbHelper
							.fetchMd5("ListOfAllContacts");
					startManagingCursor(contactDbMD5);
					boolean result = XMLContact.getContactsMD5(username, pwd);
					String md5 = XMLContact.getParsedContactMD5();
					// if the contact is present
					if (contactDbMD5.moveToFirst()) {
						if (result)
							updateDbProfile(
									contactDbMD5
											.getLong(contactDbMD5
													.getColumnIndex(ProfileDbAdapter.KEY_ROWID)),
									md5);
					} else {
						if (result)
							ProfilemDbHelper.createProfile("ListOfAllContacts",
									"ListOfAllContacts", null, null, null,
									null, "ListOfAllContacts", null, null,
									null, null, md5);
					}

					dialog.dismiss();

					Message m = new Message();
					m.what = Contacts.fillC;
					// fill contacts to the list, sending a message to
					// the handler
					Contacts.this.myViewUpdateHandler.sendMessage(m);
					Log.v("FILLCONTACTS", "ACTIVITY RESULT");
				}
			}.start();
			Log.v("FILLCONTACTS", "FUORI THREAD");
			break;
		}
		default: {
			Log.v("FILLCONTACTS", "DEFAULT");
			fillContacts(ContactmDbHelper.fetchAllContacts());
		}
		}

	}

	/**
	 * Create the profile view that has to be showed in the popup from the
	 * database
	 * 
	 * @param view
	 *            the profileView passed
	 * @param profile
	 *            the Cursor with profile data
	 * @return profileView the profile view to be showed
	 * 
	 *         Some of the textviews can be unvisible if set to null
	 */
	public View createProfileView(View view, Cursor profile) {
		startManagingCursor(profile);
		TextView fullname = (TextView) view.findViewById(R.id.full_name);
		TextView gizmoname = (TextView) view.findViewById(R.id.gizmo_name);
		TextView state = (TextView) view.findViewById(R.id.state);
		TextView city = (TextView) view.findViewById(R.id.city);
		TextView country = (TextView) view.findViewById(R.id.country);
		TextView homepage_url = (TextView) view.findViewById(R.id.homepage);
		TextView sip_uri = (TextView) view.findViewById(R.id.sip_uri);
		TextView language = (TextView) view.findViewById(R.id.language);
		TextView languageT = (TextView) view.findViewById(R.id.languageT);
		TextView sex = (TextView) view.findViewById(R.id.sex);
		TextView sexT = (TextView) view.findViewById(R.id.sexT);
		TextView birth = (TextView) view.findViewById(R.id.birth);
		TextView birthT = (TextView) view.findViewById(R.id.birthT);
		TextView description = (TextView) view.findViewById(R.id.description);
		TextView descriptionT = (TextView) view.findViewById(R.id.descriptionT);
		if (profile.moveToFirst()) {
			fullname.setText(profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_FULL)));
			gizmoname.setText("("
					+ profile.getString(profile
							.getColumnIndex(ProfileDbAdapter.KEY_GIZMO)) + ")");
			String stateString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_STATE));
			String cityString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_CITY));
			String countryString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_COUNTRY));
			String homepageString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_HOME));
			String languageString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_LANGUAGE));
			String sexString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_SEX));
			String birthString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_BIRTH));
			String descString = profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_DESCRIPTION));
			if (stateString != null)
				state.setText(stateString + ",");
			if (cityString != null)
				city.setText(cityString + ",");
			if (countryString != null)
				country.setText(countryString);
			if (homepageString != null)
				homepage_url.setText(homepageString);
			sip_uri.setText(profile.getString(profile
					.getColumnIndex(ProfileDbAdapter.KEY_SIP_URI)));
			if (languageString != null)
				language.setText(languageString);
			else {
				language.setVisibility(View.GONE);
				languageT.setVisibility(View.GONE);
			}
			if (sexString != null)
				sex.setText(sexString);
			else {
				sex.setVisibility(View.GONE);
				sexT.setVisibility(View.GONE);
			}
			if (birthString != null)
				birth.setText(birthString);
			else {
				birth.setVisibility(View.GONE);
				birthT.setVisibility(View.GONE);
			}
			if (descString != null)
				description.setText(descString);
			else {
				description.setVisibility(View.GONE);
				descriptionT.setVisibility(View.GONE);
			}
		}
		return view;
	}

	/**
	 * Create the profile view that has to be showed in the popup from the Web
	 * Services
	 * 
	 * @param view
	 *            the profileView passed
	 * @param profile
	 *            the parsed data got from the web service
	 * @return profileView the profile view to be showed
	 * 
	 *         Some of the textviews can be unvisible if set to null
	 */
	public static View createProfileView(View view, ParsedProfileDataSet profile) {
		TextView fullname = (TextView) view.findViewById(R.id.full_name);
		TextView gizmoname = (TextView) view.findViewById(R.id.gizmo_name);
		TextView state = (TextView) view.findViewById(R.id.state);
		TextView city = (TextView) view.findViewById(R.id.city);
		TextView country = (TextView) view.findViewById(R.id.country);
		TextView homepage_url = (TextView) view.findViewById(R.id.homepage);
		TextView sip_uri = (TextView) view.findViewById(R.id.sip_uri);
		TextView language = (TextView) view.findViewById(R.id.language);
		TextView languageT = (TextView) view.findViewById(R.id.languageT);
		TextView sex = (TextView) view.findViewById(R.id.sex);
		TextView sexT = (TextView) view.findViewById(R.id.sexT);
		TextView birth = (TextView) view.findViewById(R.id.birth);
		TextView birthT = (TextView) view.findViewById(R.id.birthT);
		TextView description = (TextView) view.findViewById(R.id.description);
		TextView descriptionT = (TextView) view.findViewById(R.id.descriptionT);

		fullname.setText(profile.getFullName());
		gizmoname.setText("(" + profile.getGizmoName() + ")");
		if (profile.getState() != null)
			state.setText(profile.getState() + ",");
		if (profile.getCity() != null)
			city.setText(profile.getCity() + ",");
		if (profile.getCountry() != null)
			country.setText(profile.getCountry());
		if (profile.getHomepage_url() != null)
			homepage_url.setText(profile.getHomepage_url());
		sip_uri.setText(profile.getSip_uri());
		if (profile.getLanguage() != null)
			language.setText(profile.getLanguage());
		else {
			language.setVisibility(View.GONE);
			languageT.setVisibility(View.GONE);
		}
		if (profile.getSex() != null)
			sex.setText(profile.getSex());
		else {
			sex.setVisibility(View.GONE);
			sexT.setVisibility(View.GONE);
		}
		if (profile.getBirth() != null)
			birth.setText(profile.getBirth());
		else {
			birth.setVisibility(View.GONE);
			birthT.setVisibility(View.GONE);
		}
		if (profile.getDescription() != null)
			description.setText(profile.getDescription());
		else {
			description.setVisibility(View.GONE);
			descriptionT.setVisibility(View.GONE);
		}
		return view;
	}

	/**
	 * Handler to handle messages for reloading the contact list or for showing
	 * the profile of the selected contact.
	 */
	Handler myViewUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contacts.fillC:
				fillContacts(ContactmDbHelper.fetchAllContacts());
				break;
			case Contacts.profileShow:
				showProfile();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Method to create a new profile in the database from the parsed data of
	 * the web service
	 * 
	 * @param profile
	 *            the parsed data set
	 */
	private void createDbProfile(ParsedProfileDataSet profile) {
		ProfilemDbHelper.createProfile(profile.getFullName(), profile
				.getGizmoName(), profile.getCity(), profile.getState(), profile
				.getCountry(), profile.getHomepage_url(), profile.getSip_uri(),
				profile.getLanguage(), profile.getSex(), profile.getBirth(),
				profile.getDescription(), profile.getMd5());
	}

	/**
	 * Method to update the profile in the database from the parsed data of the
	 * web service
	 * 
	 * @param profile
	 *            the parsed data set
	 */
	private void updateDbProfile(Long rowId, ParsedProfileDataSet profile) {
		ProfilemDbHelper.updateProfile(rowId, profile.getFullName(), profile
				.getGizmoName(), profile.getCity(), profile.getState(), profile
				.getCountry(), profile.getHomepage_url(), profile.getSip_uri(),
				profile.getLanguage(), profile.getSex(), profile.getBirth(),
				profile.getDescription(), profile.getMd5());
	}

	/**
	 * Method to update the profile in the database from the parsed data of the
	 * web service
	 * 
	 * @param profile
	 *            the parsed data set
	 */
	private void updateDbProfile(Long rowId, String md5) {
		ProfilemDbHelper.updateProfile(rowId, "ListOfAllContacts",
				"ListOfAllContacts", null, null, null, null,
				"ListOfAllContacts", null, null, null, null, md5);
	}
}
