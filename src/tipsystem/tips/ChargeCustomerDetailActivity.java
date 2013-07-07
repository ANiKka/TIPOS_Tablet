package tipsystem.tips;

import java.text.NumberFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.JsonHelper;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
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

	EditText m_editTextCashDeduction;
	CheckBox m_checkBoxCard;
	CheckBox m_checkBoxCash;
	CheckBox m_checkBoxPoint;
	Button m_buttonPriceSearch;
	
	int m_qIndex = 0;
	int m_isTax = 0;
	int m_isCashR = 0;
	
	ProgressDialog dialog;
	NumberFormat m_numberFormat;
	
	HashMap<String, String> m_data = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_customer_detail);
		
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
		
		m_editTextCashDeduction = (EditText)findViewById(R.id.editTextCashDeduction);
		m_isCashDeduction = (CheckBox)findViewById(R.id.checkBoxIsCashDeduction);
		m_checkBoxCard =(CheckBox)findViewById(R.id.checkBoxCard);
		m_checkBoxCash =(CheckBox)findViewById(R.id.checkBoxCash);
		m_checkBoxPoint =(CheckBox)findViewById(R.id.checkBoxPoint);
		m_checkBoxCard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				doCalculateGongjae();
			}
		});
		m_checkBoxCash.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				doCalculateGongjae();
			}
		});
		m_checkBoxPoint.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				doCalculateGongjae();
			}
		});
		m_buttonPriceSearch =(Button)findViewById(R.id.buttonPriceSearch);
		m_buttonPriceSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doCalculateGongjae();
			}
		});
		
		Intent intent = getIntent();
		
		m_period1.setText(intent.getExtras().getString("PERIOD1"));
		m_period2.setText(intent.getExtras().getString("PERIOD2"));		
		m_customerCode.setText(intent.getExtras().getString("OFFICE_CODE"));
		m_customerName.setText(intent.getExtras().getString("OFFICE_NAME"));
				
		doCalculate();
	}

	private void doCalculateGongjae() {
		boolean isCard = m_checkBoxCard.isChecked();
		boolean isCashback = m_checkBoxCash.isChecked();
		boolean isPoint = m_checkBoxPoint.isChecked();
		boolean isCash = m_isCashDeduction.isChecked();

		double rsale = Double.valueOf(m_data.get("순매출"));
		double cashback = Double.valueOf(m_data.get("캐쉬백"));
		double cash = Double.valueOf(m_data.get("현영과세"));
		double card = Double.valueOf(m_data.get("카드수수료"));
		double point = Double.valueOf(m_data.get("포인트"));
		
		String r = m_editTextCashDeduction.getText().toString();
		
		double ratio = (r.equals(""))? 0: Double.valueOf(r);
		cash = cash * ratio / 100.0f;
		
		double m = 0;
		if (isCard)   m += card;
		if (isCashback)   m += cashback;
		if (isPoint)   m += point;
		if (isCash)   m += cash;
		
		rsale -= m;
		
		m_contents[21].setText(String.valueOf(m));	//공제금액
		m_contents[22].setText(String.valueOf(rsale));	//공제후지급액
	}
	
	private void doCalculate() {

		Intent intent = getIntent();
		String query ="";
		String customerCode = intent.getExtras().getString("OFFICE_CODE");
		String customerName = intent.getExtras().getString("OFFICE_NAME");
		String period1 = m_period1.getText().toString();
		String period2 = m_period2.getText().toString();
		int year1 = Integer.parseInt(period1.substring(0, 4));
		int month1 = Integer.parseInt(period1.substring(5, 7));
		
		String tableName = null;
		
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
				+ " ON A.SALE_NUM=F.C_JEONPYO Where B.Office_Sec = '2' And A.Office_Code Like '%"+ customerCode +"%' And A.Office_Name Like '%"+customerName+"%' "
				+ " AND A.Sale_Date >= '"+period1+"' AND A.Sale_Date <= '"+period2+"' Group By A.Office_Code, A.Office_Name ) G "
				+ " Group By G.Office_Code,G.Office_Name ) G LEFT JOIN ( Select Office_Code,순매입액 From ( Select Office_Code,Sum(In_Pri) '순매입액' "
				+ " From ( Select A.Office_Code,Sum(A.In_Pri) In_Pri From InD_"+tableName+" A Inner JOIN Office_Manage B ON A.Office_Code=B.Office_Code "
				+ " Where B.Office_Sec = '2' And A.Office_Code Like '%"+ customerCode +"%' AnD A.Office_Name Like '%"+customerName+"%' AND A.In_Date >= '"+period1+"' "
				+ " AND A.In_Date <= '"+period2+"' Group By A.Office_Code ) V Group By V.Office_Code ) V ) V On G.Office_Code=V.Office_Code "
				+ " ORDER BY G.Office_Code";

		new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				updateList(results);				
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
	}
	
	private void updateList(JSONArray results)
	{
		try {
			m_data = JsonHelper.toStringHashMap(results.getJSONObject(0));

			m_contents[0].setText(m_data.get("판매"));
			m_contents[1].setText(m_data.get("반품"));
			m_contents[2].setText(m_data.get("할인"));
			m_contents[3].setText(m_data.get("순매출"));
			m_contents[4].setText(m_data.get("과세매출"));
			m_contents[5].setText(m_data.get("면세매출"));
			m_contents[6].setText(m_data.get("현금매출"));
			m_contents[7].setText(m_data.get("현금과세"));
			m_contents[8].setText(m_data.get("현금면세"));
			m_contents[9].setText(m_data.get("카드매출"));
			m_contents[10].setText(m_data.get("카드과세"));
			m_contents[11].setText(m_data.get("카드면세"));
			m_contents[12].setText(m_data.get("현영매출"));
			m_contents[13].setText(m_data.get("현영과세"));
			m_contents[14].setText(m_data.get("현영면세"));
			m_contents[15].setText(m_data.get("수_카드금액"));
			m_contents[16].setText(String.format("%.2f", Float.valueOf(m_data.get("매장수수료"))));
			m_contents[17].setText(String.format("%.2f", Float.valueOf(m_data.get("카드수수료"))));
			m_contents[18].setText(m_data.get("포인트"));
			m_contents[19].setText(m_data.get("캐쉬백"));
			m_contents[20].setText(m_data.get("현영공제"));
			m_contents[21].setText(m_data.get("공제금액"));
			m_contents[22].setText(m_data.get("공제후지급액"));
			m_contents[23].setText(m_data.get("점유율"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		doCalculateGongjae();
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
	    
}
