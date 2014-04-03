package com.inappcoins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;


public class InappManager {
	
	private static boolean initialized = false;
	private static boolean downloaded = false;
	private static List<InappItem> inapps;
	private static String postDataString = null;
	
	public static void init() {
		InappManager.inapps = new ArrayList<InappItem>();
		InappManager.downloaded = false;
		InappManager.initialized = true;
	}
	
	public static boolean isDownloadedInapps() { return downloaded; }
	
	public static List<InappItem> getInapps() {
		return inapps;
	}
	
	public static void downloadInappsFromServer() {
		if(!initialized) {
    		System.err.println("InappManager is not initialized");
    		return;
    	} 
		
		String jsonString = downloadInappsJSONString();
		parseInappsJSON(jsonString);
		downloaded = true;
	}
	
	public static void downloadInappsFromServer(Runnable runnable) {
		if(!initialized) {
    		System.err.println("InappManager is not initialized");
    		return;
    	} 
		
		downloadInappsFromServer();
		((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(runnable);
	}
	
	
    public static String downloadInappURI(final InappItem inappItem) {
		if(!initialized) {
    		System.err.println("InappManager is not initialized");
    		return "";
    	} 
		
    	Checker.restart();
		
        try {
            String url = Constants.PAYMENT_URL;
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(3);
            pairs.add(new BasicNameValuePair("inapp_key", inappItem.getInappkey()));
            pairs.add(new BasicNameValuePair("app_key", InAppCoin.getAppKey()));
            pairs.add(new BasicNameValuePair("uuid", InAppCoin.getClientUDID()));

            return postData(url, pairs);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "";
    }
	
    public static String downloadInappURI(int index) {
    	return downloadInappURI(inapps.get(index));
    }

    public static void checkPayment(PurchaseStatusListener listener) {
    	checkPaymentStatus(listener);
    }
    
    private static void checkPaymentStatus(PurchaseStatusListener listener) {
    	String jsonString = downloadRewardJSONString();
    	List<RewardItem> rewards = parseRewardsJSON(jsonString);
    	if(rewards.size() > 0) {
    		for (int i = 0; i < rewards.size(); i++) {
        		final String hash = getRewardHash(rewards.get(i));
        		if(hash.equals(rewards.get(i).getHash())) {
        			//listener.purchaseSuccess(rewards.get(i));
        			callPurchaseSuccess(listener, rewards.get(i));
        		} else {
        			//listener.purchaseFailure(null);
        			callPurchaseFailure(listener);
        		}
    		}
    	} else {
    		//listener.purchaseDidntReceivedYet();
    		callPurchaseDidntReceivedYet(listener);
    	}
    	
    }
    
    private static String getRewardHash(RewardItem reward) {
		String base = reward.getInvoice_id() + ":" + reward.getVc_value() + ":" + reward.getDatetime() + ":" + InAppCoin.getAppKey() + ":" + InAppCoin.getClientUDID() + ":" + InAppCoin.getDeveloperSecretKey();
    	String hash = sha256(base);
		return hash;
    }
    
    private static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
           throw new RuntimeException(ex);
        }
    }
    
    private static void addInapp(InappItem inappItem) {
    	if(initialized) {
    		InappManager.inapps.add(inappItem);
    	} else {
    		System.err.println("InappManager is not initialized");
    	}
    }
    
	private static String downloadRewardJSONString() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
        pairs.add(new BasicNameValuePair("uuid", InAppCoin.getClientUDID()));
        pairs.add(new BasicNameValuePair("app_key", InAppCoin.getAppKey()));
        pairs.add(new BasicNameValuePair("hash", sha256(InAppCoin.getClientUDID() + ":" + InAppCoin.getAppKey() + ":" + InAppCoin.getDeveloperSecretKey())));
        String jsonString = postData(Constants.CHECK_REWARD_URL, pairs);
		//System.out.println("downloadRewardJSON: " + jsonString);
		return jsonString;
	}
	
