package com.inappcoins;

public interface PurchaseStatusListener {

	public void purchaseSuccess(RewardItem reward);
	public void purchaseDidntReceivedYet();
	public void purchaseFailure(String[] error);
}
