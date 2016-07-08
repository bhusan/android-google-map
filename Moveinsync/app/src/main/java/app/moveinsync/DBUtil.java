package app.moveinsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by bharath on 10/5/15.
 */
public class DBUtil<T> {


    public DatabaseHelper helper = null;

    public Dao<T, Integer> dao;

    public DBUtil(Context ctx, Class cl) {
        helper = (DatabaseHelper) OpenHelperManager.getHelper(ctx,
                DatabaseHelper.class);
        this.dao = helper.getDaoFromClass(cl);
    }

    public void add(T obj) {
        try {
            if (obj == null) {
                return;
            }
            dao.create(obj);
        } catch (Exception e) {
            update(obj);
        }
    }

    public void remove(T t) {
        try {
            dao.delete(t);
        } catch (Exception e) {

        }
    }

    public List<T> fetch(int offset, int limit,String rawWhere) {
        try {
            QueryBuilder queryBuilder = dao.queryBuilder();
            if (rawWhere != null) {
                queryBuilder.where().raw(rawWhere);
            }
            queryBuilder.limit(limit);
            queryBuilder.offset(offset);

            return dao.query(queryBuilder.prepare());
        } catch (Exception e) {
        }
        return null;
    }



    public int count(String rawWhere) {
        try {
            QueryBuilder queryBuilder = dao.queryBuilder();
            if (rawWhere != null) {
                queryBuilder.where().raw(rawWhere);
            }
            return dao.query(queryBuilder.prepare()).size();
        } catch (Exception e) {
        }
        return 0;
    }

    public void update(T obj) {
        try {
            dao.update(obj);
        } catch (Exception e) {
        }
    }

    public T find(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long count() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    public void callBatchInsertUpdates(final List<T> dataList) {

        new AsyncTask<List<T>, Integer, Long>() {
            @Override
            protected Long doInBackground(List<T>... params) {
                try {
                    dao.callBatchTasks(new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            for (T data : dataList) {
                                add(data);
                            }
                            return 0;
                        }
                    });
                } catch (Exception e) {
                }
                return 1L;
            }
        }.execute(dataList);
    }

}
