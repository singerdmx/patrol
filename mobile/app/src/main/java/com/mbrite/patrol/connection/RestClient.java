package com.mbrite.patrol.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.*;
import java.net.*;
import java.util.*;

import com.mbrite.patrol.common.*;
import com.mbrite.patrol.app.*;

public enum  RestClient {

    INSTANCE;

    private URI siteURI;

    {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private URI getSiteURI(Activity activity)
            throws URISyntaxException {
        if (this.siteURI != null) {
            return this.siteURI;
        }

        String site = Utils.getSiteURI(activity);
        if (TextUtils.isEmpty(site)) {
            throw new IllegalStateException(activity.getString(R.string.error_site_url_missing));
        }
        this.siteURI = new URI(site);
        return this.siteURI;
    }

    public HttpResponse get(Activity activity, String relativeURI)
            throws IOException, URISyntaxException {
        return get(activity, relativeURI, null);
    }

    public HttpResponse get(Activity activity, String relativeURI, Map<String, String> headers)
            throws IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(getSiteURI(activity).resolve(relativeURI));
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }
        return client.execute(request);
    }
}

