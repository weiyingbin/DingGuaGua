package com.ebook.api;

import okhttp3.OkHttpClient;

public class OkHttpClientFactory {
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }
}
