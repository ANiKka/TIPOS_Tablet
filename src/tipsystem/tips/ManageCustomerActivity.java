package  tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;

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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
	
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";

	Spinner m_spin;
	ListView m_cusList;
	SimpleAdapter m_adapter; 
	
	TextView m_customerCode;
	TextView m_customerName;
	Spinner m_customerSection;

	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	
    // loading bar
    public ProgressDialog dialog; 
	
    // loading more in listview
    int currentVisibleItemCount;
    private boolean isEnd = false;
    private OnScrollListener customScrollListener = new OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        	
            currentVisibleItemCount = visibleItemCount;

            if((firstVisibleItem + visibleItemCount) == totalItemCount && firstVisibleItem != 0) 
            	isEnd = true;            
            else 
            	isEnd = false;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (isEnd && currentVisibleItemCount > 0 && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				doSearch();
		    }
        }
    };
      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_customer);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		m_customerCode = (TextView)findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView)findViewById(R.id.editTextCustomerName);
		m_customerSection = (Spinner)findViewById(R.id.spinnerCustomerCodeType);
		
		Button searchButton = (Button) findViewById(R.id.buttonCustomerSearch);
		Button registButton = (Button) findViewById(R.id.buttonCustomerRegist);
		Button renewButton = (Button) findViewById(R.id.buttonCustomerRenew);
		
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	deleteListViewAll();
	        	doSearch();
	        }
		});
        registButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doRegister();
	        }
		});
        renewButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doClearInputBox();
	        }
		});

		m_cusList= (ListView)findViewById(R.id.listviewCustomerList);
		m_cusList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fillCustomerFormFromList(position);
            }
        });
		
		m_cusList.setOnScrollListener(customScrollListener);

		String[] from = new String[] {"Office_Code", "Office_Name", "Office_Sec"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
        
        // fill in the grid_item layout
        m_adapter = new SimpleAdapter(this, mfillMaps, R.layout. activity_listview_customer_list, from, to);
        m_cusList.setAdapter(m_adapter);
        
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

	// private methods
	private void fillCustomerFormFromList(int position) {		
		String code = mfillMaps.get(position).get("Office_Code");
		String name = mfillMaps.get(position).get("Office_Name");
		String StringSection = mfillMaps.get(position).get("Office_Sec");
		int section;
		
		m_customerCode.setText(code);
		m_customerName.setText(name);
		if(StringSection.equals("매입거래처")) section = 1;
		else if(StringSection.equals("매출거래처")) section = 2;
		else section = 3;
		m_customerSection.setSelection(section);
	}
	
	public void deleteListViewAll() {
		if (mfillMaps.isEmpty()) return;
        
		mfillMaps.removeAll(mfillMaps);
		m_adapter.notifyDataSetChanged();
	}

    public void doClearInputBox() {
    	m_customerCode.setText("");
    	m_customerName.setText("");
    	m_customerSection.setSelection(0);
    }
    
    public void updateListView(JSONArray results) {
        
        for (int i = 0; i < results.length(); i++) {
        	
        	try {
            	JSONObject json = results.getJSONObject(i);
            	HashMap<String, String> map = JsonHelper.toStringHashMap(json);
            	
	            String section = map.get("Office_Sec");	            
	            if(section.equals("1")) section = "매입거래처";
	            else if(section.equals("2")) section = "매출거래처";
	            else if(section.equals("3")) section = "수수료거래처";
	            map.put("Office_Sec", section);
	            
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        m_adapter.notifyDataSetChanged();
    }

	//조회
	public void doSearch(){
		//입력된 코드 가져오기
    	String customerCode = m_customerCode.getText().toString();
	    String customerName = m_customerName.getText().toString();
	    String customerSection = String.valueOf(m_customerSection.getSelectedItemPosition());

	    String query =  "";
    	query += "select * from Office_Manage ";
    	String index = String.valueOf(mfillMaps.size());
    	query = "select TOP 50 * from Office_Manage WHERE Office_Code NOT IN (SELECT TOP "+ index + " Office_Code FROM Office_Manage) ";
    		    
	    if (!customerCode.equals("")) {
	    	query += " AND Office_Code = '" + customerCode + "'";
	    }
	    
	    if (!customerName.equals("")) {
	    	query += " AND Office_Name = '" + customerName  + "'";
	    }
	    
	    if (!customerSection.equals("0")) {
	    	query += " AND Office_Sec = '" + customerSection  + "'";
	    }
	    query += ";";
    	
    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
					updateListView(results);
				}
				else {
					Toast.makeText(getApplicationContext(), "결과가 없습니다", Toast.LENGTH_SHORT).show();					
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();	
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void doRegister(){
		//입력된 코드 가져오기
    	String customerCode = m_customerCode.getText().toString();
	    String customerName = m_customerName.getText().toString();
	    String customerSection = String.valueOf(m_customerSection.getSelectedItemPosition());

	    String query =  "";

	    if (customerCode.equals("") || customerName.equals("")  || customerSection.equals("0")) {
	    	Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
    	query += "insert into Office_Manage(Office_Code, Office_Name, Office_Sec)" +
    			"values('" + customerCode + "', '" + customerName + "', '" + customerSection + "');";
	    query += "select * from Office_Manage WHERE Office_Code = '" + customerCode + "';";
    	
    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		deleteListViewAll();
 		
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				
				if (results.length() > 0) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());		
					alertDialog.setMessage("정상적으로 등록되었습니다.");						
					alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {								
						}
					});
					alertDialog.show();
					updateListView(results);
				}
				else {
					Toast.makeText(getApplicationContext(), "알수없는 이유로 등록하지 못하였습니다", Toast.LENGTH_SHORT).show();					
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_SHORT).show();	
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	 }
}
