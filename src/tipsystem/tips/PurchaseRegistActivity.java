package tipsystem.tips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PurchaseRegistActivity extends Activity implements OnItemClickListener, OnDateSetListener{

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int BARCODE_MANAGER_REQUEST = 2;
	private static final int CUSTOMER_MANAGER_REQUEST = 3;
	private static final int PURCHASE_REGIST_REQUEST = 4;

	JSONObject m_shop;
	JSONObject m_userProfile;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	String m_id = "";

	String m_junpyo ="";
	int  m_junpyoIdx = 0;

	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;	
	Calendar m_dateCalender1;
	
	ListView m_listReadyToSend;
	SimpleAdapter m_adapter;

	int m_selectedListIndex = -1;
	
	Button m_period;
	CheckBox m_immediatePayment;
	EditText m_customerCode;
	EditText m_customerName;
	EditText m_textBarcode;
	EditText m_textProductName;
	EditText m_et_purchasePrice;
	EditText m_et_salePrice;
	EditText m_amount;
	EditText m_profitRatio;
	Button m_bt_barcodeSearch;
	List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();

	String[] from = new String[] {"BarCode", "Office_Name", "Pur_Pri", "In_Count"};
    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    
	List<HashMap<String, String>> m_purList = null;
	
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_regist);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
		m_userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
	        m_id = m_userProfile.getString("User_ID");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Button btn_Send = (Button)findViewById(R.id.buttonSend);
		Button btn_Delete = (Button)findViewById(R.id.buttonDelete);
		Button btn_SendAll = (Button)findViewById(R.id.buttonSendAll);
		
		m_listReadyToSend= (ListView)findViewById(R.id.listviewReadyToSendList);
		
		m_period = (Button)findViewById(R.id.buttonSetDate1);
		m_immediatePayment = (CheckBox)findViewById(R.id.checkBoxImmediatePayment);
		m_customerCode = (EditText)findViewById(R.id.editTextCustomerCode);
		m_customerName = (EditText)findViewById(R.id.editTextCustomerName);
		m_textBarcode = (EditText)findViewById(R.id.editTextBarcode);
		m_textProductName = (EditText)findViewById(R.id.editTextProductName);
		m_et_purchasePrice = (EditText)findViewById(R.id.editTextPurchasePrice);
		m_et_salePrice = (EditText)findViewById(R.id.editTextSalePrice);
		m_amount = (EditText)findViewById(R.id.editTextAmount);
		m_profitRatio = (EditText)findViewById(R.id.editTextProfitRatio);
		m_bt_barcodeSearch = (Button)findViewById(R.id.buttonBarcode);
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		m_dateCalender1 = Calendar.getInstance();
		m_period.setText(m_dateFormatter.format(m_dateCalender1.getTime()));

		m_purList = new ArrayList<HashMap<String, String>>();
		
		// 전송대기목록 뷰에 값 전달
		Button saveButton = (Button)findViewById(R.id.buttonSave);
		saveButton.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	            changeInputMode(1);
	        	doSendListView();
				m_selectedListIndex = -1;
	        }
		});

		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
		btn_Renew.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) { 
	            changeInputMode(0);
				clearInputBox();
				m_selectedListIndex = -1;
	        }
		});


		btn_Delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				deleteData();				
			}			
		});
		
		btn_Send.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				clearInputBox();
				deleteListAll();
			}			
		});
		
		btn_SendAll.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				sendAllData();
				m_selectedListIndex = -1;
			}			
		});
		
		m_listReadyToSend.setOnItemClickListener(this);
      
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
		TextView textView = (TextView) findViewById(R.id.textView2);
		textView.setTypeface(typeface);
      
	    changeInputMode(0);
	    getSeq();
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		CheckBox checkbox = ((CheckBox)arg1.findViewById(R.id.item5));
		if(checkbox != null) { 
			checkbox.setChecked(!checkbox.isChecked());
			// 베리 킥 포인트 요거 꼭해줘야 checkbox 에서 바로 바로 적용됩니다.
			m_adapter.notifyDataSetChanged();						
		}

		String Office_Code = mfillMaps.get(position).get("Office_Code");
		String Office_Name = mfillMaps.get(position).get("Office_Name");
		Iterator<HashMap<String, String>> iterator = m_purList.iterator();	
		
		JSONArray array = new JSONArray();
		
	    while (iterator.hasNext()) {
			 HashMap<String, String> element = iterator.next();
	         String eOffice_Code = element.get("Office_Code");
	         
	         if (eOffice_Code.equals(Office_Code)) {
	        	 JSONObject object = new JSONObject(element);
	        	 array.put(object);
	         }
	    }

		Intent intent = new Intent(this, PurchaseDetailActivity.class);
		intent.putExtra("data", array.toString());
		intent.putExtra("Office_Code", Office_Code);
		intent.putExtra("Office_Name", Office_Name);
		startActivityForResult(intent, PURCHASE_REGIST_REQUEST);
	}

	public void onClickSetDate1(View v) {
		DatePickerDialog newDlg = new DatePickerDialog(this, this,
					m_dateCalender1.get(Calendar.YEAR),
					m_dateCalender1.get(Calendar.MONTH),
					m_dateCalender1.get(Calendar.DAY_OF_MONTH));
		newDlg.show();
	};
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		m_dateCalender1.set(year, monthOfYear, dayOfMonth);
		m_period.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		getSeq();
	}

	public void makeJunPyo () {

		String period = m_period.getText().toString();
		
		String year = period.substring(0, 4);
		String month = period.substring(5, 7);
		String day = period.substring(8, 10);
		
        // 전표번호 생성 
		String posID = "P";
        try {
			String OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			posID = LocalStorage.getString(this, "currentPosID:"+OFFICE_CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        m_junpyo = year + month + day + posID + String.format("%03d", m_junpyoIdx);
        m_junpyoIdx++;
	}

	public void deleteData() {
		
		if ( m_selectedListIndex == -1) {
			Toast.makeText(this, "선택된 항목이 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mfillMaps.remove(m_selectedListIndex);
		m_adapter.notifyDataSetChanged();
		m_purList.remove(m_purList);
		m_selectedListIndex = -1;
	}
	
	public void deleteListAll() {
		if (mfillMaps.isEmpty()) return;
		
		mfillMaps.removeAll(mfillMaps);
		m_adapter.notifyDataSetChanged();		
		m_purList.removeAll(m_purList);		
		m_selectedListIndex = -1;
	}	

	public void clearInputBox () {
		m_customerCode.setText("");
		m_customerName.setText("");
		m_textBarcode.setText("");
		m_textProductName.setText("");
		m_bt_barcodeSearch.setText("");
		m_et_purchasePrice.setText("");
		m_et_salePrice.setText("");
		m_amount.setText("");
		m_profitRatio.setText("");
	}

	public void changeInputMode (int mode) {
		//m_inputMode = mode;
		if (mode==0) {
			m_textBarcode.setEnabled(true);
			m_textProductName.setEnabled(true);
			m_period.setEnabled(true);
			m_bt_barcodeSearch.setEnabled(true);
			m_customerCode.setEnabled(true);
			m_customerName.setEnabled(true);
			m_et_purchasePrice.setEnabled(true);
			m_et_salePrice.setEnabled(true);
			m_amount.setEnabled(true);
			m_profitRatio.setEnabled(true);
		}
		else {
			m_textBarcode.setEnabled(true);
			m_textProductName.setEnabled(true);
			m_period.setEnabled(false);
			m_bt_barcodeSearch.setEnabled(true);
			m_customerCode.setEnabled(false);
			m_customerName.setEnabled(false);
			m_et_purchasePrice.setEnabled(true);
			m_et_salePrice.setEnabled(true);
			m_amount.setEnabled(true);
			m_profitRatio.setEnabled(true);
		}
	}

	// 저장 버튼
	public void doSendListView(){
		
	    String purchaseDate = m_period.getText().toString();
		String immediatePayment = "" + m_immediatePayment.isChecked();
		String barcode = m_textBarcode.getText().toString();
		String productName = m_textProductName.getText().toString();
		String code = m_customerCode.getText().toString();
	    String name = m_customerName.getText().toString();
		String purchasePrice = m_et_purchasePrice.getText().toString();
		String salePrice = m_et_salePrice.getText().toString();
		String amount = m_amount.getText().toString();
		String profitRatio = m_profitRatio.getText().toString();
		
		// 비어 있는 값 확인
	    if(code.equals("") || name.equals("") || purchaseDate.equals("") || barcode.equals("") ||
	    productName.equals("") || purchasePrice.equals("") || salePrice.equals("") || amount.equals("")
	    || profitRatio.equals("")) {
	    	Toast.makeText(getApplicationContext(), "값을 모두 입력해주세요.", 0).show();
	    	return;
	    }
	    
	    String period = m_period.getText().toString();
	    
        HashMap<String, String> rmap = new HashMap<String, String>();

    	rmap.put("In_Date", period);
        rmap.put("Office_Code", code);
        rmap.put("Office_Name", name);
        rmap.put("BarCode", barcode);
        rmap.put("G_Name", productName);
        rmap.put("Pur_Pri", purchasePrice);
        rmap.put("Sell_Pri", salePrice);
        rmap.put("In_Count", amount);
        rmap.put("Profit_Rate", profitRatio);
        
        m_purList.add(rmap);
        
        //ListView 에 뿌려줌
        mfillMaps = makeFillvapsWithStockList();
        
        m_adapter = new SimpleAdapter(this, mfillMaps, R.layout.activity_listview_item4, from, to);
        m_listReadyToSend.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();
	}
	

	public List<HashMap<String, String>> makeFillvapsWithStockList(){
		List<HashMap<String, String>> fm = new ArrayList<HashMap<String, String>>();
	    
		Iterator<HashMap<String, String>> iterator = m_purList.iterator();		
	    while (iterator.hasNext()) {
			boolean isNew = true;
			HashMap<String, String> element = iterator.next();
	         String Office_Code = element.get("Office_Code");
	        
	         Iterator<HashMap<String, String>> fm_iterator = fm.iterator();
	         while (fm_iterator.hasNext()) {

	        	 HashMap<String, String>  fm_element = fm_iterator.next();
		         String fm_Office_Code = fm_element.get("Office_Code");

		         if (fm_Office_Code.equals(Office_Code)) {
		        	 isNew = false;
		        	 // 같은게 있으면 fm_element에 추가 
			         String fm_Pur_Pri = fm_element.get("Pur_Pri");
			         String fm_In_Count = fm_element.get("In_Count");
			         String Pur_Pri = element.get("Pur_Pri");
			         String In_Count = element.get("In_Count");
			         
			         fm_element.put("Pur_Pri", String.valueOf(Double.valueOf(Pur_Pri) + Double.valueOf(fm_Pur_Pri)));
			         fm_element.put("In_Count",String.valueOf(Integer.valueOf(In_Count) + Integer.valueOf(fm_In_Count))   );
		         }
	         }
	         
	         if (isNew) {
		         String BarCode = element.get("BarCode");
		         String Office_Name = element.get("Office_Name");
		         String Pur_Pri = element.get("Pur_Pri");
		         String In_Count = element.get("In_Count");
		     	 HashMap<String, String> map = new HashMap<String, String>();

		         map.put("Office_Code", Office_Code);
		         map.put("BarCode", BarCode);
		         map.put("Office_Name", Office_Name);
		         map.put("Pur_Pri",Pur_Pri  );
		         map.put("In_Count", In_Count  );
		         
		         fm.add(map);
		    }
	    }
	    
		return fm;
	}
 	

	// MSSQL
	public void sendAllData(){
		
		String tableName = null, tableName2 = null;
		String period = m_period.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));

		tableName = String.format("InD_%04d%02d", year, month);
		tableName2 = String.format("InT_%04d%02d", year, month);
		Log.i("m_purList", String.valueOf(m_purList.size()));
		
		// TODO: 소트 데이터
		List<HashMap<String, String>> purList = new ArrayList<HashMap<String, String>>();
		for ( int i = 0; i < m_purList.size(); i++ ) {
		}
		 
	    makeJunPyo();
		 // 쿼리 작성하기
		String query =  "";
		for ( int i = 0; i < m_purList.size(); i++ ) {

		    query +=  "insert into " + tableName 
		    		+ "(In_Num, In_BarCode, In_YN, In_Gubun, In_Date, BarCode, In_Seq, Office_Code, Office_Name, "
		    		+ "In_Count, Pur_Pri, Sell_Pri, Profit_Rate, Write_Date, Edit_Date, Writer, Editor) " 
		    		+ " values ("
		    		+ "'" + m_junpyo + "', "
				    + "'" + m_junpyo + "', "
				    + "'1', "
				    + "'3', "
				    + "'" + m_purList.get(i).get("In_Date").toString() + "', "
		    		+ "'" + m_purList.get(i).get("BarCode").toString() + "', "
		    		+ "'" + i + "', "
		    		+ "'" + m_purList.get(i).get("Office_Code").toString() + "', "
		    		+ "'" + m_purList.get(i).get("Office_Name").toString() + "', "
				    + "'" + m_purList.get(i).get("In_Count").toString() + "', "
					+ "'" + m_purList.get(i).get("Pur_Pri").toString() + "', "
					+ "'" + m_purList.get(i).get("Sell_Pri").toString() + "', "
					+ "'" + m_purList.get(i).get("Profit_Rate").toString() + "', "
					+ "'" + period + "', "
					+ "'" + period + "', "
					+ "'" + m_id + "', "
		    		+ "'" + m_id + "');";
		}

	    query += " select * from " + tableName + " where In_Num='" + m_junpyo +"';";
	    Log.i("qeury", query);
	    
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();

				deleteListAll();
				clearInputBox();
				Toast.makeText(getApplicationContext(), "전송완료.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
		
	//전표갯수를 구함
	public void getSeq() {

		String tableName = null;
		String period = m_period.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		
		tableName = String.format("StD_%04d%02d", year, month);
		
		// 쿼리 작성하기
		String query =  "";
	    query += " select * from " + tableName + " ;";
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				m_junpyoIdx = results.length()+1;
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
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
					m_customerCode.setText(hashMap.get("Office_Code"));
					m_customerName.setText(hashMap.get("Office_Name"));
		        }
				break;
			}
	}
	
	public void onCustomerSearch(View view)
	{
		Intent intent = new Intent(PurchaseRegistActivity.this, ManageCustomerListActivity.class);
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
					Intent intent = new Intent(PurchaseRegistActivity.this, ManageProductListActivity.class);
			    	startActivityForResult(intent, BARCODE_MANAGER_REQUEST);
				} else { // 스캔할 경우
					Intent intent = new Intent(PurchaseRegistActivity.this, ZBarScannerActivity.class);
			    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);				
				}
			}
		}); 
		builder.show();
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
						m_textProductName.setText(results.getJSONObject(0).getString("G_Name"));						
						m_et_purchasePrice.setText(results.getJSONObject(0).getString("Pur_Pri"));
						m_et_salePrice.setText(results.getJSONObject(0).getString("Sell_Pri"));
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
