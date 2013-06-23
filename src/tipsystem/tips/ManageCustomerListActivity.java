package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ManageCustomerListActivity extends Activity{

	JSONArray m_results;
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	ListView m_cusList;
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	
    // loading bar
    public ProgressDialog dialog; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_customer_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		m_cusList= (ListView)findViewById(R.id.listviewCustomerList);
		m_cusList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            returnResultData(position);
            }
        });
		
		doSearch();
	}
	
	private void returnResultData(int position) {

		Intent intent = new Intent();
		
		try {
			JSONObject result = m_results.getJSONObject(position);
			
			intent.putExtra("result", result.toString());			
		} catch (JSONException e) {			
			e.printStackTrace();
		}

		this.setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();         
		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
		actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_customer, menu);
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
	
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	
	// 조회 실행 함수 
    public void doSearch() {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	// 쿼리 작성하기
	    String query =  "";
    	query += "select * from Office_Manage ;";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				updateListView(results);
		    	Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
    }
    
    public void updateListView(JSONArray results) {

		String[] from = new String[] {"Office_Code", "Office_Name", "Office_Sec"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };

        if (results.length() ==0)  	return;
        
        m_results = results;        
        
        if (!mfillMaps.isEmpty()) mfillMaps.clear();
        
        for (int i = 0; i < results.length(); i++) {
        	
        	try {
            	JSONObject json = results.getJSONObject(i);
				String code = json.getString("Office_Code");
				String name = json.getString("Office_Name");
				String section = json.getString("Office_Sec");
				
				// prepare the list of all records
	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("Office_Code", code);
	            map.put("Office_Name", name);
	            
	            if(section.equals("1")) section = "매입거래처";
	            else if(section.equals("2")) section = "매출거래처";
	            else section = "수수료거래처";
	            
	            map.put("Office_Sec", section);
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(this, mfillMaps, R.layout. activity_listview_customer_list, from, to);
        m_cusList.setAdapter(adapter);
    }
}
