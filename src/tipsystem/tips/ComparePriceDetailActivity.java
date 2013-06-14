package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ComparePriceDetailActivity extends Activity {

	ListView m_listPriceDetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_price_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPriceDetail= (ListView)findViewById(R.id.listviewPriceDetailList);
		
		 // create the grid item mapping
        String[] from = new String[] {"바코드", "상품명", "매입가", "판매가"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
 
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("바코드", "0000" + i);
            map.put("상품명", "상품명_" + i);
            map.put("매입가", i + "000");
            map.put("판매가", i + "000");
            fillMaps.add(map);
        }

       // fill in the grid_item layout
       SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_price_detail_list, 
       		from, to);
       
       m_listPriceDetail.setAdapter(adapter);
       
       
       Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
       TextView textView = (TextView) findViewById(R.id.textView1);
       textView.setTypeface(typeface);
       
       textView = (TextView) findViewById(R.id.textView2);
       textView.setTypeface(typeface);
       
       textView = (TextView) findViewById(R.id.textView3);
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
		actionbar.setTitle("가격비교");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compare_price_detail, menu);
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
