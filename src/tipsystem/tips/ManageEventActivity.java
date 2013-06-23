package tipsystem.tips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ManageEventActivity extends Activity implements OnItemSelectedListener, OnDateSetListener, 
															OnTimeSetListener,
															OnItemLongClickListener{

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;
	SimpleDateFormat m_timeFormatter;
	
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	int m_dateMode=0;	
	
	Spinner m_spinEvent;
	ListView m_listEvent;
	SimpleAdapter adapter; 

	EditText m_eventName ;
	EditText m_textBarcode;
	EditText m_textProductName;
	EditText m_et_purchasePrice;
	EditText m_et_salePrice;
	Button m_period1;
	Button m_period2;
	Button m_bt_barcodeSearch;
	
	int m_spinnerMode = 0;
	int m_selectedListIndex = -1;
	
	List<HashMap<String, String>> m_evtList = null;
	
	String[] from = new String[] {"Evt_Name", "Evt_Gubun", "Evt_Date"};
    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

	private ProgressDialog dialog;
	
	String m_junpyo ="";
	int  m_junpyoIdx = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_event);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		Button btn_Register = (Button)findViewById(R.id.buttonRegist);
		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
//		Button btn_Modify = (Button)findViewById(R.id.buttonModify);
		
		Button btn_Send = (Button)findViewById(R.id.buttonSend);
		Button btn_Delete = (Button)findViewById(R.id.buttonDelete);
		Button btn_SendAll = (Button)findViewById(R.id.buttonSendAll);

		m_eventName = (EditText)findViewById(R.id.editTextEventName);
		m_textBarcode = (EditText)findViewById(R.id.editTextBarcode);
		m_textProductName = (EditText)findViewById(R.id.editTextProductName);
		m_et_purchasePrice = (EditText)findViewById(R.id.editTextPurchasePrice);
		m_et_salePrice = (EditText)findViewById(R.id.editTextSalePrice);
		m_bt_barcodeSearch = (Button)findViewById(R.id.buttonBarcode);
		
		m_period1 = (Button) findViewById(R.id.buttonSetDate1); 
		m_period2 = (Button) findViewById(R.id.buttonSetDate2);
		
		m_spinEvent = (Spinner)findViewById(R.id.spinnerEventType);
		m_spinEvent.setOnItemSelectedListener(this);
		m_listEvent = (ListView)findViewById(R.id.listviewReadyToSendEventList); 
		m_listEvent.setOnItemClickListener(mItemClickListener);
		m_listEvent.setOnItemLongClickListener(this);
		m_listEvent.setFocusableInTouchMode(true);
		m_listEvent.setFocusable(true);
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		m_timeFormatter = new SimpleDateFormat("HH:MM", Locale.KOREA);
		
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
				
		m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		
		m_evtList = new ArrayList<HashMap<String, String>>();

		
		btn_Register.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){

				String eventName = m_eventName.getText().toString();
				String barcode = m_textBarcode.getText().toString();
				String productName = m_textProductName.getText().toString();
				String purchasePrice = m_et_purchasePrice.getText().toString();
				String salePrice = m_et_salePrice.getText().toString();

				if (eventName.equals("") ||  barcode.equals("") || productName.equals("") || purchasePrice.equals("") || salePrice.equals("") || m_textProductName.equals("")) {
					Toast.makeText(ManageEventActivity.this, "비어있는 필드가 있습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				setDataIntoList();
				m_selectedListIndex = -1;
				changeInputMode(1);
			 }			
		});
		
		btn_Renew.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
			
				EditText eventName = (EditText)findViewById(R.id.editTextEventName);
				
				TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
				TextView productName = (TextView)findViewById(R.id.editTextProductName);
				TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
				TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
				TextView evtPurValue = (TextView)findViewById(R.id.editTextAmount);
				TextView evtSaleValue = (TextView)findViewById(R.id.editTextProfitRatio);
				
				eventName.setText("");
				barcode.setText("");
				productName.setText("");
				purchasePrice.setText("");
				salePrice.setText("");
				evtPurValue.setText("");
				evtSaleValue.setText("");

				m_selectedListIndex = -1;
				changeInputMode(0);
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
				// 전체 삭제 기능으로 대체 
				// TODO: 아이콘 바꾸어야함
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
		
		changeInputMode(0);
		getSeq();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{    
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			// 카메라 스캔을 통한 바코드 검색
			case 0 :
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
			case 1 :
				if(resultCode == RESULT_OK && data != null) {
					
		        	ArrayList<String> fillMaps = data.getStringArrayListExtra("fillmaps");		        	
		        	m_textBarcode.setText(fillMaps.get(0));
					doQueryWithBarcode(); 
		        }
				break;
			}
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
					Intent intent = new Intent(ManageEventActivity.this, ManageProductListActivity.class);
			    	startActivityForResult(intent, 1);
				} else { // 스캔할 경우
					Intent intent = new Intent(ManageEventActivity.this, ZBarScannerActivity.class);
			    	startActivityForResult(intent, ZBAR_SCANNER_REQUEST);				
				}
			}
		}); 
		builder.show();
	}
	
	public void deleteData(){
	
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		fillMaps.remove(m_selectedListIndex);
		adapter.notifyDataSetChanged();
		m_evtList.remove(m_selectedListIndex);
		m_selectedListIndex = -1;
	}
	
	public void deleteListAll() {
		if (fillMaps.isEmpty()) return;
		
		fillMaps.removeAll(fillMaps);
		adapter.notifyDataSetChanged();		
		m_evtList.removeAll(m_evtList);		
		m_selectedListIndex = -1;
	}	

	public void clearInputBox () {

		m_textBarcode.setText("");
		m_textProductName.setText("");
		m_et_purchasePrice.setText("");
		m_et_salePrice.setText("");
	}
	
	public void changeInputMode (int mode) {
		//m_inputMode = mode;
		if (mode==0) {
			m_eventName.setEnabled(true);
			m_textBarcode.setEnabled(true);
			m_textProductName.setEnabled(true);
			m_period1.setEnabled(true);
			m_period2.setEnabled(true);
			m_spinEvent.setEnabled(true);
			m_bt_barcodeSearch.setEnabled(true);
			m_et_purchasePrice.setEnabled(true);
			m_et_salePrice.setEnabled(true);
		}
		else {
			m_eventName.setEnabled(false);
			m_period1.setEnabled(false);
			m_period2.setEnabled(false);
			m_spinEvent.setEnabled(false);
			m_textBarcode.setEnabled(true);
			m_textProductName.setEnabled(true);
			m_bt_barcodeSearch.setEnabled(true);
			m_et_purchasePrice.setEnabled(true);
			m_et_salePrice.setEnabled(true);
		}
	}

	public void makeJunPyo () {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(new Date());
		
        // 전표번호 생성 
		String posID = "P";
        try {
			String OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			posID = LocalStorage.getString(this, "currentPosID:"+OFFICE_CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        m_junpyo = currentDate + posID + String.format("%04d", m_junpyoIdx);
        m_junpyoIdx++;
	}

    public void setDataIntoList(){
    
		EditText eventName = (EditText)findViewById(R.id.editTextEventName);
		
		TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
		TextView productName = (TextView)findViewById(R.id.editTextProductName);
		TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
		TextView evtPurValue = (TextView)findViewById(R.id.editTextAmount);
		TextView evtSaleValue = (TextView)findViewById(R.id.editTextProfitRatio);
		
    	String Name = eventName.getText().toString();
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();    
		String section = m_spinEvent.getSelectedItem().toString();
		
        HashMap<String, String> rmap = new HashMap<String, String>();
        rmap.put("Evt_Name", Name);
        rmap.put("Evt_Gubun", section);
        
        if ( section.equals("기간행사") == true) {
        	rmap.put("Evt_SDate", period1);
            rmap.put("Evt_EDate", period2);
            
            rmap.put("Evt_STime", "00:00");
            rmap.put("Evt_ETime", "24:00");
        }
        else {
        	Calendar date = Calendar.getInstance();
        	
        	rmap.put("Evt_SDate", m_dateFormatter.format(date.getTime()));
            rmap.put("Evt_EDate",  m_dateFormatter.format(date.getTime()));
            
            rmap.put("Evt_STime", period1);
            rmap.put("Evt_ETime", period2);
        }

        rmap.put("BarCode", barcode.getText().toString());
        rmap.put("G_Name", productName.getText().toString());
        rmap.put("Pur_Pri", purchasePrice.getText().toString());
        rmap.put("Sell_Pri", salePrice.getText().toString());
        rmap.put("Sale_Pur", evtPurValue.getText().toString());
        rmap.put("Sale_Sell", evtSaleValue.getText().toString());
        
        m_evtList.add(rmap);
        
        // Into ListView
    	HashMap<String, String> map = new HashMap<String, String>();
        map.put("Evt_Name", Name);
        map.put("Evt_Gubun", section);
        map.put("Evt_Date", period1 + " ~ " + period2  );
        fillMaps.add(map);            
        fillMaps = makeFillvapsWithStockList();
        adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3,	from, to);
        m_listEvent.setAdapter(adapter);
        adapter.notifyDataSetChanged();    
    }
    
	public List<HashMap<String, String>> makeFillvapsWithStockList(){
		List<HashMap<String, String>> fm = new ArrayList<HashMap<String, String>>();
	    
		Iterator<HashMap<String, String>> iterator = m_evtList.iterator();		
	    while (iterator.hasNext()) {
			boolean isNew = true;
			HashMap<String, String> element = iterator.next();
	         String Evt_Name = element.get("Evt_Name");
	        
	         Iterator<HashMap<String, String>> fm_iterator = fm.iterator();
	         while (fm_iterator.hasNext()) {

	        	 HashMap<String, String>  fm_element = fm_iterator.next();
		         String fm_Evt_Name = fm_element.get("Evt_Name");

		         if (fm_Evt_Name.equals(Evt_Name)) {
		        	 isNew = false;
		        	 // 같은게 있으면 fm_element에 추가 
			         String Evt_Gubun = fm_element.get("Evt_Gubun");
			         String Evt_SDate = fm_element.get("Evt_SDate");
			         String Evt_EDate = fm_element.get("Evt_EDate");
		         }
	         }
	         
	         if (isNew) {
		         String Evt_Gubun = element.get("Evt_Gubun");
		         String Evt_SDate = element.get("Evt_SDate");
		         String Evt_EDate = element.get("Evt_EDate");
		     	 HashMap<String, String> map = new HashMap<String, String>();
		         
		         map.put("Evt_Name", Evt_Name);
		         map.put("Evt_Gubun", Evt_Gubun);
		         map.put("Evt_Date", Evt_SDate + " ~ " + Evt_EDate  );
		         
		         fm.add(map);
		    }
	    }
	    
		return fm;
	}
	

	// MSSQL
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

	public void sendAllData(){
		
		String tableName = null;
		String period = m_period1.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		
		tableName = String.format("StD_%04d%02d", year, month);

		// 쿼리 작성하기
		String query =  "";
		for ( int i = 0; i < m_evtList.size(); i++ ) {
			//TODO: 새로운 전표생성
	        makeJunPyo();
	        
		    query +=  "insert into " + tableName + "(Evt_CD, Evt_Name, Evt_Gubun, BarCode, Evt_SDate, Evt_EDate, Sale_Pur, Sale_Sell) " 
		    		+ " values ("
		    		+ "'" + m_junpyo + "', "
				    + "'" + m_evtList.get(i).get("Evt_Name").toString() + "', "
		    		+ "'" + m_evtList.get(i).get("Evt_Gubun").toString() + "', "
		    		+ "'" + m_evtList.get(i).get("BarCode").toString() + "', "
		    		+ "'" + m_evtList.get(i).get("Evt_SDate").toString() + "', "
		    		+ "'" + m_evtList.get(i).get("Evt_EDate").toString() + "', "
				    + "'" + m_evtList.get(i).get("Sale_Pur").toString() + "', "
		    		+ "'" + m_evtList.get(i).get("Sale_Sell").toString() + "');";
		}

	    query = " select * from " + tableName + " where St_Num='" + m_junpyo +"';";
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
			
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
	}
	
	
	//전표갯수를 구함
	public void getSeq(){

		String tableName = null;
		String period = m_period1.getText().toString();
		
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
			
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
	}

	public void OnClickModify(View v) {
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		EditText eventName = (EditText)findViewById(R.id.editTextEventName);
		
		TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
		TextView productName = (TextView)findViewById(R.id.editTextProductName);
		TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
		TextView evtPurValue = (TextView)findViewById(R.id.editTextAmount);
		TextView evtSaleValue = (TextView)findViewById(R.id.editTextProfitRatio);
		
    	String Name = eventName.getText().toString();
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();    
		String section = m_spinEvent.getSelectedItem().toString();
		
		HashMap<String, String> map = fillMaps.get(m_selectedListIndex);
		
		map.put("Evt_Name", Name);
        map.put("Evt_Gubun", section);
        map.put("Evt_Date", period1 + " ~ " + period2  );
        
        adapter.notifyDataSetChanged();
        
        HashMap<String, String> rmap = m_evtList.get(m_selectedListIndex);
        
        rmap.put("Evt_Name", Name);
        rmap.put("Evt_Gubun", section);
        
        if ( section.equals("기간행사")==true) {
        	rmap.put("Evt_SDate", period1);
            rmap.put("Evt_EDate", period2);
            
            rmap.put("Evt_STime", "00:00");
            rmap.put("Evt_ETime", "24:00");
        }
        else {
        	Calendar date = Calendar.getInstance();
        	rmap.put("Evt_SDate", m_dateFormatter.format(date.getTime()));
            rmap.put("Evt_EDate",  m_dateFormatter.format(date.getTime()));
            
            rmap.put("Evt_STime", period1);
            rmap.put("Evt_ETime", period2);
        }

        rmap.put("BarCode", barcode.getText().toString());
        rmap.put("G_Name", productName.getText().toString());
        rmap.put("Pur_Pri", purchasePrice.getText().toString());
        rmap.put("Sell_Pri", salePrice.getText().toString());
        rmap.put("Sale_Pur", evtPurValue.getText().toString());
        rmap.put("Sale_Sell", evtSaleValue.getText().toString());
     
        m_selectedListIndex = -1;
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
		actionbar.setTitle("행사관리");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_event, menu);
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

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
	{
		//TextView text1 = (TextView)m_spin.getSelectedView();
		//m_text.setText(text1.getText());
		
		TextView evtTitle = (TextView)findViewById(R.id.textViewEventTypeTitle);
		
		if ( m_spinEvent.getSelectedItem().toString().equals("기간행사") == true )
		{
			m_spinnerMode = 0;
			
			evtTitle.setText("행사기간");
			m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
			m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		}
		else if ( m_spinEvent.getSelectedItem().toString().equals("시간행사") == true )
		{
			m_spinnerMode = 1;
			
			evtTitle.setText("행사시간");
			m_period1.setText(m_timeFormatter.format(m_dateCalender1.getTime()));
			m_period2.setText(m_timeFormatter.format(m_dateCalender2.getTime()));
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent)
	{
	}

	public void onClickSetDate1(View v) {
		if ( m_spinnerMode == 0 )
		{
			DatePickerDialog newDlg = new DatePickerDialog(this, this,
					m_dateCalender1.get(Calendar.YEAR),
					m_dateCalender1.get(Calendar.MONTH),
					m_dateCalender1.get(Calendar.DAY_OF_MONTH));
			 newDlg.show();
		}
		else if ( m_spinnerMode == 1 ) 
		{
			TimePickerDialog newDlg = new TimePickerDialog(this, this,
					m_dateCalender1.get(Calendar.HOUR_OF_DAY),
					m_dateCalender1.get(Calendar.MINUTE), true);
			newDlg.show();
		}
		 m_dateMode = 1;
	};
	
	public void onClickSetDate2(View v) {
		if ( m_spinnerMode == 0 )
		{
			DatePickerDialog newDlg = new DatePickerDialog(this, this, 
					m_dateCalender2.get(Calendar.YEAR),
					m_dateCalender2.get(Calendar.MONTH),
					m_dateCalender2.get(Calendar.DAY_OF_MONTH));
			
			newDlg.show();
		}
		else if ( m_spinnerMode == 1 ) 
		{
			TimePickerDialog newDlg = new TimePickerDialog(this, this,
					m_dateCalender2.get(Calendar.HOUR_OF_DAY),
					m_dateCalender2.get(Calendar.MINUTE), true);
			newDlg.show();
		}
		
		m_dateMode = 2;
	};
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		
		if ( m_dateMode == 1 )
		{
			m_dateCalender1.set(year, monthOfYear, dayOfMonth);
			m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		}
		else if ( m_dateMode == 2 )
		{
			m_dateCalender2.set(year, monthOfYear, dayOfMonth);
			m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
		}
		
		m_dateMode = 0;
		
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		if ( m_dateMode == 1 )
		{
			m_dateCalender1.set(Calendar.HOUR_OF_DAY, hourOfDay);
			m_dateCalender1.set(Calendar.MINUTE, minute);
			m_period1.setText(m_timeFormatter.format(m_dateCalender1.getTime()));
		}
		else if ( m_dateMode == 2 )
		{
			m_dateCalender1.set(Calendar.HOUR_OF_DAY, hourOfDay);
			m_dateCalender1.set(Calendar.MINUTE, minute);
			m_period2.setText(m_timeFormatter.format(m_dateCalender2.getTime()));
		}
		
		m_dateMode = 0;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(ManageEventActivity.this, EventDetailViewActivity.class);
		
		String period = null;
		
		if ( m_evtList.get(arg2).get("Evt_Gubun").toString().equals("기간행사") == true)
        {
        	period = m_evtList.get(arg2).get("Evt_SDate").toString() + " ~ "
        			+ m_evtList.get(arg2).get("Evt_EDate").toString();
        }
        else 
        {
        	period = m_evtList.get(arg2).get("Evt_STime").toString() + " ~ "
        			+ m_evtList.get(arg2).get("Evt_ETime").toString();
        }
		
		intent.putExtra("Evt_Name", m_evtList.get(arg2).get("Evt_Name").toString());
		intent.putExtra("Evt_Gubun", m_evtList.get(arg2).get("Evt_Gubun").toString());
		intent.putExtra("Evt_Period", period);
		
		intent.putExtra("BarCode", m_evtList.get(arg2).get("BarCode").toString());
		intent.putExtra("G_Name", m_evtList.get(arg2).get("G_Name").toString());
		intent.putExtra("Sale_Pur", m_evtList.get(arg2).get("Sale_Pur").toString());
		intent.putExtra("Sale_Sell", m_evtList.get(arg2).get("Sale_Sell").toString());
		
		startActivity(intent);
		
		return false;
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			m_selectedListIndex = arg2;
			
			EditText eventName = (EditText)findViewById(R.id.editTextEventName);
			
			TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
			TextView productName = (TextView)findViewById(R.id.editTextProductName);
			TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
			TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
			TextView evtPurValue = (TextView)findViewById(R.id.editTextAmount);
			TextView evtSaleValue = (TextView)findViewById(R.id.editTextProfitRatio);
			
//	    	String Name = eventName.getText().toString();
//			String period1 = m_period1.getText().toString();
//			String period2 = m_period2.getText().toString();    
//			String section = m_spinEvent.getSelectedItem().toString();
			
	        eventName.setText(m_evtList.get(arg2).get("Evt_Name").toString());
	        
	        if ( m_evtList.get(arg2).get("Evt_Gubun").toString().equals("기간행사") == true)
	        {
	        	m_spinEvent.setSelection(0);
	        	
	        	m_period1.setText(m_evtList.get(arg2).get("Evt_SDate").toString());
	        	m_period2.setText(m_evtList.get(arg2).get("Evt_EDate").toString());
	        }
	        else 
	        {
	        	m_spinEvent.setSelection(1);
	        	m_period1.setText(m_evtList.get(arg2).get("Evt_STime").toString());
	        	m_period2.setText(m_evtList.get(arg2).get("Evt_ETime").toString());
	        }
	        
	        barcode.setText(m_evtList.get(arg2).get("BarCode").toString());
	        productName.setText(m_evtList.get(arg2).get("G_Name").toString());
	        purchasePrice.setText(m_evtList.get(arg2).get("Pur_Pri").toString());
	        salePrice.setText(m_evtList.get(arg2).get("Sell_Pri").toString());
	        evtPurValue.setText(m_evtList.get(arg2).get("Sale_Pur").toString());
	        evtSaleValue.setText(m_evtList.get(arg2).get("Sale_Sell").toString());
	                 
		}
	};
}
