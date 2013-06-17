package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class EventDetailViewActivity extends Activity {

	ListView m_listDetailView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listDetailView= (ListView)findViewById(R.id.listviewReadyToSendEventDetailViewList);
		TextView evtName = (TextView)findViewById(R.id.textView2);
		TextView evtPeriod = (TextView)findViewById(R.id.textView5);
		TextView evtGubun = (TextView)findViewById(R.id.textView9);
		
		
		
		Intent intent = getIntent();
		
		evtName.setText(intent.getExtras().getString("Evt_Name"));
		evtPeriod.setText(intent.getExtras().getString("Evt_Period"));
		evtGubun.setText(intent.getExtras().getString("Evt_Gubun"));
				
		 // create the grid item mapping
		String[] from = new String[] {"BarCode", "G_Name", "Sale_Pur", "Sale_Sell"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		
	    HashMap<String, String> map = new HashMap<String, String>();
	    map.put("BarCode", intent.getExtras().getString("BarCode"));
		map.put("G_Name", intent.getExtras().getString("G_Name"));
		map.put("Sale_Pur", intent.getExtras().getString("Sale_Pur"));
		map.put("Sale_Sell", intent.getExtras().getString("Sale_Sell"));
	    fillMaps.add(map);
	
				
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listDetailView.setAdapter(adapter);
		
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
		actionbar.setTitle("행사전송대기목록");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_detail_view, menu);
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
