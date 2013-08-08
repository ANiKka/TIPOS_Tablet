package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
import tipsystem.tips.models.*;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ComparePriceActivity extends Activity{

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private static final int BARCODE_MANAGER_REQUEST = 2;
	private static final int CUSTOMER_MANAGER_REQUEST = 3;
	
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	TextView m_customerCode;
	TextView m_customerName;
	TextView m_barcode;
	TextView m_productionName;
	Spinner m_local;	
	
	ListView m_listPriceSearch;
	CompareListAdapter m_adapter; 
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	private ProgressDialog dialog;

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
	        	doSearchInMarket();
		    }
        }
    };
	// loading bar
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_price);

		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        m_listPriceSearch = (ListView)findViewById(R.id.listviewPriceSearchList);
        m_listPriceSearch.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	sendDataFromList(position);
            }
        });
        m_listPriceSearch.setOnScrollListener(customScrollListener);

    	//String[] from = new String[] {"BarCode", "G_Name", "Pur_Pri", "Sell_Pri"};
        //int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        //m_adapter = new SimpleAdapter(this, mfillMaps, R.layout. activity_listview_compare_list, from, to);
        m_adapter = new CompareListAdapter(this,  R.layout. activity_listview_compare_list, mfillMaps);
                
        m_listPriceSearch.setAdapter(m_adapter);
        
        m_customerCode = (TextView)findViewById(R.id.editTextCustomer);
        m_customerName = (TextView)findViewById(R.id.editTextCustomer2);
		m_barcode = (TextView)findViewById(R.id.editTextBarcord);
		m_productionName = (TextView)findViewById(R.id.editTextProductionName);
		m_local = (Spinner)findViewById(R.id.spinnerLocationType);
		
		Button searchButton = (Button) findViewById(R.id.buttonPriceSearch);
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	deleteListViewAll();
	        	doSearchInMarket();
	        	closeKeyboard();
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
						doQueryWithBarcode(barcode);   	
			    }
			}
		});

		// 거래처 코드 입력 후 포커스 딴 곳을 옮길 경우
		m_customerCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String customerCode = null; 
			    	customerCode = m_customerCode.getText().toString();
			    	if(!customerCode.equals("")) // 입력한 customerCode가 값이 있을 경우만
			    		fillBusNameFromBusCode(customerCode);	    	
			    }
			}
		});	
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView4);
        textView.setTypeface(typeface);
        
        textView = (TextView) findViewById(R.id.textView5);
        textView.setTypeface(typeface);      
        
        fetchLocation();
	}

	private void closeKeyboard() {
    	getWindow().getCurrentFocus().clearFocus();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);	    
	}

	public void deleteListViewAll() {
		if (mfillMaps.isEmpty()) return;
        
		mfillMaps.removeAll(mfillMaps);
		m_adapter.notifyDataSetChanged();
	}
	
	private void sendDataFromList(int position){

    	String customer = m_customerCode.getText().toString();
	    String local = m_local.getSelectedItem().toString();
	    
		Intent intent = new Intent(this, ComparePriceDetailActivity.class);
		intent.putExtra("Shop_Area", local);
		intent.putExtra("OFFICE_CODE", customer);
		intent.putExtra("fillMaps", mfillMaps.get(position));
    	startActivity(intent);
	}

	public void fetchLocation(){

	    String query =  "";
		query = "SELECT Shop_Area From Office_User;";
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				Toast.makeText(getApplicationContext(), results.length()+"레코드발견", Toast.LENGTH_SHORT).show();
				if (results.length()>0) {

	    			try {
						JSONObject json = results.getJSONObject(0);
						String Shop_Area = json.getString("Shop_Area");
						int position =0;
						if (Shop_Area.equals("서울")) position =1;
						if (Shop_Area.equals("경기")) position =2;
						if (Shop_Area.equals("부산")) position =3;
						if (Shop_Area.equals("대구")) position =4;
						if (Shop_Area.equals("인천")) position =5;
						if (Shop_Area.equals("대전")) position =6;
						if (Shop_Area.equals("광주")) position =7;
						if (Shop_Area.equals("강원")) position =8;
						if (Shop_Area.equals("충북")) position =9;
						if (Shop_Area.equals("충남")) position =10;
						if (Shop_Area.equals("전북")) position =11;
						if (Shop_Area.equals("전남")) position =12;
						if (Shop_Area.equals("경북")) position =13;
						if (Shop_Area.equals("경남")) position =14;
						if (Shop_Area.equals("제주")) position =15;
						m_local.setSelection(position);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void doSearchInMarket(){

    	String index = String.valueOf(mfillMaps.size());    	
    	String customer = m_customerCode.getText().toString();
	    String barcode = m_barcode.getText().toString();
    	String customerName = m_customerName.getText().toString();
	    String productionName = m_productionName.getText().toString();
	    String local = m_local.getSelectedItem().toString();
	    
	    if (customer.equals("") && barcode.equals("") && productionName.equals("") && customerName.equals("") && local.equals("전체") ){

			Toast.makeText(getApplicationContext(), "한가지이상 입력하여야 합니다", Toast.LENGTH_SHORT).show();
			return;
	    }
	    
	    String query =  "";	   
	    
		query = "SELECT TOP 25 BarCode, G_Name, Pur_Pri, Sell_Pri FROM Goods "
				+ " WHERE Goods_Use='1' AND Pur_Use='1' AND "
				+ " BarCode like '%" + barcode + "%' AND "
				+ " G_Name like '%" + productionName + "%' AND "
				+ " Bus_Code like '%" + customer + "%' AND "
				+ " Bus_Name like '%" + customerName + "%' AND "
				+ " BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods WHERE Goods_Use='1' AND Pur_Use='1' "
				+ " AND Bus_Code like '%" + customer + "%' "
				+ " AND BarCode like '%" + barcode + "%' "
				+ " AND G_Name like '%" + productionName + "%' "
				+ " AND Bus_Name like '%" + customerName + "%' "
				+ " Order By BarCode ASC) Order By BarCode ASC;";
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				Toast.makeText(getApplicationContext(), results.length()+"레코드발견", Toast.LENGTH_SHORT).show();
				if (results.length()>0) doSearchInTail(results);
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
    public void doSearchInTail(JSONArray results){

    	String customer = m_customerCode.getText().toString();
	    String local = m_local.getSelectedItem().toString();
    	String query = "";

    	for(int i = 0; i < results.length(); i++) {
    		String BarCode ="", G_Name ="", Pur_Pri ="", Sell_Pri ="";
    		try {
    			BarCode = results.getJSONObject(i).getString("BarCode");
    			G_Name = results.getJSONObject(i).getString("G_Name");
    			Pur_Pri = results.getJSONObject(i).getString("Pur_Pri");
    			Sell_Pri = results.getJSONObject(i).getString("Sell_Pri");
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		
    		if (!local.equals("전체")) {
    			query += "SELECT isNull(AVG(B.Pur_Pri), 0) AS Pur_Pri, isNull(AVG(B.Sell_Pri), 0) AS Sell_Pri, B.BarCode, A.Shop_Area, '"
    					+ G_Name +"' G_Name, '" + Pur_Pri+"' o_Pur_Pri, '" + Sell_Pri+"' o_Sell_Pri "
        				+ " FROM V_OFFICE_USER A, GOODS B"
        				+ " WHERE A.STO_CD=B.OFFICE_CODE ";
    			
    			if (!customer.equals("")) query += " AND A.STO_CD<>'"+customer+"'" ;
    			
    			query 		+= " AND B.BarCode = '" + BarCode + "' and B.Pur_Pri>0 AND B.Sell_Pri>0 "
	    					+ " AND A.Shop_Area = '" + local +"'"
    					+ " group by B.BarCode, A.Shop_Area ";
    		}
    		else {
    			query += "SELECT isNull(AVG(B.Pur_Pri), 0) AS Pur_Pri, isNull(AVG(B.Sell_Pri), 0) AS Sell_Pri, B.BarCode, '"
    					+ G_Name +"' G_Name, '" + Pur_Pri+"' o_Pur_Pri, '" + Sell_Pri+"' o_Sell_Pri "
        				+ " FROM V_OFFICE_USER A, GOODS B"
        				+ " WHERE A.STO_CD=B.OFFICE_CODE ";
    			if (!customer.equals("")) query += " AND A.STO_CD<>'"+customer+"'" ;
    			
    			query 		+= " AND B.BarCode = '" + BarCode + "' and B.Pur_Pri>0 AND B.Sell_Pri>0 "
        				+ " group by B.BarCode ";
    		}
    		
    		if(i<results.length()-1)	query += " union all ";
    		else 						query += ";";
    	}
    	
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				if (results.length()>0) comparePrice(results);
				Toast.makeText(getApplicationContext(), "비교완료", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
    }
    
    // 가격비교
    public void comparePrice(JSONArray results){

    	Log.w("MSSQL", "comparePrice IN(2):" + results.toString());

    	for(int i=0; i<results.length();i++) {

    		try {
    			JSONObject json2 = results.getJSONObject(i);
    			
    			double Pur_Pri1 = json2.getDouble("o_Pur_Pri");
    			double Pur_Pri2 = json2.getDouble("Pur_Pri");
    			double Sell_Pri1 = json2.getDouble("o_Sell_Pri");
    			double Sell_Pri2 = json2.getDouble("Sell_Pri");
    			
				HashMap<String, String> map2 = JsonHelper.toStringHashMap(json2);

	            String Pur_Pri_inc = "";	
	            if(Pur_Pri1>Pur_Pri2) Pur_Pri_inc = "+";
	            else if(Pur_Pri1==Pur_Pri2) Pur_Pri_inc = "=";
	            else Pur_Pri_inc = "-";

	            map2.put("Pur_Pri_dif", String.format("%.2f", Pur_Pri2));
	            map2.put("Pur_Pri_inc", Pur_Pri_inc);
	            
	            String Sell_Pri_inc = "";
	            if(Sell_Pri1>Sell_Pri2) Sell_Pri_inc = "+";
	            else if(Sell_Pri1==Sell_Pri2) Sell_Pri_inc = "=";
	            else Sell_Pri_inc = "-";
	            
	            map2.put("Sell_Pri_dif", String.format("%.0f", Sell_Pri2));
	            map2.put("Sell_Pri_inc", Sell_Pri_inc);
	            
    	        mfillMaps.add(map2);         
    	        
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
        m_adapter.notifyDataSetChanged();        
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			// 카메라 스캔을 통한 바코드 검색
			case ZBAR_SCANNER_REQUEST :
				if (resultCode == RESULT_OK) {
			        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
			        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
			        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
			        // The value of type indicates one of the symbols listed in Advanced Options below.
			       
			        String barcode = data.getStringExtra(ZBarConstants.SCAN_RESULT);
			        m_barcode.setText(barcode);
					doQueryWithBarcode(barcode);
					
			    } else if(resultCode == RESULT_CANCELED) {
			        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
			    }
				break;
			// 목록 검색을 통한 바코드 검색				
			case BARCODE_MANAGER_REQUEST :
				if(resultCode == RESULT_OK && data != null) {
					HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");        	
					m_barcode.setText(hashMap.get("BarCode"));
		        	doQueryWithBarcode(hashMap.get("BarCode"));
		        }
				break;
			case CUSTOMER_MANAGER_REQUEST :
				if(resultCode == RESULT_OK && data != null) {
					HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");     	
					m_customerCode.setText(hashMap.get("Office_Code"));
					m_customerName.setText(hashMap.get("Office_Name"));
		        }
				break;
			}
	}
	
	public void onCustomerSearch(View view)
	{
		String customer = m_customerCode.getText().toString();
		Intent intent = new Intent(this, ManageCustomerListActivity.class);
		intent.putExtra("customer", customer);
    	startActivityForResult(intent, CUSTOMER_MANAGER_REQUEST);
	}
	
	public void onBarcodeSearch(View view)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String list_value = pref.getString("prefSearchMethod", "");
        if (list_value.equals("camera")) {
			startCameraSearch();
        }
        else if (list_value.equals("list")) {
        	startProductList();
        }
        else {
        	// 바코드 검색 버튼 클릭시 나오는 목록 셋팅
    		final String[] option = new String[] { "목록", "카메라"};
    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Select Option");
    		
    		// 목록 선택시 이벤트 처리
    		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {

    				if(which == 0){ // 목록으로 조회할 경우
    					startProductList();
    				} else { // 스캔할 경우	
    					startCameraSearch();
    				}
    			}
    		}); 
    		builder.show();
        }
	}
	
	private void startProductList() {
		String barcode = m_barcode.getText().toString();
		Intent intent = new Intent(this, ManageProductListActivity.class);
		intent.putExtra("barcode", barcode);
    	startActivityForResult(intent, BARCODE_MANAGER_REQUEST);
	}
	
	private void startCameraSearch() {
		Intent intent = new Intent(this, ZBarScannerActivity.class);
    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
	} 

	// MSSQL
	// SQL QUERY 실행
	public void doQueryWithBarcode (String barcode) {
		
		String query = "";
		query = "SELECT G_Name FROM Goods WHERE Barcode = '" + barcode + "';";
	
		if (barcode.equals("")) return;
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();

	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				
				if (results.length() > 0) {
					try {						
						m_productionName.setText(results.getJSONObject(0).getString("G_Name"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}
	

	private void fillBusNameFromBusCode(String customerCode) {		
 		
		String query = "";		
		query = "SELECT Office_Name From Office_Manage WHERE Office_Code = '" + customerCode + "';";
		
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
					try {
						JSONObject json = results.getJSONObject(0);
						String bus_name = json.getString("Office_Name");
						m_customerName.setText(bus_name);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
		            m_customerName.setText("");
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	
			ActionBar actionbar = getActionBar();	
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(true);
			actionbar.setDisplayShowCustomEnabled(true);
			actionbar.setTitle("가격비교");			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
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

		case R.id.action_settings: 
			startActivity(new Intent(this, TIPSPreferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
