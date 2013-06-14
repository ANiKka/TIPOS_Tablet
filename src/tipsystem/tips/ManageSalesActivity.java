package tipsystem.tips;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ManageSalesActivity extends Activity implements OnItemClickListener, 
															OnDateChangeListener,
															OnTabChangeListener,
															DatePickerDialog.OnDateSetListener{

	TabHost m_tabHost;
	
	ListView m_listSalesTab1;
	ListView m_listSalesTab2;
	ListView m_listSalesTab3;
	ListView m_listSalesTab4;
	
	Button m_period1;
	Button m_period2;
	TextView m_barCode;
	TextView m_productName;
	TextView m_customerCode;
	TextView m_customerName;
	
	CalendarView m_calendar;
		
	DatePicker m_datePicker;
	
	SimpleDateFormat m_dateFormatter;
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	int m_dateMode=0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_sales);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_period1 = (Button) findViewById(R.id.buttonSetDate1); 
		m_period2 = (Button) findViewById(R.id.buttonSetDate2);
		m_barCode = (TextView) findViewById(R.id.editTextBarcode);
		m_productName = (TextView) findViewById(R.id.editTextProductName);
		m_customerCode = (TextView) findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView) findViewById(R.id.editTextCustomerName);
		
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		
		
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
				
		m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		
		
		m_tabHost = (TabHost) findViewById(R.id.tabhostManageSales);
        m_tabHost.setup();
        
        TabHost.TabSpec spec = m_tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.tab1);
        spec.setIndicator("거래처별");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("상품명");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("수수료매장");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("달력매출");
        m_tabHost.addTab(spec);
        
        m_tabHost.setOnTabChangedListener(this);
     
        m_tabHost.setCurrentTab(0);
        
        m_calendar = (CalendarView)findViewById(R.id.calendarView1);
        m_calendar.setOnDateChangeListener(this);

//        setTabList1();
//        setTabList2();
//        setTabList3();
//        setTabList4();
        
	}
	
	private void setTabList1(List<HashMap<String, String>> fillMaps)
	{
//		m_listSalesTab1= (ListView)findViewById(R.id.listviewSalesListTab1);
//		
//		 // create the grid item mapping
//		String[] from = new String[] {"바코드", "상품명", "수량", "순매출"};
//		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
//		
//		// prepare the list of all records
//		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
//		for(int i = 0; i < 10; i++)
//		{
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("바코드", "0000" + i);
//			map.put("상품명", "상품명" + i);
//			map.put("수량", i + "000");
//			map.put("순매출", i + "000");
//			fillMaps.add(map);
//		}
//		
//		// fill in the grid_item layout
//		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
//				from, to);
//		
//		m_listSalesTab1.setAdapter(adapter);
//		m_listSalesTab1.setOnItemClickListener(this);
		
		
		
	}
	
	private void setTabList2()
	{
		m_listSalesTab2= (ListView)findViewById(R.id.listviewSalesListTab2);
		
		 // create the grid item mapping
		String[] from = new String[] {"바코드", "상품명", "수량", "순매출"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("바코드", "0000" + i);
			map.put("상품명", "상품명" + i);
			map.put("수량", "수량"+ i);
			map.put("순매출", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listSalesTab2.setAdapter(adapter);
	}
	
	private void setTabList3()
	{
		m_listSalesTab3= (ListView)findViewById(R.id.listviewSalesListTab3);
		
		 // create the grid item mapping
		String[] from = new String[] {"코드", "거래처명", "순매출"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("코드", "0000" + i);
			map.put("거래처명", "거래처명" + i);
			map.put("순매출", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3, 
				from, to);
		
		m_listSalesTab3.setAdapter(adapter);
		m_listSalesTab3.setOnItemClickListener(this);
	}
	
	private void setTabList4()
	{
		m_listSalesTab4= (ListView)findViewById(R.id.listviewSalesListTab4);
		
		 // create the grid item mapping
		String[] from = new String[] {"상세항목"};
		int[] to = new int[] { R.id.textView1};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("상세항목", "상세항목 " + i);
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_text, 
				from, to);
		
		m_listSalesTab4.setAdapter(adapter);
	}
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();         
//		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
//		actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("매출관리");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_payment, menu);
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
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if ( m_listSalesTab1.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, CustomerProductDetailViewActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		else if ( m_listSalesTab3.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, ChargeCustomerDetailActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);
		}
			
		
		//Toast.makeText(this, "Item Click." + m_calendar.getId() + " ,  " + arg0.getId(), Toast.LENGTH_SHORT).show();
		
		
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Item Click." + m_calendar.getId() + " ,  " + view.getId(), Toast.LENGTH_SHORT).show();
		setTabList4();
	}
	

	public void OnClickSearch(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Search Click.", Toast.LENGTH_SHORT).show();
		
		
		
		
	};
	
	    
	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Tab Click.", Toast.LENGTH_SHORT).show();
		
		
		
		
		
		
	}

	public void onClickSetDate1(View v) {
		// TODO Auto-generated method stub
				
		DatePickerDialog newDlg = new DatePickerDialog(this, this,
				m_dateCalender1.get(Calendar.YEAR),
				m_dateCalender1.get(Calendar.MONTH),
				m_dateCalender1.get(Calendar.DAY_OF_MONTH));
		 newDlg.show();
		
		 m_dateMode = 1;
	};
	
	public void onClickSetDate2(View v) {
		// TODO Auto-generated method stub
		DatePickerDialog newDlg = new DatePickerDialog(this, this, 
				m_dateCalender2.get(Calendar.YEAR),
				m_dateCalender2.get(Calendar.MONTH),
				m_dateCalender2.get(Calendar.DAY_OF_MONTH));
		
		newDlg.show();
		
		m_dateMode = 2;
	};

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
		if ( m_dateMode == 1 )
		{
			m_dateCalender1.set(year, monthOfYear+1, dayOfMonth);
			m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		}
		else if ( m_dateMode == 2 )
		{
			m_dateCalender2.set(year, monthOfYear+1, dayOfMonth);
			m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		}
		
		m_dateMode = 0;
	}
	

	
	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected String doInBackground(String... urls) {
        	Log.i("Android"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset =null;
        	
        	try {
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("Connection","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
        	    Log.i("Connection","MSSQL open");
        	    Statement stmt = conn.createStatement();
        	    
        	    String year= urls[0];
        	    String month = urls[1];
        	    String day = urls[2];
        	    
        	    String query = "";
        	    
        	    query = "select * from SaD";
        	    Log.e("HTTPJSON","query: " + query );
            	reset = stmt.executeQuery(query);
	        	    		
        	    while(reset.next()){
					Log.w("HTTPJSON:",reset.getString(1));
					
					JSONObject Obj = new JSONObject();
				    // original part looks fine:
				    Obj.put("year",reset.getString(1).trim());
				    Obj.put("month",reset.getString(2).trim());
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
        	 return null;       	 
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
    		setTabList1(fillMaps);
        }
    }














}
