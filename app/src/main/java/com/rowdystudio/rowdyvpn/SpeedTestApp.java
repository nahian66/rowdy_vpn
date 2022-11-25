package com.rowdystudio.rowdyvpn;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * The type Speed test app.
 */
public class SpeedTestApp extends Application {
	/**
	 * The constant LOG_PREFIX.
	 */
	public static final String LOG_PREFIX = "_";
	/**
	 * The constant LOG_PREFIX_LENGTH.
	 */
	public static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length ();
	/**
	 * The constant MAX_LOG_TAG_LENGTH.
	 */
	public static final int MAX_LOG_TAG_LENGTH = 10;
	/**
	 * The constant LOGGING_ENABLED.
	 */
	public static final boolean LOGGING_ENABLED = true;

	@Override
	protected void attachBaseContext (Context base) {
		super.attachBaseContext (base);
		MultiDex.install (this);
	}

}
