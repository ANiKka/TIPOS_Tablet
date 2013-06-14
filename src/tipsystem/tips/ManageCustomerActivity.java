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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ManageCustomerActivity extends Activity implements OnItemSelectedListener{

	Spinner m_spin;
	ListView m_cusList;
	
	TextView m_customerCode;
	TextView m_customerName;
	Spinner m_customerSection;
	
	private OnClickListener m_click_search_listener = new OnClickListener() {
        public void onClick(View v) { 
        	String code = m_customerCode.getText().toString();
    	    String name = m_customerName.getText().toString();
    	    String section = m_customerSection.getSelectedItem().toString();
            new MyAsyncTask ().execute("1", code, name, section);
        }
	};
	
	private OnClickListener m_click_regist_listener = new OnClickListener() {
        public void onClick(View v) { 
        	String code = m_customerCode.getText().toString();
    	    String name = m_customerName.getText().toString();
    	    String section = m_customerSection.getSelectedItem().toString();
    	    
    	    if(code == "" || name == "" || section == "")
    	    	return;
    	    
            new MyAsyncTask ().execute("2", code, name, section);
        }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_customer);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_spin = (Spinner)findViewById(R.id.spinnerCustomerCodeType);
		m_spin.setOnItemSelectedListener(this);
		m_cusList= (ListView)findViewById(R.id.listviewCustomerList);
		
		m_customerCode = (TextView)findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView)findViewById(R.id.editTextCustomerName);
		m_customerSection = (Spinner)findViewById(R.id.spinnerCustomerCodeType);
		
		Button searchButton = (Button) findViewById(R.id.buttonCustomerSearch);
		Button registButton = (Button) findViewById(R.id.buttonCustomerRegist);
		
		searchButton.setOnClickListener(m_click_search_listener);
        registButton.setOnClickListener(m_click_regist_listener);
		
        /*
		 // create the grid item mapping
        String[] from = new String[] {"코드", "거래처명", "구분"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
 
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("코드", "0000" + i);
            map.put("거래처명", "거래처명" + i);
            map.put("구분", "구분" + i);
            fillMaps.add(map);
        }
 
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_customer_list, 
        		from, to);
        m_cusList.setAdapter(adapter);
        */
        
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item1);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item3);
        textView.setTypeface(typeface);
        
        
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();         
		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
		actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_customer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
	{
		//TextView text1 = (TextView)m_spin.getSelectedView();
		//m_text.setText(text1.getText());
		
	}
	
	public void onNothingSelected(AdapterView<?> parent)
	{
		//m_text.setText("");
	}

	// MSSQL
    
	   class MyAsyncTask extends AsyncTask<String, Integer, String>{

	        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
	        
	        protected String doInBackground(String... urls) {
	        	Log.i("Android"," MSSQL Connect Example.");
	        	Connection conn = null;
	        	ResultSet reset =null;
	        	String type = urls[0];
	        	
	        	try {
	        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
	        	    Log.i("Connection","MSSQL driver load");

	        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
	        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
	        	    Log.i("Connection","MSSQL open");
	        	    Statement stmt = conn.createStatement();
	        	    
	        	    String code = urls[1];
	        	    String name = urls[2];
	        	    String section = urls[3];
	        	    
	        	    Log.w("Connection:", section);
	        	    if(section.equals("매입거래처")){
	        	    	section = "1";
	        	    } else if(section.equals("수수료거래처")) {
	        	    	section = "2";
	        	    } else {
	        	    	section = "3";
	        	    }
	        	    
	        	    Log.w("Connection:", section); 	
	        	    String query = "";
	        	    
	        	    if(type == "2"){
	        	    	query += "insert into Office_Manage(Office_Code, Office_Name, Office_Sec) values('" + code + "', '" + name + "', '" + section + "'); select * from Office_Manage";
	        	    	Log.e("HTTPJSON","query: " + query );
	        	    	reset = stmt.executeQuery(query);
	        	    }
	        	    else {
		        	    query = "";
		        	    int i=0;
		        	    if (!code.equals("")){
		        	    	query += "Office_Code = '" + code + "' ";
		        	    	i++;
		        	    }
		        	    if (!name.equals("")){
		        	    	if(i==1)
		        	    		query += "and Office_Name = '" + name + "'";
		        	    	else
		        	    		query += "Office_Name = '" + name + "'";
		        	    }
		                Log.e("HTTPJSON","query: " + query );
		
		        	    if (query.equals("")){
		        	    	reset = stmt.executeQuery("select * from Office_Manage");
		        	    } else {
		        	    	reset = stmt.executeQuery( "select * from Office_Manage WHERE " + query);
		        	    }
	        	    }
	        	    while(reset.next()){
						Log.w("Connection:",reset.getString(2));
						
						JSONObject Obj = new JSONObject();
					    // original part looks fine:
					    Obj.put("code",reset.getString(1).trim());
					    Obj.put("name",reset.getString(2).trim());
					    Obj.put("section",reset.getString(3).trim());
					    CommArray.add(Obj);
					}
	        	    
	        	    conn.close();
	        	
	        	 } catch (Exception e)
	        	 {
	        	    Log.w("Error connection","" + e.getMessage());		   
	        	 }
	        	 
	        	 // onProgressUpdate에서 0이라는 값을 받아서 처리
	        	 publishProgress(0);
	        	 return type;        	 
	        }

	        protected void onProgressUpdate(Integer[] values) {
	            Log.e("HTTPJSON", "onProgressUpdate" );
	        }

	        protected void onPostExecute(String result) {
	        	super.onPostExecute(result);
	        	
				String[] from = new String[] {"code", "name", "section"};
		        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
		        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		 	        		
	        	Iterator<JSONObject> iterator = CommArray.iterator();
	    		while (iterator.hasNext()) {
	            	JSONObject json = iterator.next();
	            	
	            	try {
	    				String code = json.getString("code");
	    				String name = json.getString("name");
	    				String section = json.getString("section");
	    				
	    				// prepare the list of all records
			            HashMap<String, String> map = new HashMap<String, String>();
			            map.put("code", code);
			            map.put("name", name);
			            map.put("section", section);
			            fillMaps.add(map);
	    		 
	    			} catch (JSONException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}

		        // fill in the grid_item layout
		        SimpleAdapter adapter = new SimpleAdapter(ManageCustomerActivity.this, fillMaps, R.layout. activity_listview_customer_list, from, to);
		        m_cusList.setAdapter(adapter);
		        
	            Toast.makeText(getApplicationContext(), "조회 완료", 0).show();
	        }
	    };

}
