package tipsystem.tips;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
    
    List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
	private ProgressDialog dialog;
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
		
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doSearch();
	        }
		});
		
		registButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doRegist();
	        }
		});
		
		renewButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doClear();
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

		String barcode = mfillMaps.get(position).get("barcode");
		String gName = mfillMaps.get(position).get("gName");
		String busCode = mfillMaps.get(position).get("busCode");
		String busName = mfillMaps.get(position).get("busName");
		String purPri = mfillMaps.get(position).get("purPri");
		String sellPri = mfillMaps.get(position).get("sellPri");
		String taxYN = mfillMaps.get(position).get("taxYN");
		String stdSize = mfillMaps.get(position).get("stdSize");
		String obtain = mfillMaps.get(position).get("obtain");
		String purCost = mfillMaps.get(position).get("purCost");
		String profitRate = mfillMaps.get(position).get("profitRate");
		String L_Code = mfillMaps.get(position).get("L_Code");
		String M_Code = mfillMaps.get(position).get("M_Code");
		String S_Code = mfillMaps.get(position).get("S_Code");
		String surtax = mfillMaps.get(position).get("surtax");
		
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
			m_spinTaxation.setSelection(1);
		else
			m_spinTaxation.setSelection(0);
		if(surtax.equals("0")){
			m_checkSurtax.setChecked(false);
		}else{
			
			m_checkSurtax.setChecked(true);
		}
	}

	public void doSearch(){

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		// 입력된 코드 가져오기
    	String barcode = m_textBarcode.getText().toString();
	    String productName = m_textProductName.getText().toString();
	    String customerName = m_textCustomerName.getText().toString();
	    
    	// 쿼리 작성하기
	    String query =  "";
    	query += "select * from Goods ";
	    
	    if (!barcode.equals("") || !productName.equals("") || !customerName.equals("")){
	    	query += " WHERE";
		        
		    boolean added = false;
		    if (!barcode.equals("")){
		    	query += " Barcode = '" + barcode + "'";
		    	added = true;
		    }
		    
		    if (!productName.equals("")){
		    	if (added) query += " AND ";
		    	
		    	query += " G_Name = '" + productName  + "'";
		    	added = true;
		    }
		    
		    if (!customerName.equals("")){
		    	if (added) query += " AND ";
		    	
		    	query += " Bus_Name = '" + customerName  + "'";
		    	added = true;
		    }
		    query += ";";
	    } else {
	    	query = "SELECT TOP 2000 * FROM Goods WHERE BarCode NOT IN(SELECT TOP 1 BarCode FROM Goods);";
	    }
	    //query = "SELECT * FROM Goods where BarCode = '9800440210028';";
	    // 콜백함수와 함께 실행
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

	public void doRegist(){
		
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
		if(m_checkSurtax.isChecked())	surtax = "1";
		else	surtax = "0";
		
		if(taxation.equals("면세"))		taxation = "0";
		else	taxation = "1";
		
		if (barcode.equals("") || productName.equals("") || customerCode.equals("") || customerName.equals("") ||
			customerClass1.equals("") || customerClass2.equals("") || customerClass3.equals("") || standard.equals("") || acquire.equals("") ||
			purchasePrice.equals("") || purchasePriceOriginal.equals("") || salesPrice.equals("") || ratio.equals("")) {
    	Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
    	return;
    	}
		
	    String query =  "";
    	query += "insert into Goods(BarCode, G_Name, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Pri, Pur_Cost," +
    			" Sell_Pri, Profit_Rate, L_Code, M_Code, S_Code, Add_Tax) values('" + barcode + "', '" + productName + "'," +
    			"'"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "'," +
    			"'" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "'," +
    			"'" + customerClass1 + "', '" + customerClass2 + "', '" + customerClass3 + "', '" + surtax + "'); SELECT * FROM Goods WHERE BarCode = '" + barcode + "';";
    	
    	// 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					//updateListView(results);
		            Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);    	
	}
	
	public void doClear(){
		m_textBarcode.setText("");
		m_textProductName.setText("");
		m_textCustomerCode.setText("");
		m_textCustomerName.setText("");
		m_textStandard.setText("");
		m_textAcquire.setText("");
		m_textPurchasePrice.setText(""); // 매입가
		m_textSalesPrice.setText(""); //판매가
		m_textPurchasePriceOriginal.setText(""); //매입원가
		m_textDifferentRatio.setText(""); // 이의율
		m_textCustomerClassification1.setText("");
		m_textCustomerClassification2.setText("");
		m_textCustomerClassification3.setText("");
		m_spinTaxation.setSelection(0);
		m_checkSurtax.setChecked(false);
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
				String surtax = json.getString("Add_Tax");

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
	            Log.w("test", "6");
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(ManageProductActivity.this, mfillMaps, R.layout. activity_listview_product_list, from, to);
        m_listProduct.setAdapter(adapter);
    }
    
}
