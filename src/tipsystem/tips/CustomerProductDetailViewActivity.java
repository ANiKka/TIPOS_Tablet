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

import tipsystem.tips.ManageSalesActivity.MyAsyncTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CustomerProductDetailViewActivity extends Activity {

	ListView m_listDetailView;
	TextView m_period1;
	TextView m_period2;
	TextView m_customerCode;
	TextView m_customerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_product_detail_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listDetailView= (ListView)findViewById(R.id.listviewCustomerProductDetailViewList);
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerName = (TextView)findViewById(R.id.textViewCustomerName);
		
		Intent intent = getIntent();
		
		m_period1.setText(intent.getExtras().getString("PERIOD1"));
		m_period2.setText(intent.getExtras().getString("PERIOD2"));
		
		m_customerCode.setText(intent.getExtras().getString("OFFICE_CODE"));
		m_customerName.setText(intent.getExtras().getString("OFFICE_NAME"));
		
		
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
	
		new MyAsyncTask().execute(period1, period2, customerCode, customerName);
		
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
		actionbar.setTitle("거래처별 상품 상세보기");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}
	
	private void setListDetail(List<HashMap<String, String>> fillMaps)
	{
		 // create the grid item mapping
		String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listDetailView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer_product_detail_view, menu);
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
        	    
        	    
        	    String period1 = urls[0];
        		String period2 = urls[1];
        		String customerCode = urls[2];
        		String customerName = urls[3];
        		
        		String query = "";
        	    
        		int year1 = Integer.parseInt(period1.substring(0, 4));
        		int year2 = Integer.parseInt(period2.substring(0, 4));
        		
        		int month1 = Integer.parseInt(period1.substring(5, 7));
        		int month2 = Integer.parseInt(period2.substring(5, 7));
        		
        		String tableName = null;
        		String constraint = "";
        		
        		
        		for ( int y = year1; y <= year2; y++ )
        		{
        			for ( int m = month1; m <= month2; m++ )
        			{
        				
        				tableName = String.format("SaD_%04d%02d", y, m);
    					
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
        	        	    		
                	    while(reset.next())
                	    {
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
    			setListDetail(fillMaps);
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
