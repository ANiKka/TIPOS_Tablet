package tipsystem.tips;


import java.util.ArrayList;

import org.json.JSONArray;

import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "unikys.todo.MESSAGE";

	public ListView m_list;
	AlertDialog m_alert;
	public RadioGroup m_rgShop;
	// loading bar
	private ProgressDialog dialog; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewShopCode);
        textView.setTypeface(typeface);

        m_rgShop = new RadioGroup(this);
        // test
        updateTestData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	// Private Methods
    public void updateTestData() {

    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	textView.setText("0000001");
    }
    
	public void showSelectShop() {
		m_alert = new AlertDialog.Builder(this)
			.setTitle("매장을 선택하세요")
			.setView(createCustomView())
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

		        	Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
		        	startActivity(intent);
				}
			})
			.create();
		
		m_alert.show();
	}

	private View createCustomView() {
		LinearLayout linearLayoutView = new LinearLayout(this);
		//ListView list =  (ListView)findViewById(R.id.listShops);
		m_list =  new ListView(this);
		
		ArrayList<ShopSelectItem> shopList;
		ShopListAdapter listAdapter;
			
		shopList = new ArrayList<ShopSelectItem>();
		
		shopList.add(new ShopSelectItem("그린마트","192.168.123.100", true));
		shopList.add(new ShopSelectItem("사러가마트","192.168.123.101", false));
		shopList.add(new ShopSelectItem("고향마트","192.168.123.102", false));
		shopList.add(new ShopSelectItem("갈현마트","192.168.123.103", false));
		shopList.add(new ShopSelectItem("마트1","192.168.123.104", false));
		shopList.add(new ShopSelectItem("2","192.168.123.104", false));
		shopList.add(new ShopSelectItem("3","192.168.123.105", false));
		shopList.add(new ShopSelectItem("4","192.168.123.106", false));
		shopList.add(new ShopSelectItem("5","192.168.123.107", false));
		
		listAdapter = new ShopListAdapter(this, R.layout.activity_select_shop_list, shopList);
		
		m_list.setAdapter(listAdapter);
	
		linearLayoutView.setOrientation(LinearLayout.VERTICAL);
		linearLayoutView.addView(m_list);
		return linearLayoutView;
	}
	
    // 인증관련 실행 함수 
    public void onAuthentication(View view) {

    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.setCancelable(false);
 		dialog.show();
 		
 		// 입력된 코드 가져오기
    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	String code = textView.getText().toString();
    	if (code.equals("")) return;

    	// 쿼리 작성하기
	    String query =  "";
	    //query = "select * from V_OFFICE_USER where Sto_CD =" + code + ";";
	    query =  "select * " 
	    		+ "from V_OFFICE_USER, APP_SETTLEMENT " 
	    		+ "where Sto_CD = " + code
	    		+ ";";
	    
	    // 콜백함수와 함께 실행
	    new MSSQL(new MSSQL.MSSQLCallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didAuthentication(results);
			}
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
    }
    
    // DB에 접속후 호출되는 함수
    public void didAuthentication(JSONArray results) {
    	if (results.length() > 0) {
    		Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();        	
        	showSelectShop();
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();
    	}
    }
    
	class ShopListAdapter extends BaseAdapter 
	{
		Context ctx;
		int itemLayout;
		
		private ArrayList<ShopSelectItem> object;
		public ShopListAdapter(Context ctx, int itemLayout, ArrayList<ShopSelectItem> object)
		{
			super();
			this.object = object;
			this.ctx = ctx;
			this.itemLayout = itemLayout;
		}
		@Override
		public int getCount() {
			return object.size();
		}
		@Override
		public Object getItem(int position) {
			return m_list.getItemAtPosition(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if ( convertView == null ) {
				LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
				convertView = inflater.inflate(R.layout.activity_select_shop_list, parent, false);
				holder = new ViewHolder(ctx);
				holder.radioShop = (RadioButton) convertView.findViewById(R.id.radioButtonShop);
				
				//holder.radioShop.setBackgroundResource(R.drawable.check_icon);
				holder.txtIP = (TextView) convertView.findViewById(R.id.textViewShopIP);
				holder.buttonConfig = (Button) convertView.findViewById(R.id.buttonShopConfig);
				holder.txtShopName = (TextView) convertView.findViewById(R.id.textViewShopName);
				holder.buttonConfig.setOnClickListener(holder);
				holder.radioShop.setOnClickListener(holder);
				
				holder.m_position = position;
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String name = object.get(position).getName();
			String strIP = object.get(position).getIP();
			boolean isSelect = object.get(position).getIsSelect();
			
			holder.object = object.get(position);
			
			holder.txtShopName.setText(name);
			holder.radioShop.setChecked(isSelect);
			holder.txtIP.setText(strIP);
			
			//((ConfigActivity) ctx).m_rgShop.addView(holder.radioShop);
			
			return convertView;
		}
	}
	
	static class ViewHolder extends Activity implements View.OnClickListener 
	{
		public Context ctx;
		public RadioButton radioShop;
		public TextView txtIP;
		public Button buttonConfig;
		public int m_position;
		public TextView txtShopName;
		public ShopSelectItem object;
		
		public ViewHolder(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public void onClick(View v) 
		{
			if ( v.equals(radioShop) == true ) {
				Toast.makeText(ctx, "라디오클릭", Toast.LENGTH_SHORT).show();
				
				((RadioButton)v).setChecked(true);
				//((ConfigActivity) ctx).m_rgShop.check(radioShop.getId());
			}
			else {
				Toast.makeText(ctx, "버튼클릭" + m_position, Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(this, ConfigDetailActivity.class);
		    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
		    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
		    	//String message = editText.getText().toString();
		    	//intent.putExtra(EXTRA_MESSAGE, message);
		    	startActivity(intent);
			}
		}
	};
}
