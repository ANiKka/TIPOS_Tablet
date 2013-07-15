package tipsystem.tips;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.CalendarView;
import tipsystem.utils.MSSQL;
import tipsystem.utils.CalendarView.OnCellTouchListener;
import tipsystem.utils.Cell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManageSalesCalendarActivity extends Activity {

	JSONObject m_shop;
	JSONObject m_userProfile;
	String m_ip = "122.49.118.102";
	String m_port = "18971";

	Button m_buttonSetDate;
	CalendarView m_calendar;
	String m_CalendarDay;
	NumberFormat m_numberFormat;	
	ProgressDialog dialog;	
	JSONArray m_results;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_sale);
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
		m_userProfile = LocalStorage.getJSONObject(this, "userProfile"); 
		
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        m_buttonSetDate = (Button)findViewById(R.id.buttonSetDate);
		m_numberFormat = NumberFormat.getInstance();

        m_calendar = (CalendarView)findViewById(R.id.calendarView1);
        m_calendar.setOnCellTouchListener(new OnCellTouchListener() {
        	
        	@Override
        	public void onTouch(Cell cell) {
        		
    			int year  = m_calendar.getYear();
    			int month = m_calendar.getMonth()+1;
    			int day   = cell.getDayOfMonth();
        		m_CalendarDay = String.format("%04d-%02d-%02d", year, month, day);

        		updateDate();
        		didUpdate(day-1);
        	}
        });
		query();
	}
	
	public void onClickSetDatePrevious(View v) {
		m_calendar.previousMonth();
		updateDate();
		query();
	}
	
	public void onClickSetDateNext(View v) {
		m_calendar.nextMonth();
		updateDate();
		query();
	}
	
	private void updateDate() {

		int year  = m_calendar.getYear();
		int month = m_calendar.getMonth()+1;
		m_buttonSetDate.setText(String.format("%04d년 %02d월", year, month));
	}
	
	private void query()
	{
		String query = "";
		
		int year  = m_calendar.getYear();
		int month = m_calendar.getMonth()+1;
		
		String tableName = String.format("%04d%02d", year, month);
		
		query = "SELECT "
				+ " '일자'=CASE WHEN G.SALE_DATE IS NULL THEN IN_DATE ELSE G.SALE_DATE END, "
				+ " ISNULL(순매출,0) '순매출', "
				+ " ISNULL(객수,0) '객수', "
				+ " '객단가' = CASE WHEN ISNULL(순매출,0)=0 Then 0 ELSE ISNULL(순매출,0)/ISNULL(객수,0) END, "
				+ " ISNULL(IN_Pri,0) '매입금액' "
				+ " FROM ( "
				+ "  Select Sale_DATE,  "
				+ "  IsNull(Sum(TSell_Pri-TSell_RePri), 0) '순매출', "
				+ "  Count (Distinct(Sale_Num)) '객수' "
				+ "  From SaD_"+tableName+" "
				+ "  WHERE Card_YN = '0' "
				+ "  Group By Sale_DATE "
				+ " ) G FULL JOIN  ( "
				+ "      Select In_Date,Sum(In_Pri) IN_Pri "
				+ "      From InT_"+tableName+" "
				+ "      GRoup By In_Date  "
				+ " ) B ON G.SALE_DATE=B.IN_DATE "
				+ " Order by G.일자 ";
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {

				dialog.dismiss();
				dialog.cancel();
				m_results  = results;
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	private void didUpdate (int index)
	{
		TextView tv1 = (TextView)findViewById(R.id.textView1);
		TextView tv2 = (TextView)findViewById(R.id.textView2);
		TextView tv3 = (TextView)findViewById(R.id.textView3);
		TextView tv4 = (TextView)findViewById(R.id.textView4);
		tv1.setText("순매출 : 데이터가 없습니다");
		tv2.setText("객 수 : 데이터가 없습니다");
		tv3.setText("객단가 : 데이터가 없습니다");
		tv4.setText("매입금액 : 데이터가 없습니다");
		
		try {
			JSONObject son = m_results.getJSONObject(index);
					
			String rSale = String.format("순매출 : %s원", m_numberFormat.format(son.getInt("순매출")));
			String saleNum = String.format("객 수 : %s명", m_numberFormat.format(son.getInt("객수")));
			String salePri = String.format("객단가 : %s원", m_numberFormat.format(son.getInt("객단가")));
			String tPurPri = String.format("매입금액 : %s원", m_numberFormat.format(son.getInt("매입금액")));		

			tv1.setText(rSale);
			tv2.setText(saleNum);
			tv3.setText(salePri);
			tv4.setText(tPurPri);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
