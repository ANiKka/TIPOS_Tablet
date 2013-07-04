package tipsystem.tips;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

//import tipsystem.tips.CustomerProductDetailViewActivity.MyAsyncTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ChargeCustomerDetailActivity extends Activity {

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	
	TextView m_period1;
	TextView m_period2;
	TextView m_customerCode;
	TextView m_customerName;
	
	TextView m_contents[];
	
	CheckBox m_isCashDeduction;
	TextView m_ratioDeduction;
	
	int m_qIndex = 0;
	int m_isTax = 0;
	int m_isCashR = 0;
	
	ProgressDialog dialog;
	NumberFormat m_numberFormat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_customer_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		m_shop = LocalStorage.getJSONObject(this, "currentShopData");
	       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		m_period1 = (TextView)findViewById(R.id.textViewPeriod1);
		m_period2 = (TextView)findViewById(R.id.textViewPeriod2);
		m_customerCode = (TextView)findViewById(R.id.textViewCustomerCode);
		m_customerName = (TextView)findViewById(R.id.textViewCustomerName);
		
		m_numberFormat = NumberFormat.getInstance();
		
		m_contents = new TextView[24];
		
		
		m_contents[0] = (TextView)findViewById(R.id.content1);
		m_contents[1] = (TextView)findViewById(R.id.content2);
		m_contents[2] = (TextView)findViewById(R.id.content3);
		m_contents[3] = (TextView)findViewById(R.id.content4);
		m_contents[4] = (TextView)findViewById(R.id.content5);
		
		m_contents[5] = (TextView)findViewById(R.id.content6);
		m_contents[6] = (TextView)findViewById(R.id.content7);
		m_contents[7] = (TextView)findViewById(R.id.content8);
		m_contents[8] = (TextView)findViewById(R.id.content9);
		m_contents[9] = (TextView)findViewById(R.id.content10);
		
		m_contents[10] = (TextView)findViewById(R.id.content11);
		m_contents[11] = (TextView)findViewById(R.id.content12);
		m_contents[12] = (TextView)findViewById(R.id.content13);
		m_contents[13] = (TextView)findViewById(R.id.content14);
		m_contents[14] = (TextView)findViewById(R.id.content15);
		
		m_contents[15] = (TextView)findViewById(R.id.content16);
		m_contents[16] = (TextView)findViewById(R.id.content17);
		m_contents[17] = (TextView)findViewById(R.id.content18);
		m_contents[18] = (TextView)findViewById(R.id.content19);
		m_contents[19] = (TextView)findViewById(R.id.content20);
		
		m_contents[20] = (TextView)findViewById(R.id.content21);
		m_contents[21] = (TextView)findViewById(R.id.content22);
		m_contents[22] = (TextView)findViewById(R.id.content23);
		m_contents[23] = (TextView)findViewById(R.id.content24);
		
		m_isCashDeduction = (CheckBox)findViewById(R.id.checkBoxIsCashDeduction);
		m_ratioDeduction = (TextView)findViewById(R.id.editTextCashDeduction);
		
		Intent intent = getIntent();
		
		m_period1.setText(intent.getExtras().getString("PERIOD1"));
		m_period2.setText(intent.getExtras().getString("PERIOD2"));
		
		m_customerCode.setText(intent.getExtras().getString("OFFICE_CODE"));
		m_customerName.setText(intent.getExtras().getString("OFFICE_NAME"));
				
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		executeQuery1("0", period1, period2, customerCode, customerName, "0", "0");
		executeQuery2("0", period1, period2, customerCode, customerName, "0", "0");
		
		executeQuery3("0", period1, period2, customerCode, customerName, "1", "0");
		executeQuery3("0", period1, period2, customerCode, customerName, "0", "0");
		
		executeQuery3("0", period1, period2, customerCode, customerName, "1", "1");
		executeQuery3("0", period1, period2, customerCode, customerName, "0", "1");
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
		actionbar.setTitle("수수료 거래처 매출상세");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.charge_customer_detail, menu);
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
	
	public void OnClickSearch(View v)
	{
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		String customerCode = m_customerCode.getText().toString();
		String customerName = m_customerName.getText().toString();
		
		
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		executeQuery1("0", period1, period2, customerCode, customerName, "0", "0");
		executeQuery2("0", period1, period2, customerCode, customerName, "0", "0");
		
		executeQuery3("0", period1, period2, customerCode, customerName, "1", "0");
		executeQuery3("0", period1, period2, customerCode, customerName, "0", "0");
		
		executeQuery3("0", period1, period2, customerCode, customerName, "1", "1");
		executeQuery3("0", period1, period2, customerCode, customerName, "0", "1");
		
	}
	private void doCalculate() {
		
		String query ="";

		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		String constraint = "";
		
		tableName = String.format("%04d%02d", year1, month1);

		query = "Select G.Office_Code,G.Office_Name, G.판매,G.반품,G.할인, G.순매출,G.과세매출,G.면세매출, G.현금매출, G.현금과세, G.현금면세, G.카드매출, G.카드과세, "
				+ " G.카드면세, G.현영매출, G.현영과세, G.현영면세, G.매출원가합계, ISNULL(V.순매입액,0) '순매입액', G.공병매출, G.수_카드금액, G.매장수수료, G.카드수수료, G.포인트, "
				+ " G.캐쉬백, (G.현영매출 * 0) / 100 '현영공제', '0' '공제금액', '0' '공제후지급액', G.이익금, G.이익률, "
				+ " '점유율'=CASE WHEN 0 <> 0 Then (순매출/0)*100  ELSE 0 End From ( Select G.Office_Code,G.Office_Name, Sum (G.판매) '판매', "
				+ " Sum(G.반품) '반품', Sum(G.할인) '할인', Sum (G.순매출) '순매출', Sum(G.과세매출) '과세매출', Sum(G.면세매출) '면세매출', Sum (G.현금매출) '현금매출',"
				+ " Sum(G.현금과세) '현금과세', Sum(G.현금면세) '현금면세', Sum (G.카드매출) '카드매출', Sum(G.카드과세) '카드과세', Sum(G.카드면세) '카드면세', "
				+ " Sum (G.현영매출) '현영매출', Sum(G.현영과세) '현영과세', Sum(G.현영면세) '현영면세', Sum (G.매출원가) '매출원가합계', Sum (G.공병매출) '공병매출', "
				+ " Sum (G.수_카드금액) '수_카드금액', Sum (G.매장수수료) '매장수수료', Sum (G.카드수수료) '카드수수료', Sum (G.S_Point) '포인트', "
				+ " Sum (G.S_CashBackPoint) '캐쉬백', Sum (G.이익금) '이익금', "
				+ " '이익률'=Case When Sum(G.이익금)=0 Or Sum(G.순매출)=0 Then 0 Else (Sum(G.이익금)/Sum(G.순매출))*100 End "
				+ " From ( Select A.Office_Code, A.Office_Name, '판매'=Sum(Case When A.Sale_Yn='1' Then A.TSell_Pri+A.Dc_Pri Else 0 End), "
				+ " '반품'=Sum(Case When A.Sale_Yn='0' Then A.TSell_RePri+A.Dc_Pri Else 0 End), "
				+ " '할인'=Sum(Case When A.Sale_Yn='1' Then A.DC_Pri Else A.DC_Pri *-1 End), Sum (a.TSell_Pri - a.TSell_RePri) '순매출', "
				+ " '과세매출'=Sum(Case When A.Tax_YN='1' Then A.TSell_Pri-A.TSell_RePri Else 0 End), "
				+ " '면세매출'=Sum(Case When A.Tax_YN='0' Then A.TSell_Pri-A.TSell_RePri Else 0 End), "
				+ " Sum ((a.TSell_Pri - a.TSell_RePri) - a.Card_Pri) '현금매출', '현금과세'=Sum(Case When A.Tax_YN='1' Then "
				+ " (A.TSell_Pri-A.TSell_RePri)-A.Card_Pri Else 0 End), '현금면세'=Sum(Case When A.Tax_YN='0' "
				+ " Then (A.TSell_Pri-A.TSell_RePri)-A.Card_Pri Else 0 End), Sum (a.Card_Pri) '카드매출', "
				+ " '카드과세'=Sum(Case When A.Tax_YN='1' Then A.Card_Pri Else 0 End), '카드면세'=Sum(Case When A.Tax_YN='0' Then A.Card_Pri Else 0 End), "
				+ " '현영매출'=Sum(Case When C.Cash_No<>'' Then CASE WHEN F.C_SALETYPE=0 THEN Round(A.Money_Per * F.C_PRICE, 4) "
				+ " ELSE Round(A.Money_Per * F.C_PRICE * -1, 4) END Else 0 End) , '현영과세'=Sum(Case When C.Cash_No<>'' AND A.TAx_YN='1' "
				+ " Then CASE WHEN F.C_SALETYPE=0 THEN Round(A.Money_Per * F.C_PRICE, 4) ELSE Round(A.Money_Per * F.C_PRICE * -1, 4) END Else 0 End), "
				+ " '현영면세'=Sum(Case When C.Cash_No<>'' AND A.TAx_YN='0' Then CASE WHEN F.C_SALETYPE=0 THEN Round(A.Money_Per * F.C_PRICE, 4) "
				+ " ELSE Round(A.Money_Per * F.C_PRICE * -1, 4) END Else 0 End) , '매출원가'=Sum(Case When A.Sale_YN='1' "
				+ " Then A.Pur_Pri*A.Sale_Count Else A.Pur_Pri*A.Sale_Count End), Sum (a.Bot_Sell) '공병매출', "
				+ " '수_카드금액'=Sum(Case When A.Card_YN='1' Then A.Card_Pri Else 0 End), '매장수수료'=Sum(Case When A.Sale_YN='1' "
				+ " Then (A.TSell_Pri+A.Dc_Pri)*(A.Fee/100) Else ((A.TSell_RePri+A.Dc_Pri)*(A.Fee/100))*-1 End), "
				+ " '카드수수료'=Sum(Case When A.Card_YN='0' Then a.Card_Pri * (a.Card_Fee / 100) Else 0 End), Sum(A.S_Point) S_Point, "
				+ " Sum(A.S_CashBackPoint) S_CashBackPoint , Sum (a.Profit_Pri) '이익금' From SaD_"+tableName+" A LEFT JOIN SaT_"+tableName+" C "
				+ " ON A.Sale_Num=C.Sale_Num LEFT JOIN Office_Manage B ON A.Office_Code=B.Office_Code LEFT JOIN Cash_Receip_Log F "
				+ " ON A.SALE_NUM=F.C_JEONPYO Where B.Office_Sec = '2' And A.Office_Code Like '%%' And A.Office_Name Like '%%' "
				+ " AND A.Sale_Date >= '"+period1+"' AND A.Sale_Date <= '"+period2+"' Group By A.Office_Code, A.Office_Name ) G "
				+ " Group By G.Office_Code,G.Office_Name ) G LEFT JOIN ( Select Office_Code,순매입액 From ( Select Office_Code,Sum(In_Pri) '순매입액' "
				+ " From ( Select A.Office_Code,Sum(A.In_Pri) In_Pri From InD_"+tableName+" A Inner JOIN Office_Manage B ON A.Office_Code=B.Office_Code "
				+ " Where B.Office_Sec = '2' And A.Office_Code Like '%%' AnD A.Office_Name Like '%%' AND A.In_Date >= '"+period1+"' "
				+ " AND A.In_Date <= '"+period2+"' Group By A.Office_Code ) V Group By V.Office_Code ) V ) V On G.Office_Code=V.Office_Code "
				+ " ORDER BY G.Office_Code";

		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				updateList1(results);
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	private void executeQuery1(String... urls)
	{
	    String period1 = urls[1];
		String period2 = urls[2];
		String customerCode = urls[3];
		String customerName = urls[4];
	
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int year2 = Integer.parseInt(period2.substring(0, 4));
		
		int month1 = Integer.parseInt(period1.substring(5, 7));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName1 = null;
		String tableName2 = null;
		String constraint = "";
		
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				tableName1 = String.format("SaT_%04d%02d", y, m);
				tableName2 = String.format("SaD_%04d%02d", y, m);
				
				if ( customerCode.equals("") != true )
				{
					constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
				}
				
				if ( customerName.equals("") != true)
				{
					constraint = setConstraint(constraint, "Office_Name", "=", customerName);
				}
				
				// 현금매출, 수카드금액, 매장수수료, 카드수수료, 포인트, 캐쉬백
    			query = query + "select Cash_Pri, Card_Pri, SU_CardPri, Cus_Point, CashBack_Point from " + tableName1;
    			query = query + " where Sale_Num in (select Sale_Num from " + tableName2 + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
   			
//				query = "select * "
//						+ "from " + tableName1 + " inner joint " +  tableName2
//						+ " on " + tableName1 + ".Sale_Num = " + tableName2 + ".Sale_Num"
//						+ " where " + tableName1 + ".Sale_Date between '" + period1 + "' and '" + period2 + "'";
//				
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
    			
    			query = query + "); ";
    			
    			query = query + "select TSell_Pri, TSell_RePri, DC_Pri, Fee, Card_Fee from " + tableName1;
    			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
    			
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
    			
    			query = query + "; ";
    			
    			
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				updateList1(results);
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
		
	}
	
	private void updateList1(JSONArray results)
	{

		try {
			
			int cashPri = 0;
			int cardPri = 0;
			int suCardPri = 0;
			int cusPoint = 0;
			int cashBackPoint = 0;
			int fee = 0;
			int cardFee = 0;
			
			int tSellPri = 0;
			int tSellRePri = 0;
			int dcPri = 0;
			int rSale = 0;
			
			int deductionPri = 0;
			
			if ( results.length() > 0 )
			{
				
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					cashPri = cashPri + son.getInt("Cash_Pri");
					cardPri = cardPri + son.getInt("Card_Pri");
					fee = fee + son.getInt("Fee");
					cardFee = cardFee + son.getInt("Card_Fee");
					
					tSellPri = tSellPri + son.getInt("TSell_Pri");
					tSellRePri =  tSellRePri + son.getInt("TSell_RePri");
					dcPri = dcPri + son.getInt("DC_Pri");
					
					if (son.getString("Card_YN").toString().equals("1") == true )
					{
						suCardPri = suCardPri + cardPri;
					}
					
					cusPoint = cusPoint + son.getInt("Cus_Point");
					cashBackPoint = cashBackPoint + son.getInt("CashBack_Point");
					
				}

				rSale = tSellPri - (tSellRePri + dcPri);
				
				m_contents[6].setText(m_numberFormat.format(cashPri));
				        		
				m_contents[9].setText(m_numberFormat.format(cardPri));
				
				m_contents[15].setText(m_numberFormat.format(suCardPri));
				m_contents[18].setText(m_numberFormat.format(cusPoint));
				m_contents[19].setText(m_numberFormat.format(cashBackPoint));
				
				m_contents[0].setText(m_numberFormat.format(tSellPri));
				m_contents[1].setText(m_numberFormat.format(tSellRePri));
				m_contents[2].setText(m_numberFormat.format(dcPri));
				m_contents[3].setText(m_numberFormat.format(rSale));
				
				m_contents[16].setText(m_numberFormat.format(fee));
				m_contents[17].setText(m_numberFormat.format(cardFee));
				
				if ( m_isCashDeduction.isChecked() == true )
				{
					double ratio = Double.parseDouble(m_ratioDeduction.getText().toString());
					ratio = ratio / 100.0f;
					
					m_contents[20].setText(m_numberFormat.format((int)(cashPri*ratio)));
					
					deductionPri =  (suCardPri + fee + cardFee + cusPoint + cashBackPoint + (int)(cashPri*ratio));
					
				}
				else 
				{
					m_contents[20].setText("0");
					deductionPri =  (suCardPri + fee + cardFee + cusPoint + cashBackPoint);
				}
				
				m_contents[21].setText(m_numberFormat.format(deductionPri));
				m_contents[22].setText(m_numberFormat.format(rSale - deductionPri));
				
				if ( tSellPri == 0 )
				{
					m_contents[23].setText(String.format("%.2f", 0f));
				}
				else
				{
					m_contents[23].setText(String.format("%.2f", rSale /  tSellPri * 100.0f));
				}
				
			}
			else 
			{
				m_contents[6].setText(m_numberFormat.format(0));
        		
				m_contents[9].setText(m_numberFormat.format(0));
				
				m_contents[15].setText(m_numberFormat.format(0));
				m_contents[18].setText(m_numberFormat.format(0));
				m_contents[19].setText(m_numberFormat.format(0));
				
				m_contents[0].setText(m_numberFormat.format(0));
				m_contents[1].setText(m_numberFormat.format(0));
				m_contents[2].setText(m_numberFormat.format(0));
				m_contents[3].setText(m_numberFormat.format(0));
				
				m_contents[16].setText(m_numberFormat.format(0));
				m_contents[17].setText(m_numberFormat.format(0));
				
				m_contents[20].setText(m_numberFormat.format(0));
				m_contents[21].setText(m_numberFormat.format(0));
				m_contents[22].setText(m_numberFormat.format(0));
				m_contents[23].setText(String.format("%.2f", 0f));
				
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void executeQuery2(String... urls)
	{
	    String period1 = urls[1];
		String period2 = urls[2];
		String customerCode = urls[3];
		String customerName = urls[4];
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int year2 = Integer.parseInt(period2.substring(0, 4));
		
		int month1 = Integer.parseInt(period1.substring(5, 7));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName1 = null;
		String tableName2 = null;
		String constraint = "";
		
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				tableName1 = String.format("SaT_%04d%02d", y, m);
				tableName2 = String.format("SaD_%04d%02d", y, m);
				
				
				if ( customerCode.equals("") != true )
				{
					constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
				}
				
				if ( customerName.equals("") != true)
				{
					constraint = setConstraint(constraint, "Office_Name", "=", customerName);
				}
				
				// 현영매출
    			query = query + "select TSell_Pri from " + tableName1;
    			query = query + " where Cash_No is not NULL and Sale_Num in (select Sale_Num from " + tableName2 + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
   			
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
    			
    			query = query + "); ";
    			
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				updateList2(results);
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
		
	}
	
	private void updateList2(JSONArray results)
	{

		try {
			
			int tSellPri = 0;
			
			if ( results.length() > 0 )
			{
				
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					tSellPri = tSellPri + son.getInt("TSell_Pri");					
				}

				m_contents[12].setText(m_numberFormat.format(tSellPri));
				
			}
			else 
			{
				m_contents[12].setText(m_numberFormat.format(0));
				
			}
			
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void executeQuery3(String... urls)
	{

	    String period1 = urls[1];
		String period2 = urls[2];
		String customerCode = urls[3];
		String customerName = urls[4];
		String isTax = urls[5];
		String isCashR = urls[6];
		
		String query = "";
	    
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int year2 = Integer.parseInt(period2.substring(0, 4));
		
		int month1 = Integer.parseInt(period1.substring(5, 7));
		int month2 = Integer.parseInt(period2.substring(5, 7));
		
		String tableName1 = null;
		String tableName2 = null;
		String constraint = "";
		
		final int tisTax = Integer.parseInt(isTax);
		final int tisCashR = Integer.parseInt(isCashR);
		
		for ( int y = year1; y <= year2; y++ )
		{
			for ( int m = month1; m <= month2; m++ )
			{
				tableName1 = String.format("SaT_%04d%02d", y, m);
				tableName2 = String.format("SaD_%04d%02d", y, m);
				
				
				if ( customerCode.equals("") != true )
				{
					constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
				}
				
				if ( customerName.equals("") != true)
				{
					constraint = setConstraint(constraint, "Office_Name", "=", customerName);
				}
				
				// 현영매출
    			query = query + "select TSell_Pri, Cash_Pri, Card_Pri from " + tableName1;
    			query = query + " where ";
    			
    			if ( isCashR.equals("1") == true )
    			{
    				query = query + "Cash_No is not NULL and ";
    			}
    			
    			query = query + "Sale_Num in (select Sale_Num from " + tableName2 + " where Tax_YN = '" +
    					isTax + "' and Sale_Date between '" + period1 + "' and '" + period2 + "'";
   			
    			if ( constraint.equals("") != true )
    			{
    				query = query + " and " + constraint;
    			}
    			
    			query = query + "); ";
    			
			}
		}
		
		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				updateList3(results, tisCashR, tisTax);
				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
		
		
	}
	
	private void updateList3(JSONArray results, int isCashR, int isTax )
	{

		try {
			
			int tSellPri = 0;
			int cashPri = 0;
			int cardPri = 0;
			
			if ( results.length() > 0 )
			{
				
				for(int i = 0; i < results.length() ; i++)
				{
					JSONObject son = results.getJSONObject(i);
					
					tSellPri = tSellPri + son.getInt("TSell_Pri");
		    		cashPri = cashPri + son.getInt("Cash_Pri");
		    		cardPri = cardPri + son.getInt("Card_Pri");
				}

				if ( isCashR == 0 && isTax == 1 )
				{
					m_contents[4].setText(m_numberFormat.format(tSellPri));
					m_contents[7].setText(m_numberFormat.format(cashPri));
					m_contents[10].setText(m_numberFormat.format(cardPri));
				}
				else if ( isCashR == 0 && isTax == 0 )
				{
					m_contents[5].setText(m_numberFormat.format(tSellPri));
					m_contents[8].setText(m_numberFormat.format(cashPri));
					m_contents[11].setText(m_numberFormat.format(cardPri));
				}
				else if ( isCashR == 1 && isTax == 1 )
				{
					m_contents[13].setText(m_numberFormat.format(tSellPri));
				}
				else if ( isCashR == 1 && isTax == 0 )
				{
					m_contents[14].setText(m_numberFormat.format(tSellPri));
				}
				
			}
			else 
			{
				if ( isCashR == 0 && isTax == 1 )
				{
					m_contents[4].setText(m_numberFormat.format(0));
					m_contents[7].setText(m_numberFormat.format(0));
					m_contents[10].setText(m_numberFormat.format(0));
				}
				else if ( isCashR == 0 && isTax == 0 )
				{
					m_contents[5].setText(m_numberFormat.format(0));
					m_contents[8].setText(m_numberFormat.format(0));
					m_contents[11].setText(m_numberFormat.format(0));
				}
				else if ( isCashR == 1 && isTax == 1 )
				{
					m_contents[13].setText(m_numberFormat.format(0));
				}
				else if ( isCashR == 1 && isTax == 0 )
				{
					m_contents[14].setText(m_numberFormat.format(0));
				}
				
			}
			
			dialog.cancel();
			Toast.makeText(getApplicationContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();
			
		} catch (JSONException e) {
			e.printStackTrace();
			dialog.cancel();
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
    
}
