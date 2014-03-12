package com.inappcoins;

import java.util.List;

import com.inappcoins.activity.InappActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

public class InAppCoin {

    private static String  developerSecretKey = null;
    private static String  appKey = null;
    private static String  clientUDID = null;
    private static Context context = null;
    private static boolean initialized = false;


    public static void init(Context context, String clientUDID, String appKey, String developerSecretKey) {
        InAppCoin.context = context;
        InAppCoin.clientUDID = new String(clientUDID);
        InAppCoin.appKey = new String(appKey);
        InAppCoin.developerSecretKey = new String(developerSecretKey);

        InappManager.init();
        Checker.init(context);

        InAppCoin.initialized = true;
    }

    public static String  getDeveloperSecretKey() { return developerSecretKey; }
    public static String  getAppKey()         	  { return appKey; }
    public static String  getClientUDID()         { return clientUDID; }
    public static boolean isInitialized()         { return initialized; }
    public static Context getContext()            { return context; }

    
    public static void getInappsFromServer() {
    	InappManager.downloadInappsFromServer();
    }
    
    public static void getInappsFromServer(Runnable runnable) {
    	InappManager.downloadInappsFromServer(runnable);
    }
    
    public static void isDownloadedInapps() {
    	InappManager.isDownloadedInapps();
    }

    public static String downloadInappURI(int index) {
    	return InappManager.downloadInappURI(index);
    }
    
    public static void manualCheckPayment() {
    	InappManager.checkPayment((PurchaseStatusListener)context);
    }
    
    public static void purchaseInapp(final int index) {
    	purchaseInapp(context, index);
    }

    public static void purchaseInapp(final Context contextForResult, final int index) {
    	Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                	String URI = InappManager.downloadInappURI(index);
                	Intent intent = new Intent(Intent.ACTION_VIEW);
                	intent.setData(Uri.parse(URI));
                	
                	
                	PackageManager manager = context.getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                    if (infos.size() > 0) {
                        ((Activity)contextForResult).getWindow().getDecorView().post(new Runnable() {
    						@Override
    						public void run() {
    							if(contextForResult instanceof InappActivityAgent) {
    								((InappActivityAgent)contextForResult).populateInappList();
    							} else {
    								Log.w(this.getClass().getSimpleName(), "purchaseInapp() activity has not implement InappActivityAgent interface");
    							}
    						}
    					});
                        
                        ((Activity)contextForResult).startActivityForResult(intent, Constants.SEND_BTC_PAYMENT);
                	} else {
                		Intent searchIntent = new Intent(Intent.ACTION_VIEW);
                		searchIntent.setData(Uri.parse("market://search?q=" + "btc wallet" + "&c=apps"));
                        ((Activity)contextForResult).startActivity(searchIntent);
                	}
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    
    public static void showInappsActivity() {
    	((Activity)context).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				System.out.println("starting activity InappActivity");
				Intent intent = new Intent(getContext(), InappActivity.class);
				
                ((Activity)context).startActivity(intent);
			}
		});
    }
}