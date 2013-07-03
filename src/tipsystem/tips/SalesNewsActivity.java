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

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SalesNewsActivity extends Activity implements OnItemClickListener, 
														OnItemSelectedListener, 
														OnTabChangeListener,
														DatePickerDialog.OnDateSetListener{

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	TextView m_realSales;
	TextView m_viewNumber;
	TextView m_viewKNumber;
	TextView m_viewRealSalesYesterday;
	TextView m_viewPrice;
	TextView m_viewCash;
	TextView m_viewCard;
	TextView m_viewCredit;
	TextView m_viewOther;
	
	TabHost m_tabHost;
	
	ListView m_listNewsTab1;
	ListView m_listNewsTab2;
	ListView m_listNewsTab3;
	
	Spinner m_spinClassification1;
	Spinner m_spinClassification2;
	Spinner m_spinClassification3;
	
	DatePicker m_datePicker;
	Button m_buttonSetDate;
	
	SimpleDateFormat m_dateFormatter;
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	NumberFormat m_numberFormat;
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_news);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

		m_numberFormat = NumberFormat.getInstance();
		
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
		
		m_buttonSetDate = (Button) findViewById(R.id.buttonSetDate);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		// 상단 텍스트 뷰
		m_realSales = (TextView)findViewById(R.id.textViewRealSales);
		m_viewKNumber = (TextView)findViewById(R.id.textViewKNumber);
		m_viewRealSalesYesterday = (TextView)findViewById(R.id.textViewRealSalesYesterday);
		m_viewPrice = (TextView)findViewById(R.id.textViewKPrice);
		m_viewCash = (TextView)findViewById(R.id.textViewCash);
		m_viewCard = (TextView)findViewById(R.id.textViewCard);
		m_viewCredit = (TextView)findViewById(R.id.textViewCredit);
		m_viewOther = (TextView)findViewById(R.id.textViewOther);

		m_listNewsTab1= (ListView)findViewById(R.id.listviewSalesNewsListTab1);
		m_listNewsTab2= (ListView)findViewById(R.id.listviewSalesNewsListTab2);
		m_listNewsTab3= (ListView)findViewById(R.id.listviewSalesNewsListTab3);
		
		m_listNewsTab2.setOnItemClickListener(this);
		
		m_spinClassification1 = (Spinner)findViewById(R.id.spinnerClassificationType1);
		m_spinClassification1.setOnItemSelectedListener(this);
		
		m_spinClassification2 = (Spinner)findViewById(R.id.spinnerClassificationType2);
		m_spinClassification2.setOnItemSelectedListener(this);
		
		m_spinClassification3 = (Spinner)findViewById(R.id.spinnerClassificationType3);
		m_spinClassification3.setOnItemSelectedListener(this);
		
		// 탭 부분
		m_tabHost = (TabHost) findViewById(R.id.tabhostSalesNews);
        m_tabHost.setup();
             
        TabHost.TabSpec spec = m_tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.tab1);
        spec.setIndicator("시간대별");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("거래처별");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("분류별");
        m_tabHost.addTab(spec);
        
        m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
        
        String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
	
 		queryCommonInformation();
 		
        m_tabHost.setCurrentTab(0);
        m_tabHost.setOnTabChangedListener(this);
 		
