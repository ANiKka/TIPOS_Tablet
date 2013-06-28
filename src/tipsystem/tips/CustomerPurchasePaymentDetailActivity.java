package tipsystem.tips;

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
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CustomerPurchasePaymentDetailActivity extends Activity {

	JSONObject m_shop;
	
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	ListView m_listDetailView;
	
	TextView m_period1;
	TextView m_period2;
	TextView m_customerCode;
	TextView m_customerName;
	TextView m_realPurchase;
	TextView m_realPayment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_purchase_payment_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		
		m_listDetailView= (ListView)findViewById(R.id.listviewCustomerPurchasePaymentDetailViewList);
		
		Intent intent = getIntent();
		
		String period1 = intent.getExtras().getString("PERIOD1");
		String period2 = intent.getExtras().getString("PERIOD1");
		
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerName = (TextView)findViewById(R.id.textViewCustomerName);
		m_realPurchase = (TextView)findViewById(R.id.textViewRealPurchase);
		m_realPayment = (TextView)findViewById(R.id.textViewRealPayment);
		
		m_period1.setText(period1);
		m_period2.setText(period2);
		
		m_customerName.setText(customerName);
		
		int year1 = Integer.parseInt(period1.substring(0, 4));
 		//int year2 = Integer.parseInt(period2.substring(0, 4));
 		
 		int month1 = Integer.parseInt(period1.substring(5, 7));
 		//int month2 = Integer.parseInt(period2.substring(5, 7));
 		
		String tableName = String.format("SaD_%04d%02d", year1, month1);
		
		String query;
		
    	query = "select BARCODE, G_NAME, PURE_PRI, TSell_PRI, TSell_REPRI, DC_PRI form "
    			+ tableName 
	    		+ " where SALE_DATE between '" + period1 + "' and '" + period2 + "' "
	    		+ "and OFFICE_NAME = '" + customerName + "'";
	    		
     			
		// 콜백함수와 함께 실행
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {
		
			@Override
			public void onRequestCompleted(JSONArray results) {
				setListItems(results);
			}
		}).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		

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
		actionbar.setTitle("거래처 매입/매출 상세보기");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer_purchase_payment_detail_view,
				menu);
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

	
	void setListItems(JSONArray results)
	{
		try {
		
			int rPurchase = 0;
			int rPayment = 0;
			
			if ( results.length() > 0 )
			{
								
				// create the grid item mapping
				String[] from = new String[] {"BARCODE", "G_NAME", "PUR_PRI", "RSALE"};
				int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
				
				// prepare the list of all records
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
 			
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("BARCODE", son.getString("BARCODE") );
					map.put("G_NAME", son.getString("G_NAME"));
					
					map.put("PUR_PRI", String.format("%d", son.getInt("PUR_PRI")) );
					
					int rSale = son.getInt("TSell_PRI") - (son.getInt("TSell_REPRI") + son.getInt("DC_PRI"));
					
					map.put("RSALE", String.format("%d", rSale));
					
					rPayment = rPayment + rSale;
					rPurchase = rPurchase + son.getInt("PUR_PRI");
					
					fillMaps.add(map);
				}	
			
				// fill in the grid_item layout
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
						from, to);
				
				m_listDetailView.setAdapter(adapter);
				
				m_realPurchase.setText(String.format("%d", rPurchase));
				m_realPayment.setText(String.format("%d", rPayment));
								
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료 : " + results.length(), Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	
}
