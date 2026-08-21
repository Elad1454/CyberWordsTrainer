package com.elad.cisoexamtrainer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.setStatusBarColor(Color.rgb(10, 12, 34));
        w.setNavigationBarColor(Color.rgb(10, 12, 34));

        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(Color.rgb(10, 12, 34));

        try {
            String encoded = readAssetText("ciso_exam_trainer.html.gz.b64");
            byte[] compressed = Base64.decode(encoded, Base64.DEFAULT);
            String html = gunzip(compressed);
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        } catch (Exception e) {
            webView.loadData("<html><body style='background:#0a0c22;color:white;font-family:sans-serif'><h2>Failed to load app</h2><pre>" + e.getMessage() + "</pre></body></html>", "text/html", "UTF-8");
        }

        setContentView(webView);
    }

    private String readAssetText(String name) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(name), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private String gunzip(byte[] compressed) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = gis.read(buf)) > 0) out.write(buf, 0, n);
        gis.close();
        return out.toString("UTF-8");
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript("document.getElementById('home').classList.contains('hidden')", value -> {
            if ("true".equals(value)) {
                webView.evaluateJavascript("goHome()", null);
            } else {
                super.onBackPressed();
            }
        });
    }
}
