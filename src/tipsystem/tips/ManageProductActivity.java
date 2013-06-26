package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
/*
 * 기본관리 -> 상품관리
 * */
public class ManageProductActivity extends Activity{
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private static final int BARCODE_MANAGER_REQUEST = 2;
	private static final int CUSTOMER_MANAGER_REQUEST = 3;
	
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";

	TextView m_textBarcode;
	TextView m_textProductName;
	TextView m_textCustomerCode;
	TextView m_textCustomerName;
	TextView m_textCustomerClassification1;
	TextView m_textCustomerClassification2;
	TextView m_textCustomerClassification3;
	TextView m_textStandard;
	TextView m_textAcquire;
	TextView m_textPurchasePrice;
	TextView m_textPurchasePriceOriginal;
	TextView m_textSalesPrice;
	TextView m_textDifferentRatio;
	Spinner m_spinTaxation;
	CheckBox m_checkSurtax;
	Spinner m_spinGroup;
	ListView m_listProduct;
	SimpleAdapter m_adapter; 

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
		setContentView(R.layout.activity_manage_product);
		// Show the Up button in the action bar.
		setupActionBar();
		
        m_shop = LocalStorage.getJSONObject(this, "currentShopData");
        
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		m_textBarcode = (EditText)findViewById(R.id.editTextBarcode);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	fillCustomerForm(position);
            }
        });
		m_listProduct.setOnScrollListener(customScrollListener);

		String[] from = new String[] {"BarCode", "G_Name", "Pur_Pri", "Sell_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
        // fill in the grid_item layout
        m_adapter = new SimpleAdapter(this, mfillMaps, R.layout. activity_listview_product_list, from, to);
        m_listProduct.setAdapter(m_adapter);
        
		Button searchButton = (Button) findViewById(R.id.buttonProductSearch);
		Button registButton = (Button) findViewById(R.id.buttonProductRegist);
		Button renewButton = (Button) findViewById(R.id.buttonProductRenew);
		Button modifyButton = (Button) findViewById(R.id.buttonProductModify);
		
		//조회 버튼 클릭
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	deleteListViewAll();
	        	doSearch();
	        }
		});
		
		//등록 버튼 클릭		
		registButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	deleteListViewAll();
	        	doRegister();
	        }
		});
		
		// 수정 버튼 클릭
		modifyButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	deleteListViewAll();
	        	doModify();
	        }
		});

		//초기화 버튼 클릭		
		renewButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doClear();
	        }
		});

		// 바코드 입력 후 포커스 딴 곳을 옮길 경우
		m_textBarcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String barcode = null; 
			    	barcode = m_textBarcode.getText().toString();
			    	if(!barcode.equals("")) // 입력한 Barcode가 값이 있을 경우만
			    		doQueryWithBarcode();	    	
			    }
			}
		});

		// 거래처 코드 입력 후 포커스 딴 곳을 옮길 경우
		m_textCustomerCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String customerCode = m_textCustomerCode.getText().toString();
			    	if(!customerCode.equals("")) // 입력한 customerCode가 값이 있을 경우만
			    		fillBusNameFromBusCode(customerCode);	    	
			    }
			}
		});
		
		// 매입원가 + 이익률로 판매가
		m_textDifferentRatio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String ratio = m_textDifferentRatio.getText().toString();
			    	String purchasePrice = m_textPurchasePrice.getText().toString();
			    	if(!ratio.equals("") && !purchasePrice.equals("")) {
			    		float f_ratio =  Float.valueOf(ratio).floatValue();
			    		float f_purchasePrice =  Float.valueOf(purchasePrice).floatValue();
			    		fillSalePriceFromRatioAndPurchasePrice(f_ratio, f_purchasePrice);
			    	}
			    }
			}
		});
		// 매입원가 + 판매가로 이익률
		m_textPurchasePrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String salesPrice = m_textSalesPrice.getText().toString();
			    	String purchasePrice = m_textPurchasePrice.getText().toString();
			    	String ratio = m_textDifferentRatio.getText().toString();
			    	if(!salesPrice.equals("") && !purchasePrice.equals("")) {
			    		float f_salesPrice =  Float.valueOf(salesPrice).floatValue();
			    		float f_purchasePrice =  Float.valueOf(purchasePrice).floatValue();
			    		fillRatioFromSalePriceAndPurchasePrice(f_salesPrice, f_purchasePrice);
			    	} else if(!ratio.equals("") && !purchasePrice.equals("")){
			    		float f_ratio =  Float.valueOf(ratio).floatValue();
			    		float f_purchasePrice =  Float.valueOf(purchasePrice).floatValue();
			    		fillSalePriceFromRatioAndPurchasePrice(f_ratio, f_purchasePrice);
			    		
			    	}
			    }
			}
		});
		// 매입원가 + 판매가로 이익률		
		m_textSalesPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(!hasFocus){
			    	String salesPrice = m_textSalesPrice.getText().toString();
			    	String purchasePrice = m_textPurchasePrice.getText().toString();
			    	if(!salesPrice.equals("") && !purchasePrice.equals("")) {
			    		float f_salesPrice =  Float.valueOf(salesPrice).floatValue();
			    		float f_purchasePrice =  Float.valueOf(purchasePrice).floatValue();
			    		fillRatioFromSalePriceAndPurchasePrice(f_salesPrice, f_purchasePrice);
			    	}
			    }
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();        

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


	private void fillRatioFromSalePriceAndPurchasePrice(float salesPrice, float purchasePrice) {
		// TODO Auto-generated method stub
		float f_ratio = (salesPrice - purchasePrice) / purchasePrice;
		String ratio = Float.toString(f_ratio);
		m_textDifferentRatio.setText(ratio);
    }
	
	private void fillSalePriceFromRatioAndPurchasePrice(float ratio, float purchasePrice) {
		// TODO Auto-generated method stub
		float f_salesPrice = (purchasePrice * ratio) + purchasePrice;
		String salesPrice = Float.toString(f_salesPrice);
		m_textSalesPrice.setText(salesPrice);
    }
	

	// private methods
	public void updateListView(JSONArray results) {
        
        for (int i = 0; i < results.length(); i++) {
        	
        	try {
            	JSONObject json = results.getJSONObject(i);
            	HashMap<String, String> map = JsonHelper.toStringHashMap(json);
            	
	            mfillMaps.add(map);
		 
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        m_adapter.notifyDataSetChanged();
    }
	
	public void deleteListViewAll() {
		if (mfillMaps.isEmpty()) return;
        
		mfillMaps.removeAll(mfillMaps);
		m_adapter.notifyDataSetChanged();
	}
	
    // 입력 폼 채우기
    public void fillCustomerForm(int position){
    	HashMap<String, String> object = mfillMaps.get(position);

		m_textBarcode.setText(object.get("BarCode"));
		m_textProductName.setText(object.get("G_Name"));
		m_textCustomerCode.setText(object.get("Bus_Code"));
		m_textCustomerName.setText(object.get("Bus_Name"));
		m_textStandard.setText(object.get("Std_Size"));
		m_textAcquire.setText(object.get("Obtain"));
		m_textPurchasePrice.setText(object.get("Pur_Pri"));
		m_textSalesPrice.setText(object.get("Sell_Pri"));
		m_textPurchasePriceOriginal.setText(object.get("Pur_Cost"));
		m_textDifferentRatio.setText(object.get("Profit_Rate"));
		
		String class1 = "[" + object.get("L_Code") + "]" + "" + "[" + object.get("L_Name") + "]";
		String class2 = "[" + object.get("M_Code") + "]" + "" + "[" + object.get("M_Name") + "]";
		String class3 = "[" + object.get("S_Code") + "]" + "" + "[" + object.get("S_Name") + "]";
		m_textCustomerClassification1.setText(class1);
		m_textCustomerClassification2.setText(class2);
		m_textCustomerClassification3.setText(class3);
		
		if(object.get("Tax_YN").equals("0")) {
			m_spinTaxation.setSelection(1);
		} else {
			m_spinTaxation.setSelection(0);
		}
		if(object.get("Add_Tax").equals("0")){
			m_checkSurtax.setChecked(false);
		} else {
			m_checkSurtax.setChecked(true);
		}
		
		if(object.get("G_grade").equals("0"))
			m_spinGroup.setSelection(0);
		else if(object.get("G_grade").equals("A"))
			m_spinGroup.setSelection(1);
    }
    
    // 입력 폼 채우기
    public void fillCustomerFormFromJSONObject(JSONObject object){

		try {
			m_textProductName.setText(object.getString("G_Name"));
			m_textCustomerCode.setText(object.getString("Bus_Code"));
			m_textCustomerName.setText(object.getString("Bus_Name"));
			m_textStandard.setText(object.getString("Std_Size"));
			m_textAcquire.setText(object.getString("Obtain"));
			m_textPurchasePrice.setText(object.getString("Pur_Pri"));
			m_textSalesPrice.setText(object.getString("Sell_Pri"));
			m_textPurchasePriceOriginal.setText(object.getString("Pur_Cost"));
			m_textDifferentRatio.setText(object.getString("Profit_Rate"));
			
			String class1 = "[" + object.getString("L_Code") + "]" + "" + "[" + object.getString("L_Name") + "]";
			String class2 = "[" + object.getString("M_Code") + "]" + "" + "[" + object.getString("M_Name") + "]";
			String class3 = "[" + object.getString("S_Code") + "]" + "" + "[" + object.getString("S_Name") + "]";
			m_textCustomerClassification1.setText(class1);
			m_textCustomerClassification2.setText(class2);
			m_textCustomerClassification3.setText(class3);
			
			if(object.getString("Tax_YN").equals("0")) {
				m_spinTaxation.setSelection(1);
			} else {
				m_spinTaxation.setSelection(0);
			}
			if(object.getString("Add_Tax").equals("0")){
				m_checkSurtax.setChecked(false);
			} else {
				m_checkSurtax.setChecked(true);
			}
			
			if(object.getString("G_grade").equals("0"))
				m_spinGroup.setSelection(0);
			else if(object.getString("G_grade").equals("A"))
				m_spinGroup.setSelection(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
	
    // 새로 입력
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
	

	public void doRegister(){

		String query = "";
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
		
		if(taxation.equals("면세"))
			taxation = "0";
		else
			taxation = "1";
		
		if (barcode.equals("") || productName.equals("") || customerCode.equals("") || customerName.equals("") ||
			customerClass1.equals("") || customerClass2.equals("") || customerClass3.equals("") || standard.equals("") || acquire.equals("") ||
			purchasePrice.equals("") || purchasePriceOriginal.equals("") || salesPrice.equals("") || ratio.equals("")) {
			Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
			return;
    	}
		
    	query += "insert into Goods(BarCode, G_Name, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Pri, Pur_Cost," +
    			" Sell_Pri, Profit_Rate, L_Code, M_Code, S_Code, VAT_CHK, G_grade) values('" + barcode + "', '" + productName + "'," +
    			"'"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "'," +
    			"'" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "'," +
    			"'" + customerClass1 + "', '" + customerClass2 + "', '" + customerClass3 + "', '" + surtax + "', ";
    	if(group.equals(""))
    		query += "NULL);";
    	else
    		query += "'" + group + "');";
    	query += "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods WHERE BarCode = '" +
    			  barcode + "') A JOIN (SELECT * FROM Goods WHERE BarCode = '" + barcode + "') B ON A.BarCode = B.BarCode;";
    	
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
				if (results.length() > 0) {
					updateListView(results);
					
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
					alertDialog.setMessage("정상적으로 등록되었습니다..");
					alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDialog.show();
				}
				else {
					Toast.makeText(getApplicationContext(), "결과가 없습니다", Toast.LENGTH_SHORT).show();					
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
	
	public void doModify(){

		String query = "";
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
		
		String class1 = "";
		String class2 = "";
		String class3 = "";
		int stringLoc1 = customerClass1.indexOf("]");
		int stringLoc2 = customerClass2.indexOf("]");
		int stringLoc3 = customerClass3.indexOf("]");
		class1 = customerClass1.substring(1, stringLoc1);
		class2 = customerClass2.substring(1, stringLoc2);
		class3 = customerClass3.substring(1, stringLoc3);
		
		if(m_checkSurtax.isChecked())
		surtax = "1";
		else
			surtax = "0";
		
		if(taxation.equals("면세"))
			taxation = "0";
		else
			taxation = "1";
		
		if (barcode.equals("") || productName.equals("") || customerCode.equals("") || customerName.equals("") ||
			customerClass1.equals("") || customerClass2.equals("") || customerClass3.equals("") || standard.equals("") || acquire.equals("") ||
			purchasePrice.equals("") || purchasePriceOriginal.equals("") || salesPrice.equals("") || ratio.equals("")) {
			Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
			return;
    	}
		
    	query += "Update Goods Set BarCode = '" + barcode + "', G_Name = '" + productName + "', Bus_Code = '" + customerCode + "', " +
    			  "Bus_Name = '" + customerName + "', Tax_YN = '" + taxation + "', Std_Size = '" + standard + "', Obtain = '" + acquire + "', " +
    			  "Pur_Pri = '" + purchasePrice + "', Pur_Cost = '" + purchasePriceOriginal + "', Sell_Pri = '" + salesPrice + "', " + 
    			  "Profit_Rate = '" + ratio + "', L_Code = '" + class1 + "', M_Code = '" + class2 + "', S_Code = '" + class3 +
    			  "', VAT_CHK = '" + surtax + "' WHERE BarCode = '" + barcode + "';"; 
    	//query += "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods) A JOIN (SELECT TOP 50 * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods)) B ON A.BarCode = B.BarCode;";
    	query += "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods WHERE BarCode = '" +
  			  barcode + "') A JOIN (SELECT * FROM Goods WHERE BarCode = '" + barcode + "') B ON A.BarCode = B.BarCode;";
  	
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
				if (results.length() > 0) {
					updateListView(results);
					
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
					alertDialog.setMessage("정상적으로 수정되었습니다..");
					alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDialog.show();
				}
				else {
					Toast.makeText(getApplicationContext(), "결과가 없습니다", Toast.LENGTH_SHORT).show();					
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "수정 실패", Toast.LENGTH_SHORT).show();	
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
		
	
	public void doSearch(){

    	String index = String.valueOf(mfillMaps.size());  
		String query = "";
		String barcode = m_textBarcode.getText().toString();
	    String productName = m_textProductName.getText().toString();
	    String customerCode = m_textCustomerCode.getText().toString();
	    String customerName = m_textCustomerName.getText().toString();
		
		query += "select * from Goods ";
	    
	    if (!barcode.equals("") || !customerCode.equals("")){
	    	query += " WHERE";
		        
		    boolean added = false;
		    if (!barcode.equals("")){
		    	query += " Barcode = '" + barcode + "'";
		    	added = true;
		    }
		    				    
		    if (!customerCode.equals("")){
		    	if (added) query += " AND ";
		    	
		    	query += " Bus_Code = '" + customerCode  + "'";
		    	added = true;
		    }
		    query += ";";
	    } else {
	    	query = "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods) A "
	    			+ " JOIN (SELECT TOP 50 * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods)) B ON A.BarCode = B.BarCode;";
	    }

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

	public void onCustomerSearch(View view)
	{
		Intent intent = new Intent(this, ManageCustomerListActivity.class);
    	startActivityForResult(intent, CUSTOMER_MANAGER_REQUEST);
	}
	
	public void onBarcodeSearch(View view)
	{
		// 바코드 검색 버튼 클릭시 나오는 목록 셋팅
		final String[] option = new String[] { "목록", "카메라"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Option");
		
		// 목록 선택시 이벤트 처리
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				if(which == 0){ // 목록으로 조회할 경우
					Intent intent = new Intent(ManageProductActivity.this, ManageProductListActivity.class);
			    	startActivityForResult(intent, BARCODE_MANAGER_REQUEST);
				} else { // 스캔할 경우
					Intent intent = new Intent(ManageProductActivity.this, ZBarScannerActivity.class);
			    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);				
				}
			}
		}); 
		builder.show();
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
		        m_textBarcode.setText(barcode);
		        doQueryWithBarcode();
				
		    } else if(resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
		    }
			break;
		// 목록 검색을 통한 바코드 검색				
		case BARCODE_MANAGER_REQUEST :
			if(resultCode == RESULT_OK && data != null) {
				HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");        	
				m_textBarcode.setText(hashMap.get("BarCode"));
	        	doQueryWithBarcode();
	        }
			break;
		case CUSTOMER_MANAGER_REQUEST :
			if(resultCode == RESULT_OK && data != null) {
				HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");     	
				m_textCustomerCode.setText(hashMap.get("Office_Code"));
				m_textCustomerName.setText(hashMap.get("Office_Name"));
	        }
			break;
		}
	}

	// 거래처 코드로 거래처명 자동 완성
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
						m_textCustomerName.setText(bus_name);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();					
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}

	
	// SQL QUERY 실행
	public void doQueryWithBarcode () {
		
		String query = "";
		String barcode = m_textBarcode.getText().toString();
		query = "SELECT * FROM Goods WHERE Barcode = '" + barcode + "';";
	
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
						fillCustomerFormFromJSONObject(results.getJSONObject(0));
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
}
