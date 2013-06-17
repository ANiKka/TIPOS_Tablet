package tipsystem.tips;

import org.json.JSONArray;
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
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

public class ConfigDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		JSONArray shopsData = LocalStorage.getJSONArray(ConfigDetailActivity.this, "shopsData");
		String Office_Name= null, SHOP_IP = null, SHOP_PORT= null, APP_HP= null;				
		try {
			int idx = getIntent().getIntExtra("selectedShopIndex", 0);
			
			JSONObject shop = shopsData.getJSONObject(idx);
			Office_Name = shop.getString("Office_Name");
			SHOP_IP = shop.getString("SHOP_IP");
			SHOP_PORT = shop.getString("SHOP_PORT");
			APP_HP = shop.getString("APP_HP");
			Log.i("ConfigDetailActivity", shop.toString());
		} catch (JSONException e) {
			e.printStackTrace();
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
        
        editText = (EditText) findViewById(R.id.editTextPosID);
        editText = (EditText) findViewById(R.id.editTextID);
        editText = (EditText) findViewById(R.id.editTextPW);
        

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

	// private methos
	
	private void save() {

		EditText editTextPosID = (EditText) findViewById(R.id.editTextPosID);
		EditText editTextID = (EditText) findViewById(R.id.editTextID);
		EditText editTextPW = (EditText) findViewById(R.id.editTextPW);
		CheckBox checkBoxAutoLogin = (CheckBox) findViewById(R.id.checkBoxAutoLogin);
		boolean isChecked = checkBoxAutoLogin.isChecked();
		// action
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ConfigDetailActivity.this);
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
}
