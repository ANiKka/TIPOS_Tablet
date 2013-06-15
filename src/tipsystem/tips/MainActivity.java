package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.tips.ManageCustomerActivity.MyAsyncTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "unikys.todo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewShopCode);
        textView.setTypeface(typeface);
        
        // test
        updateTestData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	// Private Methods
    public void updateTestData() {

    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	textView.setText("0000001");
    }
    
    public void onAuthentication(View view) {
    	
    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	String code = textView.getText().toString();
    	if (!code.equals(""))
    		new MyAsyncTask ().execute(code);
    }    
    
    public void didLogin() {

        Toast.makeText(getApplicationContext(), "로그인 완료", 0).show();
    	Intent intent = new Intent(this, ConfigActivity.class);
    	startActivity(intent);
    }
    
	// MSSQL
    class MyAsyncTask extends AsyncTask<String, Integer, JSONObject> {

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected JSONObject doInBackground(String... params) {
        	Log.i("MSSQL"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset =null;
			JSONObject Obj = new JSONObject();
        	String code = params[0];
        	
        	try {
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("MSSQL","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/Trans","app_tips","app_tips");
        	    Log.i("MSSQL","MSSQL open");
        	    Statement stmt = conn.createStatement();

        	    //String query =  "select * from APP_USER ";
        	    String query =  "select * from APP_USER where OFFICE_CODE =" + code;
	            Log.e("MSSQL","query: " + query );
	
	        	reset = stmt.executeQuery(query);

        	    while(reset.next()){
				    Obj.put("OFFICE_CODE",reset.getString(1).trim());
				    Obj.put("APP_HP",reset.getString(2).trim());
				    Obj.put("APP_USERNAME",reset.getString(3).trim());
				    Obj.put("WRITE_DATE",reset.getString(4).trim());
				    Obj.put("WRITER",reset.getString(5).trim());
				    Obj.put("EDIT_DATE",reset.getString(6).trim());
				    Obj.put("EDITOR",reset.getString(7).trim());
				    Obj.put("BIGO",reset.getString(8).trim());
				    
					Log.w("MSSQL:", "data: " +Obj.toString());
				}
        	    
        	    conn.close();
        	
        	 } catch (Exception e) {
        	    Log.w("Error connection","" + e.getMessage());		   
        	 }
        	 
        	 // onProgressUpdate에서 0이라는 값을 받아서 처리
        	 publishProgress(0);
        	 return Obj;        	 
        }

        protected void onProgressUpdate(Integer[] values) {
            Log.e("MSSQL", "onProgressUpdate" );
        }

        protected void onPostExecute(JSONObject result) {
        	super.onPostExecute(result);
			Log.w("MSSQL:", "result: " +result.toString());
        	didLogin();
        }
    };
}
