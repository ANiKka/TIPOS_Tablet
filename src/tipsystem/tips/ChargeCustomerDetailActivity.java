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
