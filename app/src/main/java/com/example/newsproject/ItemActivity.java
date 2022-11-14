package com.example.newsproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ItemActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar loader;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opened_item);

        Intent intent=getIntent();
        url = intent.getStringExtra("link");
        loader = findViewById(R.id.loader);
        webView = findViewById(R.id.webView);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiper1);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(url);
                //Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loader.setVisibility(View.VISIBLE);
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                loader.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);

    }
}
