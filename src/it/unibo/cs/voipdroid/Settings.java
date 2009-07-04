package it.unibo.cs.voipdroid;

import it.unibo.cs.voipdroid.databases.SettingsDbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Settings provide the layout and methods to save and manage the sip account using 
 * the sqlite database.
 *
 */
public class Settings extends Activity {

	private EditText mNicknameSetting;
	private EditText mUsernameSetting;
	private EditText mPasswordSetting;
	private EditText mRegistrarSetting;
	private CheckBox mCheck;
	private Long mRowId;
	private SettingsDbAdapter mDbHelper;
	private static final int MENU_CREATE = Menu.FIRST;
	private static final int ACTIVITY_CREATE = 0;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mRowId != null) outState.putLong(SettingsDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	/**
	 * Called on activity pause, this method saves the state of the activity, 
	 * it gets the entered text (nickname,username,password, registar and register on startup)
	 * and it creates or modifies the setting as well.
	 * 
	 */
	private void saveState() {
		String nickname = mNicknameSetting.getText().toString();
		String username = mUsernameSetting.getText().toString();
		String password = mPasswordSetting.getText().toString();
		String registrar = mRegistrarSetting.getText().toString();
		String checkreg = String.valueOf(mCheck.isChecked());

		//If the database is empty
		if (mRowId == null) {
			long id = mDbHelper.createSetting(nickname, username, password,
					registrar, checkreg);
			if (id > 0)
				mRowId = id;
		} else {
			mDbHelper.updateSetting(mRowId, nickname, username, password,
					registrar, checkreg);
		}
	}

	/**
	 * Called on activity resume, this method reloads the previously 
	 * saved informations in the layout.
	 */
	private void populateFields() {
		if (mRowId != null) {
			Log.v("MROW", "EXIST!");
			Cursor settingCursor = mDbHelper.fetchSetting(mRowId);
			startManagingCursor(settingCursor);

			mNicknameSetting.setText(settingCursor.getString(settingCursor
					.getColumnIndex(SettingsDbAdapter.KEY_NICKNAME)));

			mUsernameSetting.setText(settingCursor.getString(settingCursor
					.getColumnIndex(SettingsDbAdapter.KEY_USERNAME)));

			mPasswordSetting.setText(settingCursor.getString(settingCursor
					.getColumnIndex(SettingsDbAdapter.KEY_PASSWORD)));

			mRegistrarSetting.setText(settingCursor.getString(settingCursor
					.getColumnIndex(SettingsDbAdapter.KEY_REGISTRAR)));

			mCheck.setChecked(Boolean.valueOf(settingCursor
					.getString(settingCursor
							.getColumnIndex(SettingsDbAdapter.KEY_CHECKREG))));
		}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mDbHelper = new SettingsDbAdapter(this);
		mDbHelper.open();

		this.setContentView(R.layout.settings);			//Load the settings layout and views

		mNicknameSetting = (EditText) findViewById(R.id.nickname);
		mUsernameSetting = (EditText) findViewById(R.id.username);
		mPasswordSetting = (EditText) findViewById(R.id.password);
		mRegistrarSetting = (EditText) findViewById(R.id.registrar);
		mCheck = (CheckBox) findViewById(R.id.checkRegister);

		Button saveButton = (Button) findViewById(R.id.saveSettings);

		mRowId = icicle != null ? icicle.getLong(SettingsDbAdapter.KEY_ROWID)
				: null;
		// If an intent extra is present, get it 
		// and show the corresponding RowId of the setting
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(SettingsDbAdapter.KEY_ROWID) : null;
		}else{
			Cursor setting = mDbHelper.fetchAllSettings();
			startManagingCursor(setting);
			if (setting.getCount() > 0)
			{
				mRowId = (long) 1;
				}
		}
		
		populateFields();

		// Sets save button behaviour
		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Bundle bundle = new Bundle();

//				saveState();
				if (mRowId != null) {
					bundle.putLong(SettingsDbAdapter.KEY_ROWID, mRowId);
				}
//				mDbHelper.close();
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
	}

	/**
	 * Override onKeyDown methods for the back button
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case (KeyEvent.KEYCODE_BACK): {
//			mDbHelper.close();
			Intent mIntent = new Intent();
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}
			;
		default: {
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	/**
	 * Sets the "create new account" menu option 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		MenuItem menuCreate = menu.add(0, MENU_CREATE, 0, "Create new account");
		menuCreate.setIcon(R.drawable.contatto_crea);
		return result;
	}

	/**
	 * Sets the behaviour of the new menu options, starting the new activity
	 * CreateAccount for the creation of a new sip account
	 * 
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent myIntent = new Intent();

		switch (item.getItemId()) {
		case MENU_CREATE:
			Log.v("SETTINGS", "CREATE ACCOUNT");
			myIntent.setClass(this, CreateAccount.class);
			this.startActivityForResult(myIntent, ACTIVITY_CREATE);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * When the activity CreateAccount returns a new account settings,
	 * this method set it in the layout as the new account setting.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();

		switch (requestCode) {
		case ACTIVITY_CREATE: {
			if (extras != null) {
				String[] settings = null;
				settings = extras.getStringArray("Settings");
				mUsernameSetting.setText(settings[0]);
				mRegistrarSetting.setText(settings[1]);
			}
		}
		}
	}

}
