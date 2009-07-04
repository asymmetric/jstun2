package it.unibo.cs.voipdroid;

import it.unibo.cs.voipdroid.databases.ContactsDbAdapter;
import it.unibo.cs.voipdroid.databases.SettingsDbAdapter;
import it.unibo.cs.voipdroid.tools.XMLContact;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * ContactEdit provide the layout to add or edit a contact, using either Gizmo5
 * web services and sqlite database.
 *
 *Needs update for web services (update contact)
 */
public class ContactEdit extends Activity{
	
	private EditText mNameContact;
	private EditText mSipAddressContact;
	private Long mRowId;
	private ContactsDbAdapter mDbHelper;
	private String username = null;
	private String pwd = null;
	private boolean save = false;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putLong(ContactsDbAdapter.KEY_ROWID, mRowId);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        if (save)saveState();
	}

	/**
	 * Called on activity pause, this method saves the state of the activity, 
	 * it gets the entered text and it creates or modifies the contact as well.
	 * 
	 */
	private void saveState() {
		String name = mNameContact.getText().toString();
		String sipAddress = mSipAddressContact.getText().toString();
		
		if (mRowId == null) {
			long id = mDbHelper.createContact(name, sipAddress);
			if (id > 0 ) mRowId = id;
			
			XMLContact.addContact(username, pwd, sipAddress, name);
		}else{
			Cursor contact = mDbHelper.fetchContact(mRowId);
			String jid = contact.getString(contact.getColumnIndex(ContactsDbAdapter.KEY_JID));
			XMLContact.removeContact(username, pwd, jid);
			XMLContact.addContact(username, pwd, sipAddress, name);
			
			mDbHelper.updateContact(mRowId, name, sipAddress);
		}
	}
	
	@Override
	protected void onResume() { 
		super.onResume();
		populateFields();
	}

	/**
	 * Called on activity resume, this method reloads the previously 
	 * saved informations in the layout, or the contact selected to edit.
	 */
	private void populateFields() {
		if (mRowId != null) {
			Cursor contactCursor = mDbHelper.fetchContact(mRowId);
			startManagingCursor(contactCursor);
			
			mNameContact.setText(contactCursor.getString
					(contactCursor.getColumnIndex
							(ContactsDbAdapter.KEY_NAME)));
			
			mSipAddressContact.setText(contactCursor.getString
					(contactCursor.getColumnIndex
							(ContactsDbAdapter.KEY_JID)));
		}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	
		SettingsDbAdapter mSettings = new SettingsDbAdapter(this);
		mSettings.open();
		Cursor isSetting = mSettings.fetchAllSettings();
		if (isSetting.getCount() > 0) {					//Load the account setting to open
			Long row = new Long((long) 1);				// contacts database
			Cursor setting = mSettings.fetchSetting(row);
			startManagingCursor(setting);
			
			username = setting.getString(
					setting.getColumnIndex(SettingsDbAdapter.KEY_USERNAME));
			pwd = setting.getString(
					setting.getColumnIndex(SettingsDbAdapter.KEY_PASSWORD));
		}
		mSettings.close();
		
		mDbHelper = new ContactsDbAdapter(this, username);
		mDbHelper.open();
		
		setContentView(R.layout.contact_edit);
				
		mNameContact = (EditText) findViewById(R.id.name);
		mSipAddressContact = (EditText) findViewById(R.id.address);
		
		Button saveButton = (Button) findViewById(R.id.save);
		// If an intent extra is present, get it and show the corresponding RowId of the contact
        mRowId = icicle != null ? icicle.getLong(ContactsDbAdapter.KEY_ROWID) : null;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();            
            mRowId = extras != null ? extras.getLong(ContactsDbAdapter.KEY_ROWID) : null;
        }
        
        populateFields();
        
        // Sets layout buttons (Save and cancel) behaviours.
        saveButton.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
            	save=true;
            	setResult(RESULT_OK);
                finish();
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		setResult(RESULT_CANCELED);
        		finish();
        	}
        });
	}
}
