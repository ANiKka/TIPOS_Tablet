package tipsystem.tips;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
	
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	NumberFormat m_numberFormat;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_product_detail_view);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		m_numberFormat = NumberFormat.getInstance();
		
		m_listDetailView= (ListView)findViewById(R.id.listviewCustomerProductDetailViewList);
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerCode = (TextView)findViewById(R.id.textViewCustomerCode);
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
		
		executeQuery(period1, period2, customerCode, customerName);
	}

	
	private void executeQuery(String... urls)
	{
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

		for ( int y = year1; y <= year2; y++ ) {
			for ( int m = month1; m <= month2; m++ ) {

				tableName = String.format("SaD_%04d%02d", y, m);
				
    			query = query + "select Barcode, G_Name, SUM(Sale_Count) 수량, SUM(Sale_Count) 순수량 from " + tableName;
    			query = query + " where Office_Code='"+customerCode+"' and Sale_Date between '" + period1 + "' and '" + period2 + "'";
    			
				query += " union all ";
			}
		}
		query = query.substring(0, query.length()-11);
		query += " Group by Barcode, G_Name;";

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				
				updateList(results);			
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}
	
	private void updateList(JSONArray results)
	{
		String[] from = new String[] {"Barcode", "G_Name", "수량", "순수량"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		
		try {
			
			if ( results.length() > 0 )
			{
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					HashMap<String, String> map = JsonHelper.toStringHashMap(son);
					fillMaps.add(map);
				}	
						
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4_5, from, to);
				m_listDetailView.setAdapter(adapter);
			}
			else  {
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4_5, from, to);
				m_listDetailView.setAdapter(adapter);
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			ActionBar actionbar = getActionBar();         
	//		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
	//		actionbar.setCustomView(custom_action_bar);
	
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setTitle("거래처별 상품 상세보기");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
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
}
