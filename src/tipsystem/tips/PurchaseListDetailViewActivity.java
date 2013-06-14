package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
		
		 // create the grid item mapping
		String[] from = new String[] {"바코드", "상품명", "매입가", "판매가", "수량"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++){
		    HashMap<String, String> map = new HashMap<String, String>();
		    map.put("바코드", "0000" + i);
			map.put("상품명", "상품명_" + i);
			map.put("매입가", i + "000");
			map.put("판매가", i + "000");
			map.put("수량", i + "0");
		    fillMaps.add(map);
		}
				
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
				from, to);
		
		m_listPurchaseDetail.setAdapter(adapter);
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

}
