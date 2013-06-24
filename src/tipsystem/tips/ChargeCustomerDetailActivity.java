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

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ChargeCustomerDetailActivity extends Activity {

	
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
 		
	
		new MyAsyncTask().execute("0", period1, period2, customerCode, customerName, "", ""); // SaD
		//new MyAsyncTask().execute("1", period1, period2, customerCode, customerName, "", ""); // SaT
		new MyAsyncTask().execute("2", period1, period2, customerCode, customerName, "", ""); // 현영매출
		new MyAsyncTask().execute("3", period1, period2, customerCode, customerName, "1", "0"); // 과세매출
		new MyAsyncTask().execute("3", period1, period2, customerCode, customerName, "0", "0"); // 면세매출
		
		new MyAsyncTask().execute("3", period1, period2, customerCode, customerName, "1", "1"); // 과세매출
		new MyAsyncTask().execute("3", period1, period2, customerCode, customerName, "0", "1"); // 면세매출
		
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
	
	
	class MyAsyncTask extends AsyncTask<String, Integer, String>{

        ArrayList<JSONObject> CommArray=new ArrayList<JSONObject>();
        
        int m_tabIndex = 0;
        
        protected String doInBackground(String... urls) 
        {
        	Log.i("Android"," MSSQL Connect Example.");
        	Connection conn = null;
        	ResultSet reset =null;
        	
        	try {
        	    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        	    Log.i("Connection","MSSQL driver load");

        	    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://122.49.118.102:18971/TIPS","sa","tips");
        	   // conn = DriverManager.getConnection("jdbc:jtds:sqlserver://172.30.1.18:1433/TIPS","sa","tips");
        	    Log.i("Connection","MSSQL open");
        	    Statement stmt = conn.createStatement();
        	    
        	    String tabIndex = urls[0];
        	    
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
        		
        		m_qIndex = Integer.parseInt(tabIndex);
        		m_isTax = Integer.parseInt(isTax);
        		m_isCashR = Integer.parseInt(isCashR);
        		
        		
        		if ( m_qIndex == 0)
        		{
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
                			//query = "select Cash_Pri, Card_Pri, SU_CardPri, Cus_Point, CashBack_Point from " + tableName1;
                			//query = query + " where Sale_Num in (select Sale_Num from " + tableName2 + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
        					query = "select * "
        							+ "from " + tableName1 + " inner joint " +  tableName2
        							+ " on " + tableName1 + ".Sale_Num = " + tableName2 + ".Sale_Num"
        							+ " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
        					
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			//query = query + ")";
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next())
                    	    {
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            					
            				    Obj.put("Cash_Pri",reset.getInt("Cash_Pri"));
            				    Obj.put("Card_Pri",reset.getInt("Card_Pri"));
            				    Obj.put("Card_YN",reset.getString("Card_YN"));
            				    Obj.put("Cus_Point",reset.getInt("Cus_Point"));
            				    Obj.put("CashBack_Point",reset.getInt("CashBack_Point"));
            				    Obj.put("Fee", reset.getInt("Fee"));
            				    Obj.put("Card_Fee", reset.getInt("Card_Fee"));
            				    Obj.put("TSell_Pri", reset.getInt("TSell_Pri"));
            				    Obj.put("TSell_RePri", reset.getInt("TSell_RePri"));
            				    Obj.put("DC_Pri", reset.getInt("DC_Pri"));
            				    
            				    CommArray.add(Obj);
            				}
            			}
            			
            		}        			
        		}
    			else if ( m_qIndex == 1 )
    			{
/*    				
    				for ( int y = year1; y <= year2; y++ )
            		{
            			for ( int m = month1; m <= month2; m++ )
            			{
            				
            				tableName1 = String.format("SaD_%04d%02d", y, m);
            				
        					
        					if ( customerCode.equals("") != true )
        					{
        						constraint = setConstraint(constraint, "Office_Code", "=", customerCode);
        					}
        					
        					if ( customerName.equals("") != true)
        					{
        						constraint = setConstraint(constraint, "Office_Name", "=", customerName);
        					}
        					
        					// 판매, 반품, 할인, 순매출, 현금매출, 카드매출, 수카드금액, 매장수수료, 카드수수료, 포인트, 캐쉬백
                			query = "select TSell_Pri, TSell_RePri, DC_Pri, Fee, Card_Fee from " + tableName1;
                			query = query + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next())
                    	    {
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:
            				    Obj.put("TSell_Pri",reset.getInt(1));
            				    Obj.put("TSell_RePri",reset.getInt(2));
            				    Obj.put("DC_Pri",reset.getInt(3));
            				    Obj.put("Fee",reset.getInt(4));
            				    Obj.put("Card_Fee",reset.getInt(5));
            				    CommArray.add(Obj);
            				}
            			}
            			
            		}        			
*/    				
    			}
    			else if ( m_qIndex == 2 )
    			{
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
                			query = "select TSell_Pri from " + tableName1;
                			query = query + " where Cash_No is not NULL and Sale_Num in (select Sale_Num from " + tableName2 + " where Sale_Date between '" + period1 + "' and '" + period2 + "'";
               			
                			if ( constraint.equals("") != true )
                			{
                				query = query + " and " + constraint;
                			}
                			
                			query = query + ")";
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next())
                    	    {
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:

            				    Obj.put("TSell_Pri", reset.getInt(1));
            				    
            				    CommArray.add(Obj);
            				}
            			}
            			
            		}        			
    				
    			}
    			else if ( m_qIndex == 3 )
    			{
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
                			query = "select TSell_Pri, Cash_Pri, Card_Pri from " + tableName1;
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
                			
                			query = query + ")";
                			
                			Log.e("HTTPJSON","query: " + query );
                        	reset = stmt.executeQuery(query);
            	        	    		
                    	    while(reset.next())
                    	    {
            					Log.w("HTTPJSON:",reset.getString(1));
            					
            					JSONObject Obj = new JSONObject();
            				    // original part looks fine:

            				    Obj.put("TSell_Pri", reset.getInt(1));
            				    Obj.put("Cash_Pri", reset.getInt(2));
            				    Obj.put("Card_Pri", reset.getInt(3));
            				    
            				    CommArray.add(Obj);
            				}
            			}
            			
            		}        			
    				
    			}
        		
        		        		
        	    conn.close();
        	
        	 } catch (Exception e)
        	 {
        	    Log.w("Error connection","" + e.getMessage());		   
        	 }
        	 
        	 // onProgressUpdate에서 0이라는 값을 받아서 처리
        	 publishProgress(0);
        	 return null;       	 
        }

        protected void onProgressUpdate(Integer[] values) {
            Log.e("HTTPJSON", "onProgressUpdate" );
        }

        protected void onPostExecute(String result) {
        	super.onPostExecute(result);
        	
        	if ( m_qIndex == 0 )
        	{
   
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
				
        		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
                		
        				cashPri = cashPri + json.getInt("Cash_Pri");
        				cardPri = cardPri + json.getInt("Card_Pri");
        				fee = fee + json.getInt("Fee");
        				cardFee = cardFee + json.getInt("Card_Fee");
        				
        				tSellPri = tSellPri + json.getInt("TSell_Pri");
        				tSellRePri =  tSellRePri + json.getInt("TSell_RePri");
        				dcPri = dcPri + json.getInt("DC_Pri");
        				
        				if (json.getString("Card_YN").toString().equals("1") == true )
        				{
        					suCardPri = suCardPri + cardPri;
        				}
        				
        				cusPoint = cusPoint + json.getInt("Cus_Point");
        				cashBackPoint = cashBackPoint + json.getInt("CashBack_Point");
        				
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
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
        		
        		m_contents[23].setText(String.format("%.2f", rSale /  tSellPri * 100.0f));
        		
        		//Toast.makeText(getApplicationContext(), "조회 완료: " + CommArray.size(), Toast.LENGTH_SHORT).show();
        		
        	}
        	else if ( m_qIndex == 1 )
        	{
  /*      		int tSellPri = 0;
				int tSellRePri = 0;
				int dcPri = 0;
				int fee = 0;
				int cardFee = 0;
				int rSale = 0;
				       		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
                		tSellPri = tSellPri + json.getInt("TSell_Pri");
        				tSellRePri =  tSellRePri + json.getInt("TSell_RePri");
        				dcPri = dcPri + json.getInt("DC_Pri");
        				fee = fee + json.getInt("Fee");
        				cardFee = cardFee + json.getInt("Card_Fee");
        				
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		rSale = tSellPri - (tSellRePri + dcPri);
        		
        		
        		m_contents[0].setText(String.format("%d", tSellPri));
        		m_contents[1].setText(String.format("%d", tSellRePri));
        		m_contents[2].setText(String.format("%d", dcPri));
        		m_contents[3].setText(String.format("%d", rSale));
        		
        		m_contents[16].setText(String.format("%d", fee));
        		m_contents[17].setText(String.format("%d", cardFee));
*/        		        		
        	}
        	else if ( m_qIndex == 2 )
        	{
        		int tSellPri = 0;
								       		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
                		tSellPri = tSellPri + json.getInt("TSell_Pri");
        				
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}

        		m_contents[12].setText(m_numberFormat.format(tSellPri));
      		
        	}
        	else if ( m_qIndex == 3 )
        	{
        		int tSellPri = 0;
        		int cashPri = 0;
        		int cardPri = 0;
								       		
            	Iterator<JSONObject> iterator = CommArray.iterator();
        		while (iterator.hasNext()) {
                	JSONObject json = iterator.next();
                	
                	try {
                		
                		tSellPri = tSellPri + json.getInt("TSell_Pri");
                		cashPri = cashPri + json.getInt("Cash_Pri");
                		cardPri = cardPri + json.getInt("Card_Pri");
        				
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		if ( m_isCashR == 0 && m_isTax == 1 )
        		{
        			m_contents[4].setText(m_numberFormat.format(tSellPri));
        			m_contents[7].setText(m_numberFormat.format(cashPri));
        			m_contents[10].setText(m_numberFormat.format(cardPri));
        		}
        		else if ( m_isCashR == 0 && m_isTax == 0 )
        		{
        			m_contents[5].setText(m_numberFormat.format(tSellPri));
        			m_contents[8].setText(m_numberFormat.format(cashPri));
        			m_contents[11].setText(m_numberFormat.format(cardPri));
        		}
        		else if ( m_isCashR == 1 && m_isTax == 1 )
        		{
        			m_contents[13].setText(m_numberFormat.format(tSellPri));
        		}
        		else if ( m_isCashR == 1 && m_isTax == 0 )
        		{
        			m_contents[14].setText(m_numberFormat.format(tSellPri));
        			
        			dialog.cancel();
        		}
        		
        		
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

}
