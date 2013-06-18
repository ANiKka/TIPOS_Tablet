package tipsystem.tips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

public class LoginActivity extends Activity {

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	
	// loading bar
	private ProgressDialog dialog; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		// Show the Up button in the action bar.
		setupActionBar();
				
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewID);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textViewPW);
        textView.setTypeface(typeface);

        m_shop = LocalStorage.getJSONObject(LoginActivity.this, "currentShopData");
       /*
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
        
        // test
		EditText editTextLoginID = (EditText) findViewById(R.id.editTextLoginID);
		EditText editTextLoginPW = (EditText) findViewById(R.id.editTextLoginPW);
		editTextLoginID.setText("3");
		editTextLoginPW.setText("3");
	}
	
	protected void onResume() {
		super.onResume();
		
		try {
			String OFFICE_CODE = m_shop.getString("OFFICE_CODE");

			// 해당 OFFICE_CODE 에 포스ID 가 없을경우 'P' 로 셋팅
			String posID = LocalStorage.getString(LoginActivity.this, "currentPosID:"+OFFICE_CODE);
			if (posID.equals(""))  LocalStorage.setString(LoginActivity.this, "currentPosID:"+OFFICE_CODE, "P");
				
			boolean isAutoLogin = LocalStorage.getBoolean(LoginActivity.this, "AutoLogin:"+OFFICE_CODE);
			
			if (isAutoLogin) {
				String id = LocalStorage.getString(LoginActivity.this, "LoginID:"+OFFICE_CODE);
				String pw = LocalStorage.getString(LoginActivity.this, "LoginPW:"+OFFICE_CODE);

		    	doLogin(m_ip, m_port, id, pw);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar actionbar = getActionBar();         
			LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
			actionbar.setCustomView(custom_action_bar);

			actionbar.setDisplayShowHomeEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config, menu);
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

	public void onClickLogin(View view)
	{
		EditText editTextLoginID = (EditText) findViewById(R.id.editTextLoginID);
		EditText editTextLoginPW = (EditText) findViewById(R.id.editTextLoginPW);
		
		String id = editTextLoginID.getText().toString();
		String pw = editTextLoginPW.getText().toString();
		
		editTextLoginID.setText("");		
		editTextLoginPW.setText("");
		
    	if (id.equals("") || pw.equals("") ) {
    		Toast.makeText(this, "비어있는 칸이 있습니다", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	doLogin(m_ip, m_port, id, pw);
	}
	
	// 로그인관련 실행 함수 
    public void doLogin(String ip, String port, String id, String pw) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	// 쿼리 작성하기
	    String query =  "";
	    query =  "select * " 
	    		+ "from Admin_User where User_ID='" + id + "' and User_PWD ='" + pw +"'"  
	    		+ ";";
	    //query = "select * from Admin_User ;";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didLogin(results);
			}
	    }).execute(ip + ":" + port, "TIPS", "sa", "tips", query);
    }

    // DB에 접속후 호출되는 함수
    public void didLogin(JSONArray results) {
    	if (results.length() > 0) {

    		Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show(); 
    		
    		Intent intent = new Intent(this, MainMenuActivity.class);
        	startActivity(intent);
        	finish();
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
    	}
    }
}
