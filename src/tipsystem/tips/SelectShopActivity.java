package tipsystem.tips;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;


public class SelectShopActivity extends Activity {

	ListView list;
	ArrayList<ShopSelectItem> shopList;
	ShopListAdapter listAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_shop);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Log.i("KWJ", "1");
		list =  (ListView)findViewById(R.id.listShops);
		Log.i("KWJ", "2");
		shopList = new ArrayList<ShopSelectItem>();
		
		shopList.add(new ShopSelectItem("그린마트","192.168.123.100", true));
		shopList.add(new ShopSelectItem("ab마트","192.168.123.101", false));
		shopList.add(new ShopSelectItem("사러가마트","192.168.123.102", false));
		shopList.add(new ShopSelectItem("고향마트","192.168.123.103", false));
		shopList.add(new ShopSelectItem("갈현마트","192.168.123.104", false));
		shopList.add(new ShopSelectItem("2마트","192.168.123.104", false));
		shopList.add(new ShopSelectItem("3마트","192.168.123.105", false));
		shopList.add(new ShopSelectItem("4마트","192.168.123.106", false));
		shopList.add(new ShopSelectItem("5마트","192.168.123.107", false));
		
		Log.i("KWJ", "3");
		listAdapter = new ShopListAdapter(this, R.layout.activity_select_shop_list, shopList);
		Log.i("KWJ", "4");
		list.setAdapter(listAdapter);
		Log.i("KWJ", "5");
	
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_shop, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
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
			return list.getItemAtPosition(position);
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
				LayoutInflater inflater = LayoutInflater.from(SelectShopActivity.this);
				convertView = inflater.inflate(R.layout.activity_select_shop_list, parent, false);
				holder = new ViewHolder(ctx);
				holder.shopName = (RadioButton) convertView.findViewById(R.id.radioButtonShop);
				holder.txtIP = (TextView) convertView.findViewById(R.id.textViewShopIP);
				holder.buttonConfig = (Button) convertView.findViewById(R.id.buttonShopConfig);
				holder.buttonConfig.setOnClickListener(holder);
				holder.shopName.setOnClickListener(holder);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			String name = object.get(position).getName();
			String strIP = object.get(position).getIP();
			boolean isSelect = object.get(position).getIsSelect();
			
			holder.shopName.setText(name);
			holder.shopName.setChecked(isSelect);
			holder.txtIP.setText(strIP);
			holder.buttonConfig.setText("config");
			return convertView;
			
		}
		
	}
	
	static class ViewHolder extends Activity implements View.OnClickListener 
	{
		Context ctx;
		RadioButton shopName;
		TextView txtIP;
		Button buttonConfig;
		
		public ViewHolder(Context ctx)
		{
			this.ctx = ctx;
		}
		
		@Override
		public void onClick(View v) 
		{
			if ( v.equals(shopName) == true )
			{
				Toast.makeText(ctx, "라디오클릭.", Toast.LENGTH_SHORT).show();
				
			}
			else
			{
				Toast.makeText(ctx, "버튼클릭.", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	
}
