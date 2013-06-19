package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.MSSQL;
import android.os.Bundle;
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
	
	ListView m_listPaymentView;
	
	TextView m_period1;
	TextView m_period2;
	TextView m_customerCode;
	TextView m_customerName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_detail_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPaymentView = (ListView)findViewById(R.id.listviewPaymentDetailViewList);
		
		Intent intent = getIntent();
		
		String period1 = intent.getExtras().getString("PERIOD1");
		String period2 = intent.getExtras().getString("PERIOD1");
		
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerName = (TextView)findViewById(R.id.textViewCustomerName);
		
		m_period1.setText(period1);
		m_period2.setText(period2);
		
		m_customerName.setText(customerName);
				
//		int year1 = Integer.parseInt(period1.substring(0, 4));
// 		int year2 = Integer.parseInt(period2.substring(0, 4));
// 		
// 		int month1 = Integer.parseInt(period1.substring(5, 7));
// 		int month2 = Integer.parseInt(period2.substring(5, 7));
// 		
//		String tableName = String.format("InD_%04d%02d", year1, month1);
 		
		
    	String query;
		
    	//query = "select SA_SEQ, PRO_DATE, SALE_CODE, GUBUN, 이월, 매입금액, SELL_REPRI, SELLSALE_PRI, "
    	//		+"장려금, SELLPAY_PRI, SELLIN_PRI, 미지급금액, BIGO "
    	query = "select SA_SEQ, PRO_DATE, SALE_CODE, GUBUN, SELL_REPRI, SELLSALE_PRI, "
    			+" SELLPAY_PRI, SELLIN_PRI, BIGO "
	    		+"from OFFICE_SASETTLEMENT " 
	    		+ " where PRO_DATE between '" + period1 + "' and '" + period2 + "' "
	    		+ "and OFFICE_NAME = '" + customerName + "'";
	    		
     			
		// 콜백함수와 함께 실행
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {
		
			@Override
			public void onRequestCompleted(JSONArray results) {
				setListItems(results);
			}
		}).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		

	
		
		 Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
	        TextView textView = (TextView) findViewById(R.id.textView1);
	        textView.setTypeface(typeface);
	        
	        textView = (TextView) findViewById(R.id.textView3);
	        textView.setTypeface(typeface);
	        
	        textView = (TextView) findViewById(R.id.textView2);
	        textView.setTypeface(typeface);
	        
	        textView = (TextView) findViewById(R.id.textView4);
	        textView.setTypeface(typeface);
	        
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
		actionbar.setTitle("대금결제 상세보기");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);


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
	
	void setListItems(JSONArray results)
	{
		try {
		
			if ( results.length() > 0 )
			{
				// create the grid item mapping
				 // create the grid item mapping
				String[] from = new String[] {"SA_SEQ", "PRO_DATE", "SALE_CODE", "GUBUN", "이월",
											  "매입금액", "SELL_REPRI", "SELLSALE_PRI", "장려금", "SELLPAY_PRI",
											  "SELLIN_PRI", "미지급금액", "BIGO"};
				int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5,
										R.id.item6, R.id.item7, R.id.item8, R.id.item9, R.id.item10,
										R.id.item11, R.id.item12, R.id.item13};
				
				// prepare the list of all records
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				
				for(int i = 0; i < results.length(); i++)
				{
					JSONObject son = results.getJSONObject(i);
					
				    HashMap<String, String> map = new HashMap<String, String>();
				    
				    map.put("SA_SEQ", String.format("%d", son.getInt("SA_SEQ")));
					map.put("PRO_DATE", son.getString("PRO_DATE"));
					map.put("SALE_CODE", son.getString("SALE_CODE"));
					
					String gubun = son.getString("GUBUN");
					
					if ( gubun.equals("1") == true )
					{
						map.put("GUBUN", "입금");
					}
					else if ( gubun.equals("2") == true )
					{
						map.put("GUBUN", "매출");
					}
					else if ( gubun.equals("3") == true )
					{
						map.put("GUBUN", "지급");
					} 
					else if ( gubun.equals("4") == true )
					{
						map.put("GUBUN", "반품");
					} 
					
					map.put("이월", "이월" + i);
					
					map.put("매입금액", "매입금액" + i);
					map.put("SELL_REPRI", String.format("%d", son.getInt("SELL_REPRI")));
					map.put("SELLSALE_PRI", String.format("%d", son.getInt("SELLSALE_PRI")));
					map.put("장려금", "장려금" + i);
					map.put("SELLPAY_PRI", String.format("%d", son.getInt("SELLPAY_PRI")));
					
					map.put("SELLIN_PRI", String.format("%d", son.getInt("SELLIN_PRI")));
					map.put("미지급금액", "미지급금액" + i);
					map.put("BIGO", son.getString("BIGO"));
								
				    fillMaps.add(map);
				}
				
				// fill in the grid_item layout
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item13, 
						from, to);
				
				m_listPaymentView.setAdapter(adapter);
								
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료 : " + results.length(), Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
