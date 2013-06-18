package tipsystem.tips;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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
	int mSelectedPosition = 0;
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
        
        savePhoneNumber(MainActivity.this);
        
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
					
					JSONArray shopsData = LocalStorage.getJSONArray(MainActivity.this, "shopsData");		
					try {
						JSONObject shop = shopsData.getJSONObject(mSelectedPosition);
			    		LocalStorage.setJSONObject(MainActivity.this, "currentShopData", shop);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
		        	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			    	intent.putExtra("selectedShopIndex", mSelectedPosition);
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
    	JSONArray shopsData = LocalStorage.getJSONArray(MainActivity.this, "shopsData");
		
		for( int i=0; i < shopsData.length(); i++) {
			try {
				JSONObject shop = shopsData.getJSONObject(i);
				String Office_Name = shop.getString("Office_Name");
				String SHOP_IP = shop.getString("SHOP_IP");
				//String SHOP_PORT = shop.getString("SHOP_PORT");
				
				shopList.add(new ShopSelectItem(Office_Name, SHOP_IP, false));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		listAdapter = new ShopListAdapter(this, R.layout.activity_select_shop_list, shopList);
		
		m_list.setAdapter(listAdapter);
	
		linearLayoutView.setOrientation(LinearLayout.VERTICAL);
		linearLayoutView.addView(m_list);
		return linearLayoutView;
	}
	
	private void savePhoneNumber(Context ctx)
    {
		//check phone number
    	TelephonyManager phoneManager = (TelephonyManager) 
    	getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    	String phoneNumber = phoneManager.getLine1Number();
    	
    	if (phoneNumber == null || phoneNumber.isEmpty()) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("알림");
            builder.setMessage("기기에 등록된 전화번호가 없습니다. 어플이용이 불가능합니다!");
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return ;
        }
    	else {
    		LocalStorage.setString(ctx, "phoneNumber", phoneNumber);
    	}
    }
	
    // 인증관련 실행 함수 
    public void onAuthentication(View view) {

 		// 입력된 코드 가져오기
    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
    	String code = textView.getText().toString();
    	if (code.equals("")) return;
    	
    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
    	String phoneNumber = LocalStorage.getString(MainActivity.this, "phoneNumber");

    	// 쿼리 작성하기
	    String query =  "";
	    //query = "select * from V_OFFICE_USER where Sto_CD =" + code + ";";
	    query = "select * " 
	    		+"  from APP_USER inner join V_OFFICE_USER " 
	    		+ " on APP_USER.OFFICE_CODE = V_OFFICE_USER.Sto_CD " 
	    		+ " JOIN APP_SETTLEMENT on APP_USER.OFFICE_CODE = APP_SETTLEMENT.OFFICE_CODE " 
	    		+ " where APP_HP =" + phoneNumber + ";"; 

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
    	Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();

		LocalStorage.setJSONArray(MainActivity.this, "shopsData", results);
    	showSelectShop();
    	/*
    	if (results.length() > 0) {
    		Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();

    		LocalStorage.setJSONArray(MainActivity.this, "shopsData", results);
        	showSelectShop();
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();
    	}*/
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
				holder.buttonConfig.setTag(position);
				holder.buttonConfig.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int idx = (Integer) v.getTag();

						JSONArray shopsData = LocalStorage.getJSONArray(MainActivity.this, "shopsData");	
						
						try {
							JSONObject shop = shopsData.getJSONObject(idx);
				    		LocalStorage.setJSONObject(MainActivity.this, "currentShopData", shop);
							Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
					    	startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}
			    		
					}
				});

				holder.radioShop.setTag(position);
				holder.radioShop.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mSelectedPosition = (Integer) v.getTag();
			            notifyDataSetChanged();
					}
				});
				
				holder.m_position = position;
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String name = object.get(position).getName();
			String strIP = object.get(position).getIP();
			
			holder.object = object.get(position);
			holder.txtShopName.setText(name);
			holder.radioShop.setChecked(position == mSelectedPosition);
			holder.txtIP.setText(strIP);
			
			return convertView;
		}
	}
	
	static class ViewHolder extends Activity 
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
	};
}
