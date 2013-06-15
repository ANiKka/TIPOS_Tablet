package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.tips.ManageCustomerActivity.MyAsyncTask;
import tipsystem.utils.ResultSetConverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
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

	// loading bar
	private ProgressDialog dialog; 
	
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

    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	String code = textView.getText().toString();
    	if (code.equals("")) return;

	    String query =  "";
	    //query = "select * from V_OFFICE_USER where Sto_CD =" + code + ";";
	    query =  "select * " 
	    		+ "from V_OFFICE_USER, APP_SETTLEMENT " 
	    		+ "where Sto_CD = " + code
	    		+ ";";
		new MyAsyncTask ().execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
    }
    
    public void didLogin(JSONArray results) {
    	if (results.length() > 0) {
    		Toast.makeText(getApplicationContext(), "인증 완료", 0).show();
        	Intent intent = new Intent(this, ConfigActivity.class);
        	startActivity(intent);
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "인증 실패", 0).show();
    	}
    }
    
	// MSSQL
    class MyAsyncTask extends AsyncTask<String, Integer, JSONArray> {

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected JSONArray doInBackground(String... params) {
        	Log.i("MSSQL"," MSSQL Connect Example.");
        	Connection conn = null;
        	JSONArray json = null;
        	String ip = params[0];
        	String dbname = params[1];
        	String dbid = params[2];
        	String dbpw = params[3];
        	String query = params[4];
            Log.e("MSSQL","query: " + query );
        	
        	try {
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("MSSQL","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" +ip +"/"+ dbname, dbid, dbpw);
        	    Log.i("MSSQL","MSSQL open");
        	    Statement stmt = conn.createStatement();
        	    
	        	ResultSet rs =null;
	            rs = stmt.executeQuery(query);	            
	        	json = ResultSetConverter.convert(rs);
        	   
        	    conn.close();
        	
        	 } catch (Exception e) {
        	    Log.w("Error connection","" + e.getMessage());		   
        	 }
        	 
        	 // onProgressUpdate에서 0이라는 값을 받아서 처리
        	 publishProgress(0);
        	 return json;        	 
        }

        protected void onProgressUpdate(Integer[] values) {
            Log.e("MSSQL", "onProgressUpdate" );
        }

        protected void onPostExecute(JSONArray results) {
        	super.onPostExecute(results);
			Log.w("MSSQL:", "onPostExecute: " +results.toString());

			dialog.dismiss();
			dialog.cancel();
			
        	didLogin(results);
        }
    };
}
