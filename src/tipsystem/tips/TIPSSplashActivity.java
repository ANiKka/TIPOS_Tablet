package tipsystem.tips;

import tipsystem.utils.LocalStorage;
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
        startActivity(intent);
    }
}
