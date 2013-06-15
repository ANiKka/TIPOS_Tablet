package tipsystem.tips;

import org.json.JSONArray;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
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

public class ConfigActivity extends Activity {

	// loading bar
	private ProgressDialog dialog; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Intent intent = getIntent();
		
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewID);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textViewPW);
        textView.setTypeface(typeface);
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
		Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(this, MainMenuActivity.class);
    	startActivity(intent);
	}
	
	// 인증관련 실행 함수 
    public void onAuthentication(View view) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		// 입력된 코드 가져오기
    	String id = (String)((EditText) findViewById(R.id.editTextShopCode)).getText().toString();
    	String pw = (String)((EditText) findViewById(R.id.editTextLoginPW)).getText().toString();
    	if (id.equals("") || pw.equals("") ) return;

    	// 쿼리 작성하기
	    String query =  "";
	    query =  "select * " 
	    		+ "from APP_USER " 
	    		+ ";";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didLogin(results);
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
    }

    // DB에 접속후 호출되는 함수
    public void didLogin(JSONArray results) {
    	if (results.length() > 0) {
    		// 저장소에 저장
    		LocalStorage.setJSONArray(this, "loginResult", results);
    		
    		Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show(); 
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();
    	}
    }
}