	private static List<RewardItem> parseRewardsJSON(String jsonString) {
		List<RewardItem> rewards = new ArrayList<RewardItem>();
		if(jsonString.isEmpty()) {
			return rewards;
		}
		
		try {
			JSONParser parser = new JSONParser();
	        JSONObject jsonObject;
			jsonObject = (JSONObject) parser.parse(jsonString);

			if(jsonObject.containsKey("rewards")) {
		        JSONArray jsonArray = (JSONArray) jsonObject.get("rewards");
		        for (int i = 0; i < jsonArray.size(); i++) {
		            JSONObject obj = (JSONObject)jsonArray.get(i);
		            String invoice_id = (String) obj.get("invoice_id");
		            String vc_value = (String) obj.get("vc_value");
		            String vc_name = (String) obj.get("vc_name");
		            String hash = (String) obj.get("hash");
		            String datetime = (String) obj.get("datetime");
		            
		            rewards.add(new RewardItem(invoice_id, vc_value, vc_name, hash, datetime));
		    		System.out.println("adding reward");
		        }
			} else {
	    		System.err.println("parseRewardsJSON else: " + jsonString);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rewards;
	}
	
	private static String downloadInappsJSONString() {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
        pairs.add(new BasicNameValuePair("app_key", InAppCoin.getAppKey()));
        pairs.add(new BasicNameValuePair("uuid", InAppCoin.getClientUDID()));
        String jsonString = postData(Constants.GET_INAPPS_URL, pairs);
		//System.out.println(jsonString);
		return jsonString;
	}

	private static void parseInappsJSON(String jsonString) {
		if(jsonString.isEmpty()) {
			return;
		}
		
		try {
			JSONParser parser = new JSONParser();
	        JSONObject jsonObject;
			jsonObject = (JSONObject) parser.parse(jsonString);
			if(jsonObject.containsKey("inapps")) {
		        JSONArray jsonArray = (JSONArray) jsonObject.get("inapps");
		        inapps.clear();
		        for (int i = 0; i < jsonArray.size(); i++) {
		            JSONObject obj = (JSONObject)jsonArray.get(i);
		
		            String name = (String) obj.get("name");
		            String vc_name = (String) obj.get("vc_name");
		            String vc_value = (String) obj.get("vc_value");
		            String image = (String) obj.get("image");
		            String satoshi = (String) obj.get("satoshi");
		            String inappkey = (String) obj.get("inappkey");
		            String usd_value = (String) obj.get("usd_value");
		
		            addInapp(new InappItem(name, Double.parseDouble(vc_value), vc_name, image, satoshi, inappkey, Double.parseDouble(usd_value), "WalletAdress"));
		            //System.out.println("adding inapp");
					
		        }
			} else {
	    		System.out.println("parseInappsJSON else: " + jsonString);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String postData(final String url, final List<NameValuePair> pairs) {
    	/*
		String finalUrl = url + "?";
    	for (NameValuePair pair : pairs) {
			finalUrl += pair.getName() + "=" + pair.getValue() + "&";
		}
    	System.out.println("postData: " + finalUrl);
    	*/
    	
    	try {
    		Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setHeader("inappcoins", "inappcoins sdk");
					
					try {
						httppost.setEntity(new UrlEncodedFormEntity(pairs));
						HttpResponse response = httpclient.execute(httppost);
						
						String line = "";
						StringBuilder total = new StringBuilder();
						BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						while ((line = rd.readLine()) != null) {
							total.append(line);
						}
						
						postDataString = total.toString();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						postDataString = "";
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						postDataString = "";
					} catch (IOException e) {
						e.printStackTrace();
						postDataString = "";
					}
				}
			});
    		thread.start();
    		thread.join();
    		String str = postDataString.toString();
    		postDataString = "";
    		return str;
    		
        } catch (InterruptedException e) {
			e.printStackTrace();
		}

        return "";
    }
	
	private static void callPurchaseSuccess(final PurchaseStatusListener listener, final RewardItem reward) {
		((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				listener.purchaseSuccess(reward);
			}
		});
	}
	
	private static void callPurchaseFailure(final PurchaseStatusListener listener) {
		((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				listener.purchaseFailure(null);
			}
		});
	}
	
	private static void callPurchaseDidntReceivedYet(final PurchaseStatusListener listener) {
		((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				listener.purchaseDidntReceivedYet();
			}
		});
	}
}
