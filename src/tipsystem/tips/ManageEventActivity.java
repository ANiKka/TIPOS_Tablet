package tipsystem.tips;

import java.text.ParseException;
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

import tipsystem.utils.JsonHelper;
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
import android.widget.AdapterView;
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
	JSONObject m_userProfile;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;
	SimpleDateFormat m_timeFormatter;
	Date m_Finish_Date;
	
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	int m_dateMode=0;	
	int  m_junpyoInTIdx = 0;
	
	Spinner m_spinEvent;
	ListView m_listEvent;
	SimpleAdapter m_adapter; 

	EditText m_eventName ;
	EditText m_textBarcode;
	EditText m_textProductName;
	EditText m_et_purchasePrice;
	EditText m_et_salePrice;
	Button m_period1;
	Button m_period2;
	Button m_bt_barcodeSearch;
	Button m_bt_register;
	Button m_bt_modify;
	EditText m_evtPurValue;
	EditText m_evtSaleValue;
	
	int m_spinnerMode = 0;
	int m_selectedListIndex = -1;
	int m_newOrUpdate = 0;
	HashMap<String, String> m_updateData;
	String m_junpyo;
	
	List<HashMap<String, String>> m_evtList = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> tempMap =null;
	
	private ProgressDialog dialog;
	
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
		
        m_bt_register = (Button)findViewById(R.id.buttonRegist);
        m_bt_modify = (Button)findViewById(R.id.buttonModify);
		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
		Button btn_Delete = (Button)findViewById(R.id.buttonDelete);

		m_eventName = (EditText)findViewById(R.id.editTextEventName);
		m_textBarcode = (EditText)findViewById(R.id.editTextBarcode);
		m_textProductName = (EditText)findViewById(R.id.editTextProductName);
		m_et_purchasePrice = (EditText)findViewById(R.id.editTextPurchasePrice);
		m_et_salePrice = (EditText)findViewById(R.id.editTextSalePrice);
		m_bt_barcodeSearch = (Button)findViewById(R.id.buttonBarcode);

		m_evtPurValue = (EditText)findViewById(R.id.editTextAmount);
		m_evtSaleValue = (EditText)findViewById(R.id.editTextProfitRatio);
		
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

		String[] from = new String[] {"BarCode", "G_Name", "Sale_Pur", "Sale_Sell"};
	    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4  };

		m_adapter = new SimpleAdapter(this, m_evtList, R.layout.activity_listview_product_list, from, to);
        m_listEvent.setAdapter(m_adapter);
        
		m_bt_register.setOnClickListener(new OnClickListener() {
			public void onClick(View v){

				if (checkForm()==false) return;
				
		        Iterator<HashMap<String, String>> iterator = m_evtList.iterator();		
			    while (iterator.hasNext()) {
					HashMap<String, String> element = iterator.next();
			        String BarCode = element.get("BarCode");

			        if (BarCode.equals(m_textBarcode.getText().toString()) ) { //&& Evt_CD.equals(m_junpyo)
			        	Toast.makeText(getApplicationContext(), "같은 품목은 추가할수 없습니다", Toast.LENGTH_SHORT).show();	        	
			        	return;
			        }
			    }

				blockTopInputForm();

				int section = m_spinEvent.getSelectedItemPosition()+1;
				
				String barcode = m_textBarcode.getText().toString();
				checkEvent(barcode, m_period1.getText().toString(), m_period2.getText().toString(), section+"");
			 }
		});
		m_bt_modify.setOnClickListener(new OnClickListener() {
			public void onClick(View v){

				if (checkForm()==false) return;
				modifyDataIntoList(m_selectedListIndex);
				changeRegisterMode();
			 }
		});
		
		btn_Renew.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {						
				clearInputBox();
				changeRegisterMode();
			 }			
		});
		
		btn_Delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deleteData();				
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

		String Evt_CD = getIntent().getStringExtra("Evt_CD");
		if (Evt_CD.equals("")) {
			m_newOrUpdate = 0;
			getInTSeq();

		}
		else {
			m_newOrUpdate = 1;
			m_junpyo = Evt_CD;
			blockTopInputForm();
			fetchData(Evt_CD);
		}
		getFinishDate();
	}

	public boolean checkForm() {
		String eventName = m_eventName.getText().toString();
		String barcode = m_textBarcode.getText().toString();
		String productName = m_textProductName.getText().toString();
		String purchasePrice = m_et_purchasePrice.getText().toString();
		String salePrice = m_et_salePrice.getText().toString();
		String evtPur = m_evtPurValue.getText().toString();
		String evtSale = m_evtSaleValue.getText().toString();

		if (eventName.equals("") ||  barcode.equals("") || productName.equals("") || purchasePrice.equals("") || salePrice.equals("") || m_textProductName.equals("")|| evtPur.equals("") || evtSale.equals("")) {
			Toast.makeText(ManageEventActivity.this, "비어있는 필드가 있습니다.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public void deleteData() {
	
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

	    String Evt_CD = m_evtList.get(m_selectedListIndex).get("Evt_CD");
	    String BarCode = m_evtList.get(m_selectedListIndex).get("BarCode");
	    // 쿼리 작성하기
 		String query =  "";
 	    query += " Delete From Evt_Mst where Evt_CD='"+Evt_CD +"';";
 	    Log.i("qeury", query);
 		
 	    Date sd = makeTimeWithStringDateFormat(m_period1.getText().toString());
	    Date ed = makeTimeWithStringDateFormat(m_period2.getText().toString());

	    if((m_Finish_Date.after(sd) ||m_Finish_Date.equals(sd)) && (m_Finish_Date.before(ed) ||m_Finish_Date.equals(ed))) {

	        // In between
	    	query +=  "update  Goods set "
		    		+ "Sale_SDate='', "
		    		+ "Sale_EDate='', "
		    		+ "Sale_Use='0', "
				    + "Edit_Check='0', "
				    + "Tran_Chk='0' "
		    		+"where BarCode='" + BarCode +"';"
		    		;
	    }

 	    query += " Select * From Evt_Mst where Evt_CD='"+Evt_CD +"';";
 	    
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
 				if (results.length()>0) {
 					Toast.makeText(getApplicationContext(), "이미 행사중인 상품입니다", Toast.LENGTH_SHORT).show();
 				}
 				else {
 				    m_evtList.remove(m_selectedListIndex);
 				    m_adapter.notifyDataSetChanged();
 				    if (m_evtList.size()==0) {
 				    	finish();
 				    }
 				   clearInputBox () ;
 				}
 			}

 			@Override
 			public void onRequestFailed(int code, String msg) {
 				dialog.dismiss();
 				dialog.cancel();
 				Toast.makeText(getApplicationContext(), "실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
 			}
 			
 	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void deleteListAll() {
		if (m_evtList.isEmpty()) return;
		
		m_evtList.removeAll(m_evtList);
		m_adapter.notifyDataSetChanged();	
		m_selectedListIndex = -1;
		releaseTopInputForm();
	}	

	public void clearInputBox () {

		m_selectedListIndex = -1;
		m_textBarcode.setText("");
		m_textProductName.setText("");
		m_et_purchasePrice.setText("");
		m_et_salePrice.setText("");
		m_evtSaleValue.setText("");
		m_evtPurValue.setText(""); 
	}
	
	public void clearInputBoxAll () {
		clearInputBox();
		m_eventName.setText(""); 
	}
	
	public void changeModifyMode () {
		m_bt_register.setVisibility(View.GONE);
		m_bt_modify.setVisibility(View.VISIBLE);	
		m_textBarcode.setEnabled(false);
		m_bt_barcodeSearch.setEnabled(false);	
	}

	public void changeRegisterMode () {
		m_bt_register.setVisibility(View.VISIBLE);
		m_bt_modify.setVisibility(View.GONE);
		m_textBarcode.setEnabled(true);
		m_bt_barcodeSearch.setEnabled(true);
	}
	
	public void blockTopInputForm () {
		m_eventName.setEnabled(false);
		m_period1.setEnabled(false);
		m_period2.setEnabled(false);
		m_spinEvent.setEnabled(false);
		m_textBarcode.setEnabled(true);
		m_bt_barcodeSearch.setEnabled(true);
		m_et_purchasePrice.setEnabled(true);
		m_et_salePrice.setEnabled(true);
	}
	
	public void releaseTopInputForm () {
		m_eventName.setEnabled(true);
		m_textBarcode.setEnabled(true);
		m_period1.setEnabled(true);
		m_period2.setEnabled(true);
		m_spinEvent.setEnabled(true);
		m_bt_barcodeSearch.setEnabled(true);
		m_et_purchasePrice.setEnabled(true);
		m_et_salePrice.setEnabled(true);
	}

	private void fillInputFormFromSelectedItem (int position) {
		HashMap<String, String> event = m_evtList.get(position);

		String Evt_Name = event.get("Evt_Name");
		String Evt_Gubun = event.get("Evt_Gubun");
		String Evt_SDate = event.get("Evt_SDate");
		String Evt_EDate = event.get("Evt_EDate");
		
		m_eventName.setText(Evt_Name);
		if (Evt_Gubun.equals("기간행사")) m_spinEvent.setSelection(0);
		if (Evt_Gubun.equals("연속행사")) m_spinEvent.setSelection(1);
		
		m_period1.setText(Evt_SDate);
		m_period2.setText(Evt_EDate);

		m_textBarcode.setText(event.get("BarCode"));
		m_textProductName.setText(event.get("G_Name"));
		m_et_purchasePrice.setText(event.get("Pur_Pri"));
		m_et_salePrice.setText(event.get("Sell_Pri"));
		m_evtSaleValue.setText(event.get("Sale_Sell"));
		m_evtPurValue.setText(event.get("Sale_Pur")); 
	}

	public String makeJunPyo () {

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
        
        String junpyo = currentDate + posID + String.format("%03d", m_junpyoInTIdx);
        return junpyo;        
	}

    public void modifyDataIntoList(int position) {
    	
        String Sale_Pur= m_evtPurValue.getText().toString();
        String Sale_Sell= m_evtSaleValue.getText().toString();
        String barcode= m_textBarcode.getText().toString();
        
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(new Date());

	    JSONObject userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
	    String userid ="";
        try {
        	userid = userProfile.getString("User_ID");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        String query="";
	    query +=  "update Evt_Mst set Sale_Pur= '" + Sale_Pur + "', "
	    		+ "Sale_Sell='" + Sale_Sell + "', "
	    		+ "Write_Date='" + currentDate + "', "
	    		+ "Edit_Date='" + currentDate + "', "
	    		+ "Writer='" + userid + "', "
	    		+ "Editor='" + userid + "' "
	    		+"where  Evt_CD='" + m_junpyo + "' AND BarCode='" + barcode +"';"
	    		;

	    Date sd = makeTimeWithStringDateFormat(m_period1.getText().toString());
	    Date ed = makeTimeWithStringDateFormat(m_period2.getText().toString());

	    if((m_Finish_Date.after(sd) ||m_Finish_Date.equals(sd)) && (m_Finish_Date.before(ed) ||m_Finish_Date.equals(ed))) {
	    	query +=  "update  Goods set Sale_Pur= '" + Sale_Pur + "', "
		    		+ "Sale_Sell='" + Sale_Sell + "', "
		    		+ "Sale_SDate='" + currentDate + "', "
		    		+ "Sale_EDate='" + currentDate + "', "
				    + "Sale_Use='1', "
				    + "Edit_Check='1', "
				    + "Tran_Chk='1' "
		    		+"where BarCode='" + barcode +"';"
		    		;
	    }
	    
	    query += " select * from Evt_Mst where Evt_CD='" + m_junpyo +"';";
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
				
				HashMap<String, String> event = m_evtList.get(m_selectedListIndex);
		    	event.put("BarCode", m_textBarcode.getText().toString());
		    	event.put("G_Name", m_textProductName.getText().toString());
		        event.put("Pur_Pri", m_et_purchasePrice.getText().toString());
		        event.put("Sell_Pri", m_et_salePrice.getText().toString());
		        event.put("Sale_Pur", m_evtPurValue.getText().toString());
		        event.put("Sale_Sell", m_evtSaleValue.getText().toString());

		        m_adapter.notifyDataSetChanged();  
		        
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
    
    public void setDataIntoList() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(new Date());
				
    	String Name = m_eventName.getText().toString();
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();    
		String section = m_spinEvent.getSelectedItem().toString();
		String Pur_Pri = m_et_purchasePrice.getText().toString();
		String Sell_Pri = m_et_salePrice.getText().toString();
		String Sale_Pur = m_evtPurValue.getText().toString();
		String Sale_Sell = m_evtSaleValue.getText().toString();
		
        if ( section.equals("기간행사") == true) section ="1";
        else section ="2";

        double DC_Pri = Double.valueOf(Sell_Pri)-Double.valueOf(Sale_Sell);
        double DC_Rate = DC_Pri/Double.valueOf(DC_Pri)*100;
        double Profit_Pri = Double.valueOf(Sale_Sell)-Double.valueOf(Sale_Pur);
        double Profit_Rate = Profit_Pri/Double.valueOf(Sale_Sell)*100;
        
        tempMap = new HashMap<String, String>();
        //rmap.put("Evt_CD", m_junpyo);
        tempMap.put("Evt_Name", Name);
        tempMap.put("Evt_Gubun", section);
        tempMap.put("BarCode", m_textBarcode.getText().toString());
        tempMap.put("G_Name", m_textProductName.getText().toString());
        tempMap.put("Evt_SDate", period1);
        tempMap.put("Evt_EDate", period2);            
        tempMap.put("Evt_STime", "00:00");
        tempMap.put("Evt_ETime", "24:00");
        tempMap.put("Sale_Pur", Sale_Pur);
        tempMap.put("Sale_Sell", Sale_Sell);
        tempMap.put("Pur_Pri", Pur_Pri);
        tempMap.put("Sell_Pri", Sell_Pri);
        tempMap.put("DC_Pri", String.valueOf(DC_Pri));  
        tempMap.put("DC_Rate", String.valueOf(DC_Rate));  
        tempMap.put("Profit_Rate", String.valueOf(Profit_Rate));  
        tempMap.put("Profit_Pri", String.valueOf(Profit_Pri));  

        sendData(m_evtList.size()-1);
    }
    
	public List<HashMap<String, String>> makeFillvapsWithStockList(){
		List<HashMap<String, String>> fm = new ArrayList<HashMap<String, String>>();
	    
		Iterator<HashMap<String, String>> iterator = m_evtList.iterator();		
	    while (iterator.hasNext()) {
			boolean isNew = true;
			HashMap<String, String> element = iterator.next();
	         String Evt_CD = element.get("Evt_CD");
	         String Evt_Name = element.get("Evt_Name");
	        
	         Iterator<HashMap<String, String>> fm_iterator = fm.iterator();
	         while (fm_iterator.hasNext()) {

	        	 HashMap<String, String>  fm_element = fm_iterator.next();
		         String fm_Evt_CD = fm_element.get("Evt_CD");

		         if (fm_Evt_CD.equals(Evt_CD)) {
		        	 isNew = false;
		         }
	         }
	         
	         if (isNew) {
		         String Evt_Gubun = element.get("Evt_Gubun");
		         String Evt_SDate = element.get("Evt_SDate");
		         String Evt_EDate = element.get("Evt_EDate");
		     	 HashMap<String, String> map = new HashMap<String, String>();

		         map.put("Evt_CD", Evt_CD);
		         map.put("Evt_Name", Evt_Name);
		         map.put("Evt_Gubun", Evt_Gubun);
		         map.put("Evt_Date", Evt_SDate + " ~ " + Evt_EDate  );
		         
		         fm.add(map);
		    }
	    }
	    
		return fm;
	}

	public void checkEvent(String barcode, String sdate, String edate, String Evt_Gubun) {
		
		// 쿼리 작성하기
		String query =  "";
	    query += " select * from Evt_Mst where BarCode='" + barcode +"' "
	    		+" AND Evt_Gubun<> '"+Evt_Gubun+"' "
	    		+" AND Evt_SDate <= '"+edate+"' "
	    	    +" AND Evt_EDate >= '"+sdate+"' ;";
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
				if (results.length()>0) {
					Toast.makeText(getApplicationContext(), "이미 행사중인 상품입니다", Toast.LENGTH_SHORT).show();
				}
				else {
					setDataIntoList();
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void sendData(int position) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(new Date());

	    JSONObject userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
	    String userid ="";
        try {
        	userid = userProfile.getString("User_ID");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        String query="";
		HashMap<String, String> event = tempMap;
	    query +=  "insert into  Evt_Mst (Evt_CD, Evt_Name, Evt_Gubun, BarCode, Evt_SDate, Evt_EDate, Sale_Pur, Sale_Sell, "
	    		+ " DC_Pri, DC_Rate, Profit_Rate, Profit_Pri, Write_Date, Edit_Date, Writer, Editor) " 
	    		+ " values (" 	+ "'" + m_junpyo + "', "
							    + "'" + event.get("Evt_Name").toString() + "', "
					    		+ "'" + event.get("Evt_Gubun").toString() + "', "
					    		+ "'" + event.get("BarCode").toString() + "', "
					    		+ "'" + event.get("Evt_SDate").toString() + "', "
					    		+ "'" + event.get("Evt_EDate").toString() + "', "
							    + "'" + event.get("Sale_Pur").toString() + "', "
					    		+ "'" + event.get("Sale_Sell").toString() + "', "
							    + "'" + event.get("DC_Pri").toString() + "', "								    
							    + "'" + event.get("DC_Rate").toString() + "', "
							    + "'" + event.get("Profit_Rate").toString() + "', "
							    + "'" + event.get("Profit_Pri").toString() + "', "								    
					    		+ "'" + currentDate + "', "
					    		+ "'" + currentDate+ "', "
							    + "'" + userid + "', "
					    		+ "'" + userid + "');";

	    Date sd = makeTimeWithStringDateFormat(m_period1.getText().toString());
	    Date ed = makeTimeWithStringDateFormat(m_period2.getText().toString());

	    if((m_Finish_Date.after(sd) ||m_Finish_Date.equals(sd)) && (m_Finish_Date.before(ed) ||m_Finish_Date.equals(ed))) {
	    	
	        // In between
	    	query +=  "update  Goods set Sale_Pur= '" + event.get("Sale_Pur").toString() + "', "
		    		+ "Sale_Sell='" + event.get("Sale_Sell").toString() + "', "
		    		+ "Sale_SDate='" + event.get("Evt_SDate").toString() + "', "
		    		+ "Sale_EDate='" + event.get("Evt_EDate").toString() + "', "
    			    + "Sale_Use='1', "
    			    + "Edit_Check='1', "
    			    + "Tran_Chk='1' "
		    		+"where BarCode='" + event.get("BarCode").toString() +"';"
		    		;
	    }
	    
	    query += " select * from Evt_Mst where Evt_CD='" + m_junpyo +"';";
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

		        m_evtList.add(tempMap);
		        m_adapter.notifyDataSetChanged();    
		        
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
	
	public void fetchData(String junpyo){

		// 쿼리 작성하기
		String query =  "";
	    query += " select * from Evt_Mst inner join Goods on Evt_Mst.BarCode = Goods.BarCode where Evt_CD='" + junpyo +"';";
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
				if (results.length() > 0) {
					for (int i = 0; i < results.length(); i++) {
			        	
			        	try {
			            	JSONObject json = results.getJSONObject(i);
			            	HashMap<String, String> map = JsonHelper.toStringHashMap(json);

			            	String Evt_Gubun = map.get("Evt_Gubun");
			            	String Sdate = map.get("Evt_SDate");
			            	String Edate = map.get("Evt_EDate");
			            	m_period1.setText(Sdate);
			            	m_period2.setText(Edate);
			            	m_eventName.setText(map.get("Evt_Name"));

			            	if (Evt_Gubun.equals("1")) m_spinEvent.setSelection(0);
			            	else m_spinEvent.setSelection(1);
			            	
			                m_evtList.add(map);			                
			                m_adapter.notifyDataSetChanged();  
					 
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					finish();
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "전송실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
				finish();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	//전표갯수를 구함
	public void getInTSeq() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(new Date());

		// 쿼리 작성하기
		String query =  "";
	    query = "SELECT Right(Max(Evt_CD), 3) As Evt_CD From Evt_Mst Where Write_date ='"+currentDate+"';";
	    		
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
				if (results.length()>0) {
					try {
						m_junpyoInTIdx = results.getJSONObject(0).getInt("Evt_CD")+1;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					m_junpyoInTIdx =1;
				}			

				//TODO: 새로운 전표생성
				m_junpyo = makeJunPyo();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	public void getFinishDate() {

		// 쿼리 작성하기
		String query =  "";
	    query += " Select Finish_Date from finish;";
	    		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				if (results.length()>0) {
					try {
						String Finish_Date = results.getJSONObject(0).getString("Finish_Date");
						Calendar cal = Calendar.getInstance();
						cal.setTime(makeTimeWithStringDateFormat(Finish_Date));
						cal.add(Calendar.DATE, 1);  // number of days to add
						m_Finish_Date = cal.getTime();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					m_junpyoInTIdx =1;
				}			

				//TODO: 새로운 전표생성
				m_junpyo = makeJunPyo();
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				Toast.makeText(getApplicationContext(), "실패("+String.valueOf(code)+"):" + msg, Toast.LENGTH_SHORT).show();
			}
			
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}

	public Date makeTimeWithStringDateFormat(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTime();
	}

	public void OnClickModify(View v) {
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();      
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
		if ( m_spinnerMode == 0 ) {
			DatePickerDialog newDlg = new DatePickerDialog(this, this, 
					m_dateCalender2.get(Calendar.YEAR),
					m_dateCalender2.get(Calendar.MONTH),
					m_dateCalender2.get(Calendar.DAY_OF_MONTH));
			
			newDlg.show();
		}
		else if ( m_spinnerMode == 1 ) {
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
		
	public void startDetailView (int position) {

		Intent intent = new Intent(ManageEventActivity.this, EventDetailViewActivity.class);
		
		String period = null;
		
		if ( m_evtList.get(position).get("Evt_Gubun").toString().equals("기간행사") == true) {
        	period = m_evtList.get(position).get("Evt_SDate").toString() + " ~ "
        			+ m_evtList.get(position).get("Evt_EDate").toString();
        }
        else  {
        	period = m_evtList.get(position).get("Evt_STime").toString() + " ~ "
        			+ m_evtList.get(position).get("Evt_ETime").toString();
        }
		
		String Evt_Name = m_evtList.get(position).get("Evt_Name");
		String Evt_Gubun = m_evtList.get(position).get("Evt_Gubun");
		String Evt_Date = m_evtList.get(position).get("Evt_Date");
		Iterator<HashMap<String, String>> iterator = m_evtList.iterator();	
		
		JSONArray array = new JSONArray();
		
	    while (iterator.hasNext()) {
			 HashMap<String, String> element = iterator.next();
	         String eEvt_Name = element.get("Evt_Name");
	         
	         if (eEvt_Name.equals(Evt_Name)) {
	        	 JSONObject object = new JSONObject(element);
	        	 array.put(object);
	         }
	    }

		intent.putExtra("data", array.toString());
		intent.putExtra("Evt_Name", Evt_Name);
		intent.putExtra("Evt_Gubun", Evt_Gubun);
		intent.putExtra("Evt_Period", Evt_Date);
		
		intent.putExtra("BarCode", m_evtList.get(position).get("BarCode").toString());
		intent.putExtra("G_Name", m_evtList.get(position).get("G_Name").toString());
		intent.putExtra("Sale_Pur", m_evtList.get(position).get("Sale_Pur").toString());
		intent.putExtra("Sale_Sell", m_evtList.get(position).get("Sale_Sell").toString());
		
		startActivity(intent);
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
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		
		return false;
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			fillInputFormFromSelectedItem(position);
			changeModifyMode();	
			m_selectedListIndex = position;
		}
	};

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
					
					HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("fillmaps");        	
					m_textBarcode.setText(hashMap.get("BarCode"));
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
					//새로등록
			        DialogInterface.OnClickListener newBarcodeListener = new DialogInterface.OnClickListener(){
			              @Override
			              public void onClick(DialogInterface dialog, int which){

						    String barcode = m_textBarcode.getText().toString();
						    	
			          		Intent intent = new Intent(ManageEventActivity.this, ManageProductActivity.class);
			          		intent.putExtra("barcode", barcode);
			              	startActivity(intent);
			              }
			        };
			        
			        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
			              @Override
			              public void onClick(DialogInterface dialog, int which){
			                dialog.dismiss();
			              }
			        };
			        
			        new AlertDialog.Builder(ManageEventActivity.this)
			      .setTitle("등록되지않은 바코드입니다")
			      .setNeutralButton("새로등록", newBarcodeListener)
			      .setNegativeButton("취소", cancelListener)
			      .show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}

}
