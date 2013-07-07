package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
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
public class ManageProductActivity extends Activity {
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
	Button m_buttonCustomerClassification1;
	Button m_buttonCustomerClassification2;
	Button m_buttonCustomerClassification3;
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
	HashMap<String, String> m_tempProduct =new HashMap<String, String>();

	List<HashMap<String, String>> m_Ltype = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> m_Mtype = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> m_Stype = new ArrayList<HashMap<String, String>>();

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
		m_buttonCustomerClassification1 = (Button)findViewById(R.id.buttonClassificationType1);
		m_buttonCustomerClassification2 = (Button)findViewById(R.id.buttonClassificationType2);
		m_buttonCustomerClassification3 = (Button)findViewById(R.id.buttonClassificationType3);
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
            	
            	m_tempProduct = mfillMaps.get(position);
            	updateFormView(m_tempProduct);
            }
        });
		m_listProduct.setOnScrollListener(customScrollListener);

		String[] from = new String[] {"BarCode", "G_Name", "Pur_Pri", "Sell_Pri"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
        // fill in the grid_item layout
        m_adapter = new SimpleAdapter(this, mfillMaps, R.layout.activity_listview_product_list, from, to);
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
			@Override
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
			@Override
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
			@Override
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
		
		String barcode = getIntent().getStringExtra("barcode");
		m_textBarcode.setText(barcode);
		
		fetchLName();
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
			actionbar.setTitle("상품등록");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
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
		float f_ratio = (salesPrice - purchasePrice) / purchasePrice;
		String ratio = Float.toString(f_ratio);
		m_textDifferentRatio.setText(ratio);
    }
	
	private void fillSalePriceFromRatioAndPurchasePrice(float ratio, float purchasePrice) {
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
	
    // 새로 입력
    public void doClear(){
    	m_tempProduct = null;
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
		m_spinTaxation.setSelection(0);
		m_checkSurtax.setChecked(false);
	}
	
	public void doRegister(){

		String query = "";
		String barcode = m_textBarcode.getText().toString();
	    String productName = m_textProductName.getText().toString();
	    String customerCode = m_textCustomerCode.getText().toString();
	    String customerName = m_textCustomerName.getText().toString();

	    String lname = m_buttonCustomerClassification1.getText().toString();
	    String mname = m_buttonCustomerClassification2.getText().toString();
	    String sname = m_buttonCustomerClassification3.getText().toString();
	    String lcode = getCodeFromListByName(m_Ltype, lname);
	    String mcode = getCodeFromListByName(m_Mtype, mname);
	    String scode = getCodeFromListByName(m_Stype, sname);
	    String taxation = m_spinTaxation.getSelectedItem().toString();
		String standard = m_textStandard.getText().toString();
		String acquire = m_textAcquire.getText().toString();
		String purchasePrice = m_textPurchasePrice.getText().toString();
		String purchasePriceOriginal = m_textPurchasePriceOriginal.getText().toString();
		String salesPrice = m_textSalesPrice.getText().toString();
		String ratio = m_textDifferentRatio.getText().toString();
		String surtax = null;
	    String good_use = String.valueOf(m_spinGroup.getSelectedItemPosition());
		
		if(m_checkSurtax.isChecked())
			surtax = "1";
		else
			surtax = "0";
		
		if(taxation.equals("면세"))
			taxation = "0";
		else
			taxation = "1";
		
		if (barcode.equals("") || productName.equals("") || customerCode.equals("") || customerName.equals("") ||
			standard.equals("") || acquire.equals("") || lname.equals("") || mname.equals("") || sname.equals("") ||
			purchasePrice.equals("") || purchasePriceOriginal.equals("") || salesPrice.equals("") || ratio.equals("")) {
			Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
			return;
    	}
		
    	query += "insert into Goods(BarCode, G_Name, Bus_Code, Bus_Name, Tax_YN, Std_Size, Obtain, Pur_Pri, Pur_Cost," +
    			" Sell_Pri, Profit_Rate, L_Code, L_Name, M_Code, M_Name, S_Code, S_Name, VAT_CHK, good_use) values('" + barcode + "', '" + productName + "'," +
    			"'"+ customerCode + "', '" + customerName + "', '" + taxation + "', '" + standard + "', '" + acquire + "'," +
    			"'" + purchasePrice + "', '" + purchasePriceOriginal + "', '" + salesPrice + "', '" + ratio + "'," +
    			"'" + lcode + "', '" + lname + "', '" + mcode + "', '" + mname + "', '" + scode + "', '" + sname + 
    			"', '" + surtax + "', " + "'" + good_use + "');";
    	
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


	public String getCodeFromListByName (List<HashMap<String, String>> l, String name) {
		
		Iterator<HashMap<String, String>> it = l.iterator();
		while(it.hasNext()) {
			HashMap<String, String> obj = it.next();
			if (obj.get("name").equals(name) ) {
				return obj.get("code");
			}
		}
		
		return "";
	}

	public void doModify(){

		String query = "";
		String barcode = m_textBarcode.getText().toString();
	    String productName = m_textProductName.getText().toString();
	    String customerCode = m_textCustomerCode.getText().toString();
	    String customerName = m_textCustomerName.getText().toString();

	    String lname = m_buttonCustomerClassification1.getText().toString();
	    String mname = m_buttonCustomerClassification2.getText().toString();
	    String sname = m_buttonCustomerClassification3.getText().toString();
	    String lcode = getCodeFromListByName(m_Ltype, lname);
	    String mcode = getCodeFromListByName(m_Mtype, mname);
	    String scode = getCodeFromListByName(m_Stype, sname);
	    
	    String taxation = m_spinTaxation.getSelectedItem().toString();
		String standard = m_textStandard.getText().toString();
		String acquire = m_textAcquire.getText().toString();
		String purchasePrice = m_textPurchasePrice.getText().toString();
		String purchasePriceOriginal = m_textPurchasePriceOriginal.getText().toString();
		String salesPrice = m_textSalesPrice.getText().toString();
		String ratio = m_textDifferentRatio.getText().toString();
		String surtax = null;
	    String good_use = String.valueOf(m_spinGroup.getSelectedItemPosition());
		
		if(m_checkSurtax.isChecked())
			surtax = "1";
		else
			surtax = "0";
		
		if(taxation.equals("면세"))
			taxation = "0";
		else
			taxation = "1";
		
		if (barcode.equals("") || productName.equals("") || customerCode.equals("") || customerName.equals("") ||
				standard.equals("") || acquire.equals("") || lname.equals("") || mname.equals("") || sname.equals("") ||
			purchasePrice.equals("") || purchasePriceOriginal.equals("") || salesPrice.equals("") || ratio.equals("")) {
			Toast.makeText(getApplicationContext(), "비어있는 필드가 있습니다", Toast.LENGTH_SHORT).show();
			return;
    	}
		
    	query += "Update Goods Set BarCode = '" + barcode + "', G_Name = '" + productName + "', Bus_Code = '" + customerCode + "', " +
    			  "Bus_Name = '" + customerName + "', Tax_YN = '" + taxation + "', Std_Size = '" + standard + "', Obtain = '" + acquire + "', " +
    			  "Pur_Pri = '" + purchasePrice + "', Pur_Cost = '" + purchasePriceOriginal + "', Sell_Pri = '" + salesPrice + "', " + "Profit_Rate = '" + ratio +
    			   "', L_Code = '" + lcode + "', M_Code = '" + mcode + "', S_Code = '" + scode +
    			   "', L_Name = '" + lname + "', M_Name = '" + mname + "', S_Name = '" + sname +
    			  "', VAT_CHK = '" + surtax + "', good_use='"+ good_use+"' WHERE BarCode = '" + barcode + "';"; 
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
		
		query += "select * from Goods WHERE Goods_Use='1' AND Pur_Use='1' ";
	    
	    if (!barcode.equals("") || !customerCode.equals("")|| !productName.equals("")|| !customerName.equals("")){
	    	
		    boolean added = false;
		    if (!barcode.equals("")){
		    	query += " AND Barcode = '" + barcode + "'";
		    	added = true;
		    }
		    if (!productName.equals("")){
		    	query += " AND G_Name = '" + productName + "'";
		    	added = true;
		    }
		    if (!customerName.equals("")){
		    	query += " AND Bus_Name = '" + customerName + "'";
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
		String customer = m_textCustomerCode.getText().toString();
		Intent intent = new Intent(this, ManageCustomerListActivity.class);
		intent.putExtra("customer", customer);
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
					String barcode = m_textBarcode.getText().toString();
					Intent intent = new Intent(ManageProductActivity.this, ManageProductListActivity.class);
					intent.putExtra("barcode", barcode);
					intent.putExtra("good_use", "no");
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
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
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
						m_tempProduct = JsonHelper.toStringHashMap(results.getJSONObject(0));
						updateFormView(m_tempProduct);
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

    // 입력 폼 채우기
    public void updateFormView(HashMap<String, String> object) {

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
		
		//int l = getIndexFromListByCode(m_Ltype, object.get("L_Code"));
		///int m = getIndexFromListByCode(m_Mtype, object.get("M_Code"));
		//int s = getIndexFromListByCode(m_Stype, object.get("S_Code"));
		//m_spinCustomerClassification1.setSelection(l);
		//m_spinCustomerClassification1.setSelection(m);
		//m_spinCustomerClassification1.setSelection(s);

		m_buttonCustomerClassification1.setText(object.get("L_Name"));
		m_buttonCustomerClassification2.setText(object.get("M_Name"));
		m_buttonCustomerClassification3.setText(object.get("S_Name"));
		
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
		if(object.get("Goods_Use").equals("0"))
			m_spinGroup.setSelection(0);
		else if(object.get("Goods_Use").equals("1"))
			m_spinGroup.setSelection(1);
    }
    
    private void fetchLName() {
    	String query = "";
		query = "SELECT L_Name, L_Code FROM Goods GROUP BY L_Name, L_Code;";
	
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

					for(int index = 0; index < results.length() ; index++)
					{
						HashMap<String, String> data =new HashMap<String, String>();
						JSONObject son;
						try {
							son = results.getJSONObject(index);
							String name = son.getString("L_Name");
							String code = son.getString("L_Code");
							
							data.put("name", name);
							data.put("code", code);
							m_Ltype.add(data);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}					
				}
				else {
					Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
    
	public void onClassification1(View view)
	{
		m_buttonCustomerClassification2.setText("");
		m_buttonCustomerClassification3.setText("");
		
		ArrayList<String> lSpList = new ArrayList<String>();       
		for(int index = 0; index < m_Ltype.size() ; index++)
		{
			HashMap<String, String> data = m_Ltype.get(index);
			String name = data.get("name");
			lSpList.add(name);
		}

		final CharSequence[] charSequenceItems = lSpList.toArray(new CharSequence[lSpList.size()]);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("선택하세요");
	    builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int item) {
	    		String name = charSequenceItems[item].toString();
	    		m_buttonCustomerClassification1.setText(name);
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public void onClassification2(View view)
	{
		String lname = m_buttonCustomerClassification1.getText().toString();
		if (lname.equals("")) {
			Toast.makeText(getApplicationContext(), "먼저 대분류를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
			return;
		}
		
		m_buttonCustomerClassification3.setText("");
		String query = "";
		query = "SELECT M_Name, M_Code FROM Goods WHERE L_Name='"+lname+"' GROUP BY M_Name, M_Code;";
	
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
					m_Mtype.removeAll(m_Mtype);
					
					ArrayList<String> lSpList = new ArrayList<String>();       
					for(int index = 0; index < results.length() ; index++)
					{
						HashMap<String, String> data =new HashMap<String, String>();
						JSONObject son;
						try {
							son = results.getJSONObject(index);
							String name = son.getString("M_Name");
							String code = son.getString("M_Code");
							lSpList.add(name);

							data.put("name", name);
							data.put("code", code);
							m_Mtype.add(data);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					final CharSequence[] charSequenceItems = lSpList.toArray(new CharSequence[lSpList.size()]);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
				    builder.setTitle("선택하세요");
				    builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
				    	public void onClick(DialogInterface dialog, int item) {
				    		String name = charSequenceItems[item].toString();
				    		m_buttonCustomerClassification2.setText(name);
				        }
				    });
				    AlertDialog alert = builder.create();
				    alert.show();
				}
				else {
					Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void onClassification3(View view)
	{
		String lname = m_buttonCustomerClassification1.getText().toString();
		String mname = m_buttonCustomerClassification2.getText().toString();
		if (lname.equals("")||mname.equals("")) {
			Toast.makeText(getApplicationContext(), "먼저 대분류,중분류를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String query = "";
		query = "SELECT S_Name, S_Code FROM Goods WHERE L_Name='"+lname+"' AND M_Name='"+mname+"' GROUP BY S_Name, S_Code;";

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
					m_Stype.removeAll(m_Stype);

					ArrayList<String> lSpList = new ArrayList<String>();       
					for(int index = 0; index < results.length() ; index++)
					{
						JSONObject son;
						try {
							HashMap<String, String> data =new HashMap<String, String>();
							son = results.getJSONObject(index);
							String name = son.getString("S_Name");
							String code = son.getString("S_Code");
							lSpList.add(name);
							
							data.put("name", name);
							data.put("code", code);
							m_Stype.add(data);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					final CharSequence[] charSequenceItems = lSpList.toArray(new CharSequence[lSpList.size()]);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductActivity.this);
				    builder.setTitle("선택하세요");
				    builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
				    	public void onClick(DialogInterface dialog, int item) {
				    		String name = charSequenceItems[item].toString();
				    		m_buttonCustomerClassification3.setText(name);
				        }
				    });
				    AlertDialog alert = builder.create();
				    alert.show();
				}
				else {
					Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
}
