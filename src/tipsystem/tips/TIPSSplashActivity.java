package tipsystem.tips;

import org.json.JSONArray;

import tipsystem.utils.LocalStorage;
import tipsystem.utils.MSSQL;
import tipsystem.utils.MSSQL2;
import tipsystem.utils.Reachability;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class TIPSSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        checkNetwork();
        
        savePhoneNumber(this);
        
        startMainActivity();
        
        testQuery();
    }

    private void checkNetwork() {
    	if (Reachability.isOnline(this) == false) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("네트워크에 연결되어 있지 않습니다! 네트워크 연결을 확인후 다시 실행시켜 주세요.");
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	finish();
                }
            });
            builder.show();
            return;
		}
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

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
	
	public void testQuery(){
		
		String tableName = null;		
		int year = 2013;
		int month = 6;
		
		tableName = " V_OFFICE_USER";// String.format("StD_%04d%02d", year, month);
		
		// 쿼리 작성하기
		String query =  "";
	    query += " select * from " + tableName + " ;";

		
	    // 콜백함수와 함께 실행
	    new MSSQL2(new MSSQL2.MSSQL2CallbackInterface() {

			@Override
			public void onRequestCompleted(JSONArray results) {
			}

			@Override
			public void onRequestFailed(int code, String msg) {
			}
			
	    //}).execute("122.49.118.102:18971", "TIPS", "sa", "tips", query);
		}).execute("122.49.118.102:18971", "Trans", "app_tips", "app_tips", query);
	
	}
}
