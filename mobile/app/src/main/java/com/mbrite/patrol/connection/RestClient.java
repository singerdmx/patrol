package com.mbrite.patrol.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.*;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mbrite.patrol.common.*;

public enum RestClient {

    INSTANCE;

    {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private String site;

    private String cookie;

    private String authenticationToken;

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

    public void clearSession() {
        authenticationToken = null;
        cookie = null;
    }

    public HttpResponse get(Activity activity, String relativeURI)
            throws IOException, URISyntaxException {
        return get(activity, relativeURI, null);
    }

    public HttpResponse get(Activity activity, String relativeURI, Map<String, String> headers)
            throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(getSiteURI(activity).resolve(relativeURI));
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }
        return executeRequest(request);
    }

    public HttpResponse post(Activity activity, String relativeURI, String payload, String contentType)
            throws IOException, URISyntaxException {
        HttpPost request = new HttpPost(getSiteURI(activity).resolve(relativeURI));
        StringEntity input = new StringEntity(payload, HTTP.UTF_8);
        input.setContentType(contentType);
        request.setEntity(input);
        return executeRequest(request);
    }

    public HttpResponse post(Activity activity, String relativeURI, List<BasicNameValuePair> payload)
            throws IOException, URISyntaxException {
        HttpPost request = new HttpPost(getSiteURI(activity).resolve(relativeURI));
        request.setEntity(new UrlEncodedFormEntity(payload));
        return executeRequest(request);
    }

    public void getAuthenticityToken(Activity activity)
            throws URISyntaxException, IOException {
        HttpResponse response = RestClient.INSTANCE.get(activity, Constants.LOGIN);
        setAuthenticationToken(response);
    }

    private HttpResponse executeRequest(HttpUriRequest request)
        throws IOException, URISyntaxException {
        if (cookie != null) {
            request.addHeader(Constants.COOKIE, cookie);
        }
        if (authenticationToken != null) {
            request.addHeader(new BasicHeader(Constants.X_CSRF_TOKEN, authenticationToken));
        }

        HttpClient client = new DefaultHttpClient(Constants.HTTP_PARAMS);
        HttpResponse response = client.execute(request);
        setCookie(response);
        setAuthenticationToken(response);
        return response;
    }

    private void setCookie(HttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase(Constants.COOKIES_HEADER) && header.getValue().startsWith("_blog_session")) {
                cookie = header.getValue();
                break;
            }
        }
    }

    private void setAuthenticationToken(HttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase(Constants.X_CSRF_TOKEN)) {
                authenticationToken = header.getValue();
                break;
            }
        }
    }
}

