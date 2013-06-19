package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ComparePriceActivity extends Activity{

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	
	TextView m_customer;
	TextView m_customer2;
	TextView m_barcode;
	TextView m_productionName;
	TextView m_local;	
	
	ListView m_listPriceSearch;
	private ProgressDialog dialog;
	
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_price);
		// Show the Up button in the action bar.
		setupActionBar();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);
        
        m_customer = (TextView)findViewById(R.id.editTextCustomer);
		m_customer2 = (TextView)findViewById(R.id.editTextCustomer2);
		m_barcode = (TextView)findViewById(R.id.editTextBarcord);
		m_productionName = (TextView)findViewById(R.id.editTextProductionName);
		m_local = (TextView)findViewById(R.id.editTextLocal);
		Button searchButton = (Button) findViewById(R.id.buttonPriceSearch);
		Button buttonBarcode = (Button) findViewById(R.id.buttonBarcode);
				
		m_listPriceSearch = (ListView)findViewById(R.id.listviewPriceSearchList);
		m_listPriceSearch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                sendDataFromList(position);
            }
        });
		
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doSearch();
	        }
		});
		
		// 바코드 카메라 기능
		buttonBarcode.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doBarcodeSearch();
	        }
		});
		
		// 바코드 입력 후 포커스 딴 곳을 옮길 경우
		m_barcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String barcode = null; 
			    	barcode = m_barcode.getText().toString();
			    	if(!barcode.equals("")) // 입력한 Barcode가 값이 있을 경우만
			    		fillGoodNameFromBarcode(barcode);	    	
			    }
			}
		});

		// 거래처 코드 입력 후 포커스 딴 곳을 옮길 경우
		m_customer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String customerCode = null; 
			    	customerCode = m_customer.getText().toString();
			    	if(!customerCode.equals("")) // 입력한 customerCode가 값이 있을 경우만
			    		fillBusNameFromBusCode(customerCode);	    	
			    }
			}
		});	
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
		actionbar.setTitle("가격비교");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compare_price, menu);
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
	
	private void fillGoodNameFromBarcode(String barcode) {
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		// TODO Auto-generated method stub
		String query = "";
		
		query = "SELECT G_Name From Goods WHERE BarCode = '" + barcode + "';";
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					try {
						JSONObject json = results.getJSONObject(0);
						String g_name = json.getString("G_Name");
						m_productionName.setText(g_name);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            //Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();	
		            m_productionName.setText("");
				}
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		
	}

	private void fillBusNameFromBusCode(String customerCode) {
		// TODO Auto-generated method stub
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		// TODO Auto-generated method stub
		String query = "";
		
		query = "SELECT Office_Name From Office_Manage WHERE Office_Code = '" + customerCode + "';";
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					try {
						JSONObject json = results.getJSONObject(0);
						String bus_name = json.getString("Office_Name");
						m_customer2.setText(bus_name);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            //Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
		            m_customer2.setText("");
				}
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
	}
	
	private void sendDataFromList(int position){
		String Barcode = mfillMaps.get(position).get("barcode");
		String G_Name = mfillMaps.get(position).get("gName");
		
		ArrayList<String> sendArr = new ArrayList<String>();
		sendArr.add(Barcode);
		sendArr.add(G_Name);
		
		Intent intent = new Intent(this, ComparePriceDetailActivity.class);
		intent.putExtra("fillMaps", sendArr);
    	startActivity(intent);
	}

	public void doSearch(){
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	String customer = m_customer.getText().toString();
	    String customer2 = m_customer2.getText().toString();
	    String barcode = m_barcode.getText().toString();
	    String productionName = m_productionName.getText().toString();
	    String local = m_local.getText().toString();
	    
	    String query =  "";
	    
	    query += "SELECT * FROM Goods";
	    
	    if (!customer.equals("") || !customer2.equals("") || !barcode.equals("") || !productionName.equals("") || !local.equals("")){
	    	query += " WHERE";
	    	
	    	 boolean added = false; 
	 	    if(!customer.equals("")){
	 	    	query += " Bus_Code = '" + customer + "'";
	 	    	added = true;  
	 		}
	 		if(!customer2.equals("")){
	 			if(added)	query += " AND ";
	 			query += " Bus_Name = '" + customer2 + "'";
	 			added = true;
	 		}
	 		if(!barcode.equals("")){
	 			if(added)	query += " AND ";
	 			query += " BarCode = '" + barcode + "'";
	 			added = true;
	 		}
	 		if(!productionName.equals("")){
	 			if(added)	query += " AND ";
	 			query += " G_Name = '" + productionName + "'";
	 			added = true;
	 		}
	 		query += ";";
	    } else {
	    	query = "SELECT TOP 2000 * FROM Goods WHERE BarCode NOT IN(SELECT TOP 1 BarCode FROM Goods);";
	    }
	    
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					updateListView(results);
		            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();					
				}
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
	    if (resultCode == RESULT_OK) 
	    {
	        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
	        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
	        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
	        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
	        // The value of type indicates one of the symbols listed in Advanced Options below.
	        
	        
	        String barcode = data.getStringExtra(ZBarConstants.SCAN_RESULT);
			m_barcode.setText(barcode);
			fillGoodNameFromBarcode(barcode);
			
	    } else if(resultCode == RESULT_CANCELED) {
	        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
	    }
	}
	
	public void doBarcodeSearch() {

    	Intent intent = new Intent(ComparePriceActivity.this, ZBarScannerActivity.class);
    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
	}
	
	public void updateListView(JSONArray results) {
		
		String[] from = new String[] {"barcode", "gName", "purPri", "sellPri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
        
        if (!mfillMaps.isEmpty()) mfillMaps.clear();
        
        for (int i = 0; i < results.length(); i++) {
        	Log.w("test", "4");	
        	try {
            	JSONObject json = results.getJSONObject(i);
				String barcode = json.getString("BarCode");
				String gName = json.getString("G_Name");
				String purPri = json.getString("Pur_Pri");
				String sellPri = json.getString("Sell_Pri");
				/*
				String busCode = json.getString("Bus_Code");
				String busName = json.getString("Bus_Name");
				String taxYN = json.getString("Tax_YN");
				String stdSize = json.getString("Std_Size");
				String obtain = json.getString("Obtain");
				String purCost = json.getString("Pur_Cost");
				String profitRate = json.getString("Profit_Rate");
				String L_Code = json.getString("L_Code");
				String M_Code = json.getString("M_Code");
				String S_Code = json.getString("S_Code");
				String surtax = json.getString("Add_Tax");*/

				// prepare the list of all records
	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("barcode", barcode);
	            map.put("gName", gName);
	            map.put("purPri", purPri);
	            map.put("sellPri", sellPri);
	            /*map.put("busCode", busCode);
	            map.put("busName", busName);
	            map.put("taxYN", taxYN);
	            map.put("stdSize", stdSize);
	            map.put("obtain", obtain);
	            map.put("purCost", purCost);
	            map.put("profitRate", profitRate);
	            map.put("L_Code", L_Code);
	            map.put("M_Code", M_Code);
	            map.put("S_Code", S_Code);
	            map.put("surtax", surtax);	      */    
	            Log.w("test", "6");
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(ComparePriceActivity.this, mfillMaps, R.layout. activity_listview_compare_list, from, to);
        m_listPriceSearch.setAdapter(adapter);
    }
}
