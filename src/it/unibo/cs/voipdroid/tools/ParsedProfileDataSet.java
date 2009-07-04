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
package it.unibo.cs.voipdroid.tools;

import android.util.Log;

public class ParsedProfileDataSet {

	private String state = null;
	private String city = null;
	private String country = null;
	private String homepage_url = null;
	private String sip_uri = null;
	private String language = null;
	private String sex = null;
	private String birth = null;
	private String description = null;
	private String fullname = null;
	private String gizmoname = null;
	private String md5 = null;
	private String msg = null;
	private int err = 0;

	public String getGizmoName() {
		return gizmoname;
	}

	public void setGizmoName(String name) {
		this.gizmoname = name;
	}

	public String getFullName() {
		return fullname;
	}

	public void setFullName(String name) {
		this.fullname = name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHomepage_url() {
		return homepage_url;
	}

	public void setHomepage_url(String homepage_url) {
		this.homepage_url = homepage_url;
	}

	public String getSip_uri() {
		return sip_uri;
	}

	public void setSip_uri(String sip_uri) {
		this.sip_uri = sip_uri;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5=md5;
	}
	
	public void setErr(int err) {
		this.err = err;
		if (this.err != 0) {
			Log.v("PROFILE", getMsg());
		} else
			Log.v("PROFILE", "DONE");
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}
}
