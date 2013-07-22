package tipsystem.tips;

import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;

public class ConfigActivity extends Activity {

	JSONObject m_shop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_detail);
		
		m_shop = LocalStorage.getJSONObject(ConfigActivity.this, "currentShopData");

		String Office_Name= null, OFFICE_CODE =null, SHOP_IP = null, SHOP_PORT= null, APP_HP= null;				
		try {
			Office_Name = m_shop.getString("Office_Name");
			OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			SHOP_IP = m_shop.getString("SHOP_IP");
			SHOP_PORT = m_shop.getString("SHOP_PORT");
			APP_HP = m_shop.getString("APP_HP");
			Log.i("ConfigDetailActivity", m_shop.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		boolean isAutoLogin = LocalStorage.getBoolean(ConfigActivity.this, "AutoLogin:"+OFFICE_CODE);		
		String id = LocalStorage.getString(ConfigActivity.this, "LoginID:"+OFFICE_CODE);
		String pw = LocalStorage.getString(ConfigActivity.this, "LoginPW:"+OFFICE_CODE);
		String posID = LocalStorage.getString(ConfigActivity.this, "currentPosID:"+OFFICE_CODE);

		// 해당 OFFICE_CODE 에 포스ID 가 없을경우 'P' 로 셋팅
		if (posID.equals("")) {
			posID = "P";
			LocalStorage.setString(ConfigActivity.this, "currentPosID:"+OFFICE_CODE, "P");
		}
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewShopTitle);
        textView.setTypeface(typeface);
        textView.setText(Office_Name + " 환경설정");
        
        textView = (TextView) findViewById(R.id.textViewPhoneNumber);
        textView.setTypeface(typeface);
        textView.setText(APP_HP);

        EditText editText = (EditText) findViewById(R.id.editTextShopIP);
        editText.setText(SHOP_IP);

        editText = (EditText) findViewById(R.id.editTextShopPort);
        editText.setText(SHOP_PORT);
        
        Spinner spinnerPosID = (Spinner) findViewById(R.id.spinnerPosID);
        
        if (!posID.equals("")) {
        	char c = posID.charAt(0);
        	spinnerPosID.setSelection(c - 'A');
        }
        
        EditText editTextID = (EditText) findViewById(R.id.editTextID);
        EditText editTextPW = (EditText) findViewById(R.id.editTextPW);
        editTextID.setText(id);
        editTextPW.setText(pw);

		CheckBox checkBoxAutoLogin = (CheckBox) findViewById(R.id.checkBoxAutoLogin);
		checkBoxAutoLogin.setChecked(isAutoLogin);
			
        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
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


	private void save() {

		String posID = ((Spinner) findViewById(R.id.spinnerPosID)).getSelectedItem().toString();
		String id = ((EditText) findViewById(R.id.editTextID)).getText().toString();
		String pw = ((EditText) findViewById(R.id.editTextPW)).getText().toString();
		CheckBox checkBoxAutoLogin = (CheckBox) findViewById(R.id.checkBoxAutoLogin);
		boolean isChecked = checkBoxAutoLogin.isChecked();
		
		try {
			String OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			LocalStorage.setBoolean(ConfigActivity.this, "AutoLogin:"+OFFICE_CODE, isChecked);
			LocalStorage.setString(ConfigActivity.this, "LoginID:"+OFFICE_CODE, id);
			LocalStorage.setString(ConfigActivity.this, "LoginPW:"+OFFICE_CODE, pw);
			LocalStorage.setString(ConfigActivity.this, "currentPosID:"+OFFICE_CODE, posID);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config_detail, menu);
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
