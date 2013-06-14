package tipsystem.tips;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

public class ConfigActivity extends Activity implements OnClickListener {

	public final static String EXTRA_MESSAGE = "unikys.todo.MESSAGE";
	public ListView m_list;
	AlertDialog m_alert;
	public RadioGroup m_rgShop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Intent intent = getIntent();
		
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewID);
        textView.setTypeface(typeface);
        
        
        textView = (TextView) findViewById(R.id.textViewPW);
        textView.setTypeface(typeface);
        
        m_rgShop = new RadioGroup(this);
        
		showSelectShop();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			ActionBar actionbar = getActionBar();         
			LinearLayout custom_action_bar = (LinearLayout) View.inflate(this, R.layout.activity_custom_actionbar, null);
			actionbar.setCustomView(custom_action_bar);

			actionbar.setDisplayShowHomeEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
			actionbar.setDisplayShowCustomEnabled(true);
			
			getActionBar().setDisplayHomeAsUpEnabled(true);

			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.config, menu);
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

	public void onClickLogin(View view)
	{
		Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(this, MainMenuActivity.class);
    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
	}
	
	public void showShopDetail(int index)
	{
		m_alert.cancel();
		
		Intent intent = new Intent(this, ConfigDetailActivity.class);
    	//Intent intent = new Intent(this, SelectShopActivity.class);    	
    	//EditText editText = (EditText) findViewById(R.id.editTextShopCode);
    	//String message = editText.getText().toString();
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
		
	}
	
	public void showSelectShop()
	{
		
		//LayoutInflater layout2 = getLayoutInflater();
		//View v2 = layout2.inflate(R.layout.activity_select_shop_list, null);
		
		
		m_alert = new AlertDialog.Builder(this)
			.setTitle("매장을 선택하세요")
			.setView(createCustomView())
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.create();
		
		m_alert.show();
	}
	
	private View createCustomView()
	{
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
			// TODO Auto-generated method stub
			return object.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return m_list.getItemAtPosition(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if ( convertView == null )
			{
				LayoutInflater inflater = LayoutInflater.from(ConfigActivity.this);
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
			else
			{
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
		
		public ViewHolder(Context ctx)
		{
			this.ctx = ctx;
			
		}
		
		@Override
		public void onClick(View v) 
		{
			if ( v.equals(radioShop) == true )
			{
				Toast.makeText(ctx, "라디오클릭", Toast.LENGTH_SHORT).show();
				
				((RadioButton)v).setChecked(true);
				//((ConfigActivity) ctx).m_rgShop.check(radioShop.getId());
			}
			else
			{
				Toast.makeText(ctx, "버튼클릭" + m_position, Toast.LENGTH_SHORT).show();
				((ConfigActivity) ctx).showShopDetail(0);
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
