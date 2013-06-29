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

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;

public class ManageStockActivity extends Activity implements OnItemSelectedListener, OnDateSetListener{

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	
	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";

	int  m_junpyoInTIdx = 0;
	
	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;	
	Calendar m_dateCalender1;
	SimpleAdapter adapter; 
	
	int m_selectedListIndex = -1;
	
	List<HashMap<String, String>> m_stockList = null;
	
	String[] from = new String[] {"BarCode", "G_Name", "Real_Sto", "St_Count"};
    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> m_tempProduct =new HashMap<String, String>();

	Button m_period;
	ListView m_listStock;
	EditText m_textBarcode;
	EditText m_textProductName;
	EditText m_et_purchasePrice;
	EditText m_et_salePrice;
	EditText m_et_numOfReal;
	EditText m_et_curStock;
	Button m_bt_barcodeSearch;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_stock);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		m_listStock= (ListView)findViewById(R.id.listviewReadyToSendEventList);
		
		Button btn_Register = (Button)findViewById(R.id.buttonRegist);
		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
		Button btn_Send = (Button)findViewById(R.id.buttonSend);
		Button btn_Delete = (Button)findViewById(R.id.buttonDelete);
		Button btn_SendAll = (Button)findViewById(R.id.buttonSendAll);
		
		m_period = (Button) findViewById(R.id.buttonSetDate1); 
		
		m_listStock.setOnItemClickListener(mItemClickListener);
		m_listStock.setFocusableInTouchMode(true);
		m_listStock.setFocusable(true);
		
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		m_dateCalender1 = Calendar.getInstance();
		m_period.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		m_textBarcode = (EditText)findViewById(R.id.editTextBarcode);
		m_textProductName = (EditText)findViewById(R.id.editTextProductName);
		m_et_purchasePrice = (EditText)findViewById(R.id.editTextPurchasePrice);
		m_et_salePrice = (EditText)findViewById(R.id.editTextSalePrice);
		m_et_numOfReal = (EditText)findViewById(R.id.editTextNumberOfReal);
		m_et_curStock = (EditText)findViewById(R.id.editTextCurStock);
		m_bt_barcodeSearch = (Button)findViewById(R.id.buttonBarcode);
		
		m_stockList = new ArrayList<HashMap<String, String>>();

        adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, from, to);
        m_listStock.setAdapter(adapter);
        
		btn_Register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (checkRedundancyData()==false) return;
				setDataIntoList();
				changeInputMode(1);
				clearInputBox();
			 }			
		});
		
		btn_Renew.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				changeInputMode(0);
				clearInputBox();
			 }			
		});
		
		btn_Delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				deleteData();
			}			
		});
		
		btn_Send.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 전체 삭제 기능으로 대체 
				clearInputBox();
				deleteListAll();
			}			
		});
		
		btn_SendAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendSelectedAllData();
				clearInputBox();			
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

		changeInputMode(0);
		getInTSeq();
	}
	
	public String makeJunPyo () {

		String period = m_period.getText().toString();
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		int day = Integer.parseInt(period.substring(8, 10));
		
        // 전표번호 생성 
		String posID = "P";
        try {
			String OFFICE_CODE = m_shop.getString("OFFICE_CODE");
			posID = LocalStorage.getString(this, "currentPosID:"+OFFICE_CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        String junpyo = String.format("%04d%02d%02d", year, month, day) + posID + String.format("%03d", m_junpyoInTIdx);
        return junpyo;        
	}

	public void deleteData() {
		
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageStockActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		fillMaps.remove(m_selectedListIndex);
		adapter.notifyDataSetChanged();
		m_stockList.remove(m_selectedListIndex);
		m_selectedListIndex = -1;
	}
	
	public void deleteListAll() {
		if (fillMaps.isEmpty()) return;
		
		fillMaps.removeAll(fillMaps);
		adapter.notifyDataSetChanged();		
		m_stockList.removeAll(m_stockList);		
		m_selectedListIndex = -1;
	}	

	public void clearInputBox () {

		m_selectedListIndex = -1;
		m_textBarcode.setText("");
		m_textProductName.setText("");
		m_et_purchasePrice.setText("");
		m_et_salePrice.setText("");
		m_et_numOfReal.setText("");
		m_et_curStock.setText("");
	}
	
	public void changeInputMode (int mode) {
		if (mode==0) {
			m_textBarcode.setEnabled(true);
			m_period.setEnabled(true);
			m_bt_barcodeSearch.setEnabled(true);
		}
		else {
			m_period.setEnabled(false);
			m_textBarcode.setEnabled(true);
			m_bt_barcodeSearch.setEnabled(true);
		}
	}
	
	public boolean checkRedundancyData() {
		String barcode = m_textBarcode.getText().toString();
		Iterator<HashMap<String, String>> iterator = m_stockList.iterator();		
	    while (iterator.hasNext()) {
			HashMap<String, String> element = iterator.next();
	         String barcodeinList = element.get("BarCode");
	         if (barcodeinList.equals(barcode)) {
	        	 Toast.makeText(ManageStockActivity.this, "이미 등록되어 있는 바코드입니다.", Toast.LENGTH_SHORT).show();
	 			return false;
	         }
	    }
	    return true;
	}
	
	public List<HashMap<String, String>> makeFillvapsWithStockList(){
		List<HashMap<String, String>> fm = new ArrayList<HashMap<String, String>>();
	    
		Iterator<HashMap<String, String>> iterator = m_stockList.iterator();		
	    while (iterator.hasNext()) {
			boolean isNew = true;
			HashMap<String, String> element = iterator.next();
	         String barcode = element.get("BarCode");
	        
	         Iterator<HashMap<String, String>> fm_iterator = fm.iterator();
	         while (fm_iterator.hasNext()) {

	        	 HashMap<String, String>  fm_element = fm_iterator.next();
		         String fm_barcode = fm_element.get("BarCode");

		         if (fm_barcode.equals(barcode)) {
		        	 isNew = false;
		        	 // 같은게 있으면 fm_element에 추가 
			         String fm_numOfReal = fm_element.get("Real_Sto");
			         String fm_curStock = fm_element.get("St_Count");
			         String numOfReal = element.get("Real_Sto");
			         String curStock = element.get("St_Count");
			         
			         fm_element.put("Real_Sto", String.valueOf(Integer.valueOf(fm_numOfReal) + Integer.valueOf(numOfReal)));
			         fm_element.put("St_Count", String.valueOf(Integer.valueOf(fm_curStock) + Integer.valueOf(curStock)));
		         }
	         }
	         
	         if (isNew) {
		         String productName = element.get("G_Name");
		         String numOfReal = element.get("Real_Sto");
		         String curStock = element.get("St_Count");
		     	 HashMap<String, String> map = new HashMap<String, String>();
		         map.put("BarCode", barcode);
		         map.put("G_Name", productName);
		         map.put("Real_Sto", numOfReal);
		         map.put("St_Count", curStock);
		         
		         fm.add(map);
		    }
	    }
	    
		return fm;
	}
	
	public void setDataIntoList() {
		
		String period = m_period.getText().toString();
		String barcode = m_textBarcode.getText().toString();
		String productName = m_textProductName.getText().toString();
		String purchasePrice = m_et_purchasePrice.getText().toString();
		String salePrice = m_et_salePrice.getText().toString();
		String numOfReal = m_et_numOfReal.getText().toString();
		String curStock = m_et_curStock.getText().toString();
		
		if (barcode.equals("") || productName.equals("") || purchasePrice.equals("") || salePrice.equals("") || numOfReal.equals("")|| numOfReal.equals("")) {
			Toast.makeText(ManageStockActivity.this, "비어있는 필드가 있습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
    	//선택된 상품장의 데이터를 꺼내옴
		String Bus_Code = m_tempProduct.get("Bus_Code");		// 원매입가 
		String Pur_Pri = m_tempProduct.get("Pur_Pri");		// 원매입가 
		String Sell_Pri = m_tempProduct.get("Sell_Pri");	// 원매출가
		String Pur_Cost = m_tempProduct.get("Pur_Cost");	// 매입원가 
		String Add_Tax = m_tempProduct.get("Add_Tax");	// 부가세
		String Bot_Pur = m_tempProduct.get("Bot_Pur");	// 공병매입가 
		String Bot_Sell = m_tempProduct.get("Bot_Sell");	// 공병매출가
		String Tax_YN = m_tempProduct.get("Tax_YN");		// 과세여부(0:면세,1:과세)
		String Tax_Gubun = m_tempProduct.get("VAT_CHK");	// 부가세구분(0:별도,1:포함)
		String In_YN = m_tempProduct.get("In_YN");	// 부가세구분(0:별도,1:포함)
		String Box_Use = m_tempProduct.get("Box_Use");	//
		String Pack_Use = m_tempProduct.get("Pack_Use");	// 
		String Bot_Code = m_tempProduct.get("Bot_Code");	//
		String Bot_Name = m_tempProduct.get("Bot_Name");	// 
		String Real_Sto = m_tempProduct.get("Real_Sto");	// 

		if (Bot_Pur == null) Bot_Pur = "0";
		if (Bot_Sell == null) Bot_Sell = "0";
		
		double In_Pri = Double.valueOf(purchasePrice)*Double.valueOf(numOfReal);	//총 매입가(공병포함)=매입가x수량
		double In_SellPri = Double.valueOf(salePrice)*Double.valueOf(numOfReal);	//판매가x수량
        
        HashMap<String, String> rmap = new HashMap<String, String>();
        //rmap.put("St_Num", period);
        //rmap.put("St_BarCode", barcode);
        rmap.put("St_Gubun", "2");		//실사구분(0:수기,1:핸디,2:PDA,3:기타)
        rmap.put("Set_Gubun", "0");
        rmap.put("St_Date", period);
        rmap.put("Office_Code", Bus_Code);
        rmap.put("BarCode", barcode);
        //rmap.put("St_Seq", "");
        rmap.put("St_Count", numOfReal);
        rmap.put("Tax_YN", Tax_YN);
        rmap.put("Tax_Gubun", Tax_Gubun);
        rmap.put("Bot_Code", Bot_Code);
        rmap.put("Bot_Pur", Bot_Pur);
        rmap.put("Bot_Sell", Bot_Sell);
        rmap.put("Pur_Pri", Pur_Pri);
        rmap.put("Pur_Cost", Pur_Cost);
        rmap.put("Add_Tax", Add_Tax);
        rmap.put("TPur_Pri", String.valueOf((Double.valueOf(purchasePrice)-Double.valueOf(Bot_Pur))*Double.valueOf(numOfReal)));		//총 매입가(공병제외)=매입가x수량
        rmap.put("TPur_Cost", String.valueOf(Double.valueOf(Pur_Cost)*Double.valueOf(numOfReal)));			//매입원가x수량
        rmap.put("TAdd_Tax", String.valueOf(Double.valueOf(Add_Tax)*Double.valueOf(numOfReal)));			//부가세x수량
        rmap.put("In_Pri", String.valueOf(In_Pri));		//총 매입가(공병포함)=매입가x수량
        rmap.put("Sell_Pri", Sell_Pri);
        rmap.put("TSell_Pri", String.valueOf((Double.valueOf(salePrice)-Double.valueOf(Bot_Sell))*Double.valueOf(numOfReal)));	//총 판매가(공병제외)=판매가x수량
        rmap.put("In_SellPri", String.valueOf(In_SellPri));	//판매가x수량
        rmap.put("Bot_Pri", String.valueOf(Double.valueOf(Bot_Pur)*Double.valueOf(numOfReal)));
        rmap.put("Bot_SellPri", String.valueOf(Double.valueOf(Bot_Sell)*Double.valueOf(numOfReal)));
        
        rmap.put("Bot_Code", Bot_Code);	// 
        rmap.put("Bot_Name", Bot_Name);	//         
        //rmap.put("Write_Date", currentDate);
        //rmap.put("Edit_Date", currentDate);
        //rmap.put("Writer", userid);
        //rmap.put("Editor", userid);
        rmap.put("oReal_Sto", Real_Sto);
        
        m_stockList.add(rmap);
        
        //ListView 에 뿌려줌
    	HashMap<String, String> map = new HashMap<String, String>();
        map.put("BarCode", barcode);
        map.put("G_Name", productName);
        map.put("Real_Sto", numOfReal);
        map.put("St_Count", curStock);
        fillMaps.add(map);            
  		
        adapter.notifyDataSetChanged();
    }
	
	// MSSQL
	// StT, StD 테이블에 삽입
	public void sendSelectedAllData(){

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = sdf.format(new Date());

	    JSONObject userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
	    String userid ="";
        try {
        	userid = userProfile.getString("User_ID");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String tableName = null, sttTableName = null;
		String period = m_period.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		
		tableName = String.format("StD_%04d%02d", year, month);
		sttTableName = String.format("StT_%04d%02d", year, month);
		
		//TODO: 새로운 전표생성
		String junpyo = makeJunPyo();
		
		double TTPur_Pri =0, TTAdd_Tax =0, TFPur_Pri =0, In_TPri =0, In_FPri =0, In_Pri =0, In_RePri =0;
		double TSell_Pri =0, In_SellPri =0, Bot_Pri =0, Bot_SellPri =0, Profit_Pri =0, Profit_Rate =0;
		
		// 쿼리 작성하기
		String query =  "";
		for ( int i = 0, seq=1; i < m_stockList.size(); i++, seq++ ) {

			HashMap<String, String> stock = m_stockList.get(i);
			
			query +=  "insert into " + tableName + "(St_Num, St_BarCode, St_Gubun, Set_Gubun, St_Date, Office_Code, BarCode, St_Seq, St_Count,"
					+ "Tax_YN, Tax_Gubun, Bot_Code, Bot_Pur, Bot_Sell, Pur_Pri, Pur_Cost, Add_Tax, TPur_Pri, TPur_Cost, TAdd_Tax, In_Pri,"
					+ "Sell_Pri, TSell_Pri, In_SellPri, Bot_Pri, Bot_SellPri, Write_Date, Edit_Date, Writer, Editor, oReal_Sto) " 
		    		+ " values ("
		    		+ "'" + junpyo + "', "
		    		+ "'" + junpyo + "', "
				    + "'" + stock.get("St_Gubun").toString() + "', "
				    + "'" + stock.get("Set_Gubun").toString() + "', "
				    + "'" + stock.get("St_Date").toString() + "', "
				    + "'" + stock.get("Office_Code").toString() + "', "
				    + "'" + stock.get("BarCode").toString() + "', "
				    + "'" + seq + "', "
				    + "'" + stock.get("St_Count").toString() + "', "
				    + "'" + stock.get("Tax_YN").toString() + "', "
				    + "'" + stock.get("Tax_Gubun").toString() + "', "
				    + "'" + stock.get("Bot_Code").toString() + "', "
				    + "'" + stock.get("Bot_Pur").toString() + "', "
				    + "'" + stock.get("Bot_Sell").toString() + "', "
				    + "'" + stock.get("Pur_Pri").toString() + "', "
				    + "'" + stock.get("Pur_Cost").toString() + "', "
				    + "'" + stock.get("Add_Tax").toString() + "', "
				    + "'" + stock.get("TPur_Pri").toString() + "', "
				    + "'" + stock.get("TPur_Cost").toString() + "', "
				    + "'" + stock.get("TAdd_Tax").toString() + "', "
				    + "'" + stock.get("In_Pri").toString() + "', "
				    + "'" + stock.get("Sell_Pri").toString() + "', "
				    + "'" + stock.get("TSell_Pri").toString() + "', "
				    + "'" + stock.get("In_SellPri").toString() + "', "
				    + "'" + stock.get("Bot_Pri").toString() + "', "
				    + "'" + stock.get("Bot_SellPri").toString() + "', "
				    + "'" + currentDate + "', "
				    + "'" + currentDate + "', "
				    + "'" + userid + "', "
				    + "'" + userid + "', "
					+ "'" + stock.get("oReal_Sto").toString() + "');";
			
			// InT용 데이터 누적시킴
		    String Tax_YN = stock.get("Tax_YN"); //과세여부(0:면세,1:과세)
		    //String In_YN = stock.get("In_YN");	//매입여부(0:반품,1:매입,2:행사반품,3:행사매입)

			TTAdd_Tax += Double.valueOf(stock.get("Add_Tax"));	//총 과세 부가세
			if (Tax_YN.equals("1")) TTPur_Pri += Double.valueOf(stock.get("TPur_Pri"));	//총 과세매입가(공병제외)
			if (Tax_YN.equals("0")) TFPur_Pri+= Double.valueOf(stock.get("TPur_Pri"));	//총 면세매입가(공병제외)
			if (Tax_YN.equals("1")) In_TPri+= Double.valueOf(stock.get("In_Pri"));		//총 과세매입가(공병포함)
			if (Tax_YN.equals("0")) In_FPri+= Double.valueOf(stock.get("In_Pri"));		//총 면세매입가(공병포함)

			else In_Pri+= Double.valueOf(stock.get("In_Pri"));		//총 매입가(공병포함)
			TSell_Pri+= Double.valueOf(stock.get("TSell_Pri"));	//총 판매가(공병제외)
			In_SellPri+= Double.valueOf(stock.get("In_SellPri"));	//총 판매가(공병포함)
			Bot_Pri+= Double.valueOf(stock.get("Bot_Pur"));		//공병 총 매입가
			Bot_SellPri+= Double.valueOf(stock.get("Bot_Sell"));	//공병 총 판매가
		}

	    query +=  "insert into " + sttTableName + "(St_Num, St_Date, St_Seq, TTPur_Pri, TTAdd_Tax, TFPur_Pri, In_TPri, In_FPri, In_Pri, "
	    		+ "TSell_Pri, In_SellPri, Bot_Pri, Bot_SellPri, St_Pri, Write_Date, Edit_Date, Writer, Editor ) " 
	    		+ " values ("
	    		+ "'" + junpyo + "', "
	    	    + "'" + currentDate + "', "
	    	    + "'" + "0" + "', "
	    	    + "'" + TTPur_Pri + "', "
	    	    + "'" + TTAdd_Tax + "', "
	    	    + "'" + TFPur_Pri + "', "
	    	    + "'" + In_TPri + "', "
	    	    + "'" + In_FPri + "', "
	    	    + "'" + In_Pri + "', "
	    	    + "'" + TSell_Pri + "', "
	    	    + "'" + In_SellPri + "', "
	    	    + "'" + Bot_Pri + "', "
	    	    + "'" + Bot_SellPri + "', "	 
	    	    + "'" + In_Pri + "', "	    	    
			    + "'" + currentDate + "', "
			    + "'" + currentDate + "', "
			    + "'" + userid + "', "
			    + "'" + userid + "');";

	    query += " select * from " + tableName + " where St_Num='" + junpyo +"';";

        m_junpyoInTIdx++;
        
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

				deleteListAll();
				clearInputBox();
				Toast.makeText(ManageStockActivity.this, "전송완료.", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port,  "TIPS", "sa", "tips", query);
	}


	//전표갯수를 구함
	public void getInTSeq() {

		String tableName = null;
		String period = m_period.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		
		tableName = String.format("StT_%04d%02d", year, month);
		
		// 쿼리 작성하기
		String query =  "";
	    query += "SELECT TOP 1 St_Seq, St_Num FROM " + tableName + " WHERE St_Date='"+period
	    		+"' AND (St_Seq NOT IN(SELECT TOP 0 St_Seq FROM " + tableName + ")) order by St_Num DESC;";
	    
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
						m_junpyoInTIdx = results.getJSONObject(0).getInt("St_Seq")+1;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					m_junpyoInTIdx =1;
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
						m_textProductName.setText(results.getJSONObject(0).getString("G_Name"));
						m_et_purchasePrice.setText(results.getJSONObject(0).getString("Pur_Pri"));
						m_et_salePrice.setText(results.getJSONObject(0).getString("Sell_Pri"));
						//m_et_numOfReal.setText(results.getJSONObject(0).getString("Real_Sto"));
						m_et_curStock.setText(results.getJSONObject(0).getString("Real_Sto"));
						
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
						    	
			          		Intent intent = new Intent(ManageStockActivity.this, ManageProductActivity.class);
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
			        
			        new AlertDialog.Builder(ManageStockActivity.this)
			      .setTitle("등록되지않은 바코드입니다")
			      .setNeutralButton("새로등록", newBarcodeListener)
			      .setNegativeButton("취소", cancelListener)
			      .show();
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
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
					Intent intent = new Intent(ManageStockActivity.this, ManageProductListActivity.class);
			    	startActivityForResult(intent, 1);
				} else { // 스캔할 경우
					Intent intent = new Intent(ManageStockActivity.this, ZBarScannerActivity.class);
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
	
	public void OnClickModify(View v)
	{
		if ( m_selectedListIndex == -1) {
			Toast.makeText(ManageStockActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
		TextView productName = (TextView)findViewById(R.id.editTextProductName);
		TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
		TextView numOfReal = (TextView)findViewById(R.id.editTextNumberOfReal);
		TextView curStock = (TextView)findViewById(R.id.editTextCurStock);
		
		String period = m_period.getText().toString();
		
		HashMap<String, String> map = fillMaps.get(m_selectedListIndex);
		
		map.put("BarCode", barcode.getText().toString());
        map.put("G_Name", productName.getText().toString());
        map.put("Real_Sto", numOfReal.getText().toString());
        map.put("St_Count", curStock.getText().toString());
        
        adapter.notifyDataSetChanged();
        
        HashMap<String, String> rmap = m_stockList.get(m_selectedListIndex);
        
        rmap.put("St_Date", period);
        rmap.put("BarCode", barcode.getText().toString());
        rmap.put("G_Name", productName.getText().toString());
        rmap.put("Pur_Pri", purchasePrice.getText().toString());
        rmap.put("Sell_Pri", salePrice.getText().toString());
        rmap.put("St_Count", curStock.getText().toString());
        rmap.put("Real_Sto", numOfReal.getText().toString());
     
        m_selectedListIndex = -1;
	}

	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		ActionBar actionbar = getActionBar();

		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setTitle("재고관리");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_stock, menu);
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
		
		getInTSeq();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			m_selectedListIndex = arg2;
			
			TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
			TextView productName = (TextView)findViewById(R.id.editTextProductName);
			TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
			TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
			TextView numOfReal = (TextView)findViewById(R.id.editTextNumberOfReal);
			TextView curStock = (TextView)findViewById(R.id.editTextCurStock);
			
	        barcode.setText(m_stockList.get(arg2).get("BarCode").toString());
	        productName.setText(m_stockList.get(arg2).get("G_Name").toString());
	        purchasePrice.setText(m_stockList.get(arg2).get("Pur_Pri").toString());
	        salePrice.setText(m_stockList.get(arg2).get("Sell_Pri").toString());
	        numOfReal.setText(m_stockList.get(arg2).get("Real_Sto").toString());
	        curStock.setText(m_stockList.get(arg2).get("St_Count").toString());
	        
	        m_period.setText(m_stockList.get(arg2).get("St_Date").toString());
		}
	};
}
