package tipsystem.tips;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL2;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ManageEventListActivity extends Activity {
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	ListView m_cusList;
	SimpleAdapter m_adapter; 
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();

    // loading bar
	private ProgressDialog dialog;

    // loading more in listview
    int currentVisibleItemCount;
    private boolean isEnd = false;
    private OnScrollListener customScrollListener = new OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            currentVisibleItemCount = visibleItemCount;

            if((firstVisibleItem + visibleItemCount) == totalItemCount && firstVisibleItem != 0) 
            	isEnd = true;            
            else 
            	isEnd = false;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (isEnd && currentVisibleItemCount > 0 && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				doSearch();
		    }
        }
    };
      
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_event_list);
		
        m_shop = LocalStorage.getJSONObject(this, "currentShopData");        
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		m_cusList= (ListView)findViewById(R.id.listviewManageProductList);
		m_cusList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	returnResultData(position);
            }
        });
		
		m_cusList.setOnScrollListener(customScrollListener);

        
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (!mfillMaps.isEmpty()) {
			mfillMaps.removeAll(mfillMaps);
			m_adapter.notifyDataSetChanged();	
		}

    	String[] from = new String[] {"Evt_Name", "Evt_Gubun_text", "Evt_Date"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3};
        m_adapter = new SimpleAdapter(this, mfillMaps, R.layout. activity_event_list, from, to);
        m_cusList.setAdapter(m_adapter);
        
        doSearch();
	}

	private void returnResultData(int position) {
		String Evt_CD = mfillMaps.get(position).get("Evt_CD");
  		Intent intent = new Intent(this, ManageEventActivity.class);
		intent.putExtra("Evt_CD", Evt_CD);
		startActivity(intent);
	}
	
	private void updateListView(JSONArray results) {
        
        for (int i = 0; i < results.length(); i++) {
        	
        	try {
            	JSONObject json = results.getJSONObject(i);
            	HashMap<String, String> map = JsonHelper.toStringHashMap(json);
            	
	            String Evt_Gubun = map.get("Evt_Gubun");
            	if (Evt_Gubun.equals("1")) map.put("Evt_Gubun_text", "기간행사");
            	else if(Evt_Gubun.equals("0")) map.put("Evt_Gubun_text", "시간행사");
            	else map.put("Evt_Gubun_text", "연속행사");
            	String Evt_SDate = map.get("Evt_SDate");
            	String Evt_EDate = map.get("Evt_EDate");
            	map.put("Evt_Date", Evt_SDate + " ~ " + Evt_EDate);
	            mfillMaps.add(map);
	            		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        m_adapter.notifyDataSetChanged();
    }
    
	private void doSearch() {

		String query = "";    	
		query = "SELECT Evt_CD,Evt_Name,Evt_Gubun,Evt_SDate,Evt_EDate FROM Evt_Mst GROUP BY Evt_CD,Evt_Name,Evt_Gubun,Evt_SDate,Evt_EDate ;";

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();

	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					updateListView(results);
		            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "결과가 없습니다", Toast.LENGTH_SHORT).show();							
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			ActionBar actionbar = getActionBar();         
	
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setTitle("전체 행사 조회");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer_product_detail_view, menu);
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
	
	public void onNewEvent(View v) {

  		Intent intent = new Intent(this, ManageEventActivity.class);
		intent.putExtra("Evt_CD", "");
      	startActivity(intent);
	};
}