package tipsystem.tips;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tipsystem.tips.models.ShopSelectItem;
import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
        
        String officeCode = LocalStorage.getString(this, "officeCode"); 
		if (!officeCode.equals("")) {
			showSelectShop();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
						
						String APP_EDATE = shop.getString("APP_EDATE");
						String APP_SDATE = shop.getString("APP_SDATE");
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date edate = formatter.parse(APP_EDATE);
						Date sdate = formatter.parse(APP_SDATE);

						Date today = new Date();
					    if(today.getTime() < sdate.getTime()) {

				    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				            builder.setTitle("알림");
				            builder.setMessage("아직 이용할수 없습니다.\r\n관리자에게 문의하세요\r\n1600-1833");
				            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
				                @Override
				                public void onClick(DialogInterface dialog, int which) {
				                }
				            });
				            builder.show();
				            return;
					    }
					    if(today.getTime() > edate.getTime()) {

				    		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				            builder.setTitle("알림");
				            builder.setMessage("기간이 지났습니다.\r\n관리자에게 문의하세요\r\n1600-1833");
				            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
				                @Override
				                public void onClick(DialogInterface dialog, int which) {
				                }
				            });
				            builder.show();
				            return;
					    }	

			    		LocalStorage.setJSONObject(MainActivity.this, "currentShopData", shop);
			        	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			        	startActivity(intent);
		                finish();
						   
					} catch (ParseException e) {
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).create();
		
		m_alert.show();
	}

	private View createCustomView() {
		LinearLayout linearLayoutView = new LinearLayout(this);
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
				String APP_EDATE = shop.getString("APP_EDATE");
				
				shopList.add(new ShopSelectItem(Office_Name, SHOP_IP, APP_EDATE, false));
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
	
	// 매장 정보 가져오기
    public void fetchOffices() {
    	
    	// 로딩 다이알로그 
    	dialog = new ProgressDialog(this);
 		dialog.setMessage("Loading....");
 		dialog.show();
 		
    	String phoneNumber = LocalStorage.getString(MainActivity.this, "phoneNumber");

    	// 쿼리 작성하기
	    String query =  "";
	    query = "select * " 
	    		+"  from APP_USER inner join V_OFFICE_USER " 
	    		+ " on APP_USER.OFFICE_CODE = V_OFFICE_USER.Sto_CD "
	    		+ " JOIN APP_SETTLEMENT on APP_USER.OFFICE_CODE = APP_SETTLEMENT.OFFICE_CODE " 
	    		+ " where APP_HP =" + phoneNumber + "AND DEL_YN = 0 ;";

	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				didAuthentication(results);
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
		    	Toast.makeText(getApplicationContext(), "알수없는 에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
			}
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
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
	    		+ " on APP_USER.OFFICE_CODE = V_OFFICE_USER.Sto_CD AND APP_USER.OFFICE_CODE="+code
	    		+ " JOIN APP_SETTLEMENT on APP_USER.OFFICE_CODE = APP_SETTLEMENT.OFFICE_CODE " 
	    		+ " where APP_HP =" + phoneNumber + "AND DEL_YN = 0 ;";

	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
				dialog.dismiss();
				dialog.cancel();
				if (results.length()>0) {
			    	Toast.makeText(getApplicationContext(), "인증성공", Toast.LENGTH_SHORT).show();
			    	EditText textView = (EditText) findViewById(R.id.editTextShopCode);
			    	String code = textView.getText().toString();
			        LocalStorage.setString(getApplicationContext(), "officeCode", code); 
			        
			        fetchOffices();
				}
				else {
			    	Toast.makeText(getApplicationContext(), "오피스코드가 없거나 등록되지 않은 휴대폰 번호입니다", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onRequestFailed(int code, String msg) {
				dialog.dismiss();
				dialog.cancel();
		    	Toast.makeText(getApplicationContext(), "알수없는 에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
			}
	    }).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
    }
    
    // DB에 접속후 호출되는 함수
    public void didAuthentication(JSONArray results) {
    	Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();

    	if (results.length() > 0) {
    		Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();

    		LocalStorage.setJSONArray(MainActivity.this, "shopsData", results);
        	showSelectShop();
    	}
    	else {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("인증에 실패했습니다.\r\n관리자에게 문의하세요\r\n1600-1833");
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
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
				holder.txtDate = (TextView) convertView.findViewById(R.id.textViewDate);
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
			String edate = object.get(position).getEdate();
			
			holder.object = object.get(position);
			holder.txtShopName.setText(name);
			holder.txtDate.setText("["+edate+"]");
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
		public TextView txtDate;
		public ShopSelectItem object;
		
		public ViewHolder(Context ctx) {
			this.ctx = ctx;
		}		
	};
}
