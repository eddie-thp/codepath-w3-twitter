package org.ethp.codepath.twitterclient;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TwitterApplicationDatabase.NAME, version = TwitterApplicationDatabase.VERSION)
public class TwitterApplicationDatabase {

    public static final String NAME = "TwitterApplicationDatabase";

    public static final int VERSION = 1;
}
