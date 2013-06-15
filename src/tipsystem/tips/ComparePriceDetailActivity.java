package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ComparePriceDetailActivity extends Activity {

	ListView m_listPriceDetail;
	TextView m_barcodeTxt;
	TextView m_gNameTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("HashMapTest", "1");
		setContentView(R.layout.activity_compare_price_detail);
		Log.e("HashMapTest", "2");
		// Show the Up button in the action bar.
		setupActionBar();

		Intent intent = getIntent();
		Log.e("HashMapTest", "3");
	    //HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("fillMaps");
		ArrayList<String> fillMaps = intent.getStringArrayListExtra("fillMaps");
	    Log.e("HashMapTest", "data : " + fillMaps.get(0));
	    Log.e("HashMapTest", "5");
	    //Log.e("HashMapTest", "11111111111111111111111111111111111111111111111111111111111111");
	    
		m_listPriceDetail= (ListView)findViewById(R.id.listviewPriceDetailList);


       Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
       TextView textView = (TextView) findViewById(R.id.textView1);
       textView.setTypeface(typeface);
       
       m_barcodeTxt = (TextView) findViewById(R.id.textView2);
       m_barcodeTxt.setTypeface(typeface);
       m_barcodeTxt.setText(fillMaps.get(0));
       
       textView = (TextView) findViewById(R.id.textView3);
       textView.setTypeface(typeface);
       
       m_gNameTxt = (TextView) findViewById(R.id.textView4);
       m_gNameTxt.setTypeface(typeface);
       m_gNameTxt.setText(fillMaps.get(1));
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
