package com.elad.cyberwordstrainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;
    private TextToSpeech tts;

    private static final String MOBILE_CSS =
            "html,body{width:100%!important;max-width:100%!important;overflow-x:hidden!important;}" +
            "body{min-height:100dvh!important;}" +
            ".wrap{width:100%!important;max-width:none!important;margin:0!important;padding:8px 10px 16px!important;}" +
            ".top{margin-bottom:8px!important;padding:0 2px!important;}" +
            ".brand{font-size:18px!important;line-height:1.2!important;}" +
            ".badge{font-size:11px!important;padding:6px 9px!important;}" +
            ".panel{border-radius:18px!important;padding:12px!important;box-shadow:none!important;}" +
            ".stats{grid-template-columns:repeat(4,minmax(0,1fr))!important;gap:6px!important;margin-bottom:9px!important;}" +
            ".stat{padding:7px 2px!important;border-radius:12px!important;min-width:0!important;}" +
            ".stat b{font-size:16px!important;}" +
            ".stat span{font-size:10px!important;white-space:nowrap!important;}" +
            ".counter{font-size:12px!important;line-height:1.25!important;}" +
            ".progress{height:7px!important;margin:7px 0 14px!important;}" +
            ".word-row{gap:9px!important;margin:6px 0 13px!important;}" +
            ".word{font-size:clamp(27px,8vw,34px)!important;line-height:1.08!important;}" +
            ".icon-btn{width:42px!important;height:42px!important;border-radius:12px!important;font-size:19px!important;flex:0 0 42px!important;}" +
            ".answers{gap:8px!important;}" +
            ".answer{padding:12px 11px!important;min-height:54px!important;border-radius:14px!important;font-size:16px!important;line-height:1.25!important;}" +
            ".feedback{min-height:28px!important;margin-top:7px!important;font-size:14px!important;}" +
            ".actions{gap:7px!important;margin-top:6px!important;}" +
            ".btn{min-width:0!important;padding:11px 8px!important;border-radius:13px!important;font-size:14px!important;}" +
            ".settings{grid-template-columns:1fr 1fr!important;gap:7px!important;margin-top:10px!important;}" +
            "select,label.switch{padding:10px 8px!important;border-radius:12px!important;font-size:13px!important;min-width:0!important;}" +
            ".small{font-size:10.5px!important;line-height:1.4!important;margin-top:10px!important;}" +
            ".sheet{width:100%!important;max-height:88dvh!important;border-radius:20px 20px 12px 12px!important;}" +
            "@media(max-width:380px){.brand{font-size:16px!important}.stat span{font-size:9px!important}.answer{font-size:15px!important}.btn{font-size:13px!important}}";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.82f);
            }
        });

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(15, 16, 32));
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(100);
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);

        // Keep the app content inside Android's status/navigation bar safe area.
        webView.setOnApplyWindowInsetsListener((v, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            int left = insets.getSystemWindowInsetLeft();
            int right = insets.getSystemWindowInsetRight();
            v.setPadding(left, top, right, bottom);
            return insets;
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = "(function(){var s=document.createElement('style');s.id='android-responsive';" +
                        "s.innerHTML=" + quoteJs(MOBILE_CSS) + ";document.head.appendChild(s);})();";
                view.evaluateJavascript(js, null);
            }
        });

        webView.addJavascriptInterface(new AndroidBridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
    }

    private static String quoteJs(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n") + "'";
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void speak(String text) {
            runOnUiThread(() -> {
                if (tts != null) {
                    tts.stop();
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "cyber-word");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
