package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;

public class ManageEventActivity extends Activity implements OnItemSelectedListener, OnItemClickListener{

	Spinner m_spinEvent;
	
	ListView m_listEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_event);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_spinEvent = (Spinner)findViewById(R.id.spinnerEventType);
		m_spinEvent.setOnItemSelectedListener(this);
		m_listEvent= (ListView)findViewById(R.id.listviewReadyToSendEventList);
		
		 // create the grid item mapping
        String[] from = new String[] {"행사명", "구분", "기간"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
 
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("행사명", "행사명" + i);
            map.put("구분", "구분_" + i);
            map.put("기간", "2013-05-01 ~ 2013-05-0" + i);
            fillMaps.add(map);
        }
 
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3, 
        		from, to);
        m_listEvent.setAdapter(adapter);
        m_listEvent.setOnItemClickListener(this);
        
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();         
//		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
//		actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("행사관리");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_event, menu);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, EventDetailViewActivity.class);
    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);	
	}
	
}
