package me.weilinfox.pkgsearch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int version = 4;
    private Context mContext;
    private final String CREATE_HISTORY = "CREATE TABLE IF NOT EXISTS history(" +
                                                        "name text," +
                                                        "option text," +
                                                        "time Long," +
                                                        "primary key(name, option));";
    private final String CREATE_STAR = "CREATE TABLE IF NOT EXISTS star(" +
                                                        "name text," +
                                                        "option text," +
                                                        "version text," +
                                                        "url text," +
                                                        "arch text," +
                                                        "info text," +
                                                        "canview integer);";
    private final String DROP_HISTORY = "DROP TABLE IF EXISTS history;";
    private final String DROP_STAR = "DROP TABLE IF EXISTS star;";

    public DatabaseHelper(@NotNull Context context, @NotNull String name,
                          @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, DatabaseHelper.version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_HISTORY);
        sqLiteDatabase.execSQL(CREATE_STAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                sqLiteDatabase.execSQL(DROP_STAR);
                break;
            case 2:
                String addInfo = "ALTER TABLE star ADD info text;";
                sqLiteDatabase.execSQL(addInfo);
                break;
            case 3:
                String addView = "ALTER TABLE star ADD canview integer;";
                String updateView = "UPDATE star SET canview=1;";
                sqLiteDatabase.execSQL(addView);
                sqLiteDatabase.execSQL(updateView);
                break;
            default:
                sqLiteDatabase.execSQL(DROP_HISTORY);
                sqLiteDatabase.execSQL(DROP_STAR);
                break;
        }
        onCreate(sqLiteDatabase);
    }
}
