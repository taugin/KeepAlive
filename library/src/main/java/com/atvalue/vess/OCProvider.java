package com.atvalue.vess;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class OCProvider extends ContentProvider {
    public Bundle call(String str, String str2, Bundle bundle) {
        return super.call(str, str2, bundle);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onContentProviderCreate() {
        return false;
    }

    public boolean onCreate() {
        return onContentProviderCreate();
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}