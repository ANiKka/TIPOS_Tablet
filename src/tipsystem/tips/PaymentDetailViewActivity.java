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

public class PaymentDetailViewActivity extends Activity {
	
	ListView m_listPaymentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_detail_view);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listPaymentView = (ListView)findViewById(R.id.listviewPaymentDetailViewList);
		
		 // create the grid item mapping
		String[] from = new String[] {"순번", "처리년월", "전표번호", "구분", "이월",
									  "매입금액", "반품금액", "할인금액", "장려금", "지급금액",
									  "입금금액", "미지급금액", "비고"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5,
								R.id.item6, R.id.item7, R.id.item8, R.id.item9, R.id.item10,
								R.id.item11, R.id.item12, R.id.item13};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 30; i++){
		    HashMap<String, String> map = new HashMap<String, String>();
		    map.put("순번", "" + i);
			map.put("처리년월", "2013-05-0" + i);
			map.put("전표번호", i + "000");
			map.put("구분", "구분" + i);
			map.put("이월", "이월" + i);
			
			map.put("매입금액", "매입금액" + i);
			map.put("반품금액", "반품금액" + i);
			map.put("할인금액", "할인금액" + i);
			map.put("장려금", "장려금" + i);
			map.put("지급금액", "지급금액" + i);
			
			map.put("입금금액", "입금금액" + i);
			map.put("미지급금액", "미지급금액" + i);
			map.put("비고", "비고" + i);
						
		    fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item13, 
				from, to);
		
		m_listPaymentView.setAdapter(adapter);
		
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

}
