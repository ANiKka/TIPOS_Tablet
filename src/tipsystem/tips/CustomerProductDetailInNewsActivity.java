package tipsystem.tips;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CustomerProductDetailInNewsActivity extends Activity {

	ListView m_listPurchaseDetail;
	
	JSONObject m_shop;
	
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	NumberFormat m_numberFormat;
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_product_detail_in_news);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		m_listPurchaseDetail= (ListView)findViewById(R.id.listviewCustomerProductDetailInNews);
		
		m_numberFormat = NumberFormat.getInstance();
		
		Intent intent = getIntent();
		
		String period1 = intent.getExtras().getString("PERIOD1");
		String customerCode = intent.getExtras().getString("OFFICE_CODE");
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		executeQuery(period1, customerCode, customerName);

	}

	private void setTabList1(List<HashMap<String, String>> fillMaps)
	{
		
		 // create the grid item mapping
		String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4_2, from, to);
		
		m_listPurchaseDetail.setAdapter(adapter);
	}
	
	private void executeQuery(String... urls)
	{
	    String period1 = urls[0];
		String customerCode = urls[1];
		String customerName = urls[2];
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		
		String tableName = null;
		String constraint = "";
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
				
		if ( customerCode.equals("") != true )
		{
			constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
		}
		
		if ( customerName.equals("") != true)
		{
			constraint = setConstraint(constraint, "Office_Name", "=", customerName);
		}
		
		query = "select Barcode, G_Name, Sale_Count, TSell_Pri, TSell_RePri, DC_Pri from " + tableName;
		query = query + " where Sale_Date = '" + period1 + "'";
	
		if ( constraint.equals("") != true )
		{
			query = query + " and " + constraint;
		}
		
		query = query + ";";
    	
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				try {
					
					if ( results.length() > 0 )
					{
						String[] from = new String[] {"Barcode", "G_Name", "Sale_Count", "rSale"};
				        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
				        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
			 			
						for(int i = 0; i < results.length() ; i++)
						{
							JSONObject son = results.getJSONObject(i);
							
		    				int tSell = son.getInt("TSell_Pri");
		    				int tRSell = son.getInt("TSell_RePri");
		    				int dcPri = son.getInt("DC_Pri");
		    				String saleCount = m_numberFormat.format(son.getInt("Sale_Count"));
		    				String rSale = m_numberFormat.format(tSell - (tRSell + dcPri));
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("Barcode", son.getString("Barcode") );
							map.put("G_Name", son.getString("G_Name"));
							map.put("Sale_Count", saleCount);
							map.put("rSale", rSale );
							fillMaps.add(map);
						}	
						
						setTabList1(fillMaps);
						
						Toast.makeText(getApplicationContext(), "조회 완료" + results.length(), Toast.LENGTH_SHORT).show();
						dialog.cancel();
					
					}
					else 
					{
						dialog.cancel();
					}
					
	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    			dialog.cancel();
	    		}
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer_product_detail_in_news, menu);
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
