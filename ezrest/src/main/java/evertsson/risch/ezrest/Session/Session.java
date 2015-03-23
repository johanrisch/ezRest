package com.evertsson.risch.ezRest.lib.Rest.Session;

import android.content.Context;
import android.content.SharedPreferences;

import evertsson.risch.ezrest.RestClient;

/**
 * Created by Simon Evertsson on 19/02/15.
 */
public class Session {
    /*
     * The singleton instance of the Session
     */
    private static Session instance;

    private static final String mSessionPrefs = "DELTASession";
    private static final String USERNAME_KEY = "USERNAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";

    private static final String REALM_KEY = "REALM_KEY";

    private static final String NONCE_KEY = "NONCE_KEY";

    private static final String OPAQUE_KEY = "OPAQUE_KEY";

    private static final String QOP_KEY = "QOP_KEY";

    private static final String NC_KEY = "NC_KEY";

    /* Empty constructor to prevent creation of multiple instances */
    private Session() {

    }

    /**
     * The method to get the singleton instance of this class.
     * @return The singleton instance of this class.
     */
    public static Session getInstance() {
        if(instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setSession(Context c, String username, String password, String realm) {
        setUsername(c, username);
        setPassword(c, password);
        setRealm(c, realm);
    }

    public void logout(Context c) {
        setUsername(c, null);
        setPassword(c, null);
        setRealm(c, null);
        RestClient.clearCookies();
    }

    private void putString(Context c, String key, String value) {
        SharedPreferences sharedpref = c.getSharedPreferences(mSessionPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedprefeditor = sharedpref.edit();
        sharedprefeditor.putString(key, value);
        sharedprefeditor.commit();
    }


    public String getString(Context c, String key) {
        SharedPreferences sharedpref = c.getSharedPreferences(mSessionPrefs, Context.MODE_PRIVATE);
        return sharedpref.getString(key, null);
    }

    public int getInt(Context c, String key) {
        SharedPreferences sharedpref = c.getSharedPreferences(mSessionPrefs, Context.MODE_PRIVATE);
        return sharedpref.getInt(key, 0);
    }

    private void putInt(Context c, String key, int value) {
        SharedPreferences sharedpref = c.getSharedPreferences(mSessionPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedprefeditor = sharedpref.edit();
        sharedprefeditor.putInt(key, value);
        sharedprefeditor.commit();
    }

    public String getUsername(Context c) {
        return getString(c, USERNAME_KEY);
    }

    public void setUsername(Context c, String username) {
        putString(c, USERNAME_KEY, username);
    }

    public String getPassword(Context c) {
        return getString(c, PASSWORD_KEY);
    }

    public void setPassword(Context c, String password) {
        putString(c, PASSWORD_KEY, password);
    }

    public void setRealm(Context c, String realm) {
        putString(c, REALM_KEY, realm);
    }

    public String getRealm(Context c) {
        return getString(c, REALM_KEY);
    }

//	public void setNonce(Context c, String nonce) {
//		putString(c, NONCE_KEY, nonce);
//	}
//
//	public String getNonce(Context c) {
//		return getString(c, NONCE_KEY);
//	}
//
//	public void setOpaque(Context c, String opaque) {
//		putString(c, OPAQUE_KEY, opaque);
//	}
//
//	public String getOpaque(Context c) {
//		return getString(c, OPAQUE_KEY);
//	}
//
//	public void setQop(Context c, String qop) {
//		putString(c, QOP_KEY, qop);
//	}
//
//	public String getQop(Context c) {
//		return getString(c, QOP_KEY);
//	}
//
//	public void setNc(Context c, int nc) {
//		putInt(c, NC_KEY, nc);
//	}
//
//	public int getNc(Context c) {
//		return getInt(c, NC_KEY);
//	}
}