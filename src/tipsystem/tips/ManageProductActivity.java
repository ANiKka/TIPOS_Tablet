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
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;

public class ManageProductActivity extends Activity{

	TextView m_textBarcode;
	TextView m_textProductName;
	TextView m_textCustomerCode;
	TextView m_textCustomerName;
	TextView m_textCustomerClassification1;
	TextView m_textCustomerClassification2;
	TextView m_textCustomerClassification3;
	Spinner m_spinTaxation;
	CheckBox m_checkSurtax;
	Spinner m_spinGroup;
	TextView m_textStandard;
	TextView m_textAcquire;
	TextView m_textPurchasePrice;
	TextView m_textPurchasePriceOriginal;
	TextView m_textSalesPrice;
	TextView m_textDifferentRatio;
	ListView m_listProduct;
    
	private ProgressDialog dialog; 
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	int m_type;
	
	private OnClickListener m_click_modify_listener = new OnClickListener() {
        public void onClick(View v) { 
        	String barcode = m_textBarcode.getText().toString();
    	    String productName = m_textProductName.getText().toString();
    	    String customerCode = m_textCustomerCode.getText().toString();
    	    String customerName = m_textCustomerName.getText().toString();
    	    String customerClass1 = m_textCustomerClassification1.getText().toString();
    	    String customerClass2 = m_textCustomerClassification2.getText().toString();
    	    String customerClass3 = m_textCustomerClassification3.getText().toString();
    	    String taxation = m_spinTaxation.getSelectedItem().toString();
    		String group = m_spinGroup.getSelectedItem().toString();
    		String standard = m_textStandard.getText().toString();
    		String acquire = m_textAcquire.getText().toString();
    		String purchasePrice = m_textPurchasePrice.getText().toString();
    		String purchasePriceOriginal = m_textPurchasePriceOriginal.getText().toString();
    		String salesPrice = m_textSalesPrice.getText().toString();
    		String ratio = m_textDifferentRatio.getText().toString();
    		
	    	if(barcode == "" || productName == "" || customerName == "" || customerCode == "" || customerClass1 == "" || customerClass2 == "" || customerClass3 == "" || taxation == "" || group == "" || standard == "" || acquire == "" || purchasePrice == "" || purchasePriceOriginal == "" || salesPrice == "" || ratio == ""){
	    		Toast.makeText(getApplicationContext(), "값을 모두 입력해주세요.", 0).show();
	    		return;
	    	}
            //new MyAsyncTask ().execute("3", barcode, productName, customerName, customerCode, customerClass1, customerClass2, customerClass3, taxation, group, standard, acquire, purchasePrice, purchasePriceOriginal, salesPrice, ratio);

        }
	};
	
	private OnClickListener m_click_renew_listener = new OnClickListener() {
        public void onClick(View v) { 
        	m_textBarcode.setText("");
        	m_textProductName.setText("");
        	m_textCustomerCode.setText("");
        	m_textCustomerName.setText("");
        	m_textCustomerClassification1.setText("");
    	    m_textCustomerClassification2.setText("");
    	    m_textCustomerClassification3.setText("");
    	    m_spinTaxation.setSelection(0);
    		m_spinGroup.setSelection(0);
    		m_textStandard.setText("");
    		m_textAcquire.setText("");
    		m_textPurchasePrice.setText("");
    		m_textPurchasePriceOriginal.setText("");
    		m_textSalesPrice.setText("");
    		m_textDifferentRatio.setText("");
        }
	};
	
