package tipsystem.tips;

import java.sql.Connection;
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

import tipsystem.tips.ManageSalesActivity.MyAsyncTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SalesNewsActivity extends Activity implements OnItemClickListener, 
														OnItemSelectedListener, 
														DatePickerDialog.OnDateSetListener{

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
	ListView m_listNewsTab4;
	
	Spinner m_spinClassification;
	
	DatePicker m_datePicker;
	Button m_buttonSetDate;
	
	SimpleDateFormat m_dateFormatter;
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_news);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		
		
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
		m_listNewsTab4= (ListView)findViewById(R.id.listviewSalesNewsListTab4);
		
		m_listNewsTab2.setOnItemClickListener(this);
		
		m_spinClassification = (Spinner)findViewById(R.id.spinnerClassificationType);
		m_spinClassification.setOnItemSelectedListener(this);
		
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
     
        m_tabHost.setCurrentTab(0);
        
        
        m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
        
        String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		new MyAsyncTask ().execute("10", period1, period2);
		new MyAsyncTask ().execute("11", period1, period2);
	}
	
	private void setTabList1(List<HashMap<String, String>> fillMaps)
	{
		 // create the grid item mapping
		String[] from = new String[] {"Sale_Time", "rSale", "rSale_Yes", "rDSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
				
		// prepare the list of all records
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listNewsTab1.setAdapter(adapter);
	}
	
	private void setTabList2(List<HashMap<String, String>> fillMaps)
	{
		 // create the grid item mapping
		String[] from = new String[] {"Office_Code", "Office_Name", "rSale", "ProFit_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listNewsTab2.setAdapter(adapter);
		
	}
	
	private void setTabList3(List<HashMap<String, String>> fillMaps)
	{
		
		 // create the grid item mapping
		String[] from = new String[] {"순번", "분류명", "순매출", "수량", "점유율"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
				
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
				from, to);
		
		m_listNewsTab3.setAdapter(adapter);
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
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
		m_dateCalender1.set(year, monthOfYear, dayOfMonth);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		m_dateCalender2.set(year, monthOfYear, dayOfMonth);
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
				
		String tabIndex = String.format("%d", m_tabHost.getCurrentTab());
		
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		Toast.makeText(getApplicationContext(), period1 + " , " + period2, Toast.LENGTH_SHORT).show();
		
		new MyAsyncTask ().execute("10", period1, period2);
		new MyAsyncTask ().execute("11", period1, period2);
		new MyAsyncTask ().execute(tabIndex, period1, period2);
		
	}
	
	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        ArrayList<JSONObject> CommArray1=new ArrayList<JSONObject>();
        
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
        	    
        		String query = "";
        	    
        		int year1 = Integer.parseInt(period1.substring(0, 4));
        		int month1 = Integer.parseInt(period1.substring(5, 7));
        		
        		
        		String tableName = null;
        		String constraint = "";
        		
        		m_tabIndex = Integer.parseInt(tabIndex);
        				
				//tableName = String.format("SaD_%04d%02d", y, m);
				
				
				if ( m_tabIndex == 10 ) // 공통 1
        		{
					tableName = String.format("DF_%04d%02d", year1, month1);
					
        			query = "select TSell_Pri, Sale_Num, Sale_Pri, Cash_Pri, Card_Pri, Dec_Pri from " + tableName;
        			query = query + " where Sale_Date = '" + period1 + "'";
        			
        			Log.e("HTTPJSON","query: " + query );
                	reset = stmt.executeQuery(query);
    	        	    		
            	    while(reset.next())
            	    {
    					Log.w("HTTPJSON:",reset.getString(1));
    					
    					JSONObject Obj = new JSONObject();
    				    // original part looks fine:
    				    Obj.put("TSell_Pri",reset.getInt(1));
    				    Obj.put("Sale_Num",reset.getInt(2));
    				    Obj.put("Sale_Pri",reset.getInt(3));
    				    Obj.put("Cash_Pri",reset.getInt(4));
    				    Obj.put("Card_Pri",reset.getInt(5));
    				    Obj.put("Dec_Pri",reset.getInt(6));
   	    
    				    CommArray.add(Obj);
            		}
                    	    
        		}
        		else if ( m_tabIndex == 11 ) // 상품명
        		{
        			tableName = String.format("DF_%04d%02d", year1, month1);
					
        			query = "select TSell_Pri from " + tableName;
        			query = query + " where Sale_Date = '" + period1 + "'";
        			
        			Log.e("HTTPJSON","query: " + query );
                	reset = stmt.executeQuery(query);
    	        	    		
            	    while(reset.next())
            	    {
    					Log.w("HTTPJSON:",reset.getString(1));
    					
    					JSONObject Obj = new JSONObject();
    				    // original part looks fine:
    				    Obj.put("TSell_Pri",reset.getInt(1));
   	    
    				    CommArray.add(Obj);
            		}
            	    
        		}
        		else if ( m_tabIndex == 0 ) // 수수료매장
        		{
        			tableName = String.format("SaT_%04d%02d", year1, month1);
        			
        			query = "select Sale_Time, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
        			query = query + " where Sale_Date = '" + period1 + "'";
       				
        			Log.e("HTTPJSON","query: " + query );
                	reset = stmt.executeQuery(query);
    	        	    		
            	    while(reset.next()){
    					Log.w("HTTPJSON:",reset.getString(1));
    					
    					JSONObject Obj = new JSONObject();
    				    // original part looks fine:
    				    Obj.put("Sale_Time",reset.getString(1).trim());
    				    Obj.put("TSell_Pri",reset.getInt(2));
    				    Obj.put("TSell_RePri",reset.getInt(3));
    				    Obj.put("DC_Pri",reset.getInt(4));
    				    
    				    CommArray.add(Obj);
    				}
        			
            	    
            	    query = "select Sale_Time, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
        			query = query + " where Sale_Date = '" + period2 + "'";
       				
        			Log.e("HTTPJSON","query: " + query );
                	reset = stmt.executeQuery(query);
    	        	    		
            	    while(reset.next()){
    					Log.w("HTTPJSON:",reset.getString(1));
    					
    					JSONObject Obj = new JSONObject();
    				    // original part looks fine:
    				    Obj.put("Sale_Time",reset.getString(1).trim());
    				    Obj.put("TSell_Pri",reset.getInt(2));
    				    Obj.put("TSell_RePri",reset.getInt(3));
    				    Obj.put("DC_Pri",reset.getInt(4));
    				    
    				    CommArray1.add(Obj);
    				}
            	    
        		}
        		else if ( m_tabIndex == 1 ) // 달력매출
        		{
        			
        			tableName = String.format("SaD_%04d%02d", year1, month1);
        			
        			query = "select Office_Code, Office_Name, TSell_Pri, TSell_RePri, DC_Pri, ProFit_Pri from " + tableName;
        			query = query + " where Sale_Date = '" + period1 + "'";
        			
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
        	
        	if ( m_tabIndex == 10 )
        	{
		        int tSellPri = 0;
			    int saleNum = 0;
			    int salePri = 0;
        		int cashPri = 0;
        		int cardPri = 0;
        		int decPri = 0;
    	 	        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
        			JSONObject json = iterator.next();
                	
                	try {
                		
                		tSellPri = json.getInt("TSell_Pri");
                		saleNum = json.getInt("Sale_Num");
                		salePri = json.getInt("Sale_Pri");
                		
                		cashPri = json.getInt("Cash_Pri");
                		cardPri = json.getInt("Card_Pri");
                		decPri = json.getInt("Dec_Pri");
                		
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		if ( CommArray.size() > 0 )
        		{
        			
        			m_realSales.setText(String.format("%d", tSellPri));
        			
        			m_viewKNumber.setText(String.format("%d", saleNum));
        			//m_viewRealSalesYesterday.setText(String.format("%d", tSellPri));
        			m_viewPrice.setText(String.format("%d", salePri));
        			m_viewCash.setText(String.format("%d", cashPri));
        			m_viewCard.setText(String.format("%d", cardPri));
        			m_viewCredit.setText(String.format("%d", decPri));
        			
        			m_viewOther.setText(String.format("%d", 0));
        			
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        	}
        	else if ( m_tabIndex == 11 )
        	{
        		int tSellPri = 0;

            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
        			JSONObject json = iterator.next();
                	
                	try {
                		
                		tSellPri = json.getInt("TSell_Pri");
                		
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		if ( CommArray.size() > 0 )
        		{
        			m_viewRealSalesYesterday.setText(String.format("%d", tSellPri));
        		}
        		
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        	}
        	else if ( m_tabIndex == 0 )
        	{
        		String[] from = new String[] {"Sale_Time", "rSale", "rSale_Yes", "rDSale"};
    	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    	        
    	        int [] rSale = new int [24];
    	        int [] rSale1 = new int [24];
    	        int [] rDSale = new int [24];
    	        
    	        for ( int i = 0; i < 24; i++ )
    	        {
    	        	rSale[i] = 0;	
    	        	rSale1[i] = 0;
    	        	rDSale[i] = 0;
    	        
    	        }
    	        
    	        
    	        
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
        				String tTime = json.getString("Sale_Time");
        				
        				int iTime = Integer.parseInt(tTime.substring(0, 2));
        				
        				int itSell = json.getInt("TSell_Pri");
        				int itRSell = json.getInt("TSell_RePri");
        				int idcPri = json.getInt("DC_Pri");
        				int irSale = itSell - (itRSell + idcPri);
        				
        			        				
        				rSale[iTime] = rSale[iTime] + irSale;
        				

        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
                	
            	iterator = CommArray1.iterator();
        		while (iterator.hasNext()) {
                    	JSONObject json1 = iterator.next();
                    	
                    	try {
                    		
            				String tTime = json1.getString("Sale_Time");
            				
            				int iTime = Integer.parseInt(tTime.substring(0, 2));
            				
            				int itSell = json1.getInt("TSell_Pri");
            				int itRSell = json1.getInt("TSell_RePri");
            				int idcPri = json1.getInt("DC_Pri");
            				int irSale = itSell - (itRSell + idcPri);
            				            				
            				rSale1[iTime] = rSale1[iTime] + irSale;
            				

            			} catch (JSONException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
                	
        		}
        		
            	for ( int i = 8; i < 23; i++ )
    	        {
            		
            		rDSale[i] = rSale[i] - rSale1[i];
            		
                	// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("Sale_Time", String.format("%02d", i));
		            map.put("rSale", String.format("%d", rSale[i]));
		            map.put("rSale_Yes", String.format("%d", rSale1[i]));
		            map.put("rDSale", String.format("%d", rDSale[i]));
		            fillMaps.add(map);
    	        }
    	        
        		Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        		if ( CommArray.size() > 0 )
        		{
        			setTabList1(fillMaps);
        		}
        		
        	}
        	else if ( m_tabIndex == 1 )
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
        			setTabList2(fillMaps);
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
    };
    
}
