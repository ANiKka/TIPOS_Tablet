package tipsystem.tips;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class InputQuestionActivity extends Activity implements OnItemSelectedListener{

	Spinner m_spin;
	//TextView m_text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_question);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
		TextView textView = (TextView) findViewById(R.id.textViewShopTitle);
		textView.setTypeface(typeface);
		
		textView = (TextView) findViewById(R.id.textView1);
		textView.setTypeface(typeface);

		textView = (TextView) findViewById(R.id.textView2);
		textView.setTypeface(typeface);
		
		textView = (TextView) findViewById(R.id.textView3);
		textView.setTypeface(typeface);
		
		textView = (TextView) findViewById(R.id.textView4);
		textView.setTypeface(typeface);
		
		
		m_spin = (Spinner)findViewById(R.id.spinnerQuestionType);
		m_spin.setOnItemSelectedListener(this);
		
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

}
