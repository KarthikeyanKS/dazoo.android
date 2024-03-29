
package com.mitv.models.orm.base;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.mitv.models.orm.base.OrmLiteDatabaseHelper.Upgrader;



public abstract class AbstractOrmLiteClass<T> 
{
	private static final String TAG = AbstractOrmLiteClass.class.getSimpleName();

	
	private static OrmLiteDatabaseHelper ormLiteDbHelper;

	private static HashMap<Class<?>, Dao<? extends AbstractOrmLiteClass<?>, ?>> hmClassDao = new HashMap<Class<?>, Dao<? extends AbstractOrmLiteClass<?>, ?>>();
	
	private static HashMap<Class<?>, RuntimeExceptionDao<? extends AbstractOrmLiteClass<?>, ?>> hmRuntimeDao = new HashMap<Class<?>, RuntimeExceptionDao<? extends AbstractOrmLiteClass<?>, ?>>();

	
	
	public static void initDB(Context context) 
	{
		if (ormLiteDbHelper == null) {
			ormLiteDbHelper = new OrmLiteDatabaseHelper(context);
			ormLiteDbHelper.getWritableDatabase();
			Log.d(TAG, "OrmLite Class Has Been Initialized");
			Log.d(TAG, OrmLiteDatabaseHelper.DATABASE_NAME + " (version "
					+ OrmLiteDatabaseHelper.DATABASE_VERSION
					+ ") has been created (if needed).");
		}
	}

	public static void initDB(Context context, String databaseName,
			int databaseVersion, Upgrader upgrader) {
		if (ormLiteDbHelper == null) {
			ormLiteDbHelper = new OrmLiteDatabaseHelper(context, databaseName,
					databaseVersion, upgrader);
			ormLiteDbHelper.getWritableDatabase();
			Log.d(TAG, "OrmLite Class Has Been Initialized");
			Log.d(TAG, databaseName + " (version " + databaseVersion
					+ ") has been created (if needed).");
		}
	}

	protected abstract void onBeforeSave();

	protected abstract void onAfterSave();

	@SuppressWarnings("unchecked")
	protected Dao<? extends AbstractOrmLiteClass<?>, ?> getDao()
			throws SQLException 
	{
		Dao<? extends AbstractOrmLiteClass<?>, ?> dao = null;
		
		if (!hmClassDao.containsKey(this.getClass())) 
		{
			dao = (Dao<? extends AbstractOrmLiteClass<?>, ?>) OrmLiteDatabaseHelper.getInstance().getDao(this.getClass());
			
			hmClassDao.put(this.getClass(), dao);
			
			createTableIfNeeded();
		}
		
		dao = hmClassDao.get(this.getClass());
		return dao;
	}

	protected RuntimeExceptionDao<? extends AbstractOrmLiteClass<?>, ?> getRunExpDao() {
		RuntimeExceptionDao<? extends AbstractOrmLiteClass<?>, ?> runtimeDao = null;
		if (!hmRuntimeDao.containsKey(this.getClass())) {
			OrmLiteDatabaseHelper.getInstance().getRuntimeExceptionDao(
					this.getClass());
			createTableIfNeeded();

		} else
			runtimeDao = hmRuntimeDao.get(this.getClass());
		return runtimeDao;
	}

	private void createTableIfNeeded() 
	{
		try 
		{
			TableUtils.createTableIfNotExists(OrmLiteDatabaseHelper.getInstance().getConnectionSource(), this.getClass());
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void save() throws SQLException {
		onBeforeSave();
		((Dao<AbstractOrmLiteClass<T>, ?>) getDao())
				.createOrUpdate((AbstractOrmLiteClass<T>) this);
		onAfterSave();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() throws SQLException {
		return (List<T>) getDao().queryForAll();
	}

	@SuppressWarnings("unchecked")
	public List<T> queryManyByEqual(String field, Object equal)
			throws SQLException {
		QueryBuilder<AbstractOrmLiteClass<T>, ?> queryBuilder = (QueryBuilder<AbstractOrmLiteClass<T>, ?>) getDao()
				.queryBuilder();
		queryBuilder.where().eq(field, equal);
		return (List<T>) queryBuilder.query();
	}

	@SuppressWarnings("unchecked")
	public T querySingleByEqual(String field, Object equal) throws SQLException {
		QueryBuilder<AbstractOrmLiteClass<T>, ?> queryBuilder = (QueryBuilder<AbstractOrmLiteClass<T>, ?>) getDao()
				.queryBuilder();
		queryBuilder.where().eq(field, equal);
		return (T) queryBuilder.queryForFirst();
	}

	@SuppressWarnings("unchecked")
	public List<T> queryManyByEqual(String field, Object equal,
			String orderByColumn, boolean ascending) throws SQLException {
		QueryBuilder<AbstractOrmLiteClass<T>, ?> queryBuilder = (QueryBuilder<AbstractOrmLiteClass<T>, ?>) getDao()
				.queryBuilder();
		queryBuilder.where().eq(field, equal);
		queryBuilder.orderBy(orderByColumn, ascending);
		return (List<T>) queryBuilder.query();
	}

	@SuppressWarnings("unchecked")
	public void refresh() throws SQLException {
		((Dao<AbstractOrmLiteClass<T>, ?>) getDao()).refresh(this);
	}

	
	
	public int clearTable()
	{
		try
		{
			int rows = TableUtils.clearTable(OrmLiteDatabaseHelper.getInstance().getConnectionSource(), this.getClass());
			
			return rows;
		}
		catch(SQLException sqlex)
		{
			Log.e(TAG, sqlex.getMessage(), sqlex);
			
			return 0;
		}
	}
	

	
	public void deleteById(String field, String id)
			throws SQLException
	{
		DeleteBuilder<? extends AbstractOrmLiteClass<?>, ?> deleteBuilder = getDao().deleteBuilder();
		
		deleteBuilder.where().eq(field, id);
        
		deleteBuilder.delete();
	}
}
