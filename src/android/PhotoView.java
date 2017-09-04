/*
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

package org.apache.cordova.photoview;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class PhotoView extends CordovaPlugin {
	
    public static final String TAG = "photoview";


    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		
		JSONObject options = args.optJSONObject(0);
		
		if (action.equals("GetPhoto")) { 
			
			if (options != null) {
				
				//Uri myUri = Uri.parse(options.getString("PhotoURI"));
				
				URL u = new URL(options.getString("PhotoURI"));
				HttpGet httpRequest = new HttpGet(u.toURI());
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
				InputStream instream = bufHttpEntity.getContent();
				Bitmap bmImg = BitmapFactory.decodeStream(instream);
				instream.close();
		
				// Write image to a file in sd card
				File posterFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.dexter.prosecnabrzinasrbije/files/image.jpg");
				posterFile.createNewFile();
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(posterFile));
				Bitmap mutable = Bitmap.createScaledBitmap(bmImg,bmImg.getWidth(),bmImg.getHeight(),true);
				mutable.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
				
				Uri myUri = Uri.parse("file://" + posterFile.getPath());
		
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(myUri, "image/*");
				cordova.getActivity().startActivity(intent);	
				
			}
			
			/*
			JSONObject r = new JSONObject();
			if (options != null) r.put("options", options.optLong("quality")); //opcija koju smo mu poslali preko JS
            r.put("custom", "neki moj text");
            r.put("model", this.getModel());
			*/
			
            //callbackContext.success(r);
			
        } else {
			callbackContext.error("action not recognised");
            return false;
        }
        return true;
		
    }



}
