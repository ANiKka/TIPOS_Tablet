package tipsystem.tips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;
import android.support.v4.app.NavUtils;

public class ManageStockActivity extends Activity implements OnItemSelectedListener, OnDateSetListener{

	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;
	
	Calendar m_dateCalender1;
	SimpleAdapter adapter; 
	
	Button m_period;
	
	int m_selectedListIndex = -1;
	
	List<HashMap<String, String>> m_stockList = null;
	
	String[] from = new String[] {"BarCode", "G_Name", "Real_Sto", "St_Count"};
    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    
    
	ListView m_listStock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_stock);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_listStock= (ListView)findViewById(R.id.listviewReadyToSendEventList);
		
		Button btn_Register = (Button)findViewById(R.id.buttonRegist);
		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
//		Button btn_Modify = (Button)findViewById(R.id.buttonModify);
		
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
		
		
		m_stockList = new ArrayList<HashMap<String, String>>();

		btn_Register.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){

				setDataIntoList();
				m_selectedListIndex = -1;
			 }			
		});
		
		
		btn_Renew.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
			

				TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
				TextView productName = (TextView)findViewById(R.id.editTextProductName);
				TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
				TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
				TextView numOfReal = (TextView)findViewById(R.id.editTextNumberOfReal);
				TextView curStock = (TextView)findViewById(R.id.editTextCurStock);
				
				barcode.setText("");
				productName.setText("");
				purchasePrice.setText("");
				salePrice.setText("");
				numOfReal.setText("");
				curStock.setText("");

				m_selectedListIndex = -1;
			 }			
		});
		
		
		btn_Delete.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				deleteData();
				
			}			
		});
		
		btn_Send.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				if ( m_selectedListIndex == -1)
				{
					Toast.makeText(ManageStockActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				sendSelectedData(m_selectedListIndex);
				
				Toast.makeText(ManageStockActivity.this, "전송완료.", Toast.LENGTH_SHORT).show();
				m_selectedListIndex = -1;
				
			}			
		});
		
		btn_SendAll.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				for ( int i = 0; i < m_stockList.size(); i++ )
				{
					sendSelectedData(i);
				}
				
				Toast.makeText(ManageStockActivity.this, "전송완료.", Toast.LENGTH_SHORT).show();
				m_selectedListIndex = -1;
			}			
		});
				
	}
	
	
	public void sendSelectedData(int index){
		
		String tableName = null;
		
		String period = m_period.getText().toString();
		
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		
		tableName = String.format("StD_%04d%02d", year, month);
		
		// 쿼리 작성하기
	    String query =  "";
	    query =  "insert " + tableName + "(St_Date, BarCode, Pur_Pri, Sell_Pri, St_Count) " 
	    		+ "into values (" 
	    		+ "'" + m_stockList.get(index).get("St_Date").toString() + "', "
	    		+ "'" + m_stockList.get(index).get("BarCode").toString() + "', "
	    		+ "'" + m_stockList.get(index).get("Pur_Pri").toString() + "', "
	    		+ "'" + m_stockList.get(index).get("Sell_Pri").toString() + "', "
	    		+ "'" + m_stockList.get(index).get("St_Count").toString() + "';";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

			}
			
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		
	}
	
	public void deleteData(){
		
		if ( m_selectedListIndex == -1)
		{
			Toast.makeText(ManageStockActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		fillMaps.remove(m_selectedListIndex);
		adapter.notifyDataSetChanged();
		
		m_stockList.remove(m_selectedListIndex);
		
		m_selectedListIndex = -1;
	}
	
	public void OnClickModify(View v)
	{
		
		if ( m_selectedListIndex == -1)
		{
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

	public void setDataIntoList(){
	    
		TextView barcode = (TextView)findViewById(R.id.editTextBarcode);
		TextView productName = (TextView)findViewById(R.id.editTextProductName);
		TextView purchasePrice = (TextView)findViewById(R.id.editTextPurchasePrice);
		TextView salePrice = (TextView)findViewById(R.id.editTextSalePrice);
		TextView numOfReal = (TextView)findViewById(R.id.editTextNumberOfReal);
		TextView curStock = (TextView)findViewById(R.id.editTextCurStock);
		
		String period = m_period.getText().toString();
		
    	HashMap<String, String> map = new HashMap<String, String>();
        map.put("BarCode", barcode.getText().toString());
        map.put("G_Name", productName.getText().toString());
        map.put("Real_Sto", numOfReal.getText().toString());
        map.put("St_Count", curStock.getText().toString());
        fillMaps.add(map);            
  		
        adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item4, from, to);
        m_listStock.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    
        HashMap<String, String> rmap = new HashMap<String, String>();
        
        rmap.put("St_Date", period);

        rmap.put("BarCode", barcode.getText().toString());
        rmap.put("G_Name", productName.getText().toString());
        rmap.put("Pur_Pri", purchasePrice.getText().toString());
        rmap.put("Sell_Pri", salePrice.getText().toString());
        rmap.put("St_Count", curStock.getText().toString());
        rmap.put("Real_Sto", numOfReal.getText().toString());
        
        m_stockList.add(rmap);
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
		// TODO Auto-generated method stub
		
		DatePickerDialog newDlg = new DatePickerDialog(this, this,
					m_dateCalender1.get(Calendar.YEAR),
					m_dateCalender1.get(Calendar.MONTH),
					m_dateCalender1.get(Calendar.DAY_OF_MONTH));
			 newDlg.show();
		
	};
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		m_dateCalender1.set(year, monthOfYear, dayOfMonth);
		m_period.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			
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
