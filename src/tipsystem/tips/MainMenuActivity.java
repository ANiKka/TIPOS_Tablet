package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

public class MainMenuActivity extends Activity {

	JSONObject m_shop;
	JSONObject m_userProfile;
	String m_APP_USER_GRADE;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	// loading bar
	private ProgressDialog dialog; 
	
	ListView m_listBoard;
	JSONArray m_results;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData"); 
		m_userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
		
        Log.i("currentShopData", m_shop.toString() );
        Log.i("userProfile", m_userProfile.toString() );
		
		String Office_Name= null, OFFICE_CODE =null, SHOP_IP = null, SHOP_PORT= null, APP_HP= null, OFFICE_CODE2 =null;	
        try {
			Office_Name = m_shop.getString("Office_Name");
			OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			
			m_APP_USER_GRADE = m_userProfile.getString("APP_USER_GRADE");
			OFFICE_CODE2 =m_userProfile.getString("OFFICE_CODE"); //수수료매장코드
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewShopTitle);
        textView.setTypeface(typeface);
        textView.setText(Office_Name);
        
        m_listBoard= (ListView)findViewById(R.id.listViewBoard);
        m_listBoard.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	
				try {
	            	Intent intent = new Intent(MainMenuActivity.this, InputQuestionActivity.class);
	            	intent.putExtra("MODE", "UPDATE");
	            	intent.putExtra("NOTICE", m_results.getJSONObject(position).toString());
	            	startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }
        });
        
		fetchNotices(m_ip,m_port,OFFICE_CODE);
	}

	public void onClickQuestion(View view)
	{
		Intent intent = new Intent(this, InputQuestionActivity.class);
		intent.putExtra("MODE", "NEW");
    	startActivity(intent);
	}
	
	public void onClickDefaultManage(View view)
	{
        // 수수료매장인경우
        if (m_APP_USER_GRADE.equals("2")) {
    		Toast.makeText(getApplicationContext(), "수수료매장은 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        
		Intent intent = new Intent(this, ManageCodeActivity.class);
    	startActivity(intent);
	}
	
	public void onClickPurchaseManage(View view)
	{
        // 수수료매장인경우
        if (m_APP_USER_GRADE.equals("2")) {
    		Toast.makeText(getApplicationContext(), "수수료매장은 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        
		Intent intent = new Intent(this, ManagePurchaseActivity.class);
    	startActivity(intent);
	}

	public void onClickSalesNews(View view)
	{
        // 수수료매장인경우
        if (m_APP_USER_GRADE.equals("2")) {
    		Toast.makeText(getApplicationContext(), "수수료매장은 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        // 부관리자인경우
        if (m_APP_USER_GRADE.equals("1")) {
    		Toast.makeText(getApplicationContext(), "부관리자는 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	 return;
        }
        
		Intent intent = new Intent(this, SalesNewsActivity.class);
    	startActivity(intent);
	}
	
	public void onClickSalesManage(View view)
	{
		// 부관리자인경우
        if (m_APP_USER_GRADE.equals("1")) {
    		Toast.makeText(getApplicationContext(), "부관리자는 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        
		Intent intent = new Intent(this, ManageSalesActivity.class);
    	startActivity(intent);
	}
	
	public void onClickEventManage(View view)
	{
        // 수수료매장인경우
        if (m_APP_USER_GRADE.equals("2")) {
    		Toast.makeText(getApplicationContext(), "수수료매장은 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        
		Intent intent = new Intent(this, ManageEventListActivity.class);
    	startActivity(intent);
	}
	
	public void onClickManageStock(View view)
	{
        // 수수료매장인경우
        if (m_APP_USER_GRADE.equals("2")) {
    		Toast.makeText(getApplicationContext(), "수수료매장은 사용할수 없습니다", Toast.LENGTH_SHORT).show(); 
        	return;
        }
        
		Intent intent = new Intent(this, ManageStockActivity.class);
    	startActivity(intent);
	}
	
	//  
    public void fetchNotices(String ip, String port, String code) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	// 쿼리 작성하기
	    String query =  "";
	    query = "SELECT * FROM MULT_BBS WHERE (B_GUBUN  = '0') OR (B_GUBUN = '3' AND OFFICE_CODE="+code+")  ORDER BY B_REGROUP DESC, B_RESTEP;"; 
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didFetchNotices(results);
			}
	    }).execute(ip + ":" + port, "trans", "app_tips", "app_tips", query);
    }

    // DB에 접속후 호출되는 함수
    public void didFetchNotices(JSONArray results) {
    	if (results.length() > 0) {
    		
    		m_results = results;
    		m_listBoard= (ListView)findViewById(R.id.listViewBoard);
    		
    		 // create the grid item mapping
    		String[] from = new String[] {"B_Title"};
    		int[] to = new int[] { R.id.itemContent};
    		
    		// prepare the list of all records
    		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    		
    		for(int i = 0; i < results.length(); i++){
    			try {
					JSONObject notice = results.getJSONObject(i);
					HashMap<String, String> map = JsonHelper.toStringHashMap(notice);
	    		    
	    		    String B_GUBUN = map.get("B_Gubun");
	    		    if (B_GUBUN.equals("0")) {
		    		    fillMaps.add(map);
	    		    }
				} catch (JSONException e) {
					e.printStackTrace();
				}
    		}
    		
    		for(int i = 0; i < results.length(); i++){
    			try {
					JSONObject notice = results.getJSONObject(i);
					HashMap<String, String> map = JsonHelper.toStringHashMap(notice);
	    		    
	    		    String B_GUBUN = map.get("B_Gubun");
	    		    if (B_GUBUN.equals("3")) {
		    		    fillMaps.add(map);
	    		    }
				} catch (JSONException e) {
					e.printStackTrace();
				}    			
    		}
    				
    		// fill in the grid_item layout
    		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_board, from, to);
    		
    		m_listBoard.setAdapter(adapter);
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
