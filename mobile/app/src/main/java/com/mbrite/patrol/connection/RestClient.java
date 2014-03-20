package com.mbrite.patrol.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;
import java.net.*;

import com.mbrite.patrol.common.*;
import com.mbrite.patrol.app.*;

public class RestClient {

    private URI siteURI;

    {
        StrictMode.ThreadPolicy policy =
            new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public RestClient(Activity activity) throws URISyntaxException {
        String site = Utils.getSiteURI(activity);
        if (TextUtils.isEmpty(site)) {
            throw new IllegalStateException(activity.getString(R.string.error_site_url_missing));
        }
        this.siteURI = new URI(site);
    }

    public String get(String relativeURI) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(this.siteURI.resolve(relativeURI));
        HttpResponse response = client.execute(request);
        return Utils.convertStreamToString(response.getEntity().getContent());
    }

    public String post(String relativeURI, String payload, String contentType) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(this.siteURI.resolve(relativeURI));
        StringEntity input = new StringEntity(payload);
        post.setEntity(input);
        if (contentType != null) {
            input.setContentType(contentType);
        }
        HttpResponse response = client.execute(post);
        return Utils.convertStreamToString(response.getEntity().getContent());
    }

//    private class Connection extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object... arg0) {
//            connect();
//            return null;
//        }
//
//    }
}

