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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TableRow;
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
		
		
		m_listSalesTab1= (ListView)findViewById(R.id.listviewSalesListTab1);
		m_listSalesTab2= (ListView)findViewById(R.id.listviewSalesListTab2);
		m_listSalesTab3= (ListView)findViewById(R.id.listviewSalesListTab3);
		m_listSalesTab4= (ListView)findViewById(R.id.listviewSalesListTab4);
		
		
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
        //m_tabHost.getCurrentTab();
        
        m_calendar = (CalendarView)findViewById(R.id.calendarView1);
        m_calendar.setOnDateChangeListener(this);

        
	}
	
	private void setTabList1(List<HashMap<String, String>> fillMaps)
	{

		String[] from = new String[] {"Office_Code", "Office_Name", "rSale", "ProFit_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listSalesTab1.setAdapter(adapter);
		m_listSalesTab1.setOnItemClickListener(this);
		
	}
	
	private void setTabList2(List<HashMap<String, String>> fillMaps)
	{
		
		 // create the grid item mapping
		String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listSalesTab2.setAdapter(adapter);
	}
	
	private void setTabList3(List<HashMap<String, String>> fillMaps)
	{
		
		 // create the grid item mapping
		String[] from = new String[] {"Office_Code", "Office_Name", "rSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
		
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3, 
				from, to);
		
		m_listSalesTab3.setAdapter(adapter);
		m_listSalesTab3.setOnItemClickListener(this);
	}
	
	private void setTabList4(List<HashMap<String, String>> fillMaps)
	{
		
		
		 // create the grid item mapping
		String[] from = new String[] {"content"};
		int[] to = new int[] { R.id.textView1};
		
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
			String code = ((TextView) arg1.findViewById(R.id.item1)).getText().toString();
			String name = ((TextView) arg1.findViewById(R.id.item2)).getText().toString();
			
			//Toast.makeText(this, "Item Click." + name , Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(this, CustomerProductDetailViewActivity.class);  	
			String period1 = m_period1.getText().toString();
			String period2 = m_period2.getText().toString();
			
	    	intent.putExtra("PERIOD1", period1);
	    	intent.putExtra("PERIOD2", period2);
	    	intent.putExtra("OFFICE_CODE", code);
	    	intent.putExtra("OFFICE_NAME", name);
	    	
	    	startActivity(intent);	
		}
		else if ( m_listSalesTab3.getId() == arg0.getId() )
		{
			String code = ((TextView) arg1.findViewById(R.id.item1)).getText().toString();
			String name = ((TextView) arg1.findViewById(R.id.item2)).getText().toString();
			
			Intent intent = new Intent(this, ChargeCustomerDetailActivity.class);
			String period1 = m_period1.getText().toString();
			String period2 = m_period2.getText().toString();
			
	    	intent.putExtra("PERIOD1", period1);
	    	intent.putExtra("PERIOD2", period2);
	    	intent.putExtra("OFFICE_CODE", code);
	    	intent.putExtra("OFFICE_NAME", name);
	    	
	    	startActivity(intent);
		}
			
		
		//Toast.makeText(this, "Item Click." + m_calendar.getId() + " ,  " + arg0.getId(), Toast.LENGTH_SHORT).show();
		
		
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Item Click." + m_calendar.getId() + " ,  " + view.getId(), Toast.LENGTH_SHORT).show();

		String period1 = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
		String tabIndex = String.format("%d", m_tabHost.getCurrentTab());
		
		new MyAsyncTask().execute(tabIndex, period1, period1 , "", "", "", "");
		
	}
	

	public void OnClickSearch(View v) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Search Click.", Toast.LENGTH_SHORT).show();
		
		
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String barCode = m_barCode.getText().toString();
		String productName = m_productName.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		String tabIndex = String.format("%d", m_tabHost.getCurrentTab());
		
		new MyAsyncTask().execute(tabIndex, period1, period2, barCode, productName, customerCode, customerName);
		
		
	};
	
	    
	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Tab Click.", Toast.LENGTH_SHORT).show();
		
		
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String barCode = m_barCode.getText().toString();
		String productName = m_productName.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		String tabIndex = String.format("%d", m_tabHost.getCurrentTab());
		
		new MyAsyncTask ().execute(tabIndex, period1, period2, barCode, productName, customerCode, customerName);
		
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
			m_dateCalender1.set(year, monthOfYear, dayOfMonth);
			m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		}
		else if ( m_dateMode == 2 )
		{
			m_dateCalender2.set(year, monthOfYear, dayOfMonth);
			m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		}
		
		m_dateMode = 0;
	}
	

	
	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        int m_tabIndex = 0;
        
        protected String doInBackground(String... urls) 
        {
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
        	    
        	    String tabIndex = urls[0];
        	    
        	    String period1 = urls[1];
        		String period2 = urls[2];
        		String barCode = urls[3];
        		String productName = urls[4];
        		String customerCode = urls[5];
        		String customerName = urls[6];
        		
        		String query = "";
        	    
        		int year1 = Integer.parseInt(period1.substring(0, 4));
        		int year2 = Integer.parseInt(period2.substring(0, 4));
        		
        		int month1 = Integer.parseInt(period1.substring(5, 7));
        		int month2 = Integer.parseInt(period2.substring(5, 7));
        		
        		String tableName = null;
        		String constraint = "";
        		
        		m_tabIndex = Integer.parseInt(tabIndex);
        		
        		for ( int y = year1; y <= year2; y++ )
        		{
        			for ( int m = month1; m <= month2; m++ )
        			{
        				
        				tableName = String.format("SaD_%04d%02d", y, m);
        				
        				
        				if ( m_tabIndex == 0 ) // 거래처별
                		{
        					
        					
        					if ( barCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Barcode", "=", barCode);
        					}
        					
        					if ( productName.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "G_Name", "=", productName);
        					}
        					
        					if ( customerCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
        					}
        					
        					if ( customerName.equals("") != true)
        					{
        						constraint = setConstraint(constraint, "Office_Name", "=", customerName);
        					}
        					
                			query = "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri, ProFit_Pri from " + tableName;
                			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next()){
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            				    Obj.put("Office_Code",reset.getString(1).trim());
            				    Obj.put("Office_Name",reset.getString(2).trim());
            				    Obj.put("TSell_Pri",reset.getInt(3));
            				    Obj.put("TSell_RePri",reset.getInt(4));
            				    Obj.put("DC_Pri",reset.getInt(5));
            				    Obj.put("ProFit_Pri",reset.getInt(6));
   
            				    
            				    CommArray.add(Obj);
            				}
                    	    
                			
                		}
                		else if ( m_tabIndex == 1 ) // 상품명
                		{
                			if ( barCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Barcode", "=", barCode);
        					}
        					
        					if ( productName.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "G_Name", "=", productName);
        					}
        					
        					if ( customerCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
        					}
        					
        					if ( customerName.equals("") != true)
        					{
        						constraint = setConstraint(constraint, "Office_Name", "=", customerName);
        					}
        					
                			query = "select Barcode, G_Name, Sale_Count, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
                			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next()){
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            				    Obj.put("Barcode",reset.getString(1).trim());
            				    Obj.put("G_Name",reset.getString(2).trim());
            				    Obj.put("Sale_Count",reset.getInt(3));
            				    Obj.put("TSell_Pri",reset.getInt(4));
            				    Obj.put("TSell_RePri",reset.getInt(5));
            				    Obj.put("DC_Pri",reset.getInt(6));
            				    
            				    CommArray.add(Obj);
            				}
                    	    
                		}
                		else if ( m_tabIndex == 2 ) // 수수료매장
                		{
                			if ( barCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Barcode", "=", barCode);
        					}
        					
        					if ( productName.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "G_Name", "=", productName);
        					}
        					
        					if ( customerCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
        					}
        					
        					if ( customerName.equals("") != true)
        					{
        						constraint = setConstraint(constraint, "Office_Name", "=", customerName);
        					}
        					
                			query = "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
                			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next()){
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            				    Obj.put("Office_Code",reset.getString(1).trim());
            				    Obj.put("Office_Name",reset.getString(2).trim());
            				    Obj.put("TSell_Pri",reset.getInt(3));
            				    Obj.put("TSell_RePri",reset.getInt(4));
            				    Obj.put("DC_Pri",reset.getInt(5));
            				    
            				    CommArray.add(Obj);
            				}
                			
                			
                		}
                		else if ( m_tabIndex == 3 ) // 달력매출
                		{
                			
                			tableName = String.format("DF_%04d%02d", y, m);
                			
                			query = "select TSell_Pri, Sale_Num, Sale_Pri, TPur_Pri from " + tableName;
                			query = query + " where Sale_Date = '" + period1 + "'";
               			
                			                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next()){
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            				    Obj.put("TSell_Pri",reset.getInt(1));
            				    Obj.put("Sale_Num",reset.getInt(2));
            				    Obj.put("Sale_Pri",reset.getInt(3));
            				    Obj.put("TPur_Pri",reset.getInt(4));

            				    CommArray.add(Obj);
            				}	
                		}
        			}
        			
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
        	
        	
        	
        	if ( m_tabIndex == 0 )
        	{
        		String[] from = new String[] {"Office_Code", "Office_Name", "rSale", "ProFit_Pri"};
    	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    	 	        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
        				String code = json.getString("Office_Code");
        				String name = json.getString("Office_Name");
        				int tSell = json.getInt("TSell_Pri");
        				int tRSell = json.getInt("TSell_RePri");
        				int dcPri = json.getInt("DC_Pri");
        				String sProfit = String.format("%d", json.getInt("ProFit_Pri"));
        				
        				String rSale = String.format("%d", tSell - (tRSell + dcPri));
        				
        				// prepare the list of all records
    		            HashMap<String, String> map = new HashMap<String, String>();
    		            map.put("Office_Code", code);
    		            map.put("Office_Name", name);
    		            map.put("rSale", rSale);
    		            map.put("ProFit_Pri", sProfit);
    		            fillMaps.add(map);
        		 
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        		if ( CommArray.size() > 0 )
        		{
        			setTabList1(fillMaps);
        		}
        		
        	}
        	else if ( m_tabIndex == 1 )
        	{
        		String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
    	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    	 	        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
        				String code = json.getString("Barcode");
        				String name = json.getString("G_Name");
        				int tSell = json.getInt("TSell_Pri");
        				int tRSell = json.getInt("TSell_RePri");
        				int dcPri = json.getInt("DC_Pri");
        				String saleCount = String.format("%d", json.getInt("Sale_Count"));
        				
        				String rSale = String.format("%d", tSell - (tRSell + dcPri));
        				
        				// prepare the list of all records
    		            HashMap<String, String> map = new HashMap<String, String>();
    		            map.put("Barcode", code);
    		            map.put("G_Name", name);
    		            map.put("Sale_Count", saleCount);
    		            map.put("rSale", rSale);
    		            fillMaps.add(map);
        		 
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        		if ( CommArray.size() > 0 )
        		{
        			setTabList2(fillMaps);
        		}
        		
        	}
        	else if ( m_tabIndex == 2 )
        	{
        		String[] from = new String[] {"Office_Code", "Office_Name", "rSale"};
    	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
    	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    	 	        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
        				String code = json.getString("Office_Code");
        				String name = json.getString("Office_Name");
        				int tSell = json.getInt("TSell_Pri");
        				int tRSell = json.getInt("TSell_RePri");
        				int dcPri = json.getInt("DC_Pri");
        				        				
        				String rSale = String.format("%d", tSell - (tRSell + dcPri));
        				
        				// prepare the list of all records
    		            HashMap<String, String> map = new HashMap<String, String>();
    		            map.put("Office_Code", code);
    		            map.put("Office_Name", name);
    		            map.put("rSale", rSale);
    		            fillMaps.add(map);
        		 
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        		if ( CommArray.size() > 0 )
        		{
        			setTabList3(fillMaps);
        		}
        		
        	}
        	else if ( m_tabIndex == 3 )
        	{
        		String[] from = new String[] {"content"};
    	        int[] to = new int[] { R.id.textView1};
    	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    	 	        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {

                		
        				String rSale = String.format("순매출 : %d원", json.getInt("TSell_Pri"));
        				String saleNum = String.format("객 수 : %d명", json.getInt("Sale_Num"));
        				String salePri = String.format("객단가 : %d원", json.getInt("Sale_Pri"));
        				String tPurPri = String.format("매입금액 : %d원", json.getInt("TPur_Pri"));
        				
        				// prepare the list of all records
    		            HashMap<String, String> map1 = new HashMap<String, String>();
    		            HashMap<String, String> map2 = new HashMap<String, String>();
    		            HashMap<String, String> map3 = new HashMap<String, String>();
    		            HashMap<String, String> map4 = new HashMap<String, String>();
        				
    		            map1.put("content", rSale);
    		            fillMaps.add(map1);
    		            
    		            map2.put("content", saleNum);
    		            fillMaps.add(map2);
    		            
    		            map3.put("content", salePri);
    		            fillMaps.add(map3);
    		            
    		            map4.put("content", tPurPri);
    		            fillMaps.add(map4);
        		 
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        		if ( CommArray.size() > 0 )
        		{
        			setTabList4(fillMaps);
        		}
        		
        	}
    		
        }
        
        private String setConstraint(String str, String field, String op, String value)
        {
        	if ( str.equals("") != true )
        	{
        		str = str + " and ";
        	}
        	
        	str = str + field + " " + op + " '" + value + "'";
        	
        	return str;
        }
    }
}
