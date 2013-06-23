package tipsystem.tips;

/*
 * 기본관리 -> 상품관리 -> 검색버튼
 * */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class ManageProductListActivity extends Activity {

	JSONObject m_shop;
	String m_ip = "122.49.118.102";
	String m_port = "18971";
	
	private ProgressDialog dialog;
    List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();
    ArrayList<ProductList> productArray = new ArrayList<ProductList>();
    int index = 0;
    int size = 100;
    int firstPosition = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_product_list);
		// Show the Up button in the action bar.
		setupActionBar();

        m_shop = LocalStorage.getJSONObject(this, "currentShopData");
       
        try {
			m_ip = m_shop.getString("SHOP_IP");
	        m_port = m_shop.getString("SHOP_PORT");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		doSearch(index, size);
	}

	private void doSearch(int index, int size) {
		// TODO Auto-generated method stub
		// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
		// TODO Auto-generated method stub
		String query = "";
		
		query = "SELECT TOP " + size + " * FROM Goods WHERE BarCode NOT IN(SELECT TOP " + index + " BarCode FROM Goods);";
		
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length() > 0) {
					Log.w("confirm","search");
					settingArray(results);
		            Toast.makeText(getApplicationContext(), "조회 완료", Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(getApplicationContext(), "조회 실패", Toast.LENGTH_SHORT).show();					
				}
			}
	    }).execute(m_ip+":"+m_port, "TIPS", "sa", "tips", query);
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
		actionbar.setTitle("전체 상품 조회");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer_product_detail_view, menu);
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

	private void returnFillMaps(int position) {

		Intent intent = new Intent();

		ArrayList<String> sendArr = new ArrayList<String>();
		sendArr.add(productArray.get(position).Barcode);
		sendArr.add(productArray.get(position).G_Name);
		sendArr.add(productArray.get(position).Pur_Pri);
		sendArr.add(productArray.get(position).Sell_Pri);
		sendArr.add(productArray.get(position).Bus_Code);
		sendArr.add(productArray.get(position).Bus_Name);
		sendArr.add(productArray.get(position).taxYN);
		sendArr.add(productArray.get(position).stdSize);
		sendArr.add(productArray.get(position).obtain);
		sendArr.add(productArray.get(position).purCost);
		sendArr.add(productArray.get(position).profitRate);
		sendArr.add(productArray.get(position).L_Code);
		sendArr.add(productArray.get(position).M_Code);
		sendArr.add(productArray.get(position).S_Code);
		sendArr.add(productArray.get(position).L_Name);
		sendArr.add(productArray.get(position).M_Name);
		sendArr.add(productArray.get(position).S_Name);
		sendArr.add(productArray.get(position).surtax);
		
		intent.putExtra("fillmaps", sendArr);
		this.setResult(RESULT_OK, intent);
		finish();
	}
	
	public void settingArray(JSONArray results){
		
		ProductList pl;
		for(int i = 0; i < size-index; i++){
			JSONObject json;
			try {
				json = results.getJSONObject(i);
				pl = new ProductList(json.getString("BarCode"),
									 json.getString("G_Name"),
									 json.getString("Pur_Pri"),
									 json.getString("Sell_Pri"),
									 json.getString("Bus_Code"),
									 json.getString("Bus_Name"),
									 json.getString("Tax_YN"),
									 json.getString("Std_Size"),
									 json.getString("Obtain"),
									 json.getString("Pur_Cost"),
									 json.getString("Profit_Rate"),
									 json.getString("L_Code"),
									 json.getString("M_Code"),
									 json.getString("S_Code"),
									 json.getString("L_Name"),
									 json.getString("M_Name"),
									 json.getString("S_Name"),
									 json.getString("VAT_CHK"));
				productArray.add(pl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		ProductListAdapter ProductList = new ProductListAdapter(this, R.layout.activity_product_list, productArray);
		ListView m_listProduct = (ListView)findViewById(R.id.listviewManageProductList);
		m_listProduct.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            returnFillMaps(position);
	        }
	    });
		
		
		firstPosition = m_listProduct.getFirstVisiblePosition();
		m_listProduct.setAdapter(ProductList);
		m_listProduct.setSelection(firstPosition);

	}

	class ProductList {
		ProductList(String aBarcode, String aG_Name, String aPur_Pri, String aSell_Pri, String aBusCode, String aBusName,
					String ataxYN, String astdSize, String aobtain, String apurCost, String aprofitRate, String aL_Code,
					String aM_Code, String aS_Code,  String aL_Name, String aM_Name, String aS_Name, String asurtax){
			Barcode = aBarcode;
			G_Name = aG_Name;
			Pur_Pri = aPur_Pri;
			Sell_Pri = aSell_Pri;
			Bus_Code = aBusCode;
			Bus_Name= aBusName;
			taxYN = ataxYN;
			stdSize = astdSize;
			obtain = aobtain;
			purCost = apurCost;
			profitRate = aprofitRate;
			L_Code = aL_Code;
			M_Code = aM_Code;
			S_Code = aS_Code;
			L_Name = aL_Name;
			M_Name = aM_Name;
			S_Name = aS_Name;
			surtax = asurtax;
		}	
		String Barcode;
		String G_Name;
		String Pur_Pri;
		String Sell_Pri;
		String Bus_Code;
		String Bus_Name;
		String taxYN;
		String stdSize;
		String obtain;
		String purCost;
		String profitRate;
		String L_Code;
		String M_Code;
		String S_Code;
		String L_Name;
		String M_Name;
		String S_Name;
		String surtax;
		
	}
	
	class ProductListAdapter extends BaseAdapter 
	{

		Context ctx;
		LayoutInflater Inflater;
		ArrayList<ProductList> arr_Goods;
		int itemLayout;
		
		public ProductListAdapter(Context actx, int aitemLayout, ArrayList<ProductList> aarr_Goods)
		{
			ctx = actx;
			Inflater = (LayoutInflater)actx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arr_Goods = aarr_Goods;
			itemLayout = aitemLayout;
		}

		@Override
		public int getCount() {
			return arr_Goods.size();
		}
		@Override
		public String getItem(int position) {
			return arr_Goods.get(position).Barcode;
		}
		@Override
		public long getItemId(int position) {

			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			
			if (convertView == null) {
				convertView = Inflater.inflate(itemLayout, parent, false);
			} 
			
			TextView barcode = (TextView)convertView.findViewById(R.id.item1);
			TextView g_name = (TextView)convertView.findViewById(R.id.item2);
			TextView sell_pri = (TextView)convertView.findViewById(R.id.item3);
			TextView pur_pri = (TextView)convertView.findViewById(R.id.item4);
			
			
			barcode.setText(arr_Goods.get(position).Barcode);
			g_name.setText(arr_Goods.get(position).G_Name);
			sell_pri.setText(arr_Goods.get(position).Pur_Pri);
			pur_pri.setText(arr_Goods.get(position).Sell_Pri);

			if(position == size-3){
				index = size;
				size = size * 2;
				
				doSearch(index, size);
			}
			return convertView;
		}
	}
	

}

