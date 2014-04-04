InAppCoins Android SDK Documentation

Definitions

InAppItem - information about in-app package
Name 		- Name of the in-app
VC value 	- Reward value for the in-app
VC name 	- Reward currency type
Image 		- Name of the image for URL request
USD value	 - Price for the in-app in USD

RewardItem - information about accepted payment
Invoice ID	- 
VC value	- Reward value for the in-app
VC name 	- Reward currency type
Hash 		- Hash of the in-app for confirmation 
Datetime 	- Date and time when the purchase was accepted

InAppCoin - main class you will use
UDID		- the unique identifier of the gamer that purchased the product
App Key	- the identifier of the application (copy from developer section)
Secret Key	- the secret key to authenticate the developer (copy from developer section)
init()			- initialization of SDK
showInappsActivity() 	- show 

PurchaseActionListener - listens to 3rd party BTC wallet
PurchaseActionListener starts listening when the users is redirected to the BTC Wallet.
Please be advised that the PurchaseActionListener is currently under testing. 

userSentPurchase()		- called when user sent BTC
userCancelPurchase()	- called when user didn’t send BTC (e.g. cancel wallet application)

PurchaseStatusListener - listens to InAppCoins server
SDK automatically checks the server once in a while and calls one of these methods

purchaseSuccess()		- called when the server receives and confirms the payment
				- parameter RewardItem contains information about successful transaction
purchaseDidntReceivedYet()	- called when the server has not received any payment
purchaseFailure()		- called when the server receives payment with errors (e.g. fake purchase, hash mismatch)
				- parameter errors contains information about each error

Implementation

MainActivity.java
package com.inappscoins.examplepopup;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.inappcoins.InAppCoin;
import com.inappcoins.PurchaseActionListener;
import com.inappcoins.PurchaseStatusListener;
import com.inappcoins.RewardItem;

public class MainActivity extends Activity implements PurchaseStatusListener, PurchaseActionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String udid = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		InAppCoin.init(this, udid, "0000000-000000-000000-00000", "aaaaaaaaaaaaa");
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InAppCoin.showInnapsActivity();				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void purchaseSuccess(RewardItem reward) {
		// TODO: Your code. Here you add Virtual Currency to the Player
	}

	@Override
	public void purchaseDidntReceivedYet() {
		// TODO: Your code
	}

	@Override
	public void purchaseFailure(String[] error) {
		// TODO: Your code
	}

	@Override
	public void userSentPurchase() {
		// TODO: Your code		
	}

	@Override
	public void userCancelPurchase() {
		// TODO: Your code
	}
}



AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.packgame.name"
    android:versionCode="1"
    android:versionName="1.0" >
    
    <uses-permission android:name="android.permission.INTERNET"/>
    
    <uses-sdk
        android:minSdkVersion="9"
        android:targetSdkVersion="18" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
        <activity
            android:name="your.packgame.name.MainActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:label="@string/app_name" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity
            android:name="com.inappcoins.activity.InappActivity"
            android:theme="@android:style/Theme.Dialog"
            android:configChanges="keyboardHidden|orientation|screenSize" />

        </application>

</manifest>

Show in-app packages
InAppCoin.showInappsActivity();

