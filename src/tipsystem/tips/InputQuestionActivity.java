package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class InputQuestionActivity extends Activity implements OnItemSelectedListener{

	// loading bar
	private ProgressDialog dialog; 
	Spinner m_spin;
	Button m_cancelButton;
	Button m_saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_question);
		// Show the Up button in the action bar.
		setupActionBar();
		
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
        
		String mode = getIntent().getStringExtra("MODE");
		if (mode.equals("UPDATE")) {

			String data = getIntent().getStringExtra("NOTICE");
			try {
				JSONObject json = new JSONObject(data);
				EditText editTextManagerName = (EditText) findViewById(R.id.editTextManagerName);
				EditText editTextContectNumber = (EditText) findViewById(R.id.editTextContectNumber);
				EditText editTextCustomerCode = (EditText) findViewById(R.id.editTextCustomerCode);
				//editTextManagerName.setText(json.getString("APP_USERNAME"));
				//editTextContectNumber.setText(json.getString("APP_HP"));
				editTextCustomerCode.setText(json.getString("B_Content"));				
				//m_spin.setSelection(Integer.valueOf(json.getString("APP_GUBUN")));
				
				m_saveButton.setVisibility(View.INVISIBLE);
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

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ActionBar actionbar = getActionBar();         
		//LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
		//actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setTitle("문의하기");
		//actionbar.setDisplayShowCustomEnabled(true);
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

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
		//TextView text1 = (TextView)m_spin.getSelectedView();
		//m_text.setText(text1.getText());
		
	}
	
	public void onNothingSelected(AdapterView<?> parent)
	{
		//m_text.setText("");
	}
	
	// private methos
	private void save() {

		EditText editTextManagerName = (EditText) findViewById(R.id.editTextManagerName);
		EditText editTextContectNumber = (EditText) findViewById(R.id.editTextContectNumber);
		EditText editTextCustomerCode = (EditText) findViewById(R.id.editTextCustomerCode);
		
		// ..
		
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


	// 새로운 공지사항 추가 함수 
    public void addOrUpdateNewNotice(int type, String b_ip, String code, String content, String name, String phone, String gubun) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	// 쿼리 작성하기
	    String query =  "";
	    
	    if (type==0) {
		    query = "INSERT INTO Mult_BBS(b_gubun,b_pass,b_html,b_title,b_content,b_ip,b_writerID,OFFICE_CODE,APP_USERNAME,APP_HP,APP_GUBUN)"; 
		    query += " VALUES('3','','1',CONVERT(VARCHAR(100)," + content+"),"+content+",'"+b_ip+"'," + code+"," + code+",'" + name+"','" + phone+"','" + gubun+"');";
	    }
	    else {   
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
    	if (results.length() > 0) {
    		
    	}
    }
}
