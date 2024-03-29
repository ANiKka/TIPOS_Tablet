package tipsystem.tips;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
import tipsystem.utils.StringFormat;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SalesNewsActivity extends Activity implements OnItemClickListener, 
														OnItemSelectedListener, 
														OnTabChangeListener,
														DatePickerDialog.OnDateSetListener{

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	TextView m_realSales;
	TextView m_viewNumber;
	TextView m_viewKNumber;
	TextView m_viewRealSalesYesterday;
	TextView m_viewPrice;
	TextView m_viewCash;
	TextView m_viewCard;
	TextView m_viewCredit;
	TextView m_viewOther;
	
	TabHost m_tabHost;
	
	ListView m_listNewsTab1;
	ListView m_listNewsTab2;
	ListView m_listNewsTab3;
	
	Spinner m_spinClassification1;
	Spinner m_spinClassification2;
	Spinner m_spinClassification3;
	
	DatePicker m_datePicker;
	Button m_buttonSetDate;
	
	SimpleDateFormat m_dateFormatter;
	Calendar m_dateCalender1;
	Calendar m_dateCalender2;
	
	NumberFormat m_numberFormat;
	ProgressDialog dialog;

	SimpleAdapter adapter1;
	SimpleAdapter adapter2;
	SimpleAdapter adapter3;

	List<HashMap<String, String>> mfillMaps1 = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> mfillMaps2 = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> mfillMaps3 = new ArrayList<HashMap<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_news);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		m_numberFormat = NumberFormat.getInstance();		
		m_dateCalender1 = Calendar.getInstance();
		m_dateCalender2 = Calendar.getInstance();
		
		m_buttonSetDate = (Button) findViewById(R.id.buttonSetDate);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
		
		// 상단 텍스트 뷰
		m_realSales = (TextView)findViewById(R.id.textViewRealSales);
		m_viewKNumber = (TextView)findViewById(R.id.textViewKNumber);
		m_viewRealSalesYesterday = (TextView)findViewById(R.id.textViewRealSalesYesterday);
		m_viewPrice = (TextView)findViewById(R.id.textViewKPrice);
		m_viewCash = (TextView)findViewById(R.id.textViewCash);
		m_viewCard = (TextView)findViewById(R.id.textViewCard);
		m_viewCredit = (TextView)findViewById(R.id.textViewCredit);
		m_viewOther = (TextView)findViewById(R.id.textViewOther);
		m_listNewsTab1= (ListView)findViewById(R.id.listviewSalesNewsListTab1);
		m_listNewsTab2= (ListView)findViewById(R.id.listviewSalesNewsListTab2);
		m_listNewsTab3= (ListView)findViewById(R.id.listviewSalesNewsListTab3);
		
		m_listNewsTab2.setOnItemClickListener(this);

		String[] from1 = new String[] {"G_Hour", "순매출", "전일순매출", "전일대비차액"};
		//String[] from1 = new String[] {"시간", "순매출", "전일매출", "전일대비차액"};
        int[] to1 = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		adapter1 = new SimpleAdapter(this, mfillMaps1, R.layout.activity_listview_item4, from1, to1);		
		m_listNewsTab1.setAdapter(adapter1);	
		
		String[] from2 = new String[] {"Office_Code", "Office_Name", "순매출", "이익금"};
        int[] to2 = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };
        
		adapter2 = new SimpleAdapter(this, mfillMaps2, R.layout.activity_listview_item4_2, from2, to2);		
		m_listNewsTab2.setAdapter(adapter2);	

		String[] from3 = new String[] {"순번", "분류명", "순매출", "수량", "이익금"};
	    int[] to3 = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5 };
        
		adapter3 = new SimpleAdapter(this, mfillMaps3, R.layout.activity_listview_item5, from3, to3);		
		m_listNewsTab3.setAdapter(adapter3);
		
		m_spinClassification1 = (Spinner)findViewById(R.id.spinnerClassificationType1);
		m_spinClassification1.setOnItemSelectedListener(this);
		
		m_spinClassification2 = (Spinner)findViewById(R.id.spinnerClassificationType2);
		m_spinClassification2.setOnItemSelectedListener(this);
		
		m_spinClassification3 = (Spinner)findViewById(R.id.spinnerClassificationType3);
		m_spinClassification3.setOnItemSelectedListener(this);
		
		// 탭 부분
		m_tabHost = (TabHost) findViewById(R.id.tabhostSalesNews);
        m_tabHost.setup();
             
        TabHost.TabSpec spec = m_tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.tab1);
        spec.setIndicator("시간대별");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("거래처별");
        m_tabHost.addTab(spec);
        
        spec = m_tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("분류별");
        m_tabHost.addTab(spec);
        
        
        m_tabHost.setCurrentTab(0);
        m_tabHost.setOnTabChangedListener(this);

		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));	
        m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
		
 		queryCommonInformation();
        doQuery();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if ( m_listNewsTab2.getId() == arg0.getId() )
		{
			Intent intent = new Intent(this, CustomerProductDetailInNewsActivity.class);
			
			String code = ((TextView) arg1.findViewById(R.id.item1)).getText().toString();
			String name = ((TextView) arg1.findViewById(R.id.item2)).getText().toString();			
			String period1 = m_buttonSetDate.getText().toString();
			
	    	intent.putExtra("PERIOD1", period1);
	    	intent.putExtra("OFFICE_CODE", code);
	    	intent.putExtra("OFFICE_NAME", name);
	    	startActivity(intent);	
		}		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
  		 		
		if ( arg0.getId() == m_spinClassification1.getId() )
		{
			String typeL = m_spinClassification1.getItemAtPosition(arg2).toString();

			queryComboBoxInTab3(1, typeL, "", "");
		}
		else if ( arg0.getId() == m_spinClassification2.getId() )
		{
			String typeL = m_spinClassification1.getItemAtPosition(m_spinClassification1.getSelectedItemPosition()).toString();
			String typeM = m_spinClassification2.getItemAtPosition(arg2).toString();

			queryComboBoxInTab3(2, typeL, typeM, "");
		}
		else if ( arg0.getId() == m_spinClassification3.getId() )
		{			
			String typeL = m_spinClassification1.getItemAtPosition(m_spinClassification1.getSelectedItemPosition()).toString();
			String typeM = m_spinClassification2.getItemAtPosition(m_spinClassification2.getSelectedItemPosition()).toString();
			String typeS = m_spinClassification3.getItemAtPosition(arg2).toString();
			
			queryListForTab3(typeL, typeM, typeS);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public void onClickSetDate(View view) {
		DatePickerDialog newDlg = new DatePickerDialog(this, this,
				m_dateCalender1.get(Calendar.YEAR),
				m_dateCalender1.get(Calendar.MONTH),
				m_dateCalender1.get(Calendar.DAY_OF_MONTH));
		 newDlg.show();
	}
	
	public void onClickSetDatePrevious(View view) {
		m_dateCalender1.add(Calendar.DAY_OF_MONTH, -1);	
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));	
				
 		queryCommonInformation();
 		doQuery();
	}
	
	public void onClickSetDateNext(View view) {
		m_dateCalender1.add(Calendar.DAY_OF_MONTH, 1);
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, 1);	
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));			
 		
 		queryCommonInformation();
 		doQuery();
	}
	
	@Override
	public void onTabChanged(String tabId) {
 		
 		doQuery();
	};
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
		m_dateCalender1.set(year, monthOfYear, dayOfMonth);
		m_buttonSetDate.setText(m_dateFormatter.format(m_dateCalender1.getTime()));		
		m_dateCalender2.set(year, monthOfYear, dayOfMonth);
		m_dateCalender2.add(Calendar.DAY_OF_MONTH, -1);
		
 		queryCommonInformation();
 		doQuery();
	}
	
	private void doQuery () {

 		switch (m_tabHost.getCurrentTab())
 		{
	 		case 0:
	 			queryForTab1(); break;
	 		case 1:
	 			queryListForTab2(); break;
	 		case 2:
	 			queryComboBoxInTab3(0, "", "", ""); 
	 			break;
 		}
	}
	
	private void queryCommonInformation()
	{
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = String.format("%04d%02d", year1, month1);
		String tableName2 = String.format("%04d%02d", year2, month2);
		
		query = "Select "
				+ " a.Sale_Date, "
				+ " ISNULL(순매출,0) '순매출', ISNULL(현금,0) '현금', ISNULL(카드,0) '카드', ISNULL(외상,0) '외상',  "
				+ " ISNULL(기타,0) '기타', "
				+ " ISNULL(객수,0) '객수', ISNULL(객단가,0) '객단가',  "
				+ " ISNULL(순매출_B,0) '전일_순매출', ISNULL(객수_B,0) '전일_객수', ISNULL(객단가_B,0) '전일_객단가'  "
				+ " From ( "
		 		+ " Select     'a' a, "
		 		+ " sale_date, "
		 		+ " 순매출,객수,현금,카드,외상,기타, "
		 		+ " '객단가' = CASE WHEN 순매출=0 Then 0 ELSE 순매출/객수 END "
		 		+ " From ( "
		  		+ " Select "
		  		+ "  a.Sale_Date, "
		       		+ " Sum (b.TSell_Pri - b.TSell_RePri) '순매출', "
		       		+ " Count (Distinct(a.Sale_Num)) '객수', "
		       		+ " Sum (Round(a.Cash_Pri * b.Money_Per, 4)) '현금', "
		       		+ " Sum (b.Card_Pri) '카드', "
		       		+ " Sum (Round(a.Dec_Pri * b.Money_Per, 4)) '외상', "
		       		+ " Sum (Round(a.CMS_Pri * b.Money_Per, 4)) +  Sum (Round(a.Cus_PointUse * b.Money_Per, 4)) + Sum (Round(a.Sub_Pri * b.Money_Per, 4)) + Sum (Round(a.Gift_Pri * b.Money_Per, 4)) + Sum (Round(a.CashBack_PointUse * b.Money_Per, 4)) + Sum (Round(a.Cut_Pri * b.Money_Per, 4)) '기타' "
		       		+ " From SaD_"+tableName+" B, SaT_"+tableName+" A "
		       		+ " Where A.Sale_Num=B.Sale_Num And A.Sale_Date='" + period1 + "'  "
		  		+ " And B.Card_YN = '0' "
		       		+ " Group By A.Sale_Date "
		 		+ " ) G  "
				+ " ) A FULL JOIN ( "
		 		+ " Select     'a' a, "
		 		+ " 순매출 '순매출_B', "
		 		+ " 객수 '객수_B' , "
		 		+ " '객단가_B' = CASE WHEN 순매출=0 Then 0 ELSE 순매출/객수 END "
		 		+ " From ( "
		  		+ " Select "
		       		+ " Sum (b.TSell_Pri - b.TSell_RePri) '순매출', "
		       		+ " Count (Distinct(a.Sale_Num)) '객수' "
		       		+ " From SaD_"+tableName2+" B, SaT_"+tableName2+" A "
		       		+ " Where A.Sale_Num=B.Sale_Num And A.Sale_Date='" + period2 + "'  "
		  		+ " And B.Card_YN = '0' "
		       		+ " Group By A.Sale_Date "
		 		+ " ) G  "
				+ " ) B ON A.a=B.a ";
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateCommonInformation(results);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	private void updateCommonInformation(JSONArray results)
	{
		try {		
			if ( results.length() > 0 ) {
				for(int i = 0; i < results.length() ; i++) {
					JSONObject son = results.getJSONObject(i);
						m_realSales.setText(m_numberFormat.format(son.getInt("순매출")));		    			
		    			m_viewKNumber.setText(m_numberFormat.format(son.getInt("객수")));		    			
		    			m_viewPrice.setText(m_numberFormat.format(son.getInt("객단가")));
		    			m_viewCash.setText(m_numberFormat.format(son.getInt("현금")));
		    			m_viewCard.setText(m_numberFormat.format(son.getInt("카드")));
		    			m_viewCredit.setText(m_numberFormat.format(son.getInt("외상")));		    			
		    			m_viewOther.setText(m_numberFormat.format(son.getInt("기타")));
						m_viewRealSalesYesterday.setText(m_numberFormat.format(son.getInt("전일_순매출")));
				}	
			}
			else {
				m_realSales.setText(m_numberFormat.format(0));
				m_viewKNumber.setText(m_numberFormat.format(0));
				m_viewPrice.setText(m_numberFormat.format(0));
				m_viewCash.setText(m_numberFormat.format(0));
				m_viewCard.setText(m_numberFormat.format(0));
				m_viewCredit.setText(m_numberFormat.format(0));
				m_viewOther.setText(m_numberFormat.format(0));			
				m_viewRealSalesYesterday.setText(m_numberFormat.format(0));	
			}			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 시간대별
	private void queryForTab1()
	{
		mfillMaps1.removeAll(mfillMaps1);
		
		String period1 = m_buttonSetDate.getText().toString();
		String period2 = m_dateFormatter.format(m_dateCalender2.getTime());
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		int year2 = Integer.parseInt(period2.substring(0, 4));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName = String.format("%04d%02d", year1, month1);
		String tableName2 = String.format("%04d%02d", year2, month2);
		
		query = "Select A.G_Hour, ISNULL(b.순매출,0) '순매출', ISNULL(c.전일순매출,0) '전일순매출', ISNULL(b.순매출,0) - ISNULL(c.전일순매출,0) '전일대비차액'"
				+ " From Temp_Day A" 
				+ " LEFT JOIN ("
				+ " Select     LEFT(B.Sale_time,2) 'sTime', "
				+ " Sum (a.TSell_Pri - a.TSell_RePri) '순매출' "
				+ " From SaD_"+tableName+" A, SaT_"+tableName+" B "
				+ " Where A.Sale_Num=B.Sale_Num And A.Sale_date='" + period1 + "' " 
				+ " And A.Card_YN = '0' " 	
				+ " Group By LEFT(B.Sale_time,2) " 
				+ " )  B ON  A.G_Hour=B.sTime " 
				+ " LEFT JOIN ( "	
				+ " Select     LEFT(B.Sale_time,2) 'sTime1', "
				+ " Sum (a.TSell_Pri - a.TSell_RePri) '전일순매출' "	
				+ " From SaD_"+tableName2+" A, SaT_"+tableName2+" B "	
				+ " Where A.Sale_Num=B.Sale_Num And A.Sale_date='" + period2 + "' "  
				+ " And A.Card_YN = '0' " 
				+ " Group By LEFT(B.Sale_time,2) " 
				+ " ) C ON  A.G_Hour=C.sTime1 " 	
				+ " Where A.G_Hour<>'' " 
				+ " Order by A.G_Hour ASC;"; 
						
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();

		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				
				for(int index = 0; index < results.length() ; index++) {
					
					try {
						JSONObject son = results.getJSONObject(index);
						HashMap<String, String> map = JsonHelper.toStringHashMap(son);
						map.put("순매출", StringFormat.convertToNumberFormat(map.get("순매출")));
						map.put("전일순매출", StringFormat.convertToNumberFormat(map.get("전일순매출")));
						map.put("전일대비차액", StringFormat.convertToNumberFormat(map.get("전일대비차액")));
						mfillMaps1.add(map);	
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				adapter1.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter1.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);		
	}
	
	// 거래처별
	private void queryListForTab2()
	{
		String period1 = m_buttonSetDate.getText().toString();		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = String.format("SaD_%04d%02d", year1, month1);
		
		query = "Select A.Office_Code, A.Office_Name, " 
				+ " IsNull(Sum(A.TSell_Pri-A.TSell_RePri), 0) '순매출', "
				+ " IsNull(Sum(A.ProFit_Pri), 0) '이익금' " 
				//+ " '이익금'=Case When Sum(A.ProFit_Pri) = 0 Or Sum(A.TSell_Pri-A.TSell_RePri) = 0 Then 0 Else (Sum(A.ProFit_Pri)/Sum(A.TSell_Pri-A.TSell_RePri))*100 End"
				+ " From "+ tableName+" A " 
				+ " Where A.Sale_Date='" + period1 + "' And A.Card_YN = '0' " 
				+ " Group By A.Office_Code, Office_Name " 
				+ " ORDER BY Office_Code";

		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				if ( results.length() > 0 ) updateListForTab2(results);
				adapter2.notifyDataSetChanged();	
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter2.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);	
	}
	
	private void updateListForTab2(JSONArray results)
	{		
		for(int index = 0; index < results.length() ; index++) {
			
			try {
				JSONObject son = results.getJSONObject(index);
				HashMap<String, String> map = JsonHelper.toStringHashMap(son);

				map.put("순매출", StringFormat.convertToNumberFormat(map.get("순매출")));
				map.put("이익금", StringFormat.convertToNumberFormat(map.get("이익금")));

				mfillMaps2.add(map);	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void queryListForTab3(String... urls)
	{
		String period1 = m_buttonSetDate.getText().toString();
		
		final String typeL = urls[0];
		final String typeM = urls[1];
		final String typeS = urls[2];
		
		String query = "";
		String constraint = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
		
		if ( typeL.equals("") != true && typeL.equals("전체") != true) {
			constraint = setConstraint(constraint, "L_Name", "=", typeL);
		}
		
		if ( typeM.equals("") != true && typeM.equals("전체") != true) {
			constraint = setConstraint(constraint, "M_Name", "=", typeM);
		}
		
		if ( typeS.equals("") != true && typeS.equals("전체") != true) {
			constraint = setConstraint(constraint, "S_Name", "=", typeS);
		}
		
		query = "select (TSell_Pri-TSell_RePri-DC_Pri) 순매출, Sale_Count, ProFit_Pri, L_Name, M_Name, S_Name from " + tableName
				+ " where Sale_Date = '" + period1 + "'";

		if ( constraint.equals("") != true ) {
			query = query + " and " + constraint;
		}

		mfillMaps3.removeAll(mfillMaps3);
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				if ( results.length() > 0 ) updateListForTab3(results, typeL, typeM, typeS);
				adapter3.notifyDataSetChanged();	
				Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();	
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
				adapter3.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	
	private void updateListForTab3(JSONArray results, String typeL, String typeM, String typeS)
	{
		try {
			ArrayList<String> lSpListType = new ArrayList<String>();
	        ArrayList<Integer> lSpListSale = new ArrayList<Integer>();
	        ArrayList<Integer> lSpListSaleCnt = new ArrayList<Integer>();
	        ArrayList<Integer> lSpListProfit = new ArrayList<Integer>();
	        
			for(int index = 0; index < results.length() ; index++)
			{
				JSONObject son = results.getJSONObject(index);
				String sName = "";
				
				if ( typeL.equals("전체") == true ) {
					sName = son.getString("L_Name");
				}
				else if ( typeM.equals("전체") == true ) {
					sName = son.getString("M_Name");
				}
				else if ( typeS.equals("전체") == true ) {
					sName = son.getString("S_Name");
				}
				else {
					sName = son.getString("S_Name");
				}
			
				int rSale = son.getInt("순매출");
				int ProFit_Pri = son.getInt("ProFit_Pri");
				int Sale_Count = son.getInt("Sale_Count");
				
				boolean isExist = false;
           		
        		for ( int i = 0; i < lSpListType.size(); i++ ) {
        			if ( lSpListType.get(i).toString().equals(sName) == true ) {
        				Integer rsale = lSpListSale.get(i).intValue() + rSale;
        				Integer sCount = lSpListSaleCnt.get(i).intValue() + Sale_Count;
        				Integer sProfit = lSpListProfit.get(i).intValue() + ProFit_Pri;
        				
        				lSpListSale.set(i, rsale);
        				lSpListSaleCnt.set(i, sCount);
        				lSpListProfit.set(i, sProfit);
        				isExist = true;	        				
        				break;
        			}
        		}
        		
        		if ( isExist == false ) {    				
    				lSpListType.add(sName);
    				lSpListSale.add(rSale);
    				lSpListSaleCnt.add(Sale_Count);
    				lSpListProfit.add(ProFit_Pri);
        		}
			}				
			
			for ( int i = 0; i < lSpListType.size(); i++ ) {

	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("순번", String.format("%d", i+1));
	            map.put("분류명", lSpListType.get(i));
	            map.put("순매출", m_numberFormat.format(lSpListSale.get(i).intValue()) );
	            map.put("수량", m_numberFormat.format(lSpListSaleCnt.get(i).intValue()) );
	            map.put("이익금", m_numberFormat.format(lSpListProfit.get(i).intValue()) );
				mfillMaps3.add(map);	
			}	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void queryComboBoxInTab3(final int iCombo, String... urls)
	{
		String period1 = m_buttonSetDate.getText().toString();
		String query = "";
		String constraint = "";
		
		String typeL = urls[0];
		String typeM = urls[1];
		String typeS = urls[2];
		
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
		tableName = String.format("SaD_%04d%02d", year1, month1);
		
		if ( iCombo == 0 )
		{
			query = "select L_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
		}
		else if ( iCombo == 1 )
		{
			
			if ( typeL.equals("") != true && typeL.equals("전체") != true )
			{
				constraint = setConstraint(constraint, "L_Name", "=", typeL);
			}
				
			query = "select M_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
			
			if ( constraint.equals("") != true )
			{
				query = query + " and " + constraint;
			}
			
		}
		else if ( iCombo == 2 )
		{
			if ( typeL.equals("") != true && typeL.equals("전체") != true )
			{
				constraint = setConstraint(constraint, "L_Name", "=", typeL);
			}
			
			if ( typeM.equals("") != true && typeM.equals("전체") != true)
			{
				constraint = setConstraint(constraint, "M_Name", "=", typeM);
			}
			
			query = "select S_Name from " + tableName;
			query = query + " where Sale_Date = '" + period1 + "'";
			
			if ( constraint.equals("") != true )
			{
				query = query + " and " + constraint;
			}			
		}		

		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				
				updateComboBoxInTab3(results, iCombo);
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);

	}
	
	private void updateComboBoxInTab3(JSONArray results, int iCombo)
	{
		ArrayList<String> lSpList = new ArrayList<String>();       
		
		try {
			
			if ( results.length() > 0 )
			{
				 		   	        
				String lName = null;
				
				lSpList.add("전체");
		        
				for(int index = 0; index < results.length() ; index++)
				{
					JSONObject son = results.getJSONObject(index);
					
					if ( iCombo == 0 )
					{
						lName = son.getString("L_Name");
					}
					else if ( iCombo == 1 )
					{
						lName = son.getString("M_Name");
					}
					else if ( iCombo == 2 )
					{
						lName = son.getString("S_Name");
					}
					
	        		boolean isExist = false;
	        		
	        		for ( int i = 0; i < lSpList.size(); i++ )
	        		{
	        			if ( lSpList.get(i).toString().equals(lName) == true )
	        			{
	        				isExist = true;
	        				break;
	        			}
	        		}
	        		
	        		if ( isExist == true )
	        		{
	        			continue;
	        		}

		            lSpList.add(lName);
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SalesNewsActivity.this, android.R.layout.simple_spinner_item, lSpList);
				
				if ( iCombo == 0 )
				{
					m_spinClassification1.setAdapter(adapter);
				}
				else if ( iCombo == 1 )
				{
					m_spinClassification2.setAdapter(adapter);
				}
				else if ( iCombo == 2 )
				{
					m_spinClassification3.setAdapter(adapter);
				}
			}
			else 
			{
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SalesNewsActivity.this, android.R.layout.simple_spinner_item, lSpList);
				
				if ( iCombo == 0 )
				{
					m_spinClassification1.setAdapter(adapter);
				}
				else if ( iCombo == 1 )
				{
					m_spinClassification2.setAdapter(adapter);
				}
				else if ( iCombo == 2 )
				{
					m_spinClassification3.setAdapter(adapter);
				}				
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String setConstraint(String str, String field, String op, String value)
    {
    	if ( str.equals("") != true )
    	{
    		str = str + " and ";
    	}
    	
    	str = str + field + " " + op + " '" + value + "'";
    	
    	return str;
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
			actionbar.setTitle("매출속보");
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales_news, menu);
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

		case R.id.action_settings: 
			startActivity(new Intent(this, TIPSPreferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}    
}
