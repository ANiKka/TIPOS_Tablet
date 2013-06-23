package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PurchaseDetailActivity extends Activity {

	ListView m_listPurchaseDetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPurchaseDetail= (ListView)findViewById(R.id.listviewPurchaseDetailList);
		
		 // create the grid item mapping
		String[] from = new String[] {"바코드", "상품명", "매입가", "판매가", "수량"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();		
		String data = getIntent().getStringExtra("data");
		try {
			JSONArray jsons = new JSONArray(data);
			
			for(int i = 0; i < jsons.length(); i++){
				JSONObject obj = jsons.getJSONObject(i);
			    HashMap<String, String> map = new HashMap<String, String>();
			    map.put("바코드", obj.getString("BarCode"));
				map.put("상품명", obj.getString("G_Name"));
				map.put("매입가", obj.getString("Pur_Pri"));
				map.put("판매가", obj.getString("Sell_Pri"));
				map.put("수량", obj.getString("In_Count"));
			    fillMaps.add(map);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String Office_Name = getIntent().getStringExtra("Office_Name");
		String Office_Code = getIntent().getStringExtra("Office_Code");

		EditText customerCode = (EditText)findViewById(R.id.editTextCustomerCode);
		EditText customerName = (EditText)findViewById(R.id.editTextCustomerName);
		customerCode.setText(Office_Name);
		customerName.setText(Office_Code);
		customerCode.setEnabled(false);
		customerName.setEnabled(false);
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_purchase_detail_list, 
				from, to);
		
		m_listPurchaseDetail.setAdapter(adapter);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setTypeface(typeface);
		  
		textView = (TextView) findViewById(R.id.textView4);
		textView.setTypeface(typeface);
		  
		textView = (TextView) findViewById(R.id.textView5);
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
		actionbar.setTitle("매입 상세보기");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_detail, menu);
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
