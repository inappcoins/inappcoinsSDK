package com.inappcoins.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/*
http://stackoverflow.com/questions/541966/how-do-i-do-a-lazy-load-of-images-in-listview

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

public class DrawableManager {
	private class UrlImage {
		public boolean isDownloaded;
		public Drawable drawable;
		public ArrayList<ImageView> imageViews;
		
		public UrlImage() {
			isDownloaded = false;
			imageViews = new ArrayList<ImageView>();
		}
	}
	
    private final Map<String, UrlImage> urlImageMap;
    private Handler handler = null;
    
    public DrawableManager() {
    	//Log.d(this.getClass().getSimpleName(), "creating DrawableManager");
        urlImageMap = new HashMap<String, UrlImage>();
        handler = new Handler();
    }

    public Drawable fetchDrawable(String urlString) {
    	if (urlImageMap.containsKey(urlString)) {
            if (urlImageMap.get(urlString).isDownloaded) {
            	return urlImageMap.get(urlString).drawable;
            } else {
                Log.e(this.getClass().getSimpleName(), "url: " + urlString + " is not downloaded, should not be called");
                return null;
            }
        }
        
        try {
        	urlImageMap.put(urlString, new UrlImage());

            InputStream is = fetch(urlString);
            final Drawable drawable = Drawable.createFromStream(is, "src");

            if (drawable != null) {
                //drawableMap.put(urlString, drawable);
                //Log.d(this.getClass().getSimpleName(), "saving drawable to url: " + urlString);
            	urlImageMap.get(urlString).drawable = drawable;
            	urlImageMap.get(urlString).isDownloaded = true;
            } else {
              Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
              urlImageMap.remove(urlString);
            }
            return drawable;
            
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
			return null;
		}
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
    	if (urlImageMap.containsKey(urlString)) {
        	if (urlImageMap.get(urlString).isDownloaded) {
        		imageView.setImageDrawable(urlImageMap.get(urlString).drawable);
        		//Log.d(this.getClass().getSimpleName(), "url = " + urlString + " already downloaded, returning");
        		return;
        	} else {
        		// fucking recycling scrollview
        		for (Map.Entry<String, UrlImage> entry : urlImageMap.entrySet()) {
        		    String key = entry.getKey();
        		    UrlImage value = entry.getValue();
        		    for (int i = 0; i < value.imageViews.size(); i++) {
        		    	if(value.imageViews.get(i) == imageView && !key.equals(urlString)) {
        	                value.imageViews.remove(i);
        		    	}
					}
        		}
        		if(!urlImageMap.get(urlString).imageViews.contains(imageView)) {
            		urlImageMap.get(urlString).imageViews.add(imageView);
        		}
        		return;
        	}
        }
    	
        Thread thread = new Thread() {
            @Override
            public void run() {
                final Drawable drawable = fetchDrawable(urlString);
                handler.post(new Runnable() {
					@Override
					public void run() {
						//Log.d(this.getClass().getSimpleName(), "url = " + urlString + " listView.size() = " + urlImageMap.get(urlString).imageViews.size());
						for (ImageView iView : urlImageMap.get(urlString).imageViews) {
							iView.setImageDrawable(drawable);
						}
						urlImageMap.get(urlString).imageViews.clear();
					}
				});
            }
        };
        thread.start();
    }

    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}
