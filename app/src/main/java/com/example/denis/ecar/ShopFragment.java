package com.example.denis.ecar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by denis on 29.07.2017.
 */

public class ShopFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String url = "https://www.tesla.com/de_DE/designyours";
        View v = inflater.inflate(R.layout.fragment_shop,container,false);
        WebView webView = (WebView)v.findViewById(R.id.wv_Shop);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.tesla.com/de_DE/designyours");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
