package eu.tjenwellens.timetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.tjenwellens.timetracker.calendar.Kalender;
import eu.tjenwellens.timetracker.macro.MacroFactory;
import eu.tjenwellens.timetracker.macro.MacroI;
import eu.tjenwellens.timetracker.main.ActiviteitFactory;
import eu.tjenwellens.timetracker.main.ActiviteitI;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper
{
    // singleton
    private static DatabaseHandler dbh;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "timetracker";
    // Contacts table name
    private static final String TABLE_ACTIVITEITEN = "activiteiten";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_KAL_NAME = "kalender_name";
    private static final String KEY_TITLE = "title";
    private static final String KEY_START_MILLIS = "start_millis";
    private static final String KEY_STOP_MILLIS = "stop_millis";
    private static final String KEY_DETAILS = "details";
    // Create table
    private static final String CREATE_ACTIVITEITEN_TABLE = "CREATE TABLE " + TABLE_ACTIVITEITEN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_KAL_NAME + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_START_MILLIS + " INTEGER,"
            + KEY_STOP_MILLIS + " INTEGER,"
            // mind the , before the )
            + KEY_DETAILS + " TEXT"
            + ")";
    private Context context;
    // Macros table name
    private static final String TABLE_MACROS = "macros";
    // Create table
    private static final String CREATE_MACROS_TABLE = "CREATE TABLE " + TABLE_MACROS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_KAL_NAME + " TEXT,"
            + KEY_TITLE + " TEXT"
            + ")";

    private DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DatabaseHandler getInstance(Context context)
    {
        if (dbh == null || dbh.context != context)
        {
            dbh = new DatabaseHandler(context);
        }
        return dbh;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_ACTIVITEITEN_TABLE);
        db.execSQL(CREATE_MACROS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITEITEN);
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MACROS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new contact
    public void addActiviteit(ActiviteitI activiteit)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, activiteit.getActiviteitId());        // id
        values.put(KEY_KAL_NAME, activiteit.getKalenderName()); // kalender
        values.put(KEY_TITLE, activiteit.getActiviteitTitle());        // title
        values.put(KEY_START_MILLIS, activiteit.getBeginMillis()); // begin
        values.put(KEY_STOP_MILLIS, activiteit.getEndMillis()); // end
        values.put(KEY_DETAILS, activiteit.getDescription()); // description

        // Inserting Row
        db.insert(TABLE_ACTIVITEITEN, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public ActiviteitI getActiviteit(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String table = TABLE_ACTIVITEITEN;
        String[] columns = new String[]
        {
            KEY_ID, KEY_KAL_NAME, KEY_TITLE, KEY_START_MILLIS, KEY_STOP_MILLIS, KEY_DETAILS
        };
        String selection = KEY_ID + "=?";
        String[] selectionArgs = new String[]
        {
            String.valueOf(id)
        };
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        int db_id = Integer.parseInt(cursor.getString(0));
        String db_kalendar_name = cursor.getString(1);
        Kalender kalender = Kalender.getKalenderByName(context, db_kalendar_name);
        String db_title = cursor.getString(2);
        long db_start = Long.parseLong(cursor.getString(3));
        long db_stop = Long.parseLong(cursor.getString(4));
        String db_description = cursor.getString(5);
        cursor.close();
        db.close();

        ActiviteitI activiteit = ActiviteitFactory.loadActiviteit(context, db_id, kalender, db_title, db_start, db_stop, db_description);
        // return contact
        return activiteit;
    }

    public void addAllActiviteiten(List<ActiviteitI> activiteiten)
    {
        for (ActiviteitI activiteit : activiteiten)
        {
            addActiviteit(activiteit);
        }
    }

    // Getting All Contacts
    public List<ActiviteitI> getAllActiviteiten()
    {
        List<ActiviteitI> contactList = new ArrayList<ActiviteitI>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITEITEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                int db_id = Integer.parseInt(cursor.getString(0));
                String db_kalendar_name = cursor.getString(1);
                Kalender kalender = Kalender.getKalenderByName(context, db_kalendar_name);
                String db_title = cursor.getString(2);
                long db_start = Long.parseLong(cursor.getString(3));
                long db_stop = Long.parseLong(cursor.getString(4));
                String db_description = cursor.getString(5);

                ActiviteitI activiteit = ActiviteitFactory.loadActiviteit(context, db_id, kalender, db_title, db_start, db_stop, db_description);

                // Adding contact to list
                contactList.add(activiteit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateActiviteit(ActiviteitI activiteit)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, activiteit.getActiviteitId());        // id
        values.put(KEY_KAL_NAME, activiteit.getKalenderName()); // kalender
        values.put(KEY_TITLE, activiteit.getActiviteitTitle());        // title
        values.put(KEY_START_MILLIS, activiteit.getBeginMillis()); // begin
        values.put(KEY_STOP_MILLIS, activiteit.getEndMillis()); // end
        values.put(KEY_DETAILS, activiteit.getDescription()); // description
        int result = db.update(TABLE_ACTIVITEITEN, values, KEY_ID + " = ?",
                new String[]
                {
                    String.valueOf(activiteit.getActiviteitId())
                });
        db.close();
        // updating row
        return result;
    }

    // Deleting single contact
    public void deleteActiviteit(ActiviteitI activiteit)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITEITEN, KEY_ID + " = ?",
                new String[]
                {
                    String.valueOf(activiteit.getActiviteitId())
                });
        db.close();
    }

    // Getting contacts Count
    public int getActiviteitenCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_ACTIVITEITEN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void clearActiviteiten()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITEITEN, null, null);
        db.close();
    }

    public void addMacro(MacroI macro)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, macro.getId()); // kalender
        values.put(KEY_KAL_NAME, macro.getKalenderName()); // kalender
        values.put(KEY_TITLE, macro.getActiviteitTitle());        // title

        // Inserting Row
        db.insert(TABLE_MACROS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single contact
    public int updateMacro(MacroI macro)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, macro.getId()); // kalender
        values.put(KEY_KAL_NAME, macro.getKalenderName()); // kalender
        values.put(KEY_TITLE, macro.getActiviteitTitle());        // title
        int result = db.update(TABLE_MACROS, values, KEY_ID + " = ?",
                new String[]
                {
                    String.valueOf(macro.getId())
                });
        db.close();
        // updating row
        return result;
    }

    public void addAllMacros(List<MacroI> macros)
    {
        for (MacroI macro : macros)
        {
            addMacro(macro);
        }
    }

    public void clearMacros()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MACROS, null, null);
        db.close();
    }

    // Deleting single contact
    public void deleteMacro(MacroI macro)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MACROS, KEY_ID + " = ?",
                new String[]
                {
                    String.valueOf(macro.getId())
                });
        db.close();
    }

    public List<MacroI> getAllMacros()
    {
        List<MacroI> macros = new ArrayList<MacroI>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MACROS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                int db_id = Integer.parseInt(cursor.getString(0));
                String db_kalendar_name = cursor.getString(1);
                Kalender kalender = Kalender.getKalenderByName(context, db_kalendar_name);
                String db_title = cursor.getString(2);

                MacroI macro = MacroFactory.loadMacro(context, db_id, db_title, kalender);

                // Adding contact to list
                macros.add(macro);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return macros;
    }
}