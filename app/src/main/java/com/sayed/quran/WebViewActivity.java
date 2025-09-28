package com.sayed.quran;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled") // Only enable JavaScript if you trust the content!
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(WebViewActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webview);

        // Configure settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Be cautious with this
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(false); // Security best practice
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Enable debugging in dev mode (optional)
        WebView.setWebContentsDebuggingEnabled(true);

        // Set clients
        webView.setWebViewClient(new ControlledWebViewClient());
        webView.setWebChromeClient(new WebChromeClient()); // Enables JS alerts, etc.

        // Load a URL
        webView.loadUrl("http://localhost:6969");
    }

    // Handle back button to navigate back in WebView
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Custom WebViewClient to control navigation, block URLs, handle errors
    private class ControlledWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            // Block or redirect certain URLs
            if (url.contains("blockedsite.com")) {
                Toast.makeText(WebViewActivity.this, "This site is blocked.", Toast.LENGTH_SHORT).show();
                return true; // Cancel the request
            }

            // Allow the WebView to handle the URL
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Toast.makeText(WebViewActivity.this, "Error loading page", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            // Toast.makeText(WebViewActivity.this, "HTTP error: " + errorResponse.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
