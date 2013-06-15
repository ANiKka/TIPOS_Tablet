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
import android.app.ProgressDialog;
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

public class ComparePriceActivity extends Activity{

	TextView m_customer;
	TextView m_customer2;
	TextView m_barcode;
	TextView m_productionName;
	TextView m_local;	
	
	ListView m_listPriceSearch;
	private ProgressDialog dialog;
	
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	
	private OnClickListener m_click_search_listener = new OnClickListener() {
        public void onClick(View v) {
        	dialog = new ProgressDialog(ComparePriceActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
            
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
		Button searchButton = (Button) findViewById(R.id.buttonPriceSearch);
		m_listPriceSearch = (ListView)findViewById(R.id.listviewPriceSearchList);
		m_listPriceSearch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                sendDataFromList(position);
            }
        });
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
	
	private void sendDataFromList(int position){
		String Barcode = mfillMaps.get(position).get("Barcode");
		String G_Name = mfillMaps.get(position).get("G_Name");
		
		ArrayList<String> sendArr = new ArrayList<String>();
		sendArr.add(Barcode);
		sendArr.add(G_Name);
		
		Intent intent = new Intent(this, ComparePriceDetailActivity.class);
		intent.putExtra("fillMaps", sendArr);
    	startActivity(intent);
	}


	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected String doInBackground(String... urls) {
        	Log.i("Android"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset = null;
        	int i = 0;
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
        	    	if(!customer.equals("")){
        	    			query = "Bus_Code = '" + customer + "' ";
        	    			i++;
        	    	}
        	    	if(!customer2.equals("")){
        	    		if(i>0){
        	    			query += "and Bus_Name = '" + customer2 + "' ";
        	    		} else {
        	    			query = "Bus_Name = '" + customer2 + "' ";
        	    			i++;
        	    		}
        	    	}
        	    	if(!barcode.equals("")){
        	    		if(i>0){
        	    			query += "and Barcode = '" + barcode + "' ";
        	    		} else {
        	    			query = "Barcode = '" + barcode + "' ";
        	    			i++;
        	    		}
        	    	}
        	    	
        	    	if(!productionName.equals("")){
        	    		if(i>0){
        	    			query += "and G_Name = '" + productionName + "' ";
        	    		} else {
        	    			query = "G_Name = '" + productionName + "' ";
        	    			i++;
        	    		}
        	    	}
        	    	query = "select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where " + query ;
        	    	Log.w("MSSQL", "Query : " + query);

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
        	
        	dialog.dismiss();
            dialog.cancel();
            
			String[] from = new String[] {"Barcode", "G_Name", "Pur_Pri", "Sell_Pri"};
	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
	        
	 	        		
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
		            mfillMaps.add(map);
    		 
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}

	        // fill in the grid_item layout
	   //     SimpleAdapter adapter = new SimpleAdapter(ComparePriceActivity.this, mfillMaps, R.layout.activity_listview_compare_list, from, to);
	   //     m_listPriceSearch.setAdapter(adapter);
	        
            Toast.makeText(getApplicationContext(), "조회 완료", 0).show();
        }
    }


}
