package com.mbrite.patrol.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.mbrite.patrol.common.*;

public enum  RestClient {

    INSTANCE;

    {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private String site;

    private URI getSiteURI(Activity activity)
            throws URISyntaxException {
        site = Utils.getSiteURI(activity).trim();
        if (TextUtils.isEmpty(site)) {
            site = Constants.DEFAULT_SITE_URL;
        }
        return new URI(site);
    }

    public String getSite() {
        return site;
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

    public HttpResponse post(Activity activity, String relativeURI, String payload, String contentType)
            throws IOException, URISyntaxException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(getSiteURI(activity).resolve(relativeURI));
        StringEntity input = new StringEntity(payload);
        input.setContentType(contentType);
        postRequest.setEntity(input);
        return httpClient.execute(postRequest);
    }
}

