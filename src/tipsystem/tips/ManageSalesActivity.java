package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ArrayAdapter;
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

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int BARCODE_MANAGER_REQUEST = 2;
	private static final int CUSTOMER_MANAGER_REQUEST = 3;

	JSONObject m_shop;
	JSONObject m_userProfile;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	String m_APP_USER_GRADE;
	String m_OFFICE_CODE;	// 수수료매장일때 고정될 오피스코드
	String m_OFFICE_NAME;	// 수수료매장일때 고정될 오피스코드
	
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
	
	String m_CalendarDay;
	CalendarView m_calendar;
		
	DatePicker m_datePicker;
	
	SimpleDateFormat m_dateFormatter;
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	int m_dateMode=0;
	
	NumberFormat m_numberFormat;
	
	ProgressDialog dialog;
	
	//List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
    //ArrayList<CustomerList> customerArray = new ArrayList<CustomerList>();
    int index = 0;
    int size = 100;
    int firstPosition = 0;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_sales);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
		m_userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
		
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
			m_APP_USER_GRADE =m_userProfile.getString("APP_USER_GRADE");
			m_OFFICE_CODE = m_userProfile.getString("OFFICE_CODE");
			m_OFFICE_NAME = m_userProfile.getString("OFFICE_NAME");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		m_period1 = (Button) findViewById(R.id.buttonSetDate1); 
		m_period2 = (Button) findViewById(R.id.buttonSetDate2);
		m_barCode = (TextView) findViewById(R.id.editTextBarcode);
		m_productName = (TextView) findViewById(R.id.editTextProductName);
		m_customerCode = (TextView) findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView) findViewById(R.id.editTextCustomerName);
				
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		m_numberFormat = NumberFormat.getInstance();
				
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
		
		m_CalendarDay = m_dateFormatter.format(m_dateCalender1.getTime());
				
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
        
        m_listSalesTab1.setOnItemClickListener(this);
        m_listSalesTab3.setOnItemClickListener(this);
        
        // 바코드 입력 후 포커스 딴 곳을 옮길 경우
        m_barCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
 			//@Override
 			public void onFocusChange(View v, boolean hasFocus) {
 			    if(!hasFocus){
 			    	String barcode = null; 
 			    	barcode = m_barCode.getText().toString();
 			    	if(!barcode.equals("")) // 입력한 Barcode가 값이 있을 경우만
 			    		doQueryWithBarcode();
 			    }
 			}
 		});

 		// 거래처 코드 입력 후 포커스 딴 곳을 옮길 경우
        m_customerCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
 			//@Override
 			public void onFocusChange(View v, boolean hasFocus) {
 			    if(!hasFocus){
 			    	String customerCode = m_customerCode.getText().toString();
 			    	if(!customerCode.equals("")) // 입력한 customerCode가 값이 있을 경우만
 			    		fillBusNameFromBusCode(customerCode);	    	
 			    }
 			}
 		});	
        
        //수수료매장의 경우 오피스코드 고정
        if (m_APP_USER_GRADE.equals("2")) {
        	Button buttonCustomer = (Button) findViewById(R.id.buttonCustomer);
        	buttonCustomer.setEnabled(false);
        	m_customerCode.setEnabled(false);
        	m_customerCode.setText(m_OFFICE_CODE); 
        	m_customerName.setText(m_OFFICE_NAME); 
	    	//fillBusNameFromBusCode(m_OFFICE_CODE);	    	      	
        }
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
			
			//String period1 = m_period1.getText().toString();
			//String period2 = m_period2.getText().toString();
			
	    	intent.putExtra("PERIOD1", m_period1.getText().toString());
	    	intent.putExtra("PERIOD2", m_period2.getText().toString());
	    	
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
	
		m_CalendarDay = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryListForTab3(); break;
	 		case 3:
	 			queryListForTab4(m_CalendarDay); break;
 		}
		
	}
	
	public void OnClickRenew(View v) {

        if (!m_APP_USER_GRADE.equals("2")) {
    		m_customerCode.setText("");
    		m_customerName.setText("");        	
        }
		m_barCode.setText("");
		m_productName.setText("");
	}

	public void OnClickSearch(View v) {
		//Toast.makeText(this, "Search Click.", Toast.LENGTH_SHORT).show();		
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryListForTab3(); break;
	 		case 3:
	 			queryListForTab4(m_CalendarDay); break;
 		}
	};
	
	    
	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Tab Click.", Toast.LENGTH_SHORT).show();
	
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryListForTab3(); break;
	 		case 3:
	 			queryListForTab4(m_CalendarDay); break;
 		}
 		
	}

	public void onClickSetDate1(View v) {
				
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
	
	
	
	private void queryListForTab1()
	{
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String barCode = m_barCode.getText().toString();
		String productName = m_productName.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = null;

		String constraint = "";
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				
				tableName = String.format("SaD_%04d%02d", y, m);
				
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
				
				query = query + "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri, ProFit_Pri, Sale_Date from " + tableName;
				query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
				
				if ( constraint.equals("") != true )
				{
					query = query + " and " + constraint;
				}
				
				query = query + "; ";
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				updateListForTab1(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
	}
		
	private void updateListForTab1(JSONArray results)
	{

		String[] from = new String[] {"Office_Code", "Office_Name", "rSale", "ProFit_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
				ArrayList<String> lSpListCode = new ArrayList<String>();
    	        ArrayList<String> lSpListName = new ArrayList<String>();
    	        ArrayList<Integer> lSpListSale = new ArrayList<Integer>();
    	        ArrayList<Integer> lSpListProfit = new ArrayList<Integer>();
	 			
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					String code = son.getString("Office_Code");
    				String name = son.getString("Office_Name");
    				int tSell = son.getInt("TSell_Pri");
    				int tRSell = son.getInt("TSell_RePri");
    				int dcPri = son.getInt("DC_Pri");
    				        				
    				boolean isExist = false;
               		
            		for ( int i = 0; i < lSpListCode.size(); i++ )
            		{
            			if ( lSpListCode.get(i).toString().equals(code) == true )
            			{
            				Integer rsale = lSpListSale.get(i).intValue() + ((tSell - (tRSell + dcPri)));
            				Integer profit = lSpListProfit.get(i).intValue() + son.getInt("ProFit_Pri");
            				
            				lSpListSale.set(i, rsale);
            				lSpListProfit.set(i, profit);
            				
            				isExist = true;
            				break;
            			}
            		}
            		
            		if ( isExist == false )
            		{
            			Integer rsale = tSell - (tRSell + dcPri);
        				Integer profit = son.getInt("ProFit_Pri");
        				
        				lSpListCode.add(code);
        				lSpListName.add(name);
        				lSpListSale.add(rsale);
        				lSpListProfit.add(profit);
            		}
				}	
				
				for ( int i = 0; i < lSpListCode.size(); i++ )
        		{
        			// prepare the list of all records
    	            HashMap<String, String> map = new HashMap<String, String>();
    	            
    	            map.put("Office_Code", lSpListCode.get(i));
    	            map.put("Office_Name", lSpListName.get(i));
    	            map.put("rSale", m_numberFormat.format(lSpListSale.get(i).intValue()) );
    	            map.put("ProFit_Pri", m_numberFormat.format(lSpListProfit.get(i).intValue()));
    	            fillMaps.add(map);
        		}
        		
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listSalesTab1.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "조회 완료: " + lSpListCode.size(), Toast.LENGTH_SHORT).show();
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listSalesTab1.setAdapter(adapter);
				
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void queryListForTab2()
	{
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String barCode = m_barCode.getText().toString();
		String productName = m_productName.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = null;
		String constraint = "";
		
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				
				tableName = String.format("SaD_%04d%02d", y, m);
				
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
				
    			query = query + "select Barcode, G_Name, Sale_Count, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
    			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
   			
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
				
				query = query + "; ";
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				updateListForTab2(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
	}
		
	private void updateListForTab2(JSONArray results)
	{

		String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
				ArrayList<String> lSpListCode = new ArrayList<String>();
		        ArrayList<String> lSpListName = new ArrayList<String>();
		        ArrayList<Integer> lSpListSale = new ArrayList<Integer>();
		        ArrayList<Integer> lSpListSaleCnt = new ArrayList<Integer>();
	 			
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					String code = son.getString("Barcode");
					String name = son.getString("G_Name");
					int tSell = son.getInt("TSell_Pri");
					int tRSell = son.getInt("TSell_RePri");
					int dcPri = son.getInt("DC_Pri");

					boolean isExist = false;
	           		
	        		for ( int i = 0; i < lSpListCode.size(); i++ )
	        		{
	        			if ( lSpListCode.get(i).toString().equals(code) == true )
	        			{
	        				Integer rsale = lSpListSale.get(i).intValue() + ((tSell - (tRSell + dcPri)));
	        				Integer profit = lSpListSaleCnt.get(i).intValue() + son.getInt("Sale_Count");
	        				
	        				lSpListSale.set(i, rsale);
	        				lSpListSaleCnt.set(i, profit);
	        				
	        				isExist = true;
	        				break;
	        			}
	        		}
	        		
	        		if ( isExist == false )
	        		{
	        			Integer rsale = tSell - (tRSell + dcPri);
	    				Integer count = son.getInt("Sale_Count");
	    				
	    				lSpListCode.add(code);
	    				lSpListName.add(name);
	    				lSpListSale.add(rsale);
	    				lSpListSaleCnt.add(count);
	        		}
				}	
				
				for ( int i = 0; i < lSpListCode.size(); i++ )
				{
					// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            
		            map.put("Barcode", lSpListCode.get(i));
		            map.put("G_Name", lSpListName.get(i));
		            map.put("Sale_Count", String.format("%d", lSpListSaleCnt.get(i).intValue()));
		            map.put("rSale", m_numberFormat.format(lSpListSale.get(i).intValue()));
		            fillMaps.add(map);
				}
        		
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listSalesTab2.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "조회 완료: " + lSpListCode.size(), Toast.LENGTH_SHORT).show();
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listSalesTab2.setAdapter(adapter);
				
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
				
			}			
		} catch (JSONException e) {
			e.printStackTrace();			
		}
		
	}
	
	
	private void queryListForTab3()
	{
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String barCode = m_barCode.getText().toString();
		String productName = m_productName.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = null;
		String tableName2 = null;
		String constraint = "";
		
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				
				tableName = String.format("SaD_%04d%02d", y, m);
				
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
				
    			query = query + "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
    			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
   			
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
    			
				query = query + "; ";
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				updateListForTab3(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
	}
		
	private void updateListForTab3(JSONArray results)
	{

		String[] from = new String[] {"Office_Code", "Office_Name", "rSale"};
	    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
        
		
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
				ArrayList<String> lSpListCode = new ArrayList<String>();
			    ArrayList<String> lSpListName = new ArrayList<String>();
			    ArrayList<Integer> lSpListSale = new ArrayList<Integer>();
	 			
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					String code = son.getString("Office_Code");
					String name = son.getString("Office_Name");
					int tSell = son.getInt("TSell_Pri");
					int tRSell = son.getInt("TSell_RePri");
					int dcPri = son.getInt("DC_Pri");
				
					boolean isExist = false;
		       		
		    		for ( int i = 0; i < lSpListCode.size(); i++ )
		    		{
		    			if ( lSpListCode.get(i).toString().equals(code) == true )
		    			{
		    				Integer rsale = lSpListSale.get(i).intValue() + ((tSell - (tRSell + dcPri)));
		    				
		    				lSpListSale.set(i, rsale);
		    				
		    				isExist = true;
		    				break;
		    			}
		    		}
		    		
		    		if ( isExist == false )
		    		{
		    			Integer rsale = tSell - (tRSell + dcPri);
						
						lSpListCode.add(code);
						lSpListName.add(name);
						lSpListSale.add(rsale);
		    		}
				}	
				
				for ( int i = 0; i < lSpListCode.size(); i++ )
				{
					// prepare the list of all records
			        HashMap<String, String> map = new HashMap<String, String>();
			        
			        map.put("Office_Code", lSpListCode.get(i));
			        map.put("Office_Name", lSpListName.get(i));
			        map.put("rSale", m_numberFormat.format(lSpListSale.get(i).intValue()) );
			        fillMaps.add(map);
				}
        		
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3, 
						from, to);
				
				m_listSalesTab3.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "조회 완료: " + lSpListCode.size(), Toast.LENGTH_SHORT).show();
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3, 
						from, to);
				
				m_listSalesTab3.setAdapter(adapter);
				
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void queryListForTab4(String period)
	{
		String query = "";
	    
		int year1 = Integer.parseInt(period.substring(0, 4));
		int month1 = Integer.parseInt(period.substring(5, 7));
		
		String tableName = null;

		tableName = String.format("DF_%04d%02d", year1, month1);
		
		query = "select TSell_Pri, Sale_Num, Sale_Pri, TPur_Pri from " + tableName;
		query = query + " where Sale_Date = '" + period + "'";
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				updateListForTab4(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
	}
		
	private void updateListForTab4(JSONArray results)
	{

		String[] from = new String[] {"content"};
	    int[] to = new int[] { R.id.textView1};
        
		
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					String rSale = String.format("순매출 : %s원", m_numberFormat.format(son.getInt("TSell_Pri")));
					String saleNum = String.format("객 수 : %s명", m_numberFormat.format(son.getInt("Sale_Num")));
					String salePri = String.format("객단가 : %s원", m_numberFormat.format(son.getInt("Sale_Pri")));
					String tPurPri = String.format("매입금액 : %s원", m_numberFormat.format(son.getInt("TPur_Pri")));
					
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
		            
				}	
				
				
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_text, 
						from, to);
				
				m_listSalesTab4.setAdapter(adapter);
				
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_text, 
						from, to);
				
				m_listSalesTab4.setAdapter(adapter);
				
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
	

 	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			// 카메라 스캔을 통한 바코드 검색
			case ZBAR_SCANNER_REQUEST :
				if (resultCode == RESULT_OK) {
			        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
			        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
			        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
			        // The value of type indicates one of the symbols listed in Advanced Options below.
			       
			        String barcode = data.getStringExtra(ZBarConstants.SCAN_RESULT);
			        m_barCode.setText(barcode);
					doQueryWithBarcode();
					
			    } else if(resultCode == RESULT_CANCELED) {
			        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
			    }
				break;
			// 목록 검색을 통한 바코드 검색				
			case BARCODE_MANAGER_REQUEST :
				if(resultCode == RESULT_OK && data != null) {
					
		        	ArrayList<String> fillMaps = data.getStringArrayListExtra("fillmaps");		        	
		        	m_barCode.setText(fillMaps.get(0));
					doQueryWithBarcode(); 
		        }
				break;
			case CUSTOMER_MANAGER_REQUEST :
				if(resultCode == RESULT_OK && data != null) {
					String result = data.getStringExtra("result");
					try {
						JSONObject json = new JSONObject(result);
						m_customerCode.setText(json.getString("Office_Code"));
						m_customerName.setText(json.getString("Office_Name"));
			        	//m_textBarcode.setText(fillMaps.get(0));
					} catch (JSONException e) {
						e.printStackTrace();
					}
		        }
				break;
			}
	}
	
	public void onCustomerSearch(View view)
	{
		Intent intent = new Intent(this, ManageCustomerListActivity.class);
    	startActivityForResult(intent, CUSTOMER_MANAGER_REQUEST);
	}
	
	public void onBarcodeSearch(View view)
	{
		// 바코드 검색 버튼 클릭시 나오는 목록 셋팅
		final String[] option = new String[] { "목록", "카메라"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Option");
		
		// 목록 선택시 이벤트 처리
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				if(which == 0){ // 목록으로 조회할 경우
					Intent intent = new Intent(ManageSalesActivity.this, ManageProductListActivity.class);
			    	startActivityForResult(intent, BARCODE_MANAGER_REQUEST);
				} else { // 스캔할 경우
					Intent intent = new Intent(ManageSalesActivity.this, ZBarScannerActivity.class);
			    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);				
				}
			}
		}); 
		builder.show();
	}
	

	// MSSQL
	// SQL QUERY 실행
	public void doQueryWithBarcode () {
		
		String query = "";
		String barcode = m_barCode.getText().toString();
		query = "SELECT * FROM Goods WHERE Barcode = '" + barcode + "';";
	
		if (barcode.equals("")) return;
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();

	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				
				if (results.length() > 0) {
					try {						
						m_productName.setText(results.getJSONObject(0).getString("G_Name"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}
	
	// 거래처 코드로 거래처명 자동 완성
		private void fillBusNameFromBusCode(String customerCode) {
			// TODO Auto-generated method stub
			// 로딩 다이알로그 
	    	dialog = new ProgressDialog(this);
	 		dialog.setMessage("Loading....");
	 		dialog.setCancelable(false);
	 		dialog.show();
	 		
			// TODO Auto-generated method stub
			String query = "";
			
			query = "SELECT Office_Name From Office_Manage WHERE Office_Code = '" + customerCode + "';";
		    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

				@Override
				public void onRequestCompleted(JSONArray results) {
					dialog.dismiss();
					dialog.cancel();
					if (results.length() > 0) {
						try {
							JSONObject json = results.getJSONObject(0);
							String bus_name = json.getString("Office_Name");
							m_customerName.setText(bus_name);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
					}
					else {
			            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();					
					}
				}
		    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		}
}