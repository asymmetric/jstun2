package it.unibo.cs.voipdroid.tools;

import it.unibo.cs.voipdroid.Contacts;
import android.database.Cursor;
import android.util.Log;



public class ParsedContactDataSet {
	private String nick = null;
	private String jid = null;
	private String msg = null;
	private String md5 = null;
	private int err = 0;
	boolean b_nick = false;
	boolean b_jid = false;

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
		b_jid = true;
		if (b_nick == true) {
			Log.v("CONTACT", getNick());
/*			XMLContact.getProfile(getJid());
			ParsedProfileDataSet profile = XMLContact.getParsedProfile();
			String md5 = profile.getMd5();*/
			Cursor cCursor = Contacts.ContactmDbHelper.fetchName(getJid());
			Log.v("CURSOR", String.valueOf(cCursor.getCount()));
			/*Cursor pCursor = Contacts.ProfilemDbHelper.fetchMd5(ManageContact
					.SubStringJid(getJid()));
			Log.v("CURSOR", String.valueOf(pCursor.getCount()));
			String pMd5 = null;*/
			if (cCursor.getCount() == 0) {
				Contacts.ContactmDbHelper.createContact(getNick(), getJid());
//				createDbProfile(profile);
			} /*else if (pCursor.moveToFirst()) {
				pMd5 = pCursor.getString(pCursor
						.getColumnIndex(ProfileDbAdapter.KEY_MD5));
				Log.v("MD5", pMd5);
				if (!md5.equals(pMd5)) {
					Log.v("MD5", "DIVERSO");
					updateDbProfile(pCursor.getLong(pCursor
							.getColumnIndex(ProfileDbAdapter.KEY_ROWID)),
							profile);
				}
			} else if (pCursor.getCount() == 0){
				createDbProfile(profile);
			}*/
			cCursor.close();
			b_nick = false;
			b_jid = false;
		}
	}

/*	private void createDbProfile(ParsedProfileDataSet profile) {
		Contacts.ProfilemDbHelper.createProfile(profile.getFullName(), profile
				.getGizmoName(), profile.getCity(), profile.getState(), profile
				.getCountry(), profile.getHomepage_url(), profile.getSip_uri(),
				profile.getLanguage(), profile.getSex(), profile.getBirth(),
				profile.getDescription(), profile.getMd5());
	}

	private void updateDbProfile(Long rowId, ParsedProfileDataSet profile) {
		Contacts.ProfilemDbHelper.updateProfile(rowId, profile.getFullName(),
				profile.getGizmoName(), profile.getCity(), profile.getState(),
				profile.getCountry(), profile.getHomepage_url(), profile
						.getSip_uri(), profile.getLanguage(), profile.getSex(),
				profile.getBirth(), profile.getDescription(), profile.getMd5());
	}*/

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
		b_nick = true;
	}

	public void setErr(int err) {
		this.err = err;
		if (this.err != 0) {
			Log.v("CONTACTS", getMsg());
		} else
			Log.v("CONTACTS", "DONE");
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
	
	public String getMd5() {
		return this.md5;
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
