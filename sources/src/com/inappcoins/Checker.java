package com.inappcoins;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;

public class Checker extends Timer{
	private static long defaultDelay = 30*1000;
	private static long defaultPeriod = 30*1000;
	private static int[] defaultArray = {30*1000, 30*1000, 30*1000, 30*1000, 1*60*1000, 1*60*1000, 2*60*1000, 2*60*1000, 5*60*1000, 5*60*1000, 15*60*1000, 30*60*1000, 60*60*1000};
	private static int[] testArray = {10*1000, 5*1000, 10*1000, 10*1000};

	private static Timer timer;
	private static int arrayIndex = 0;
	private static int[] delayArray;
	private static boolean isScheduled = false;
	private static boolean isEnded = false;
	private static boolean initialized = false;
	
	public static void init(final Context context) {
		Checker.delayArray = defaultArray;
		Checker.restart();
		Checker.initialized = true;
	}
	
	public static void restart() {
		System.out.println("Restarting checker...");
		Checker.arrayIndex = 0;
		Checker.isEnded = false;
		Checker.setNextCheck();
	}
	
	public static void setNextCheck() {
		if(!isEnded) {
			if(arrayIndex >= delayArray.length) {
				arrayIndex = 0;
			}
			if(isScheduled) {
				timer.cancel();
				isScheduled = false;
			}
			timer = new Timer("BTC payment checker");
			timer.schedule(new CheckerTask(), delayArray[arrayIndex]); //(timerTask, defaultDelay, defaultPeriod);
			isScheduled = true;
			arrayIndex++;
			if(arrayIndex >= delayArray.length) {
				isEnded = true;
			}
		}
	}
	
	public static boolean isInitialized() {
		return initialized;
	}
}

class CheckerTask extends TimerTask {
	@Override
	public void run() {
		System.out.println("checking payment...");
		((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				if(!Checker.isInitialized()) {
		    		System.err.println("Warning: Checker is not initialized");
				}
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						InappManager.checkPayment((PurchaseStatusListener)InAppCoin.getContext());
						Checker.setNextCheck();
					}
				});
				t.start();
			}
		});
	}
}
