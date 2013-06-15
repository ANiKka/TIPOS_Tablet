package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PurchaseRegistActivity extends Activity implements OnItemClickListener, OnClickListener{

	ListView m_listReadyToSend;
	SimpleAdapter m_adapter;
	
	TextView m_purchaseDate;
	CheckBox m_immediatePayment;
	TextView m_customerCode;
	TextView m_customerName;
	TextView m_barcode;
	TextView m_productName;
	TextView m_purchasePrice;
	TextView m_salePrice;
	TextView m_amount;
	TextView m_profitRatio;
	
	private OnClickListener m_click_save_listener = new OnClickListener() {
        public void onClick(View v) { 
        	
        	String code = m_customerCode.getText().toString();
    	    String name = m_customerName.getText().toString();
    	    
    	    String purchaseDate = m_purchaseDate.getText().toString();
    		String immediatePayment = "" + m_immediatePayment.isChecked();
    		String barcode = m_barcode.getText().toString();
    		String productName = m_productName.getText().toString();
    		String purchasePrice = m_purchasePrice.getText().toString();
    		String salePrice = m_salePrice.getText().toString();
    		String amount = m_amount.getText().toString();
    		String profitRatio = m_profitRatio.getText().toString();
    		 
    	    if(code == "" || name == "" || purchaseDate == "" || barcode == "" || productName == "" || purchasePrice == ""
    	    		|| salePrice == "" || amount == "" || profitRatio == "")
    	    {
    	    	Toast.makeText(getApplicationContext(), "값을 모두 입력해주세요.", 0).show();
    	    	return;
    	    }
    	    
            new MyAsyncTask ().execute("1", code, name, purchaseDate, immediatePayment, barcode, productName, purchasePrice
            		, salePrice, amount, profitRatio);
        }
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_regist);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listReadyToSend= (ListView)findViewById(R.id.listviewReadyToSendList);
		
		m_purchaseDate = (TextView)findViewById(R.id.editTextPurchaseDate);
		m_immediatePayment = (CheckBox)findViewById(R.id.checkBoxImmediatePayment);
		m_customerCode = (TextView)findViewById(R.id.editTextCustomerCode);
		m_customerName = (TextView)findViewById(R.id.editTextCustomerName);
		m_barcode = (TextView)findViewById(R.id.editTextBarcode);
		m_productName = (TextView)findViewById(R.id.editTextProductName);
		m_purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		m_salePrice = (TextView)findViewById(R.id.editTextSalePrice);
		m_amount = (TextView)findViewById(R.id.editTextAmount);
		m_profitRatio = (TextView)findViewById(R.id.editTextProfitRatio);
		
		Button saveButton = (Button)findViewById(R.id.buttonSave);
		
		saveButton.setOnClickListener(m_click_save_listener);
		
		
		 // create the grid item mapping
//      String[] from = new String[] {"코드", "거래처","총매입가", "수량"};
//      int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4};
//
//      // prepare the list of all records
//      List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
//      for(int i = 0; i < 10; i++){
//          HashMap<String, String> map = new HashMap<String, String>();
//          map.put("코드", "0000" + i);
//          map.put("거래처", "거래처_" + i);
//          map.put("총매입가", "0000" + i);
//          map.put("수량", i + "000");
//
//          fillMaps.add(map);
//      }
//
//      // fill in the grid_item layout
//      m_adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_readytosend_list, 
//      		from, to);
//      
//      m_listReadyToSend.setAdapter(m_adapter);
      m_listReadyToSend.setOnItemClickListener(this);
      
      Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
      TextView textView = (TextView) findViewById(R.id.textView2);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView3);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView4);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView5);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView5);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView6);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView7);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView8);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView9);
      textView.setTypeface(typeface);
      
      textView = (TextView) findViewById(R.id.textView10);
      textView.setTypeface(typeface);
      
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
		actionbar.setTitle("매입등록");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_regist, menu);
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
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		// TODO Auto-generated method stub
		
		CheckBox checkbox = ((CheckBox)arg1.findViewById(R.id.item5));
		if(checkbox != null) 
		{ 
			
		//	Toast.makeText(this, "Item Click.", Toast.LENGTH_SHORT).show();
			
			checkbox.setChecked(!checkbox.isChecked());
			// 베리 킥 포인트 요거 꼭해줘야 checkbox 에서 바로 바로 적용됩니다.
			m_adapter.notifyDataSetChanged();
			
			
			Intent intent = new Intent(this, PurchaseDetailActivity.class);
	    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
	    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
	    	//String message = editText.getText().toString();
	    	//intent.putExtra(EXTRA_MESSAGE, message);
	    	startActivity(intent);
	    	
		}
			
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onClick.", Toast.LENGTH_SHORT).show();
	}
	
	
	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        protected String doInBackground(String... urls) {
        	Log.i("Android"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset =null;
        	String type = urls[0];
        	int i = 0;
        	
        	try {
        		
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("Connection","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
        	    Log.i("Connection","MSSQL open");
        	    Statement stmt = conn.createStatement();
        	    Log.i("Connection","Button Click type is " + type);
        	    
        		
        	    String customerCode = urls[1];
        	    String customerName = urls[2];
        	    String purchaseDate = urls[3];
        	    
        	    String immediatePayment = urls[4];
        	    String barcode = urls[5];
        	    String productName = urls[6];
        	    String purchasePrice = urls[7];
        	    String salePrice = urls[8];
        	    String amount = urls[9];
        	    String profitRatio = urls[10];
        	            	    
        	    String query = "";
        	    

        	    //query = "insert into InD_201209(In_Date, BarCode, Office_Code, Office_Name, Pur_Pri, Sell_Pri, In_Count, Add_Tax) values('" 
        	    //+ purchaseDate + "', '" + barcode + "', '"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "', '" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "'); select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where BarCode = '" + barcode + "'and G_Name = '" + productName + "'and Bus_Name = '" + customerName + "';";

        	    
                Log.e("HTTPJSON","query: " + query );
    	    	reset = stmt.executeQuery(query);    
        	    while(reset.next()){
					
					JSONObject Obj = new JSONObject();
				    // original part looks fine:
				    Obj.put("barcode",reset.getString(1).trim());
				    Obj.put("productName",reset.getString(2).trim());
				    Obj.put("showPurchasePrice",reset.getString(3).trim());
				    Obj.put("showSellPrice",reset.getString(4).trim());
				    CommArray.add(Obj);
				}
        	    
        	    conn.close();
        	
        	 } catch (Exception e)
        	 {
        	    Log.w("Error connection","" + e.getMessage());		   
        	 }
        	 
        	 // onProgressUpdate에서 0이라는 값을 받아서 처리
        	 publishProgress(0);
        	 return type;        	 
        }

        protected void onProgressUpdate(Integer[] values) {
            Log.e("HTTPJSON", "onProgressUpdate" );
        }

        protected void onPostExecute(String result) {
        	super.onPostExecute(result);
        	
			String[] from = new String[] {"barcode", "productName", "showPurchasePrice", "showSellPrice"};
	        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	 	        		
        	Iterator<JSONObject> iterator = CommArray.iterator();
    		while (iterator.hasNext()) {
            	JSONObject json = iterator.next();
            	
            	try {
    				String barcode = json.getString("barcode");
    				String productName = json.getString("productName");
    				String showPurchasePrice = json.getString("showPurchasePrice");
    				String showSellPrice = json.getString("showSellPrice");
    				
    				// prepare the list of all records
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("barcode", barcode);
		            map.put("productName", productName);
		            map.put("showPurchasePrice", showPurchasePrice);
		            map.put("showSellPrice", showSellPrice);
		            fillMaps.add(map);
    		 
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}

	        // fill in the grid_item layout
	        //SimpleAdapter adapter = new SimpleAdapter(ManageProductActivity.this, fillMaps, R.layout. activity_listview_product_list, from, to);
	        //m_listProduct.setAdapter(adapter);
	        
            Toast.makeText(getApplicationContext(), "조회 완료", 0).show();
        }
    };
}
