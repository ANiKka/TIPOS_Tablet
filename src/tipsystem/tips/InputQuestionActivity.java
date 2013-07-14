package tipsystem.tips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class InputQuestionActivity extends Activity implements OnItemSelectedListener{

	// loading bar
	private ProgressDialog dialog; 
	Spinner m_spin;
	Button m_cancelButton;
	Button m_saveButton;

	JSONObject m_shop;
	JSONObject m_userProfile;
	String m_office_code;
	
	String m_Mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_question);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData"); 
		m_userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
		
        Log.i("currentShopData", m_shop.toString() );
        Log.i("userProfile", m_userProfile.toString() );
			
        try {
			m_office_code = m_shop.getString("OFFICE_CODE");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
		
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setTypeface(typeface);

		textView = (TextView) findViewById(R.id.textView2);
		textView.setTypeface(typeface);
		
		textView = (TextView) findViewById(R.id.textView3);
		textView.setTypeface(typeface);
		
		textView = (TextView) findViewById(R.id.textView4);
		textView.setTypeface(typeface);
		
		TextView textViewShopTitle = (TextView) findViewById(R.id.textViewShopTitle);
		textViewShopTitle.setTypeface(typeface);
		
		m_spin = (Spinner)findViewById(R.id.spinnerQuestionType);
		m_spin.setOnItemSelectedListener(this);
		
		m_cancelButton = (Button) findViewById(R.id.buttonCancel);
		m_cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		m_saveButton = (Button) findViewById(R.id.buttonSave);
        m_saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
			}
		});

		EditText editTextManagerName = (EditText) findViewById(R.id.editTextManagerName);
		EditText editTextContectNumber = (EditText) findViewById(R.id.editTextContectNumber);
		EditText editTextContent = (EditText) findViewById(R.id.editTextContent);

		String phoneNumber = LocalStorage.getString(this, "phoneNumber");
		editTextContectNumber.setText(phoneNumber);
		
        m_Mode = getIntent().getStringExtra("MODE");
		if (m_Mode.equals("UPDATE")) {

			String data = getIntent().getStringExtra("NOTICE");
			try {
				JSONObject json = new JSONObject(data);
				editTextManagerName.setText(json.getString("APP_USERNAME"));
				editTextContectNumber.setText(json.getString("APP_HP"));
				editTextContent.setText(json.getString("B_Content"));		
				String APP_GUBUN = json.getString("APP_GUBUN");
				int gubun = 0;
				if (APP_GUBUN.equals("카드결재")) gubun = 0;
				if (APP_GUBUN.equals("프로그램")) gubun = 1;
				if (APP_GUBUN.equals("POS")) gubun = 2;
				if (APP_GUBUN.equals("AS")) gubun = 3;
				if (APP_GUBUN.equals("기타")) gubun = 4;
				m_spin.setSelection(gubun);

				//m_saveButton.setVisibility(View.INVISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		JSONObject currentShopData = LocalStorage.getJSONObject(this, "currentShopData"); 

		String Office_Name= null;	
        try {
			Office_Name = currentShopData.getString("Office_Name");
			textViewShopTitle.setText(Office_Name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// private methos
	private void save() {

		EditText editTextManagerName = (EditText) findViewById(R.id.editTextManagerName);
		EditText editTextContectNumber = (EditText) findViewById(R.id.editTextContectNumber);
		EditText editTextContent = (EditText) findViewById(R.id.editTextContent);
		
		//AS/카드결제/기타
		int gubun = m_spin.getSelectedItemPosition();
		String strGubun = "기타";
		switch (gubun) {
		case 0: strGubun ="카드결재"; break;
		case 1: strGubun ="프로그램"; break;
		case 2: strGubun ="POS"; break;
		case 3: strGubun ="AS"; break;
		case 4: strGubun ="기타"; break;
		}
		// ..
		addNewNotice("ip", m_office_code, editTextContent.getText().toString(), editTextManagerName.getText().toString(),
				editTextContectNumber.getText().toString(), strGubun);
		
	}

	// 새로운 공지사항 추가 함수 
    public void addNewNotice(String b_ip, String code, String content, String name, String phone, String gubun) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
    	// 쿼리 작성하기
	    String query =  "";
	    
	    if (m_Mode.equals("UPDATE")) {
		    query = "INSERT INTO Mult_BBS(b_gubun,b_pass,b_html,b_title,b_content,b_ip,b_writerID,OFFICE_CODE,APP_USERNAME,APP_HP,APP_GUBUN)" 
		    		+ " VALUES('3','','1',CONVERT(VARCHAR(100),'" + content+"'),'"+content+"','"
		    		+ b_ip +"','" + code+"','" + code+"','" + name+"','" + phone+"','" + gubun+"');";

	    	query += "UPDATE MULT_BBS SET B_REGROUP= B_IDX"; 
	    	query += " WHERE B_REGROUP = 0 ;";
	    }
	    else {
	    	query = "UPDATE Mult_BBS SET(b_gubun,b_pass,b_html,b_title,b_content,b_ip,b_writerID,OFFICE_CODE,APP_USERNAME,APP_HP,APP_GUBUN)" 
		    		+ " VALUES('3','','1',CONVERT(VARCHAR(100),'" + content+"'),'"+content+"','"
		    		+ b_ip +"','" + code+"','" + code+"','" + name+"','" + phone+"','" + gubun+"');";

	    	query = "UPDATE MULT_BBS SET B_REGROUP= B_IDX"; 
	    	query += " WHERE B_REGROUP = 0 ;";
	    }
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didAddOrUpdateNewNotice(results);
			}
	    }).execute("122.49.118.102:18971", "trans", "app_tips", "app_tips", query);
    }

    // DB에 접속후 호출되는 함수
    public void didAddOrUpdateNewNotice(JSONArray results) {

		AlertDialog.Builder builder = new AlertDialog.Builder(InputQuestionActivity.this);
        builder.setTitle("알림");
        builder.setMessage("저장이 완료되었습니다.");
        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
    
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	
			getActionBar().setDisplayHomeAsUpEnabled(true);		
			ActionBar actionbar = getActionBar();         
			//LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
			//actionbar.setCustomView(custom_action_bar);
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setTitle("문의하기");		
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.input_question, menu);
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
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
	{
	}
	
	public void onNothingSelected(AdapterView<?> parent)
	{
	}	
}
