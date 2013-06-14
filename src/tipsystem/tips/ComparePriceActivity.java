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
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ComparePriceActivity extends Activity implements OnItemClickListener{

	TextView m_customer;
	TextView m_customer2;
	TextView m_barcode;
	TextView m_productionName;
	TextView m_local;	
	
	ListView m_listPriceSearch;
	
	private OnClickListener m_click_search_listener = new OnClickListener() {
        public void onClick(View v) {
        	
        	String customer = m_customer.getText().toString();
    	    String customer2 = m_customer2.getText().toString();
    	    String barcode = m_barcode.getText().toString();
    	    String productionName = m_productionName.getText().toString();
    	    String local = m_local.getText().toString();
    	    
    	    new MyAsyncTask ().execute(customer, customer2, barcode, productionName, local);
        }
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_price);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPriceSearch= (ListView)findViewById(R.id.listviewPriceSearchList);
		
		 // create the grid item mapping
//        String[] from = new String[] {"바코드", "상품명", "매입가", "판매가"};
//        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
// 
//        // prepare the list of all records
//        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
//        for(int i = 0; i < 10; i++){
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("바코드", "0000" + i);
//            map.put("상품명", "상품명_" + i);
//            map.put("매입가", i + "000");
//            map.put("판매가", i + "000");
//            fillMaps.add(map);
//        }
// 
//        // fill in the grid_item layout
//        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_price_search_list, 
//        		from, to);
//        
//      m_listPriceSearch.setAdapter(adapter);
        m_listPriceSearch.setOnItemClickListener(this);
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        
        
        m_customer = (TextView)findViewById(R.id.editTextCustomer);
		m_customer2 = (TextView)findViewById(R.id.editTextCustomer2);
		m_barcode = (TextView)findViewById(R.id.editTextBarcord);
		m_productionName = (TextView)findViewById(R.id.editTextProductionName);
		m_local = (TextView)findViewById(R.id.editTextLocal);
		m_listPriceSearch = (ListView)findViewById(R.id.listviewPriceSearchList);
		Button searchButton = (Button) findViewById(R.id.buttonPriceSearch);
		
        searchButton.setOnClickListener(m_click_search_listener);
        
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
		actionbar.setTitle("가격비교");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compare_price, menu);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		// TODO Auto-generated method stub
		
		//Toast.makeText(this, "Item Click.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ComparePriceDetailActivity.class);
    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
		
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected String doInBackground(String... urls) {
        	Log.i("Android"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset = null;
        	
        	try {
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("Connection","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
        	    Log.i("Connection","MSSQL open");
        	    Statement stmt = conn.createStatement();
        	    
        	    String customer = urls[0];
        	    String customer2 = urls[1];
        	    String barcode = urls[2];
        	    String productionName = urls[3];
        	    String local = urls[4];
        	    String query = "";
        	    
        	    if(customer.equals("") && customer2.equals("") && barcode.equals("") && productionName.equals("") && local.equals(""))
        	    {
        	    	query += "select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods";
        	    }
        	    else
        	    {
        	    	//query += "select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where Bus_Code = '" + customer + "' and Bus_Name = '" + customer2 + "' and Barcode = '" + barcode + "' and G_Name = '" + productionName + "'";  
        	    	query += "select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where Barcode = '" + barcode + "'";
        	    }
        	    reset = stmt.executeQuery(query);
        	    
        	    while(reset.next()){
					Log.w("Connection:",reset.getString(2));
					
					JSONObject Obj = new JSONObject();
				    // original part looks fine:
				    Obj.put("Barcode",reset.getString(1).trim());
				    Obj.put("G_Name",reset.getString(2).trim());
				    Obj.put("Pur_Pri",reset.getString(3).trim());
				    Obj.put("Sell_Pri",reset.getString(4).trim());
				    CommArray.add(Obj);
				}
        	    conn.close();
        	
        	 } catch (Exception e) {
        	    Log.w("Error connection","" + e.getMessage());		   
        	 }
        	publishProgress(0);
			return null;
        }

        protected void onProgressUpdate(Integer[] values) {
            Log.e("HTTPJSON", "onProgressUpdate" );
        }

        protected void onPostExecute(String result) {
        	super.onPostExecute(result);
        	
			String[] from = new String[] {"Barcode", "G_Name", "Pur_Pri", "Sell_Pri"};
	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	 	        		
        	Iterator<JSONObject> iterator = CommArray.iterator();
    		while (iterator.hasNext()) {
            	JSONObject json = iterator.next();
            	
            	try {
    				String Barcode = json.getString("Barcode");
    				String G_Name = json.getString("G_Name");
    				String Pur_Pri = json.getString("Pur_Pri");
    				String Sell_Pri = json.getString("Sell_Pri");
    				
    				// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("Barcode", Barcode);
		            map.put("G_Name", G_Name);
		            map.put("Pur_Pri", Pur_Pri);
		            map.put("Sell_Pri", Sell_Pri);
		            fillMaps.add(map);
    		 
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}

	        // fill in the grid_item layout
	        SimpleAdapter adapter = new SimpleAdapter(ComparePriceActivity.this, fillMaps, R.layout.activity_compare_price, from, to);
	        m_listPriceSearch.setAdapter(adapter);
	        
            Toast.makeText(getApplicationContext(), "조회 완료", 0).show();
        }
    }


}
