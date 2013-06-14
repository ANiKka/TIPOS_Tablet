package tipsystem.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import android.os.AsyncTask;
import android.util.Log;

public class MSSQL extends AsyncTask<Void, Integer, Void>{

    // doInBackground 메소드가 실행되기 전에 실행되는 메소드
	@Override
	protected void onPreExecute() {
	     // UI 작업을 수행하는 부분
	    super.onPreExecute();
	}
	
	// 실제 비즈니스 로직이 처리될 메소드(Thread 부분이라고 생각하면 됨)
	@Override
	protected Void doInBackground(Void... params) {
	
	Log.i("Android"," MSSQL Connect Example.");
	Connection conn = null;
	try {
	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
	    Log.i("Connection","MSSQL driver load");
	    
	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
	    Log.i("Connection","MSSQL open");
	    Statement stmt = conn.createStatement();
	    ResultSet reset = stmt.executeQuery("select * from Office_Manage");
	
	    //Print the data to the console
	    while(reset.next()){
	    	Log.w("Connection:",reset.getString(2));
	    }
	    conn.close();

	 } catch (Exception e)
	 {
	    Log.w("Error connection","" + e.getMessage());		   
	 }
	 
	 // onProgressUpdate에서 0이라는 값을 받아서 처리
	     publishProgress(0);
	     return null;
	 }
	
	 // doInBackground에서 넘긴 values 값을 받아서 처리하는 부분
	 @Override
	 protected void onProgressUpdate(Integer... values) {
	     switch(values[0]) {
	     case 0:
	         // doInBackground에서 publicshProgress로 넘긴값을 처리하는 부분
	         break;
	     }
	     super.onProgressUpdate(values);
	 }
	
	 // 모든 작업이 끝난 후 처리되는 메소드
	 @Override
	 protected void onPostExecute(Void result) {
	     super.onPostExecute(result);
	 }
}