	private OnClickListener m_click_search_listener = new OnClickListener() {
        public void onClick(View v) { 
        	dialog = new ProgressDialog(ManageProductActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
            
        	String barcode = m_textBarcode.getText().toString();
    	    String productName = m_textProductName.getText().toString();
    	    String customerName = m_textCustomerName.getText().toString();
            new MyAsyncTask ().execute("1", barcode, productName, customerName);
        }
	};
	
	
	private OnClickListener m_click_regist_listener = new OnClickListener() {
        public void onClick(View v) { 
        	
        	dialog = new ProgressDialog(ManageProductActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
            
        	String barcode = m_textBarcode.getText().toString();
    	    String productName = m_textProductName.getText().toString();
    	    String customerCode = m_textCustomerCode.getText().toString();
    	    String customerName = m_textCustomerName.getText().toString();
    	    String customerClass1 = m_textCustomerClassification1.getText().toString();
    	    String customerClass2 = m_textCustomerClassification2.getText().toString();
    	    String customerClass3 = m_textCustomerClassification3.getText().toString();
    	    String taxation = m_spinTaxation.getSelectedItem().toString();
    		String group = m_spinGroup.getSelectedItem().toString();
    		String standard = m_textStandard.getText().toString();
    		String acquire = m_textAcquire.getText().toString();
    		String purchasePrice = m_textPurchasePrice.getText().toString();
    		String purchasePriceOriginal = m_textPurchasePriceOriginal.getText().toString();
    		String salesPrice = m_textSalesPrice.getText().toString();
    		String ratio = m_textDifferentRatio.getText().toString();
    		String surtax = null;
    		if(m_checkSurtax.isChecked())
    			surtax = "1";
    		else
    			surtax = "0";
    		 
    		
	    	if(barcode == "" || productName == "" || customerName == "" || customerCode == "" || customerClass1 == "" || customerClass2 == "" || customerClass3 == "" || taxation == "" || group == "" || standard == "" || acquire == "" || purchasePrice == "" || purchasePriceOriginal == "" || salesPrice == "" || ratio == ""){
	    		Toast.makeText(getApplicationContext(), "값을 모두 입력해주세요.", 0).show();
	    		return;
	    	}
            new MyAsyncTask ().execute("2", barcode, productName, customerName, customerCode, customerClass1, customerClass2, customerClass3, taxation, group, standard, acquire, purchasePrice, purchasePriceOriginal, salesPrice, ratio, surtax);

        }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_product);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_textBarcode = (TextView)findViewById(R.id.editTextBarcode);
		m_textProductName = (TextView)findViewById(R.id.editTextProductName);
		m_textCustomerCode = (TextView)findViewById(R.id.editTextCustomerCode);
		m_textCustomerName = (TextView)findViewById(R.id.editTextCustomerName);
		m_textCustomerClassification1 = (TextView)findViewById(R.id.editTextCustomerClassification1);
		m_textCustomerClassification2 = (TextView)findViewById(R.id.editTextCustomerClassification2);
		m_textCustomerClassification3 = (TextView)findViewById(R.id.editTextCustomerClassification3);
		m_spinTaxation = (Spinner)findViewById(R.id.spinnerTaxationType);
		m_checkSurtax = (CheckBox)findViewById(R.id.checkBoxSurtax);
		m_spinGroup = (Spinner)findViewById(R.id.spinnerGroupType);
		m_textStandard = (TextView)findViewById(R.id.editTextStandard);
		m_textAcquire = (TextView)findViewById(R.id.editTextAcquire);
		m_textPurchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		m_textPurchasePriceOriginal = (TextView)findViewById(R.id.editTextPurchasePriceOriginal);
		m_textSalesPrice = (TextView)findViewById(R.id.editTextSalesPrice);
		m_textDifferentRatio = (TextView)findViewById(R.id.editTextDifferentRatio);
		m_listProduct = (ListView)findViewById(R.id.listviewProductList);
		m_listProduct.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                fillCustomerFormFromList(position);
            }
        });
		
		Button searchButton = (Button) findViewById(R.id.buttonProductSearch);
		Button registButton = (Button) findViewById(R.id.buttonProductRegist);
		Button renewButton = (Button) findViewById(R.id.buttonProductRenew);
		Button modifyButton = (Button) findViewById(R.id.buttonProductModify);
		

	    searchButton.setOnClickListener(m_click_search_listener);
	    registButton.setOnClickListener(m_click_regist_listener);
		renewButton.setOnClickListener(m_click_renew_listener);
	    modifyButton.setOnClickListener(m_click_modify_listener);
		//m_spinTaxation.setOnItemSelectedListener(this);
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
		actionbar.setTitle("상품등록");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_product, menu);
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

	private void fillCustomerFormFromList(int position) {

		String barcode = fillMaps.get(position).get("barcode");
		String gName = fillMaps.get(position).get("gName");
		String busCode = fillMaps.get(position).get("busCode");
		String busName = fillMaps.get(position).get("busName");
		String purPri = fillMaps.get(position).get("purPri");
		String sellPri = fillMaps.get(position).get("sellPri");
		String taxYN = fillMaps.get(position).get("taxYN");
		String stdSize = fillMaps.get(position).get("stdSize");
		String obtain = fillMaps.get(position).get("obtain");
		String purCost = fillMaps.get(position).get("purCost");
		String profitRate = fillMaps.get(position).get("profitRate");
		String L_Code = fillMaps.get(position).get("L_Code");
		String M_Code = fillMaps.get(position).get("M_Code");
		String S_Code = fillMaps.get(position).get("S_Code");
		String surtax = fillMaps.get(position).get("surtax");
		
		m_textBarcode.setText(barcode);
		m_textProductName.setText(gName);
		m_textCustomerCode.setText(busCode);
		m_textCustomerName.setText(busName);
		m_textStandard.setText(stdSize);
		m_textAcquire.setText(obtain);
		m_textPurchasePrice.setText(purPri); // 매입가
		m_textSalesPrice.setText(sellPri); //판매가
		m_textPurchasePriceOriginal.setText(purCost); //매입원가
		m_textDifferentRatio.setText(profitRate); // 이의율
		m_textCustomerClassification1.setText(L_Code);
		m_textCustomerClassification2.setText(M_Code);
		m_textCustomerClassification3.setText(S_Code); 
		if(taxYN.equals("0"))
			m_spinTaxation.setSelection(0);
		else
			m_spinTaxation.setSelection(1);
		if(surtax.equals("0")){
			m_checkSurtax.setChecked(false);
		}else{
			
			m_checkSurtax.setChecked(true);
		}
	}


	// MSSQL
	
		class MyAsyncTask extends AsyncTask<String, Integer, String>{

	        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
	        
	        protected String doInBackground(String... urls) {
	        	Log.i("Android"," MSSQL Connect Example.");
	        	Connection conn = null;
	        	ResultSet reset =null;
	        	String type = urls[0];
	        	int i = 0;
	        	
	        	if(type.equals("1"))
	        		m_type = 1;
	        	else
	        		m_type = 2;
	        	
	        	try {
	        		
	        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
	        	    Log.i("Connection","MSSQL driver load");

	        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
	        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
	        	    Log.i("Connection","MSSQL open");
	        	    Statement stmt = conn.createStatement();
	        	    Log.i("Connection","Button Click type is " + type);
	        	    String barcode = urls[1];
	        	    String productName = urls[2];
	        	    String customerName = urls[3];
	        	    
	        	    String customerCode = null;
	        	    String customerClass1 = null;
	        	    String customerClass2 = null;
	        	    String customerClass3 = null;
	        	    String taxation = null;
	        	    String group = null;
	        		String standard = null;
	        		String acquire = null;
	        		String purchasePrice = null;
	        		String purchasePriceOriginal = null;
	        		String salesPrice = null;
	        		String ratio = null;
	        		String surtax = null;
	        	    if(type == "2"){
		        	    customerCode = urls[4];
		        	    customerClass1 = urls[5];
		        	    customerClass2 = urls[6];
		        	    customerClass3 = urls[7];
		        	    taxation = urls[8];
		        		if(taxation.equals("면세"))
		        			taxation = "0";
		        		else
		        			taxation = "1";
		        		
		        	    group = urls[9];
		        		standard = urls[10];
		        		acquire = urls[11];
		        		purchasePrice = urls[12];
		        		purchasePriceOriginal = urls[13];
		        		salesPrice = urls[14];
		        		ratio = urls[15];
		        		surtax = urls[16];
	        	   }
	        	    String showPurchasePrice;
	        	    String showSellPrice;
	        	    String query = "";
	        	    
	        	  
	        	    if(type == "1"){
	            	
	        	    	if(!barcode.equals("")){
	        	    		query = "Barcode = '" + barcode + "' ";
	        	    		i++;
	        	    	}
	        	    	if(!productName.equals("")){
	        	    		if(i==1){
	        	    			query += "and G_Name = '" + productName + "' ";
	        	    			i++;
	        	    		} else {
	        	    			query = "G_Name = '" + productName + "' ";
	        	    			i++;
	        	    		}
	        	    	}
	        	    	if(!customerName.equals("")){
	        	    		if(i > 0){
	        	    			query += "and Bus_Name = '" + customerName + "'";
	        	    		} else {
	        	    			query = "Bus_Name = '" + customerName + "'";	
	        	    		}
	        	    	}
	        	    	
	        	    	if(barcode.equals("") && productName.equals("") && customerName.equals(""))
	        	    	{
	        	    		query = "select BarCode, G_Name, Pur_Pri, Sell_Pri,  Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Cost, Profit_Rate, L_Code, M_Code, S_Code, Add_Tax from Goods";
	        	    		
	        	    		//query = "select * from Goods";
	        	    	}
	        	    	else
	        	    	{
	        	    		query = "select BarCode, G_Name, Pur_Pri, Sell_Pri, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Cost, Profit_Rate, L_Code, M_Code, S_Code, Add_Tax from Goods where " + query;
	        	    		//query = "select * from Goods where " + query;
	        	    	}
	        	    } 
	        	    else if (type == "2") 
	        	    {
	        	    	query = "insert into Goods(BarCode, G_Name, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Pri, Pur_Cost, Sell_Pri, Profit_Rate, L_Code, M_Code, S_Code, Add_Tax) values('" + barcode + "', '" + productName + "', '"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "', '" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "', '" + customerClass1 + "', '" + customerClass2 + "', '" + customerClass3 + "', '" + surtax + "'); select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where BarCode = '" + barcode + "' and G_Name = '" + productName + "' and Bus_Name = '" + customerName + "';";
	        	    }
	        	    else if (type == "3")
	        	    {
	        	    	
	        	    	//query = "update Goods set BarCode, G_Name, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Pri, Pur_Cost, Sell_Pri, Profit_Rate) values('" + barcode + "', '" + productName + "', '"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "', '" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "'); select BarCode, G_Name, Pur_Pri, Sell_Pri from Goods where BarCode = '" + barcode + "'and G_Name = '" + productName + "'and Bus_Name = '" + customerName + "';";
	        	    }
	        	    
	                Log.e("HTTPJSON","query: " + query );
	    	    	reset = stmt.executeQuery(query);    
	        	    while(reset.next()){	        	    	
						JSONObject Obj = new JSONObject();
					    // original part looks fine:
					    Obj.put("barcode",reset.getString(1).trim());
					    Obj.put("gName",reset.getString(2).trim());
					    Obj.put("purPri",reset.getString(3).trim());
					    Obj.put("sellPri",reset.getString(4).trim());
					    Obj.put("busCode",reset.getString(5).trim());
					    Obj.put("busName",reset.getString(6).trim());
					    Obj.put("taxYN",reset.getString(7).trim());
					    Obj.put("stdSize",reset.getString(8).trim());
					    Obj.put("obtain",reset.getString(9).trim());
					    Obj.put("purCost",reset.getString(10).trim());
					    Obj.put("profitRate",reset.getString(11).trim());
					    Obj.put("L_Code",reset.getString(12).trim());
					    Obj.put("M_Code",reset.getString(13).trim());
					    Obj.put("S_Code",reset.getString(14).trim());
					    Obj.put("surtax",reset.getString(15).trim());
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
	           	
	        	dialog.dismiss();
	            dialog.cancel();
	
			    		
				//String[] from = new String[] {"barcode", "gName", "purPri", "sellPri",
	    		//"busCode", "busName", "taxYN", "stdSize", "obtain", "purcost", "profitRate"};
			    String[] from = new String[] {"barcode", "gName", "purPri", "sellPri"};
				
		        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
		        //List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		 	    
		        if (!fillMaps.isEmpty()) fillMaps.clear();
		        
	        	Iterator<JSONObject> iterator = CommArray.iterator();
	    		while (iterator.hasNext()) {
	            	JSONObject json = iterator.next();
	            	
	            	try {
	            		
	    				String barcode = json.getString("barcode");
	    				String gName = json.getString("gName");
	    				String purPri = json.getString("purPri");
	    				String sellPri = json.getString("sellPri");
	    				String busCode = json.getString("busCode");
	    				String busName = json.getString("busName");
	    				String taxYN = json.getString("taxYN");
	    				String stdSize = json.getString("stdSize");
	    				String obtain = json.getString("obtain");
	    				String purCost = json.getString("purCost");
	    				String profitRate = json.getString("profitRate");
	    				String L_Code = json.getString("L_Code");
	    				String M_Code = json.getString("M_Code");
	    				String S_Code = json.getString("S_Code");
	    				String surtax = json.getString("surtax");
	    				
	    				// prepare the list of all records
			            HashMap<String, String> map = new HashMap<String, String>();
			            map.put("barcode", barcode);
			            map.put("gName", gName);
			            map.put("purPri", purPri);
			            map.put("sellPri", sellPri);
			            map.put("busCode", busCode);
			            map.put("busName", busName);
			            map.put("taxYN", taxYN);
			            map.put("stdSize", stdSize);
			            map.put("obtain", obtain);
			            map.put("purCost", purCost);
			            map.put("profitRate", profitRate);
			            map.put("L_Code", L_Code);
			            map.put("M_Code", M_Code);
			            map.put("S_Code", S_Code);
			            map.put("surtax", surtax);
			            fillMaps.add(map);
	    		 
	    			} catch (JSONException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}

		        // fill in the grid_item layout
		        SimpleAdapter adapter = new SimpleAdapter(ManageProductActivity.this, fillMaps, R.layout. activity_listview_product_list, from, to);
		        m_listProduct.setAdapter(adapter);
		        
		        if(m_type == 1)
		        	Toast.makeText(getApplicationContext(), "조회 완료", 0).show();
		        else
		        	Toast.makeText(getApplicationContext(), "등록 완료", 0).show();
	        }
	    }

}
