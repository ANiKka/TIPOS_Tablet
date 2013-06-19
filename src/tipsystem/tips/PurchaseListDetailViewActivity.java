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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PurchaseListDetailViewActivity extends Activity {

	ListView m_listPurchaseDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_list_detail_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPurchaseDetail= (ListView)findViewById(R.id.listviewPurchaseDetailViewList);
		
		Intent intent = getIntent();
		
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		String inDate = intent.getExtras().getString("IN_DATE");
		
		int year1 = Integer.parseInt(inDate.substring(0, 4));
 		int month1 = Integer.parseInt(inDate.substring(5, 7));
 		
		String tableName = String.format("InD_%04d%02d", year1, month1);
		
    	String query;
		
    	query = "select " + tableName + ".BARCODE, " + "Goods.G_NAME, " + tableName + ".PUR_PRI, " 
     			+ tableName + ".SELL_PRI, " + tableName + ".IN_COUNT "
					//query = "select * "
	    		+"from " + tableName + " inner join GOODS on "
	    		+ tableName + ".BARCODE = GOODS.BARCODE " 
	    		+ "where " + tableName + ".IN_DATE = '" + inDate + "' and OFFICE_NAME = '" + customerName + "'";
	    		
     			
		// 콜백함수와 함께 실행
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {
		
			@Override
			public void onRequestCompleted(JSONArray results) {
				setListItems(results);
			}
		}).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		
		 
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
		actionbar.setTitle("매입목록 상세보기");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_list_detail_view, menu);
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
				String[] from = new String[] {"BARCODE", "G_NAME", "PUR_PRI", "SELL_PRI", "IN_COUNT"};
				int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
				
				// prepare the list of all records
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
 			
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("BARCODE", son.getString("BARCODE") );
					map.put("G_NAME", son.getString("G_NAME"));
					
					map.put("PUR_PRI", String.format("%d", son.getInt("PUR_PRI")) );
					map.put("SELL_PRI", String.format("%d", son.getInt("SELL_PRI")) );
					map.put("IN_COUNT", String.format("%d", son.getInt("IN_COUNT")) );
					
					fillMaps.add(map);
				}	
			
				// fill in the grid_item layout
				SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
						from, to);
				
				m_listPurchaseDetail.setAdapter(adapter);
								
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료 : " + results.length(), Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
