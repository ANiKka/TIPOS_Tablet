package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ComparePriceDetailActivity extends Activity {

	ListView m_listPriceDetail;
	TextView m_barcodeTxt;
	TextView m_gNameTxt;
	TextView m_purPriTxt;
	TextView m_sellPriTxt;
	
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();	
	
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_compare_price_detail);
		
		// Show the Up button in the action bar.
		setupActionBar();

		Intent intent = getIntent();
		HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("fillMaps");   
		String BarCode = hashMap.get("BarCode");
		String G_Name = hashMap.get("G_Name");
		String Pur_Pri = hashMap.get("Pur_Pri");
		String Sell_Pri = hashMap.get("Sell_Pri");
		
		m_listPriceDetail= (ListView)findViewById(R.id.listviewPriceDetailList);

		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setTypeface(typeface);
		textView = (TextView) findViewById(R.id.textView3);
		textView.setTypeface(typeface);
		      
		m_barcodeTxt = (TextView) findViewById(R.id.textView4);
		m_barcodeTxt.setTypeface(typeface);
		m_barcodeTxt.setText(BarCode);
		
		m_gNameTxt = (TextView) findViewById(R.id.textView2);
		m_gNameTxt.setTypeface(typeface);
		m_gNameTxt.setText(G_Name);
		
		m_purPriTxt = (TextView) findViewById(R.id.textView6);
		m_purPriTxt.setTypeface(typeface);
		m_purPriTxt.setText(Pur_Pri);
		
		m_sellPriTxt = (TextView) findViewById(R.id.textView8);
		m_sellPriTxt.setTypeface(typeface);
		m_sellPriTxt.setText(Sell_Pri);
		
		comparePrice(BarCode, G_Name);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();
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

	private void comparePrice(String BarCode, String G_Name) {
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		String query = "";
 		query = "SELECT * FROM Goods WHERE BarCode = '" + BarCode + "' and G_Name = '" + G_Name + "';";
 		Log.w("MSSQL", "Query : " + query);
 		
 		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					updateListView(results);
		            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();					
				}
			}
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
 		
	}
	
	public void updateListView(JSONArray results) {
		
        if (!mfillMaps.isEmpty()) mfillMaps.clear();
        
        for (int i = 0; i < results.length(); i++) {
        	try {
            	JSONObject json = results.getJSONObject(i);
            	HashMap<String, String> map = JsonHelper.toStringHashMap(json);

	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        
		String[] from = new String[] {"Barcode", "G_Name", "Pur_Pri", "Sell_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
        
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(ComparePriceDetailActivity.this, mfillMaps, R.layout. activity_listview_compare_detail_list, from, to);
        m_listPriceDetail.setAdapter(adapter);
    }

}
