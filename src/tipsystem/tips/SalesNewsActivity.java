package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	int m_year;
	int m_month;
	int m_day;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_news);
		// Show the Up button in the action bar.
		setupActionBar();
		
		final Calendar c = Calendar.getInstance();
		m_year = c.get(Calendar.YEAR);
		m_month = c.get(Calendar.MONTH);
		m_day = c.get(Calendar.DAY_OF_MONTH);
		
		m_buttonSetDate = (Button) findViewById(R.id.buttonSetDate);
		m_buttonSetDate.setText(m_year + "-" + m_month + "-" + m_day);
		
		// 상단 텍스트 뷰
		m_realSales = (TextView)findViewById(R.id.textViewRealSales);
		m_viewKNumber = (TextView)findViewById(R.id.textViewKNumber);
		m_viewRealSalesYesterday = (TextView)findViewById(R.id.textViewRealSalesYesterday);
		m_viewPrice = (TextView)findViewById(R.id.textViewKPrice);
		m_viewCash = (TextView)findViewById(R.id.textViewCash);
		m_viewCard = (TextView)findViewById(R.id.textViewCard);
		m_viewCredit = (TextView)findViewById(R.id.textViewCredit);
		m_viewOther = (TextView)findViewById(R.id.textViewOther);

		
		
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
        
        spec = m_tabHost.newTabSpec("tag4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("포스별");
        m_tabHost.addTab(spec);
     
        m_tabHost.setCurrentTab(0);
        
        //setTabList1();
        //setTabList2();
        //setTabList3();
        //setTabList4();
	}
	
	private void setTabList1(List<HashMap<String, String>> fillMaps)
	{
		m_listNewsTab1= (ListView)findViewById(R.id.listviewSalesNewsListTab1);
		
		 // create the grid item mapping
		//String[] from = new String[] {"시간", "순매출", "전일매출", "전일대비차액"};
		//int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		String[] from = new String[] {"code", "name", "section"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
		
		// prepare the list of all records
		//List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
//		for(int i = 0; i < 10; i++)
//		{
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("시간", "0" + i);
//			map.put("순매출", i + "000");
//			map.put("전일매출", i + "000");
//			map.put("전일대비차액", i + "000");
//			fillMaps.add(map);
//		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listNewsTab1.setAdapter(adapter);
	}
	
	private void setTabList2()
	{
		m_listNewsTab2= (ListView)findViewById(R.id.listviewSalesNewsListTab2);
		
		 // create the grid item mapping
		String[] from = new String[] {"거래처코드", "거래처명", "순매출", "이익금"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("거래처코드", "0000" + i);
			map.put("거래처명", "거래처명" + i);
			map.put("순매출", i + "000");
			map.put("이익금", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listNewsTab2.setAdapter(adapter);
		m_listNewsTab2.setOnItemClickListener(this);
	}
	
	private void setTabList3()
	{
		m_spinClassification = (Spinner)findViewById(R.id.spinnerClassificationType);
		m_spinClassification.setOnItemSelectedListener(this);
		
		m_listNewsTab3= (ListView)findViewById(R.id.listviewSalesNewsListTab3);
		
		 // create the grid item mapping
		String[] from = new String[] {"순번", "분류명", "순매출", "수량", "점유율"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("순번", "" + i);
			map.put("분류명", "분류명" + i);
			map.put("순매출", i + "000");
			map.put("수량", i + "0");
			map.put("점유율", i + "0");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
				from, to);
		
		m_listNewsTab3.setAdapter(adapter);
	}
	
	private void setTabList4()
	{
		m_listNewsTab4= (ListView)findViewById(R.id.listviewSalesNewsListTab4);
		
		 // create the grid item mapping
		String[] from = new String[] {"포스번호", "순매출", "객수", "객단가"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("포스번호", "" + i);
			map.put("순매출", i + "000");
			map.put("객수", i + "0");
			map.put("객단가", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listNewsTab4.setAdapter(adapter);
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
		 DatePickerDialog newDlg = new DatePickerDialog(this, this, m_year, m_month, m_day);
		 newDlg.show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		m_year = year;
		m_month = monthOfYear;
		m_month += 1;
		m_day = dayOfMonth;
		m_buttonSetDate.setText(m_year + "-" + m_month + "-" + m_day); 
		
		new MyAsyncTask ().execute(Integer.toString(m_year), Integer.toString(m_month), Integer.toString(m_day));
		
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
    };
    
}
