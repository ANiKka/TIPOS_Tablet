package tipsystem.tips;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ManageCodeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_code);
		
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setTypeface(typeface);        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_code, menu);
		return true;
	}
	
	public void onClickManageCustomer(View view)
	{
		Intent intent = new Intent(this, ManageCustomerActivity.class);
    	startActivity(intent);
	}
	
	public void onClickManageProduct(View view)
	{
		Intent intent = new Intent(this, ManageProductActivity.class);
    	intent.putExtra("barcode", "");
    	startActivity(intent);
	}
	
	public void onClickComparePrice(View view)
	{
		Intent intent = new Intent(this, ComparePriceActivity.class);
    	startActivity(intent);
	}

}
