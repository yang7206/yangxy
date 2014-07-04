package com.loopj.android.http.model.undo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

	private static final String DBNAME="downloadDb";
	
	private static final int VERSION=2;
	
	private final String TABLE_NAME="taskInfo";
	//下载地址
	private final String TABLE_DOWNLOAD_URL="downloadUrl";
	//保存地址
	private final String TABLE_STORE_PATH="storePath";
	//文件名
	private final String TABLE_FILE_NAME="fileName";
	//文件大小
	private final String TABLE_FILE_SIZE="fileSize";
	//线程数
	private final String TABLE_THREAD_SIZE="threadSize";
	//已下载区间
	private final String TABLE_DOWNLOADED_Range="downloadedRange";
	//下载进度
	private final String TABLE_DLPERCENT="dlPercent";
	
	public TaskDbHelper(Context ctx){
		super(ctx, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sb=new StringBuffer();
		sb.append("CREATE TABLE"+TABLE_NAME+"(");
		
		
		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}

}
