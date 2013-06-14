package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tipsystem.tips.ManageCustomerActivity.MyAsyncTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class ManageEventActivity extends Activity implements OnItemSelectedListener, OnItemClickListener{

	Spinner m_spinEvent;
	
	ListView m_listEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_event);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Button btn_Register = (Button)findViewById(R.id.buttonRegist);
		
		final EditText eventName = (EditText)findViewById(R.id.editTextEventName);
		final EditText period01 = (EditText)findViewById(R.id.editTextPeriod1);
		final EditText period02 = (EditText)findViewById(R.id.editTextPeriod2);
		final EditText barcode = (EditText)findViewById(R.id.editTextBarcode);
		final EditText pName = (EditText)findViewById(R.id.editTextProductName);
		final EditText pPrice = (EditText)findViewById(R.id.editTextPurchasePrice);
		final EditText sPrice = (EditText)findViewById(R.id.editTextSalePrice);
		final EditText Amount = (EditText)findViewById(R.id.editTextAmount);
		final EditText profitRatio = (EditText)findViewById(R.id.editTextProfitRatio);
				
		btn_Register.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				
				String Name = eventName.getText().toString();
				String period1 = period01.getText().toString();
				String period2 = period02.getText().toString();
				String code =  barcode.getText().toString();
				String Pname = pName.getText().toString();
				String PPrice = pPrice.getText().toString();
				String SPrice = sPrice.getText().toString();
				String amount = Amount.getText().toString();
				String ratio = profitRatio.getText().toString();
				
				 if(Name == "" || period1 == "" || period2  == "" || code == "" || Pname == "" ||
						 PPrice == "" || SPrice == "" || amount == "" || ratio == "")
		    	    	return;
		    	    
		         //   new MyAsyncTask().execute("2", Name, period1, period2, code, Pname, PPrice, SPrice, amount, ratio);

			}			
		});
		
		
		
		
		m_spinEvent = (Spinner)findViewById(R.id.spinnerEventType);
		m_spinEvent.setOnItemSelectedListener(this);
		m_listEvent= (ListView)findViewById(R.id.listviewReadyToSendEventList);
		
		 // create the grid item mapping
        String[] from = new String[] {"���������", "������", "������"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
 
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("���������", "���������" + i);
            map.put("������", "������_" + i);
            map.put("������", "2013-05-01 ~ 2013-05-0" + i);
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
		actionbar.setTitle("������������");
		
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
