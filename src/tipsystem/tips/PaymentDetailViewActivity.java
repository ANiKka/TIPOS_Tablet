package tipsystem.tips;

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
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PaymentDetailViewActivity extends Activity {
	
	JSONObject m_shop;
	
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	ListView m_listPaymentView;
	
	TextView m_period1;
	TextView m_period2;
	TextView m_customerCode;
	TextView m_customerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_detail_view);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		m_listPaymentView = (ListView)findViewById(R.id.listviewPaymentDetailViewList);
		
		Intent intent = getIntent();
		
		String period1 = intent.getExtras().getString("PERIOD1");
		String period2 = intent.getExtras().getString("PERIOD1");
		
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		String customerCode = intent.getExtras().getString("OFFICE_CODE");
		
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerName = (TextView)findViewById(R.id.textViewCustomerName);
		
		m_period1.setText(period1);
		m_period2.setText(period2);
		
		m_customerName.setText(customerName);

		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
    	String query ="";
		for ( int y = year1; y <= year2; y++ ) {
			for ( int m = month1; m <= month2; m++ ) {

     			query = "select * " 
		    	    		+ " from OFFICE_SETTLEMENT " 
		    	    		+ " where Office_Code='"+customerCode+"' and PRO_DATE between '" + period1 + "' and '" + period2 + "'";

				query += " union all ";				
			}
		}
		query = query.substring(0, query.length()-11);
		query += "; ";
		
		// 콜백함수와 함께 실행
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {
		
			@Override
			public void onRequestCompleted(JSONArray results) {
				setListItems(results);
			}
		}).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setTypeface(typeface);
	}

	void setListItems(JSONArray results)
	{
		try {
			if ( results.length() > 0 ) {
				// create the grid item mapping
				String[] from = new String[] {"In_Seq", "Pro_Date", "Sale_Code", "Gubun", "이월", "Buy_Pri", "Buy_RePri", "Sale_Pri", 
												"Sub_Pri", "Pay_Pri", "In_Pri", "In_Pri", "Bigo"};
				int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5, R.id.item6, R.id.item7, R.id.item8,
										R.id.item9, R.id.item10, R.id.item11, R.id.item12, R.id.item13};
				
				// prepare the list of all records
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
 			
				for(int i = 0; i < results.length() ; i++) {

					JSONObject son = results.getJSONObject(i);
					HashMap<String, String> map = JsonHelper.toStringHashMap(son);
					fillMaps.add(map);
				}	
				// fill in the grid_item layout
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item13, from, to);				
				m_listPaymentView.setAdapter(adapter);		
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료 : " + results.length(), Toast.LENGTH_SHORT).show();

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
	
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setTitle("대금결제 상세보기");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment_detail_view, menu);
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
