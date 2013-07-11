package tipsystem.tips;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
//import tipsystem.utils.CalendarView;
//import tipsystem.utils.CalendarView.OnCellTouchListener;
//import tipsystem.utils.Cell;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
	
	SimpleAdapter adapter1;
	SimpleAdapter adapter2;
	SimpleAdapter adapter3;

	List<HashMap<String, String>> mfillMaps1 = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> mfillMaps2 = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> mfillMaps3 = new ArrayList<HashMap<String, String>>();
	
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
	
    int index = 0;
    int size = 100;
    int firstPosition = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_sales);
		
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
		
		String[] from1 = new String[] {"Office_Code", "Office_Name", "순매출", "이익금"};
        int[] to1 = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		adapter1 = new SimpleAdapter(this, mfillMaps1, R.layout.activity_listview_item4_2, from1, to1);		
		m_listSalesTab1.setAdapter(adapter1);	
		
		String[] from2 = new String[] {"Barcode", "G_Name", "수량", "순매출"};
        int[] to2 = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		adapter2 = new SimpleAdapter(this, mfillMaps2, R.layout.activity_listview_item4_2, from2, to2);		
		m_listSalesTab2.setAdapter(adapter2);	

		String[] from3 = new String[] {"Office_Code", "Office_Name", "순매출"};
	    int[] to3 = new int[] { R.id.item1, R.id.item2, R.id.item3 };
        
		adapter3 = new SimpleAdapter(this, mfillMaps3, R.layout.activity_listview_item3, from3, to3);		
		m_listSalesTab3.setAdapter(adapter3);
		
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

        /*
        m_calendar = (CalendarView)findViewById(R.id.calendarView1);

        m_calendar.setOnCellTouchListener(new OnCellTouchListener() {
        	
        	@Override
        	public void onTouch(Cell cell) {

    			int year  = m_calendar.getYear();
    			int month = m_calendar.getMonth();
    			int day   = cell.getDayOfMonth();
        		m_CalendarDay = String.format("%04d-%02d-%02d", year, month, day);

         		doQuery();
        	}
        });*/
        
        /* 3.0 이상 지원
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        m_calendar = (CalendarView)findViewById(R.id.calendarView1);
	        m_calendar.setOnDateChangeListener(new OnDateChangeListener() {
	
	            @Override
	            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
	
	        		m_CalendarDay = String.format("%04d-%02d-%02d", year, month+1, dayOfMonth);
	
	         		doQuery();
	            }
	        });
        }
       
        m_listSalesTab1.setOnItemClickListener(this);
        m_listSalesTab3.setOnItemClickListener(this);
        
        // 바코드 입력 후 포커스 딴 곳을 옮길 경우
        m_barCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
 			
        	@Override
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
 			@Override
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
        }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if ( m_listSalesTab1.getId() == arg0.getId() )
		{
			String code = ((TextView) arg1.findViewById(R.id.item1)).getText().toString();
			String name = ((TextView) arg1.findViewById(R.id.item2)).getText().toString();
						
			Intent intent = new Intent(this, CustomerProductDetailViewActivity.class);			
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
	}

	public void OnClickRenew(View v) {

        if (!m_APP_USER_GRADE.equals("2")) {
    		m_customerCode.setText("");
    		m_customerName.setText("");        	
        }
		m_barCode.setText("");
		m_productName.setText("");
	}
	
	@Override
	public void onTabChanged(String tabId) {
	
 		//doQuery();
	}

	public void OnClickSearch(View v) {

 		doQuery();
	};
	
	public void doQuery() {

 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryListForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryListForTab3(); break;
	 		case 3: {	 	        
	 			queryListForTab4(m_CalendarDay); break;
	 		}
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
		
		DatePickerDialog newDlg = new DatePickerDialog(this, this, 
				m_dateCalender2.get(Calendar.YEAR),
				m_dateCalender2.get(Calendar.MONTH),
				m_dateCalender2.get(Calendar.DAY_OF_MONTH));
		
		newDlg.show();
		
		m_dateMode = 2;
	};

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
		if ( m_dateMode == 1 ) {
			m_dateCalender1.set(year, monthOfYear, dayOfMonth);
			m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		}
		else if ( m_dateMode == 2 ) {
			m_dateCalender2.set(year, monthOfYear, dayOfMonth);
			m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		}
		
		m_dateMode = 0;
	}
	
	private void queryListForTab1()
	{
		mfillMaps1.removeAll(mfillMaps1);
		
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
		
		query += "select T.Office_Code, T.Office_Name, SUM(T.순매출) 순매출, SUM(T.이익금) 이익금 FROM (";
		for ( int y = year1; y <= year2; y++ ) {
			int m1 = 1, m2 = 12;
			if (y == year1) m1 = month1;
			if (y == year2) m2 = month2;
			for ( int m = m1; m <= m2; m++ ) {
				
				String tableName = String.format("SaD_%04d%02d", y, m);
				
				query += "select Office_Code, Office_Name, SUM(TSell_Pri-TSell_RePri-DC_Pri) 순매출, SUM(ProFit_Pri) 이익금 from " + tableName
						+ " where Sale_Date between '" + period1 + "' and '" + period2 + "'"
					    		+ " and Barcode like '%"+ barCode +"%'"
					    		+ " and G_Name like '%"+ productName +"%'"
					    		+ " and Office_Code like '%"+ customerCode +"%'"
					    		+ " and Office_Name like '%"+ customerName +"%'"
							    + " GROUP BY Office_Code, Office_Name ";
				
				query += " union all ";				
			}
		}
		query = query.substring(0, query.length()-11);		
		query += ") T GROUP BY Office_Code, Office_Name order by Office_Code asc; ";

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();

		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				if ( results.length() > 0 ) 
					updateListForTab1(results);
				adapter1.notifyDataSetChanged();	
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter1.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}

	private void updateListForTab1(JSONArray results)
	{		
		for(int index = 0; index < results.length() ; index++) {
			
			try {
				JSONObject son = results.getJSONObject(index);
				HashMap<String, String> map = JsonHelper.toStringHashMap(son);
				mfillMaps1.add(map);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void queryListForTab2()
	{
		mfillMaps2.removeAll(mfillMaps2);
		
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
		
		query += "select T.Barcode, T.G_Name, SUM(T.수량) 수량, SUM(T.순매출) 순매출 FROM (";
		for ( int y = year1; y <= year2; y++ ) {
			int m1 = 1, m2 = 12;
			if (y == year1) m1 = month1;
			if (y == year2) m2 = month2;
			for ( int m = m1; m <= m2; m++ ) {
				
				String tableName = String.format("SaD_%04d%02d", y, m);
				
    			query = query + "select A.Barcode, A.G_Name, SUM(A.Sale_Count) 수량, SUM(A.TSell_Pri-A.TSell_RePri-A.DC_Pri) 순매출 "
    					+ " from " + tableName + " as A inner join Goods as B on A.Barcode = B.BarCode "
    					+ " where B.Goods_Use='1' AND B.Pur_Use='1' AND A.Sale_Date between '" + period1 + "' and '" + period2 + "'"
					    		+ " and A.Barcode like '%"+ barCode +"%'"
					    		+ " and A.G_Name like '%"+ productName +"%'"
					    		+ " and A.Office_Code like '%"+ customerCode +"%'"
					    		+ " and A.Office_Name like '%"+ customerName +"%'"
							    + " GROUP BY A.Barcode, A.G_Name ";
   			
				query += " union all ";				
			}
		}
		query = query.substring(0, query.length()-11);		
		query += ") T GROUP BY T.Barcode, T.G_Name order by T.Barcode asc ; ";

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				if ( results.length() > 0 ) 
					updateListForTab2(results);
				adapter2.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter2.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}
		
	private void updateListForTab2(JSONArray results)
	{		
		for(int index = 0; index < results.length() ; index++) {
			
			try {
				JSONObject son = results.getJSONObject(index);
				HashMap<String, String> map = JsonHelper.toStringHashMap(son);
				mfillMaps2.add(map);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void queryListForTab3()
	{
		mfillMaps3.removeAll(mfillMaps3);
		
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
		
		query = "Select G.Office_Code,G.Office_Name,  G.순매출"
				+ " From ("
				+ " Select G.Office_Code,G.Office_Name, "
				+ " Sum (G.순매출) '순매출' "
				+ " From (";
		for ( int y = year1; y <= year2; y++ ) {
			int m1 = 1, m2 = 12;
			if (y == year1) m1 = month1;
			if (y == year2) m2 = month2;
			for ( int m = m1; m <= m2; m++ ) {
				
				String tableName = String.format("%04d%02d", y, m);
				
				query += "Select A.Office_Code, A.Office_Name, Sum (a.TSell_Pri - a.TSell_RePri) '순매출' "
						+ " From SaD_"+tableName+" A LEFT JOIN  SaT_"+tableName+" C"
						+ " ON A.Sale_Num=C.Sale_Num"
						+ " LEFT JOIN Office_Manage B"
						+ " ON A.Office_Code=B.Office_Code"
						+ " Where B.Office_Sec = '2'" 
						+ " And A.Office_Code Like '%" + customerCode + "%' And  A.Office_Name Like '%" + customerName + "%'" 
						+ " AND A.Sale_Date >= '" + period1 + "' AND A.Sale_Date <= '" + period2 + "'" 
						+ " Group By A.Office_Code, A.Office_Name";

				query += " union all ";	
			}
		}
		query = query.substring(0, query.length()-11);
		query += " ) G"
				+ " Group By G.Office_Code,G.Office_Name" 
				+ " ) G" 
				+ " ORDER BY G.Office_Code;";
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				if (results.length()>0)
					updateListForTab3(results);
				adapter3.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter3.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
		
	private void updateListForTab3(JSONArray results)
	{
		for(int index = 0; index < results.length() ; index++) {
			
			try {
				JSONObject son = results.getJSONObject(index);
				HashMap<String, String> map = JsonHelper.toStringHashMap(son);
				mfillMaps3.add(map);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
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

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
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
				
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_text, from, to);
				m_listSalesTab4.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			}
			else {
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_text, from, to);
				
				m_listSalesTab4.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
    private String setConstraint(String str, String field, String op, String value)
    {
    	if ( str.equals("") != true ) {
    		str = str + " and ";
    	}
    	
    	return str + field + " " + op + " '" + value + "'";
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
		        	HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");        	
		        	m_barCode.setText(hashMap.get("BarCode"));
					doQueryWithBarcode(); 
		        }
				break;
			case CUSTOMER_MANAGER_REQUEST :
				if(resultCode == RESULT_OK && data != null) {
					HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");     	
					m_customerCode.setText(hashMap.get("Office_Code"));
					m_customerName.setText(hashMap.get("Office_Name"));
		        }
				break;
			}
	}
	
	public void onCustomerSearch(View view)
	{
		String customer = m_customerCode.getText().toString();
		Intent intent = new Intent(this, ManageCustomerListActivity.class);
		intent.putExtra("customer", customer);
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
					String barcode = m_barCode.getText().toString();
					Intent intent = new Intent(ManageSalesActivity.this, ManageProductListActivity.class);
					intent.putExtra("barcode", barcode);
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
		
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
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
		
		String query = "";
		
		query = "SELECT Office_Name From Office_Manage WHERE Office_Code = '" + customerCode + "';";
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

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
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			ActionBar actionbar = getActionBar();
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setTitle("매출관리");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
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
	
}