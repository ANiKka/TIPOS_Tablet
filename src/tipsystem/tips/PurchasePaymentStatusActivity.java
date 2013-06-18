package tipsystem.tips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.tips.ManageSalesActivity.MyAsyncTask;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
public class PurchasePaymentStatusActivity extends Activity implements OnItemClickListener, 
																		OnDateChangeListener,
																		OnTabChangeListener,
																		DatePickerDialog.OnDateSetListener{
	
	TabHost m_tabHost;
	
	ListView m_listPurchaseTab1;
	ListView m_listPurchaseTab2;
	ListView m_listPurchaseTab3;
	
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
	String m_rBarCode = null;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_payment_status);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_period1 = (Button) findViewById(R.id.buttonSetDate1); 
		m_period2 = (Button) findViewById(R.id.buttonSetDate2);
		m_barCode = (TextView) findViewById(R.id.editTextBarcode);
		m_productName = (TextView) findViewById(R.id.editTextProductName);
		m_customerCode = (TextView) findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView) findViewById(R.id.editTextCustomerName);
		
		m_listPurchaseTab1= (ListView)findViewById(R.id.listviewPurchaseListTab1);
		m_listPurchaseTab2= (ListView)findViewById(R.id.listviewPurchaseListTab2);
		m_listPurchaseTab3= (ListView)findViewById(R.id.listviewPurchaseListTab3);
		
		m_listPurchaseTab1.setOnItemClickListener(this);
		m_listPurchaseTab2.setOnItemClickListener(this);
		m_listPurchaseTab3.setOnItemClickListener(this);
		
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		
		
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
				
		m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		
		
		
		
		
		m_tabHost = (TabHost) findViewById(R.id.tabhostPurchasePaymentStatus);
        m_tabHost.setup();
             
        TabHost.TabSpec spec = m_tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.tab1);
        spec.setIndicator("매입목록");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("결제현황");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("매입/매출");
        m_tabHost.addTab(spec);
     
        m_tabHost.setCurrentTab(0);

       
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView6);
        textView.setTypeface(typeface);
	}
	
	private void setTabList1(JSONArray results)
	{
		
		
		 // create the grid item mapping
		String[] from = new String[] {"In_Num", "In_Date", "Office_Name", "In_Pri"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
	
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		
		for(int i = 0; i < results.length() ; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("전표번호", "0000" + i);
			map.put("매입일", "2013-05-0" + i);
			map.put("거래처명", "거래처명"+ i);
			map.put("총매입가", i + "000");
			fillMaps.add(map);
		}
		
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_product_list, 
				from, to);
		
		m_listPurchaseTab1.setAdapter(adapter);
		m_listPurchaseTab1.setOnItemClickListener(this);
	}
	
	
	
	private void setTabList2(JSONArray results)
	{
		
		
		 // create the grid item mapping
		String[] from = new String[] {"코드", "거래처명", "이월", "지급금액", "미지급금액"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		
		for(int i = 0; i < results.length() ; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("전표번호", "0000" + i);
			map.put("매입일", "2013-05-0" + i);
			map.put("거래처명", "거래처명"+ i);
			map.put("총매입가", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
				from, to);
		
		m_listPurchaseTab2.setAdapter(adapter);
		
	}
	
	private void setTabList3(JSONArray results)
	{
		
		
		 // create the grid item mapping
		String[] from = new String[] {"코드", "거래처명", "순매입", "순매출"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		
		for(int i = 0; i < results.length() ; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("전표번호", "0000" + i);
			map.put("매입일", "2013-05-0" + i);
			map.put("거래처명", "거래처명"+ i);
			map.put("총매입가", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listPurchaseTab3.setAdapter(adapter);
		m_listPurchaseTab3.setOnItemClickListener(this);
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
		actionbar.setTitle("매입/대금 결제현황");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_payment_status, menu);
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
		
		if ( m_listPurchaseTab1.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, PurchaseListDetailViewActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		else if ( m_listPurchaseTab2.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, PaymentDetailViewActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		else if ( m_listPurchaseTab3.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, CustomerPurchasePaymentDetailActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		
		
		//Toast.makeText(this, "Item Click." + m_listPurchaseTab1.getId() + " ,  " + arg0.getId(), Toast.LENGTH_SHORT).show();
		
		
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
		
		executeQuery(tabIndex, period1, period2, barCode, productName, customerCode, customerName);

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
		
		//new MyAsyncTask ().execute(tabIndex, period1, period2, barCode, productName, customerCode, customerName);
		
	
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

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		String period1 = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
		String tabIndex = String.format("%d", m_tabHost.getCurrentTab());
		
		//new MyAsyncTask().execute(tabIndex, period1, period1 , "", "", "", "");
		
	}

	
	private void executeQuery(String... urls)
	{
		
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
 		
 		String rBarCode = null;
 		
 		int iTabIndex = Integer.parseInt(tabIndex);
 		
 		if ( iTabIndex == 0 ) // 매입목록
 		{
 			// prepare the list of all records
 			final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
 			
	 		for ( int y = year1; y <= year2; y++ )
	 		{
	 			for ( int m = month1; m <= month2; m++ )
	 			{	
 					tableName = String.format("InD_%04d%02d", y, m);
 					
 					if ( productName.equals("") != true )
 					{
 						query = "select Barcode from Goods where G_Name = '" + productName + "'";
 						m_rBarCode = "";
 						
 						// 콜백함수와 함께 실행
 					    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

 							@Override
 							public void onRequestCompleted(JSONArray results) {
 								try {
 									
 									if ( results.length() > 0 )
 	 								{
 										m_rBarCode = results.getString(0);	
 	 								}
 									else 
 									{
 
 									}
 					    		} catch (JSONException e) {
 					    			e.printStackTrace();
 					    		}
 								
 							}
 					    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
 						
 					    if ( m_rBarCode.equals("") != true && barCode.equals("") == true )
 					    {
 					    	barCode = m_rBarCode;
 					    }
 					    else if ( m_rBarCode.equals("") == true && barCode.equals("") == true )
 					    {
 					    	barCode = "null";
 					    }
 						
 						//constraint = setConstraint(constraint, "G_Name", "=", productName);
 					}
 					
 					if ( barCode.equals("") != true )
 					{
 						constraint = setConstraint(constraint, "Barcode", "=", barCode);
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
         			query = query + " where In_Date between '" + period1 + "' and '" + period2 + "'";
        			
         			if ( constraint.equals("") != true )
         			{
         				query = query + " and " + constraint;
         			}
         			
         		// 콜백함수와 함께 실행
				    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

						@Override
						public void onRequestCompleted(JSONArray results) {
							try {
								
								if ( results.length() > 0 )
 								{
									// create the grid item mapping
						 			String[] from = new String[] {"In_Num", "In_Date", "Office_Name", "In_Pri"};
						 			int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
						 			
									for(int i = 0; i < results.length() ; i++)
									{
										JSONObject son = results.getJSONObject(i);
										
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("In_Num", son.getString("In_Num") );
										map.put("In_Date", son.getString("In_Date"));
										map.put("Office_Name", son.getString("Office_Name"));
										map.put("In_Pri", String.format("%d", son.getInt("In_Pri")) );
										fillMaps.add(map);
									}	
									// fill in the grid_item layout
									SimpleAdapter adapter = new SimpleAdapter(PurchasePaymentStatusActivity.this, 
											fillMaps, R.layout.activity_listview_product_list, 
											from, to);
									
									m_listPurchaseTab1.setAdapter(adapter);
									Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
									
 								}
				    		} catch (JSONException e) {
				    			e.printStackTrace();
				    		}
							
						}
				    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
						         			
         		}
 			}
 		}
 		else if ( iTabIndex == 1 ) // 결재현황
 		{
 		// prepare the list of all records
 			final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
 			
	 		for ( int y = year1; y <= year2; y++ )
	 		{
	 			for ( int m = month1; m <= month2; m++ )
	 			{	
 					tableName = String.format("InD_%04d%02d", y, m);
 					
 					if ( productName.equals("") != true )
 					{
 						query = "select Barcode from Goods where G_Name = '" + productName + "'";
 						m_rBarCode = "";
 						
 						// 콜백함수와 함께 실행
 					    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

 							@Override
 							public void onRequestCompleted(JSONArray results) {
 								try {
 									
 									if ( results.length() > 0 )
 	 								{
 										m_rBarCode = results.getString(0);	
 	 								}
 									else 
 									{
 
 									}
 					    		} catch (JSONException e) {
 					    			e.printStackTrace();
 					    		}
 								
 							}
 					    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
 						
 					    if ( m_rBarCode.equals("") != true && barCode.equals("") == true )
 					    {
 					    	barCode = m_rBarCode;
 					    }
 					    else if ( m_rBarCode.equals("") == true && barCode.equals("") == true )
 					    {
 					    	barCode = "null";
 					    }
 						
 						//constraint = setConstraint(constraint, "G_Name", "=", productName);
 					}
 					
 					if ( barCode.equals("") != true )
 					{
 						constraint = setConstraint(constraint, "Barcode", "=", barCode);
 					}
 					
 					if ( customerCode.equals("") != true )
 					{
 						constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
 					}
 					
 					if ( customerName.equals("") != true)
 					{
 						constraint = setConstraint(constraint, "Office_Name", "=", customerName);
 					}
 					 					
         			query = "select In_Num, In_Date, Office_Name, In_Pri from " + tableName;
         			query = query + " where In_Date between '" + period1 + "' and '" + period2 + "'";
        			
         			if ( constraint.equals("") != true )
         			{
         				query = query + " and " + constraint;
         			}
         			
         		// 콜백함수와 함께 실행
				    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

						@Override
						public void onRequestCompleted(JSONArray results) {
							try {
								
								if ( results.length() > 0 )
 								{
									// create the grid item mapping
						 			String[] from = new String[] {"In_Num", "In_Date", "Office_Name", "In_Pri"};
						 			int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
						 			
									for(int i = 0; i < results.length() ; i++)
									{
										JSONObject son = results.getJSONObject(i);
										
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("In_Num", son.getString("In_Num") );
										map.put("In_Date", son.getString("In_Date"));
										map.put("Office_Name", son.getString("Office_Name"));
										map.put("In_Pri", String.format("%d", son.getInt("In_Pri")) );
										fillMaps.add(map);
									}	
									// fill in the grid_item layout
									SimpleAdapter adapter = new SimpleAdapter(PurchasePaymentStatusActivity.this, 
											fillMaps, R.layout.activity_listview_product_list, 
											from, to);
									
									m_listPurchaseTab1.setAdapter(adapter);
									Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
									
 								}
				    		} catch (JSONException e) {
				    			e.printStackTrace();
				    		}
							
						}
				    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
						         			
         		}
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
    
    
    // DB에 접속후 호출되는 함수
    public void checkProductName(JSONArray results) {
    	if (results.length() > 0) {
    		// 저장소에 저장
    		//LocalStorage.setJSONArray(this, "PPSPrductResults", results);
    		
    		try {
    			m_rBarCode = results.getString(0);
    	     	
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    		 
    	}
    	else {
    		
    	}
    }
    
}
