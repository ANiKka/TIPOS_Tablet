package tipsystem.tips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.tips.ManageProductListActivity.ProductList;
import tipsystem.tips.ManageProductListActivity.ProductListAdapter;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
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
import android.widget.AdapterView.OnItemClickListener;
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
    
	int index = 0;
	int size = 100;
	int firstPosition = 0; 
		
    ArrayList<ProductList> productArray = new ArrayList<ProductList>();
    

	private ProgressDialog dialog;
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
		Button searchButton = (Button) findViewById(R.id.buttonProductSearch);
		Button registButton = (Button) findViewById(R.id.buttonProductRegist);
		Button renewButton = (Button) findViewById(R.id.buttonProductRenew);
		Button modifyButton = (Button) findViewById(R.id.buttonProductModify);
		Button buttonBarcode = (Button) findViewById(R.id.buttonBarcode);
		
		// 바코드 검색 버튼 클릭시 나오는 목록 셋팅
		final String[] option = new String[] { "목록", "카메라"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Option");
		
		// ManageProductListActivity연결 intent 객체 생성
		final Intent intent = new Intent(this, ManageProductListActivity.class);
		// 목록 선택시 이벤트 처리
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
				if(which == 0){ // 목록으로 조회할 경우
			    	startActivityForResult(intent, 1);
				} else { // 스캔할 경우
					Intent intent = new Intent(ManageProductActivity.this, ZBarScannerActivity.class);
			    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);				
				}
          }
		}); 
		
		//조회 버튼 클릭
		searchButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doQuery(1);
	        }
		});
		//등록 버튼 클릭		
		registButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doQuery(2);
	        }
		});
		
		// 수정 버튼 클릭
		modifyButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doQuery(3);
	        }
		});

		//초기화 버튼 클릭		
		renewButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	        	doClear();
	        }
		});

		final AlertDialog listDialog = builder.create();
		// 검색 버튼 클릭시 다이얼로그 박스 보여줌
		buttonBarcode.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	listDialog.show();
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
			    		doQuery(0);	    	
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
		
		m_listProduct.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		fillCustomerFormFromArray(productArray, position);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
		super.onActivityResult(requestCode, resultCode, data);
		
		if(!productArray.isEmpty()) productArray.clear();
		switch(requestCode){
			// 카메라 스캔을 통한 바코드 검색
			case 0 :
				if (resultCode == RESULT_OK) 
			    {
			        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
			        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			        Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
			        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
			        // The value of type indicates one of the symbols listed in Advanced Options below.
			       
			        String barcode = data.getStringExtra(ZBarConstants.SCAN_RESULT);
					m_textBarcode.setText(barcode);
					doQuery(0);
					
			    } else if(resultCode == RESULT_CANCELED) {
			        Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
			    }
				break;
			// 목록 검색을 통한 바코드 검색				
			case 1 :
				if(resultCode == RESULT_OK && data != null) {
					
		        	ArrayList<String> fillMaps = data.getStringArrayListExtra("fillmaps");		        	
		        	ProductList pl = new ProductList(fillMaps.get(0),
								    				fillMaps.get(1),
								    				fillMaps.get(2),
								    				fillMaps.get(3),
								    				fillMaps.get(4),
								    				fillMaps.get(5),
								    				fillMaps.get(6),
								    				fillMaps.get(7),
								    				fillMaps.get(8),
								    				fillMaps.get(9),
								    				fillMaps.get(10),
								    				fillMaps.get(11),
								    				fillMaps.get(12),
								    				fillMaps.get(13),
								    				fillMaps.get(14),
								    				fillMaps.get(15),
								    				fillMaps.get(16),
								    				fillMaps.get(17),
								    				fillMaps.get(18));
		    		productArray.add(pl);
		    		fillCustomerFormFromArray(productArray, 0);
		        }
		        // 수행을 제대로 하지 못한 경우
		        else if(resultCode == RESULT_CANCELED)
		        {
		        	
		        }
				break;
			}
	     
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
	public void doQuery(final int code){
		
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
				
		switch(code){
		
			case 0 :
				query = "SELECT * FROM Goods WHERE Barcode = '" + m_textBarcode.getText().toString() + "';";
				break;
				
			case 1 :
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
			    	query = "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods) A JOIN (SELECT TOP " + size + " * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods)) B ON A.BarCode = B.BarCode;";
			    }
			    break;
			// 등록 
			case 2 :

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
		    	
		    	break;
		    // 수정
			case 3 :
				
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
		    			  "', VAT_CHK = '" + surtax + "' WHERE BarCode = '" + barcode + "';" +
		    			  "SELECT A.G_grade, B.* FROM (SELECT isNull(G_grade, 0) AS G_Grade, BarCode FROM Goods) A JOIN (SELECT TOP " + size + " * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods)) B ON A.BarCode = B.BarCode;";
		    	break;
				
		}
		
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
					if(code == 0){  
						settingArray(results, 0);
					} else if (code == 1 || code == 2){ // code == 1(조회) || code == 2(등록)
						settingArray(results, 1);
						if(code == 1)
							Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
						else
							onAlert(code);
					} else {
						settingArray(results, 3);
						onAlert(code);						
					}
				}
				else {
					if(code < 2)
						Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
					else 
						Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_SHORT).show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);				
	}

	public void onAlert(int code) {
		// TODO Auto-generated method stub
		// super.onBackPressed(); //지워야 실행됨
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		if(code == 2)
			alertDialog.setMessage("정상적으로 등록되었습니다..");
		else
			alertDialog.setMessage("정상적으로 수정되었습니다..");
		alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// process전체 종료
				dialog.cancel();
			}
		});
		alertDialog.show();
	} 

	
	// 리스트뷰 띄우기
    public void settingArray(JSONArray results, int code){
		
    	if(code == 3){
			productArray.clear();
    	}
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
									json.getString("G_grade"));
					productArray.add(pl);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(code == 0)
			fillCustomerFormFromArray(productArray, 0);
		else if (code == 1 || code == 2 || code == 3)
			showListView();
    }
    
    // 리스트뷰 보이기
    public void showListView (){
    	Log.w("asdf", "asdfasdf");
    	ProductListAdapter ProductList = new ProductListAdapter(this, R.layout.activity_listview_product_list, productArray);
		ListView m_listProduct = (ListView)findViewById(R.id.listviewProductList);
		
		//ProductList.notifyDataSetChanged();
		firstPosition = m_listProduct.getFirstVisiblePosition();
		m_listProduct.setAdapter(ProductList);
		m_listProduct.setSelection(firstPosition);

	}

    // 입력 폼 채우기
    public void fillCustomerFormFromArray(ArrayList<ProductList> productArray1, int position){

		m_textBarcode.setText(productArray1.get(position).Barcode);
		m_textProductName.setText(productArray1.get(position).G_Name);
		m_textCustomerCode.setText(productArray1.get(position).Bus_Code);
		m_textCustomerName.setText(productArray1.get(position).Bus_Name);
		m_textStandard.setText(productArray1.get(position).stdSize);
		m_textAcquire.setText(productArray1.get(position).obtain);
		m_textPurchasePrice.setText(productArray1.get(position).Pur_Pri); // 매입가
		m_textSalesPrice.setText(productArray1.get(position).Sell_Pri); //판매가
		m_textPurchasePriceOriginal.setText(productArray1.get(position).purCost); //매입원가
		m_textDifferentRatio.setText(productArray1.get(position).profitRate); // 이의율
		String class1 = "[" + productArray1.get(position).L_Code + "]" + "" + "[" + productArray1.get(position).L_Name + "]";
		String class2 = "[" + productArray1.get(position).M_Code + "]" + "" + "[" + productArray1.get(position).M_Name + "]";
		String class3 = "[" + productArray1.get(position).S_Code + "]" + "" + "[" + productArray1.get(position).S_Name + "]";
		m_textCustomerClassification1.setText(class1);
		m_textCustomerClassification2.setText(class2);
		m_textCustomerClassification3.setText(class3);
		if(productArray1.get(position).taxYN.equals("0")) {
			m_spinTaxation.setSelection(1);
		} else {
			m_spinTaxation.setSelection(0);
		}
		if(productArray1.get(position).surtax.equals("0")){
			m_checkSurtax.setChecked(false);
		} else {
			m_checkSurtax.setChecked(true);
		}
		
		if(productArray1.get(position).G_grade.equals("0"))
			m_spinGroup.setSelection(0);
		else if(productArray1.get(position).G_grade.equals("A"))
			m_spinGroup.setSelection(1);
			

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
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("AlertDialog")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();     
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		return alert;
	}
	
	class ProductList {
		ProductList(String aBarcode, String aG_Name, String aPur_Pri, String aSell_Pri, String aBusCode, String aBusName,
					String ataxYN, String astdSize, String aobtain, String apurCost, String aprofitRate, String aL_Code,
					String aM_Code, String aS_Code, String aL_Name, String aM_Name, String aS_Name, String asurtax, String ag_grade){
			Barcode = aBarcode;
			G_Name = aG_Name;
			Pur_Pri = aPur_Pri;
			Sell_Pri = aSell_Pri;
			Bus_Code = aBusCode;
			Bus_Name= aBusName;
			taxYN = ataxYN;
			stdSize = astdSize;
			obtain = aobtain;
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
			}	
		String Barcode;
		String G_Name;
		String Pur_Pri;
		String Sell_Pri;
		String Bus_Code;
		String Bus_Name;
		String taxYN;
		String stdSize;
		String obtain;
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
			//returnFillMaps(position);
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
			sell_pri.setText(arr_Goods.get(position).Pur_Pri);
			pur_pri.setText(arr_Goods.get(position).Sell_Pri);

			if(position == size-3){
				index = size;
				size = size * 2;
				doQuery(1);
			}
			return convertView;
		}
	}
}
