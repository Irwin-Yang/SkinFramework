package com.irwin.skin;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by Irwin on 2015/11/16.
 */
public abstract class AbstractSPProvider {

    public abstract SharedPreferences getDB();

    /**
     * Initialize shared preferences.
     *
     * @param context
     * @param name    Desired preferences file. If a preferences file by this name does not
     *                exist, it will be created when you retrieve an editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).
     * @param mode    Operating mode. Use 0 or MODE_PRIVATE for the default operation,
     *                MODE_WORLD_READABLE and MODE_WORLD_WRITEABLE to control permissions.
     * @return
     */
    protected SharedPreferences initDB(Context context, String name, int mode) {
        return context.getSharedPreferences(name, mode);
    }

    /**
     * Convenience method for saving key\value and applying.
     * Only support basic types and String set.
     *
     * @param key
     * @param value
     */
    public void putApply(String key, Object value) {
        SharedPreferences.Editor editor = getDB().edit();
        put(editor, key, value);
        editor.apply();
    }

    /**
     * Do not use this method directly for it does not commit the operation.
     *
     * @param editor
     * @param key
     * @param value
     */
    void put(SharedPreferences.Editor editor, String key, Object value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        } else {
            throw new UnsupportedOperationException("Unsupported type!");
        }
    }

    /**
     * Convenience method for saving key\value and committing.Only support basic types and String set.
     *
     * @param key
     * @param value
     */
    public void putCommit(String key, Object value) {
        SharedPreferences.Editor editor = getDB().edit();
        put(editor, key, value);
        editor.commit();
    }

    /**
     * Retrieve all values from the preferences.
     * <p/>
     * <p>Note that you <em>must not</em> modify the collection returned
     * by this method, or alter any of its contents.  The consistency of your
     * stored data is not guaranteed if you do.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     * @throws NullPointerException
     */
    public Map<String, ?> getAll() {
        return getDB().getAll();
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * @throws ClassCastException
     */
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        return getDB().getString(key, defValue);
    }

    /**
     * Retrieve a set of String values from the preferences.
     * <p/>
     * <p>Note that you <em>must not</em> modify the set instance returned
     * by this call.  The consistency of the stored data is not guaranteed
     * if you do, nor is your ability to modify the instance at all.
     *
     * @param key       The name of the preference to retrieve.
     * @param defValues Values to return if this preference does not exist.
     * @return Returns the preference values if they exist, or defValues.
     * Throws ClassCastException if there is a preference with this name
     * that is not a Set.
     * @throws ClassCastException
     */
    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return getDB().getStringSet(key, defValues);
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public int getInt(String key, int defValue) {
        return getDB().getInt(key, defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * @throws ClassCastException
     */
    public long getLong(String key, long defValue) {
        return getDB().getLong(key, defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * @throws ClassCastException
     */
    public float getFloat(String key, float defValue) {
        return getDB().getFloat(key, defValue);
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * @throws ClassCastException
     */
    public boolean getBoolean(String key, boolean defValue) {
        return getDB().getBoolean(key, defValue);
    }

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     * otherwise false.
     */
    public boolean contains(String key) {
        return getDB().contains(key);
    }

}
