package app.moveinsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bharat on 10/5/15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static  final String moveinsync = "moveinsync";
    private static List<Class> tableClasses = new ArrayList<Class>();

    private void initializeTableClasses(Context context) {
        addToTableClasses(SavedLocationObject.class);
    }

    public static void addToTableClasses(Class cl) {
        try {
            if (!tableClasses.contains(cl)) {
                tableClasses.add(cl);
            }
        } catch (Exception e) {
        }
    }


    public DatabaseHelper(Context context) {
        super(context, moveinsync, null, 1);
        initializeTableClasses(context);

    }

    private Map<Class, Dao> daoMap = new HashMap<Class, Dao>();

    private static Map<Class, DBUtil> dbUtilMap = new HashMap<Class, DBUtil>();

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            for (Class cl : tableClasses) {
                TableUtils.createTableIfNotExists(connectionSource, cl);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(),
                    "Can't create database" + e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            // Put commands to properly upgrade the tables
            for (Class cl : tableClasses) {
                TableUtils.dropTable(connectionSource, cl, true);
            }

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(),
                    "Can't drop databases" + e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    public Dao getDaoFromClass(Class cl) {
        Dao dao = daoMap.get(cl);
        if (dao == null) {
            try {
                dao = getDao(cl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            daoMap.put(cl, dao);
        }
        return dao;
    }

    public static DBUtil getDBUtil(Context ctx, Class cl) {
        DBUtil dbUtil = dbUtilMap.get(cl);
        if (dbUtil == null) {
            dbUtil = new DBUtil(ctx, cl);
            dbUtilMap.put(cl, dbUtil);
        }
        return dbUtil;
    }
}
