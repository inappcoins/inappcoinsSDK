package com.inappcoins;

public class RewardItem {
	private String invoice_id, vc_value, vc_name, hash, datetime;

	public RewardItem(String invoice_id, String vc_value, String vc_name, String hash, String datetime) {
		this.invoice_id = invoice_id;
		this.vc_value = vc_value;
		this.vc_name = vc_name;
		this.hash = hash;
		this.datetime = datetime;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getVc_value() {
		return vc_value;
	}

	public void setVc_value(String vc_value) {
		this.vc_value = vc_value;
	}

	public String getVc_name() {
		return vc_name;
	}

	public void setVc_name(String vc_name) {
		this.vc_name = vc_name;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
}
