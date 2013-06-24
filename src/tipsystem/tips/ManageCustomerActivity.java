package  tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.MSSQL;

import android.os.Bundle;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ManageCustomerActivity extends Activity{

	Spinner m_spin;
	ListView m_cusList;
	
	TextView m_customerCode;
	TextView m_customerName;
	Spinner m_customerSection;
	
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	
    // loading bar
    public ProgressDialog dialog; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_customer);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//m_spin = (Spinner)findViewById(R.id.spinnerCustomerCodeType);
		//m_spin.setOnItemSelectedListener(this);
		m_cusList= (ListView)findViewById(R.id.listviewCustomerList);
		m_cusList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                fillCustomerFormFromList(position);
            }
        });
		
		m_customerCode = (TextView)findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView)findViewById(R.id.editTextCustomerName);
		m_customerSection = (Spinner)findViewById(R.id.spinnerCustomerCodeType);
		
		Button searchButton = (Button) findViewById(R.id.buttonCustomerSearch);
		Button registButton = (Button) findViewById(R.id.buttonCustomerRegist);
		Button renewButton = (Button) findViewById(R.id.buttonCustomerRenew);
		
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doQuery(1);
	        }
		});
        registButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doQuery(2);
	        }
		});
        renewButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doClear();
	        }
		});
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item1);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.item3);
        textView.setTypeface(typeface);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();         
		LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
		actionbar.setCustomView(custom_action_bar);

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowCustomEnabled(true);
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_customer, menu);
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
	
	// private methods
	private void fillCustomerFormFromList(int position) {		
		String code = mfillMaps.get(position).get("Office_Code");
		String name = mfillMaps.get(position).get("Office_Name");
		String StringSection = mfillMaps.get(position).get("Office_Sec");
		int section;
		Log.w("Log", "section : " + StringSection);
		m_customerCode.setText(code);
		m_customerName.setText(name);
		if(StringSection.equals("매입거래처")) section = 1;
		else if(StringSection.equals("매출거래처")) section = 2;
		else section = 3;
		m_customerSection.setSelection(section);
	}
	
    public void doQuery(final int code){
 		
    	//입력된 코드 가져오기
    	String customerCode = m_customerCode.getText().toString();
	    String customerName = m_customerName.getText().toString();
	    String customerSection = String.valueOf(m_customerSection.getSelectedItemPosition());

	    String query =  "";
	    switch(code){
	    // 등록
	    case 0 :
		    if (customerCode.equals("") || customerName.equals("")  || customerSection.equals("0")) {
		    	Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
		    	return;
		    }
		    
	    	query += "insert into Office_Manage(Office_Code, Office_Name, Office_Sec)" +
	    			"values('" + customerCode + "', '" + customerName + "', '" + customerSection + "');";
		    query += "select * from Office_Manage WHERE Office_Code = '" + customerCode + "';";
		    break;
		//조회
	    case 1 :
	    	query += "select * from Office_Manage ";
		    
		    if (!customerCode.equals("") || !customerName.equals("") || !customerSection.equals("0")){
		    	query += " WHERE";
		    }
		    
		    boolean added =false;
		    if (!customerCode.equals("")){
		    	query += " Office_Code = '" + customerCode + "'";
		    	added = true;
		    }
		    
		    if (!customerName.equals("")){
		    	if (added) query += " AND ";
		    	
		    	query += " Office_Name = '" + customerName  + "'";
		    	added = true;
		    }
		    
		    if (!customerSection.equals("0")){
		    	if (added) query += " AND ";
		    	
		    	query += " Office_Sec = '" + customerSection  + "'";
		    	added = true;
		    }
		    query += ";";
		    break;
	    
	    case 2 :
		    if (customerCode.equals("") || customerName.equals("")  || customerSection.equals("0")) {
		    	Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
		    	return;
		    }
	    	query = "select * from Office_Manage WHERE Office_Code = '" + customerCode + "' or Office_Name = '" + customerName + "';";
	    	break;	    	
	    }
    	
    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					if(code == 0) {
						onAlert();
						updateListView(results);
					} else if (code == 1) {
						Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
						updateListView(results);
					} else {
						Toast.makeText(getApplicationContext(), "이미 존재하는 거래처입니다.", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					if(code == 0)
						Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_SHORT).show();
					else if(code == 1)
						Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
					else
						doQuery(0);					
				}
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
	    
	    
    }
    
    public void doClear() {
    	m_customerCode.setText("");
    	m_customerName.setText("");
    	m_customerSection.setSelection(0);
    }
    
    public void updateListView(JSONArray results) {

		String[] from = new String[] {"Office_Code", "Office_Name", "Office_Sec"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };

        if (!mfillMaps.isEmpty()) mfillMaps.clear();
        
        for (int i = 0; i < results.length(); i++) {
        	
        	try {
            	JSONObject json = results.getJSONObject(i);
				String code = json.getString("Office_Code");
				String name = json.getString("Office_Name");
				String section = json.getString("Office_Sec");
				
				// prepare the list of all records
	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("Office_Code", code);
	            map.put("Office_Name", name);
	            
	    	    Log.w("test", "customerSection1 : " + section );
	    	    
	            if(section.equals("1")) section = "매입거래처";
	            else if(section.equals("2")) section = "매출거래처";
	            else if(section.equals("3")) section = "수수료거래처";
	            
	            map.put("Office_Sec", section);
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(ManageCustomerActivity.this, mfillMaps, R.layout. activity_listview_customer_list, from, to);
        m_cusList.setAdapter(adapter);
    }
    
    public void onAlert() {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);		
		alertDialog.setMessage("정상적으로 등록되었습니다.");
		
		alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// process전체 종료
				dialog.cancel();
			}
		});
		alertDialog.show();
	} 

}
