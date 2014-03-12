package com.inappcoins.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inappcoins.Constants;
import com.inappcoins.InAppCoin;
import com.inappcoins.InappActivityAgent;
import com.inappcoins.InappItem;
import com.inappcoins.InappManager;
import com.inappcoins.PurchaseActionListener;

public class InappActivity extends Activity implements InappActivityAgent {

    private ListView inappListView = null;
    
    DrawableManager drawableManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_inapp);
		if(!InAppCoin.isInitialized()) {
			Log.d(this.getClass().getSimpleName(), "populateInappList()");
			finish();
		}
		
		inappListView 	= (ListView) findViewById(R.id.inappListView);
		drawableManager = new DrawableManager();
		
		inappListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	//Toast.makeText(getApplicationContext(), "Click on index: " + position, Toast.LENGTH_SHORT).show();
            	view.findViewById(R.id.itemProgressBar).setVisibility(View.VISIBLE);
            	InAppCoin.purchaseInapp(InappActivity.this, position);
            }
        });
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				InAppCoin.getInappsFromServer(new Runnable() {
					@Override
					public void run() {
						populateInappList();
						findViewById(R.id.progressBar1).setVisibility(View.GONE);
					}
				});				
			}
		});
		thread.start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inapp, menu);
		return true;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d(this.getClass().getSimpleName(), "onActivityResult InappActivity");

        ((Activity)InAppCoin.getContext()).getWindow().getDecorView().post(new Runnable() {
			@Override
			public void run() {
				if (requestCode == Constants.SEND_BTC_PAYMENT) {
			        switch (resultCode) {
					case RESULT_OK:
						((PurchaseActionListener)InAppCoin.getContext()).userSentPurchase();
						break;
					case RESULT_CANCELED:
						((PurchaseActionListener)InAppCoin.getContext()).userCancelPurchase();
						break;
					default: break;
					}
			    }				
			}
		});

		super.onActivityResult(requestCode, resultCode, data);
	}
	
    public void populateInappList() {
    	Log.d(this.getClass().getSimpleName(), "populateInappList()");

        ArrayAdapter<InappItem> adapter = new InappListAdapter();
        inappListView.setAdapter(adapter);
    }
	
    private class InappListAdapter extends ArrayAdapter<InappItem> {
        public InappListAdapter() {
            super(InappActivity.this, R.layout.inapplistview_item, InappManager.getInapps());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.inapplistview_item, parent, false);
            }
            InappItem currentInapp = InappManager.getInapps().get(position);

            TextView name = (TextView) convertView.findViewById(R.id.inappName);
            TextView info = (TextView) convertView.findViewById(R.id.inappInfo);
            TextView price = (TextView) convertView.findViewById(R.id.inappPrice);
            ImageView image = (ImageView) convertView.findViewById(R.id.inappImage);
            
            name.setText(currentInapp.getName());
            info.setText("reward: " + Double.toString(currentInapp.getVc_value()) + " " + currentInapp.getVc_name());
            price.setText("$" + Double.toString(currentInapp.getUsd_value()));
            if(!currentInapp.getImage().isEmpty()) {
            	drawableManager.fetchDrawableOnThread(Constants.PICTURE_URL + currentInapp.getImage(), image);
            }
            
            return convertView;
        }
    }
}
