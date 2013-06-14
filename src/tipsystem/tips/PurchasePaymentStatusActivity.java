package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
public class PurchasePaymentStatusActivity extends Activity implements OnItemClickListener{
	
	TabHost m_tabHost;
	
	ListView m_listPurchaseTab1;
	ListView m_listPurchaseTab2;
	ListView m_listPurchaseTab3;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_payment_status);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		m_tabHost = (TabHost) findViewById(R.id.tabhostPurchasePaymentStatus);
        m_tabHost.setup();
             
        TabHost.TabSpec spec = m_tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.tab1);
        spec.setIndicator("매입목록");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("결제현황");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("매입/매출");
        m_tabHost.addTab(spec);
     
        m_tabHost.setCurrentTab(0);

        setTabList1();
        setTabList2();
        setTabList3();
        
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView6);
        textView.setTypeface(typeface);
	}
	
	private void setTabList1()
	{
		m_listPurchaseTab1= (ListView)findViewById(R.id.listviewPurchaseListTab1);
		
		 // create the grid item mapping
		String[] from = new String[] {"전표번호", "매입일", "거래처명", "총매입가"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("전표번호", "0000" + i);
			map.put("매입일", "2013-05-0" + i);
			map.put("거래처명", "거래처명"+ i);
			map.put("총매입가", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_product_list, 
				from, to);
		
		m_listPurchaseTab1.setAdapter(adapter);
		m_listPurchaseTab1.setOnItemClickListener(this);
	}
	
	private void setTabList2()
	{
		m_listPurchaseTab2= (ListView)findViewById(R.id.listviewPurchaseListTab2);
		
		 // create the grid item mapping
		String[] from = new String[] {"코드", "거래처명", "이월", "지급금액", "미지급금액"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("코드", "0000" + i);
			map.put("거래처명", "거래처명" + i);
			map.put("이월", "이월"+ i);
			map.put("지급금액", i + "000");
			map.put("미지급금액", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item5, 
				from, to);
		
		m_listPurchaseTab2.setAdapter(adapter);
		m_listPurchaseTab2.setOnItemClickListener(this);
	}
	
	private void setTabList3()
	{
		m_listPurchaseTab3= (ListView)findViewById(R.id.listviewPurchaseListTab3);
		
		 // create the grid item mapping
		String[] from = new String[] {"코드", "거래처명", "순매입", "순매출"};
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
		
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < 10; i++)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("코드", "0000" + i);
			map.put("거래처명", "거래처명" + i);
			map.put("순매입", i + "000");
			map.put("순매출", i + "000");
			fillMaps.add(map);
		}
		
		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, 
				from, to);
		
		m_listPurchaseTab3.setAdapter(adapter);
		m_listPurchaseTab3.setOnItemClickListener(this);
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
		actionbar.setTitle("매입/대금 결제현황");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_payment_status, menu);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if ( m_listPurchaseTab1.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, PurchaseListDetailViewActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		else if ( m_listPurchaseTab2.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, PaymentDetailViewActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		else if ( m_listPurchaseTab3.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, CustomerPurchasePaymentDetailActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);	
		}
		
		
		//Toast.makeText(this, "Item Click." + m_listPurchaseTab1.getId() + " ,  " + arg0.getId(), Toast.LENGTH_SHORT).show();
		
		
	}

}
