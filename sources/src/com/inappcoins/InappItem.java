package com.inappcoins;

/**
 * Created by Pavel Drabek | Craneballs LLC on 04.02.14.
 */
public class InappItem {

    private String name, vc_name, image, satoshi, inappkey;
    private double vc_value, usd_value;
    private String walletAdress;

    public InappItem(String name, double vc_value, String vc_name, String image, String satoshi, String inappkey, double usd_value, String walletAdress) {
        this.name = name;
        this.vc_value = vc_value;
        this.vc_name = vc_name;
        this.image = image;
        this.satoshi = satoshi;
        this.inappkey = inappkey;
        this.usd_value = usd_value;
        this.walletAdress = walletAdress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVc_name() {
        return vc_name;
    }

    public void setVc_name(String vc_name) {
        this.vc_name = vc_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSatoshi() {
        return satoshi;
    }

    public void setSatoshi(String satoshi) {
        this.satoshi = satoshi;
    }

    public String getInappkey() {
        return inappkey;
    }

    public void setInappkey(String inappkey) {
        this.inappkey = inappkey;
    }

    public double getVc_value() {
        return vc_value;
    }

    public void setVc_value(double vc_value) {
        this.vc_value = vc_value;
    }

    public double getUsd_value() {
        return usd_value;
    }

    public void setUsd_value(double usd_value) {
        this.usd_value = usd_value;
    }

    public String getWalletAdress() {
        return walletAdress;
    }

    public void setWalletAdress(String walletAdress) {
        this.walletAdress = walletAdress;
    }
}