//		new MyAsyncTask ().execute("10", period1, period2);
//		new MyAsyncTask ().execute("11", period1, period2);
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
		actionbar.setTitle("매출속보");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales_news, menu);
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
		
		if ( m_listNewsTab2.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, CustomerProductDetailInNewsActivity.class);
			
			String code = ((TextView) arg1.findViewById(R.id.item1)).getText().toString();
			String name = ((TextView) arg1.findViewById(R.id.item2)).getText().toString();
			
			String period1 = m_buttonSetDate.getText().toString();
			
	    	intent.putExtra("PERIOD1", period1);
	    	intent.putExtra("OFFICE_CODE", code);
	    	intent.putExtra("OFFICE_NAME", name);

	    	startActivity(intent);	
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		if ( arg0.getId() == m_spinClassification1.getId() )
		{
			String typeL = m_spinClassification1.getItemAtPosition(arg2).toString();
			
			queryComboBoxInTab3(1, typeL, "", "");
		}
		else if ( arg0.getId() == m_spinClassification2.getId() )
		{
			String typeL = m_spinClassification1.getItemAtPosition(m_spinClassification1.getSelectedItemPosition()).toString();
			String typeM = m_spinClassification2.getItemAtPosition(arg2).toString();
			
			queryComboBoxInTab3(2, typeL, typeM, "");
		}
		else if ( arg0.getId() == m_spinClassification3.getId() )
		{			
			String typeL = m_spinClassification1.getItemAtPosition(m_spinClassification1.getSelectedItemPosition()).toString();
			String typeM = m_spinClassification2.getItemAtPosition(m_spinClassification2.getSelectedItemPosition()).toString();
			String typeS = m_spinClassification3.getItemAtPosition(arg2).toString();
			
			queryListForTab3(typeL, typeM, typeS);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	public void onClickSetDate(View view)
	{
		DatePickerDialog newDlg = new DatePickerDialog(this, this,
				m_dateCalender1.get(Calendar.YEAR),
				m_dateCalender1.get(Calendar.MONTH),
				m_dateCalender1.get(Calendar.DAY_OF_MONTH));
		 newDlg.show();
	}
	
	public void onClickSetDatePrevious(View view)
	{
		m_dateCalender1.add(Calendar.DAY_OF_MONTH, -1);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
		
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
		
 		queryCommonInformation();

 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryComboBoxInTab3(0, "", "", ""); break;
 		}
	}
	
	public void onClickSetDateNext(View view)
	{
		m_dateCalender1.add(Calendar.DAY_OF_MONTH, 1);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, 1);
		
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		queryCommonInformation();

 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryComboBoxInTab3(0, "", "", ""); break;
 		}
	}
	
	@Override
	public void onTabChanged(String tabId) {
		
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
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
	 			queryComboBoxInTab3(0, "", "", ""); break;
 		}
	};
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
		m_dateCalender1.set(year, monthOfYear, dayOfMonth);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		m_dateCalender2.set(year, monthOfYear, dayOfMonth);
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
				
  		// 로딩 다이알로그 
    	dialog = new ProgressDialog(SalesNewsActivity.this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 			
 		queryCommonInformation();

 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryComboBoxInTab3(0, "", "", ""); break;
 		}
	}
	
	
	private void queryCommonInformation()
	{
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = null;
		String tableName2 = null;
		
		tableName = String.format("DF_%04d%02d", year1, month1);
		tableName2 = String.format("DF_%04d%02d", year2, month2);
		
		if ( tableName.equals(tableName2) == true )
		{
			query = "select TSell_Pri, Sale_Num, Sale_Pri, Cash_Pri, Card_Pri, Dec_Pri, Sale_Date from " + tableName;
			query = query + " where Sale_Date between '" + period2 + "' and '" + period1 + "'";
		}
		else
		{
			query = "select TSell_Pri, Sale_Num, Sale_Pri, Cash_Pri, Card_Pri, Dec_Pri, Sale_Date from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "';";
			
			query = query + " select TSell_Pri, Sale_Date from " + tableName2;
			query = query + " where Sale_Date = '" + period2 + "'";
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateCommonInformation(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	private void updateCommonInformation(JSONArray results)
	{
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		try {
		
			if ( results.length() > 0 )
			{
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					String sDate = son.getString("Sale_Date");
					
					if ( sDate.equals(period1) == true )
					{
						m_realSales.setText(m_numberFormat.format(son.getInt("TSell_Pri")));
		    			
		    			m_viewKNumber.setText(m_numberFormat.format(son.getInt("Sale_Num")));
		    			
		    			m_viewPrice.setText(m_numberFormat.format(son.getInt("Sale_Pri")));
		    			m_viewCash.setText(m_numberFormat.format(son.getInt("Cash_Pri")));
		    			m_viewCard.setText(m_numberFormat.format(son.getInt("Card_Pri")));
		    			m_viewCredit.setText(m_numberFormat.format(son.getInt("Dec_Pri")));
		    			
		    			m_viewOther.setText(m_numberFormat.format(0));	
					}
					else if ( sDate.equals(period2) == true )
					{
						m_viewRealSalesYesterday.setText(m_numberFormat.format(son.getInt("TSell_Pri")));
					}
				}	
			}
			else 
			{
				m_realSales.setText(m_numberFormat.format(0));
				m_viewKNumber.setText(m_numberFormat.format(0));
				m_viewPrice.setText(m_numberFormat.format(0));
				m_viewCash.setText(m_numberFormat.format(0));
				m_viewCard.setText(m_numberFormat.format(0));
				m_viewCredit.setText(m_numberFormat.format(0));
				m_viewOther.setText(m_numberFormat.format(0));
				
			}
			
			dialog.cancel();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
		}
	}
	
	
	private void queryListForTab1()
	{
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = null;
		String tableName2 = null;
		
		tableName = String.format("SaT_%04d%02d", year1, month1);
		tableName2 = String.format("SaT_%04d%02d", year2, month2);
		
		if ( tableName.equals(tableName2) == true )
		{
			query = "select Sale_Time, TSell_Pri, TSell_RePri, DC_Pri, Sale_Date from " + tableName;
			query = query + " where Sale_Date between '" + period2 + "' and '" + period1 + "'";
		}
		else
		{
			query = "select Sale_Time, TSell_Pri, TSell_RePri, DC_Pri, Sale_Date from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "';";
			
			query = query + " select Sale_Time, TSell_Pri, TSell_RePri, DC_Pri, Sale_Date from " + tableName2;
			query = query + " where Sale_Date = '" + period2 + "'";
		}
	
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateListForTab1(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
	}
	
	
	private void updateListForTab1(JSONArray results)
	{
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		String[] from = new String[] {"Sale_Time", "rSale", "rSale_Yes", "rDSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		try {
			
			if ( results.length() > 0 )
			{
		        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
		        int [] rSale = new int [25];
		        int [] rSale1 = new int [25];
		        int [] rDSale = new int [25];
		        
		        for ( int i = 0; i < 25; i++ )
		        {
		        	rSale[i] = 0;
		        	rSale1[i] = 0;
		        	rDSale[i] = 0;
		        }
	 			
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					String sDate = son.getString("Sale_Date");
					
					if ( sDate.equals(period1) == true )
					{
						String tTime = son.getString("Sale_Time");
						
						int iTime = Integer.parseInt(tTime.substring(0, 2));
						
						int itSell = son.getInt("TSell_Pri");
						int itRSell = son.getInt("TSell_RePri");
						int idcPri = son.getInt("DC_Pri");
						int irSale = itSell - (itRSell + idcPri);
									
						rSale[iTime] = rSale[iTime] + irSale;
					}
					else if ( sDate.equals(period2) == true )
					{
						String tTime = son.getString("Sale_Time");
						
						int iTime = Integer.parseInt(tTime.substring(0, 2));
						
						int itSell = son.getInt("TSell_Pri");
						int itRSell = son.getInt("TSell_RePri");
						int idcPri = son.getInt("DC_Pri");
						int irSale = itSell - (itRSell + idcPri);
						            				
						rSale1[iTime] = rSale1[iTime] + irSale;
					}
				}	
				
				for ( int i = 0; i < 24; i++ )
		        {
		    		rDSale[i] = rSale[i] - rSale1[i];
		    		
		        	// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("Sale_Time", String.format("%02d", i));
		            map.put("rSale", m_numberFormat.format(rSale[i]));
		            map.put("rSale_Yes", m_numberFormat.format(rSale1[i]));
		            map.put("rDSale", m_numberFormat.format(rDSale[i]));
		            fillMaps.add(map);
		        }
				
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listNewsTab1.setAdapter(adapter);
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listNewsTab1.setAdapter(adapter);
				
			}
			
			dialog.cancel();
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
		}
	}
	
	private void queryListForTab2()
	{
		String period1 = m_buttonSetDate.getText().toString();
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
		
		query = "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri, ProFit_Pri from " + tableName;
		query = query + " where Sale_Date = '" + period1 + "';";
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateListForTab2(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	
	private void updateListForTab2(JSONArray results)
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
				
				m_listNewsTab2.setAdapter(adapter);
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
						from, to);
				
				m_listNewsTab2.setAdapter(adapter);
				
			}
			
			dialog.cancel();
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
		}
	}
	
	
	private void queryListForTab3(String... urls)
	{
		String period1 = m_buttonSetDate.getText().toString();
		
		final String typeL = urls[0];
		final String typeM = urls[1];
		final String typeS = urls[2];
		
		String query = "";
		String constraint = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
		
		if ( typeL.equals("") != true && typeL.equals("전체") != true)
		{
			constraint = setConstraint(constraint, "L_Name", "=", typeL);
		}
		
		if ( typeM.equals("") != true && typeM.equals("전체") != true)
		{
			constraint = setConstraint(constraint, "M_Name", "=", typeM);
		}
		
		if ( typeS.equals("") != true && typeS.equals("전체") != true)
		{
			constraint = setConstraint(constraint, "S_Name", "=", typeS);
		}
		
		query = "select TSell_Pri, TSell_RePri, DC_Pri, Sale_Count, ProFit_Pri, L_Name, M_Name, S_Name from " + tableName;
		query = query + " where Sale_Date = '" + period1 + "'";

		if ( constraint.equals("") != true )
		{
			query = query + " and " + constraint;
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateListForTab3(results, typeL, typeM, typeS);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	
	private void updateListForTab3(JSONArray results, String typeL, String typeM, String typeS)
	{
		String[] from = new String[] {"Count", "T_Name", "rSale", "Sale_Count", "ProFit_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5 };
        
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
				ArrayList<String> lSpListType = new ArrayList<String>();
		        ArrayList<Integer> lSpListSale = new ArrayList<Integer>();
		        ArrayList<Integer> lSpListSaleCnt = new ArrayList<Integer>();
		        ArrayList<Integer> lSpListProfit = new ArrayList<Integer>();
		        
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					String sName = "";
					
					if ( typeL.equals("전체") == true )
					{
						sName = son.getString("L_Name");
					}
					else if ( typeM.equals("전체") == true )
					{
						sName = son.getString("M_Name");
					}
					else if ( typeS.equals("전체") == true )
					{
						sName = son.getString("S_Name");
					}
					else
					{
						sName = son.getString("S_Name");
					}
				
					int tSell = son.getInt("TSell_Pri");
					int tRSell = son.getInt("TSell_RePri");
					int dcPri = son.getInt("DC_Pri");
					
					boolean isExist = false;
	           		
	        		for ( int i = 0; i < lSpListType.size(); i++ )
	        		{
	        			if ( lSpListType.get(i).toString().equals(sName) == true )
	        			{
	        				Integer rsale = lSpListSale.get(i).intValue() + ((tSell - (tRSell + dcPri)));
	        				Integer sCount = lSpListSaleCnt.get(i).intValue() + son.getInt("Sale_Count");
	        				Integer sProfit = lSpListProfit.get(i).intValue() + son.getInt("ProFit_Pri");
	        				
	        				lSpListSale.set(i, rsale);
	        				lSpListSaleCnt.set(i, sCount);
	        				lSpListProfit.set(i, sProfit);
	        				
	        				isExist = true;
	        				
	        				break;
	        			}
	        		}
	        		
	        		if ( isExist == false )
	        		{
	        			Integer rsale = tSell - (tRSell + dcPri);
	    				Integer profit = son.getInt("ProFit_Pri");
	    				Integer saleCnt = son.getInt("Sale_Count");
	    				
	    				lSpListType.add(sName);
	    				lSpListSale.add(rsale);
	    				lSpListSaleCnt.add(saleCnt);
	    				lSpListProfit.add(profit);
	        		}
				}	
				
				
				for ( int i = 0; i < lSpListType.size(); i++ )
				{
					// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("Count", String.format("%d", i+1));
		            map.put("T_Name", lSpListType.get(i));
		            map.put("rSale", m_numberFormat.format(lSpListSale.get(i).intValue()) );
		            map.put("Sale_Count", m_numberFormat.format(lSpListSaleCnt.get(i).intValue()) );
		            map.put("ProFit_Pri", m_numberFormat.format(lSpListProfit.get(i).intValue()) );
		            fillMaps.add(map);	
				}
				
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
						from, to);
				
				m_listNewsTab3.setAdapter(adapter);
				
			}
			else 
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
						from, to);
				
				m_listNewsTab3.setAdapter(adapter);
				
				
			}
			
			dialog.cancel();
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
		}
	
	}
	
	private void queryComboBoxInTab3(final int iCombo, String... urls)
	{
		String period1 = m_buttonSetDate.getText().toString();
		String query = "";
		String constraint = "";
		
		String typeL = urls[0];
		String typeM = urls[1];
		String typeS = urls[2];
		
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
		
		if ( iCombo == 0 )
		{
			query = "select L_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
		}
		else if ( iCombo == 1 )
		{
			
			if ( typeL.equals("") != true && typeL.equals("전체") != true )
			{
				constraint = setConstraint(constraint, "L_Name", "=", typeL);
			}
				
			query = "select M_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
			
			if ( constraint.equals("") != true )
			{
				query = query + " and " + constraint;
			}
			
		}
		else if ( iCombo == 2 )
		{
			if ( typeL.equals("") != true && typeL.equals("전체") != true )
			{
				constraint = setConstraint(constraint, "L_Name", "=", typeL);
			}
			
			if ( typeM.equals("") != true && typeM.equals("전체") != true)
			{
				constraint = setConstraint(constraint, "M_Name", "=", typeM);
			}
			
			query = "select S_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
			
			if ( constraint.equals("") != true )
			{
				query = query + " and " + constraint;
			}
			
		}
		

		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateComboBoxInTab3(results, iCombo);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);

	}
	
	private void updateComboBoxInTab3(JSONArray results, int iCombo)
	{
		ArrayList<String> lSpList = new ArrayList<String>();       
		
		try {
			
			if ( results.length() > 0 )
			{
				 		   	        
				String lName = null;
				
				lSpList.add("전체");
		        
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					if ( iCombo == 0 )
					{
						lName = son.getString("L_Name");
					}
					else if ( iCombo == 1 )
					{
						lName = son.getString("M_Name");
					}
					else if ( iCombo == 2 )
					{
						lName = son.getString("S_Name");
					}
					
	        		boolean isExist = false;
	        		
	        		for ( int i = 0; i < lSpList.size(); i++ )
	        		{
	        			if ( lSpList.get(i).toString().equals(lName) == true )
	        			{
	        				isExist = true;
	        				break;
	        			}
	        		}
	        		
	        		if ( isExist == true )
	        		{
	        			continue;
	        		}

		            lSpList.add(lName);
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SalesNewsActivity.this, android.R.layout.simple_spinner_item, lSpList);
				
				if ( iCombo == 0 )
				{
					m_spinClassification1.setAdapter(adapter);
				}
				else if ( iCombo == 1 )
				{
					m_spinClassification2.setAdapter(adapter);
				}
				else if ( iCombo == 2 )
				{
					m_spinClassification3.setAdapter(adapter);
				}
			}
			else 
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SalesNewsActivity.this, android.R.layout.simple_spinner_item, lSpList);
				
				if ( iCombo == 0 )
				{
					m_spinClassification1.setAdapter(adapter);
				}
				else if ( iCombo == 1 )
				{
					m_spinClassification2.setAdapter(adapter);
				}
				else if ( iCombo == 2 )
				{
					m_spinClassification3.setAdapter(adapter);
				}
				
			}
			
			dialog.cancel();
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
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
