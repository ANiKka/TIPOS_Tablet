package tipsystem.tips;


import org.json.JSONArray;

import tipsystem.utils.MSSQL;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "unikys.todo.MESSAGE";

	// loading bar
	private ProgressDialog dialog; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Fonts/NanumGothic.ttf");
        TextView textView = (TextView) findViewById(R.id.textViewShopCode);
        textView.setTypeface(typeface);
        
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
        	Intent intent = new Intent(this, ConfigActivity.class);
        	startActivity(intent);
    	}
    	else {
    		Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();
    	}
    }
}
