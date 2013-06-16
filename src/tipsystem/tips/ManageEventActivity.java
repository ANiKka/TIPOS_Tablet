package tipsystem.tips;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import tipsystem.utils.MSSQL;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
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

	DatePicker m_datePicker;
	SimpleDateFormat m_dateFormatter;
	SimpleDateFormat m_timeFormatter;
	
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	int m_dateMode=0;	
	
	Spinner m_spinEvent;
	ListView m_listEvent;
	SimpleAdapter adapter; 
	
	Button m_period1;
	Button m_period2;
	
	int m_spinnerMode = 0;
	int m_selectedListIndex = -1;
	
	List<HashMap<String, String>> m_evtList = null;
	
	String[] from = new String[] {"Evt_Name", "Evt_Gubun", "Evt_Date"};
    int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_event);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Button btn_Register = (Button)findViewById(R.id.buttonRegist);
		Button btn_Renew = (Button)findViewById(R.id.buttonRenew);
//		Button btn_Modify = (Button)findViewById(R.id.buttonModify);
		
		Button btn_Send = (Button)findViewById(R.id.buttonSend);
		Button btn_Delete = (Button)findViewById(R.id.buttonDelete);
		Button btn_SendAll = (Button)findViewById(R.id.buttonSendAll);
		


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

				setDataIntoList();
				m_selectedListIndex = -1;
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
					Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				sendSelectedData(m_selectedListIndex);
				
				Toast.makeText(ManageEventActivity.this, "전송완료.", Toast.LENGTH_SHORT).show();
				m_selectedListIndex = -1;
				
			}			
		});
		
		btn_SendAll.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				for ( int i = 0; i < m_evtList.size(); i++ )
				{
					sendSelectedData(i);
				}
				
				Toast.makeText(ManageEventActivity.this, "전송완료.", Toast.LENGTH_SHORT).show();
				m_selectedListIndex = -1;
			}			
		});

	}
	
	public void sendSelectedData(int index){
		
		String gubun = null;
		if (m_evtList.get(index).get("Evt_Name").toString().equals("기간행사") == true )
		{
			gubun = "1";
		}
		else
		{
			gubun = "0";
		}
		
		// 쿼리 작성하기
	    String query =  "";
	    query =  "insert Evt_Mst(Evt_Name, Evt_Gubun, Evt_SDate, Evt_EDate, Evt_STime, Evt_ETime, BarCode, Sale_Pur, Sale_Sell) " 
	    		+ "into values (" 
	    		+ "'" + m_evtList.get(index).get("Evt_Name").toString() + "', "
	    		+ "'" + gubun + "'"
	    		+ "'" + m_evtList.get(index).get("Evt_SDate").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("Evt_EDate").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("Evt_STime").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("Evt_ETime").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("BarCode").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("Sale_Pur").toString() + "', "
	    		+ "'" + m_evtList.get(index).get("Sale_Sell").toString() + "';";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

			}
			
	    }).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
	    
	    
	    query =  "";
	    query =  "insert Finish(Finish_Date) " 
	    		+ "into values (" 
	    		+ "'" + m_evtList.get(index).get("Evt_EDate").toString() + "';";
	    
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
			Toast.makeText(ManageEventActivity.this, "선택된 행사가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		fillMaps.remove(m_selectedListIndex);
		adapter.notifyDataSetChanged();
		
		m_evtList.remove(m_selectedListIndex);
		
		m_selectedListIndex = -1;
	}
	
	public void OnClickModify(View v)
	{
		
		if ( m_selectedListIndex == -1)
		{
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
        
        if ( section.equals("기간행사")==true)
        {
        	rmap.put("Evt_SDate", period1);
            rmap.put("Evt_EDate", period2);
            
            rmap.put("Evt_STime", "00:00");
            rmap.put("Evt_ETime", "24:00");
        }
        else 
        {
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
		
    	HashMap<String, String> map = new HashMap<String, String>();
        map.put("Evt_Name", Name);
        map.put("Evt_Gubun", section);
        map.put("Evt_Date", period1 + " ~ " + period2  );
        fillMaps.add(map);            
  		
        adapter = new SimpleAdapter(this, fillMaps, R.layout.activity_listview_item3,	from, to);
        m_listEvent.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    
        HashMap<String, String> rmap = new HashMap<String, String>();
        rmap.put("Evt_Name", Name);
        rmap.put("Evt_Gubun", section);
        
        if ( section.equals("기간행사") == true)
        {
        	rmap.put("Evt_SDate", period1);
            rmap.put("Evt_EDate", period2);
            
            rmap.put("Evt_STime", "00:00");
            rmap.put("Evt_ETime", "24:00");
        }
        else 
        {
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
		//m_text.setText("");
	}

	public void onClickSetDate1(View v) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
