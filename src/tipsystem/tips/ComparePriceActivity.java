package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.tips.ManageProductActivity.ProductList;
import tipsystem.tips.ManageProductActivity.ProductListAdapter;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
	ArrayList<ProductList> productArray = new ArrayList<ProductList>();
	int index = 0;
	int size = 30;
	int firstPosition = 0; 
	
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
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
	        	doSearchInMarket();
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
		
		ArrayList<String> sendArr = new ArrayList<String>();
		sendArr.add(productArray.get(position).Barcode);
		sendArr.add(productArray.get(position).G_Name);
		sendArr.add(productArray.get(position).Pur_Pri);
		sendArr.add(productArray.get(position).Sell_Pri);
		
		Intent intent = new Intent(this, ComparePriceDetailActivity.class);
		intent.putExtra("fillMaps", sendArr);
    	startActivity(intent);
	}

	public void doSearchInMarket(){

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
	    	query = "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods) A JOIN (SELECT TOP " + size + " * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods)) B ON A.BarCode = B.BarCode;";
	    }
	    
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				settingArray(results);
				//doSearchInTail();
				//Toast.makeText(getApplicationContext(), "전송완료.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	// 리스트뷰 띄우기
    public void settingArray(JSONArray results){
		
    	//productArray.clear();
		ProductList pl;
		for(int i = 0; i < size-index; i++){
			JSONObject json;
			try {
				json = results.getJSONObject(i);
				pl = new ProductList(json.getString("BarCode"),
									json.getString("G_Name"),
									json.getString("Pur_Pri"),
									json.getString("Sell_Pri"),
									json.getString("Bus_Code"),
									json.getString("Bus_Name"),
									json.getString("Tax_YN"),
									json.getString("Std_Size"),
									json.getString("Obtain"),
									json.getString("Pur_Cost"),
									json.getString("Profit_Rate"),
									json.getString("L_Code"),
									json.getString("M_Code"),
									json.getString("S_Code"),
									json.getString("L_Name"),
									json.getString("M_Name"),
									json.getString("S_Name"),
									json.getString("VAT_CHK"),
									json.getString("G_grade"),
									"0",
									"0");
					productArray.add(pl);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//compareQuery();
		showListView();
    }
    
    // 리스트뷰 보이기
    public void showListView (){
    	ProductListAdapter ProductList = new ProductListAdapter(this, R.layout.activity_listview_compare_list, productArray);
		ListView m_listPriceSearch = (ListView)findViewById(R.id.listviewCustomerList);
		
		firstPosition = m_listPriceSearch.getFirstVisiblePosition();
		m_listPriceSearch.setAdapter(ProductList);
		m_listPriceSearch.setSelection(firstPosition);
	}

    public void doSearchInTail(){
    	
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
    	Log.w("MSSQL", "comparePrice() IN");
    	String query1 = "";
    	for(int i = index; i < size-index; i++) {
    		    		
    		query1 += "SELECT isNull(AVG(Pur_Pri), 0) AS Pur_Pri , isNull(AVG(Sell_Pri), 0) AS Sell_Pri FROM Goods WHERE BarCode = '" + productArray.get(i).Barcode + "' ";
    		if(i<size-index-1)
    			query1 += "union all ";
    		else
    			query1 += ";";
   			
    	}	
    	
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				comparePrice(results);
				Toast.makeText(getApplicationContext(), "전송완료.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query1);
    }
    
    // 가격비교
    public void comparePrice(JSONArray results){
    	
    	Log.w("MSSQL", "comparePrice IN");

    	
    	
    	HashMap<String, String> map = new HashMap<String, String>();
    	for(int i = index; i < size - index; i++){
    		JSONObject json;
    		try{
	    		json = results.getJSONObject(i);
	    		map.put("Pur_Pri", json.getString("Pur_Pri"));
	    		map.put("Sell_Pri", json.getString("Sell_Pri"));
	    		mfillMaps.add(map);
    		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	}
    	
    	comparePrice2();
    	
    	
    }
    
    public void comparePrice2(){
    	float Pur_Pri1;
    	float Pur_Pri2;
    	float Sell_Pri1;
    	float Sell_Pri2;
    	
    	Log.w("MSSQL", "pur_pri : " + mfillMaps.get(0).get("Pur_Pri"));
    	Log.w("MSSQL", "pur_pri : " + productArray.get(0).Pur_Pri);

    	//for(int i = index; i < size - index; i++){
    	ProductList pl = new ProductList("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","1","0");
		Pur_Pri1 = Float.parseFloat(productArray.get(29).Pur_Pri);
		Pur_Pri2 = Float.parseFloat(mfillMaps.get(0).get("Pur_Pri"));
		Sell_Pri1 = Float.parseFloat(productArray.get(29).Sell_Pri);
		Sell_Pri2 = Float.parseFloat(mfillMaps.get(0).get("Sell_Pri"));
		
		Log.w("MSSQL", "compare_pur : " + productArray.get(0).Compare_Pur);
		if (Pur_Pri1 > Pur_Pri2){
			Log.w("MSSQL", "compare_pur change");
			productArray.set(0, pl);
			Log.w("MSSQL", "compare_pur : " + productArray.get(0).Compare_Pur);
		}else if (Pur_Pri1 == Pur_Pri2){
			productArray.set(0, null).Compare_Pur = "같" +
					"음";
		}
		if (Sell_Pri1 > Sell_Pri2){
			productArray.set(0, null).Compare_Sell = "1";
		}else if (Sell_Pri1 == Sell_Pri2){
			productArray.set(0, null).Compare_Pur = "같음";
		}
    	
		
    	showListView();
    	
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

	class ProductList {
		ProductList(String aBarcode, String aG_Name, String aPur_Pri, String aSell_Pri, String aBusCode, String aBusName,
					String ataxYN, String astdSize, String aobtain, String apurCost, String aprofitRate, String aL_Code,
					String aM_Code, String aS_Code, String aL_Name, String aM_Name, String aS_Name, String asurtax,
					String ag_grade, String acompare_Pur, String acompare_Sell){
			Barcode = aBarcode;
			G_Name = aG_Name;
			Pur_Pri = aPur_Pri;
			Sell_Pri = aSell_Pri;
			Bus_Code = aBusCode;
			Bus_Name= aBusName;
			taxYN = ataxYN;
			stdSize = astdSize;
			Obtain = aobtain;
			purCost = apurCost;
			profitRate = aprofitRate;
			L_Code = aL_Code;
			M_Code = aM_Code;
			S_Code = aS_Code;
			L_Name = aL_Name;
			M_Name = aM_Name;
			S_Name = aS_Name;			
			surtax = asurtax;
			G_grade = ag_grade;
			Compare_Pur = acompare_Pur;
			Compare_Sell = acompare_Sell;
			}	
		String Barcode;
		String G_Name;
		String Pur_Pri;
		String Sell_Pri;
		String Bus_Code;
		String Bus_Name;
		String taxYN;
		String stdSize;
		String Obtain;
		String purCost;
		String profitRate;
		String L_Code;
		String M_Code;
		String S_Code;
		String L_Name;
		String M_Name;
		String S_Name;
		String surtax;		
		String G_grade;
		String Compare_Pur;
		String Compare_Sell;
	}
	
	class ProductListAdapter extends BaseAdapter 
	{

		Context ctx;
		LayoutInflater Inflater;
		ArrayList<ProductList> arr_Goods;
		int itemLayout;
		
		public ProductListAdapter(Context actx, int aitemLayout, ArrayList<ProductList> aarr_Goods)
		{
			ctx = actx;
			Inflater = (LayoutInflater)actx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arr_Goods = aarr_Goods;
			itemLayout = aitemLayout;
		}

		@Override
		public int getCount() {
			return arr_Goods.size();
		}
		@Override
		public String getItem(int position) {
			return arr_Goods.get(position).Barcode;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			
			if (convertView == null) {
				convertView = Inflater.inflate(itemLayout, parent, false);
			} 
			
			TextView barcode = (TextView)convertView.findViewById(R.id.item1);
			TextView g_name = (TextView)convertView.findViewById(R.id.item2);
			TextView sell_pri = (TextView)convertView.findViewById(R.id.item3);
			TextView pur_pri = (TextView)convertView.findViewById(R.id.item4);
			
			
			barcode.setText(arr_Goods.get(position).Barcode);
			g_name.setText(arr_Goods.get(position).G_Name);
			sell_pri.setText(arr_Goods.get(position).Pur_Pri + " : " + arr_Goods.get(position).Compare_Pur);
			pur_pri.setText(arr_Goods.get(position).Sell_Pri + " : " + arr_Goods.get(position).Compare_Sell);

			if(position == size-3){
				index = size;
				size = size * 2;
				doSearchInMarket();
			}
			return convertView;
		}
	}

}
