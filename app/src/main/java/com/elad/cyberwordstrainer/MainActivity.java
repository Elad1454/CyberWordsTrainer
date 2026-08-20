package com.elad.cyberwordstrainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
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
    private int topInsetCss = 0;
    private int bottomInsetCss = 0;
    private int leftInsetCss = 0;
    private int rightInsetCss = 0;
    private boolean pageReady = false;

    private static final String MOBILE_CSS =
            ":root{--android-top-inset:0px;--android-bottom-inset:0px;--android-left-inset:0px;--android-right-inset:0px;}" +
            "html,body{width:100%!important;max-width:100%!important;overflow-x:hidden!important;}" +
            "html{height:100%!important;}" +
            "body{height:100dvh!important;min-height:100dvh!important;overflow-y:hidden!important;}" +
            ".wrap{width:100%!important;max-width:720px!important;margin:0 auto!important;" +
            "padding-top:calc(var(--android-top-inset) + clamp(6px,1.2dvh,12px))!important;" +
            "padding-right:calc(var(--android-right-inset) + clamp(8px,2.2vw,14px))!important;" +
            "padding-bottom:calc(var(--android-bottom-inset) + clamp(8px,1.8dvh,18px))!important;" +
            "padding-left:calc(var(--android-left-inset) + clamp(8px,2.2vw,14px))!important;}" +
            ".top{margin-bottom:clamp(6px,1dvh,12px)!important;padding:0 2px!important;min-height:32px!important;}" +
            ".brand{font-size:clamp(17px,4.8vw,23px)!important;line-height:1.15!important;white-space:nowrap!important;}" +
            ".badge{font-size:clamp(10px,2.8vw,13px)!important;padding:clamp(5px,1vw,7px) clamp(8px,2vw,11px)!important;white-space:nowrap!important;}" +
            ".panel{border-radius:clamp(16px,4vw,22px)!important;padding:clamp(9px,2.7vw,15px)!important;box-shadow:none!important;}" +
            ".stats{grid-template-columns:repeat(4,minmax(0,1fr))!important;gap:clamp(5px,1.5vw,9px)!important;margin-bottom:clamp(7px,1.2dvh,12px)!important;}" +
            ".stat{padding:clamp(6px,1.1dvh,9px) 2px!important;border-radius:clamp(11px,3vw,14px)!important;min-width:0!important;}" +
            ".stat b{font-size:clamp(15px,4vw,19px)!important;}" +
            ".stat span{font-size:clamp(9px,2.5vw,11px)!important;white-space:nowrap!important;}" +
            ".counter{font-size:clamp(11px,3vw,13px)!important;line-height:1.2!important;}" +
            ".progress{height:clamp(6px,.8dvh,8px)!important;margin:clamp(5px,.8dvh,8px) 0 clamp(10px,1.6dvh,16px)!important;}" +
            ".word-row{gap:clamp(7px,2vw,11px)!important;margin:clamp(4px,.7dvh,8px) 0 clamp(9px,1.4dvh,14px)!important;}" +
            ".word{font-size:clamp(26px,7.8vw,40px)!important;line-height:1.05!important;}" +
            ".icon-btn{width:clamp(40px,11vw,48px)!important;height:clamp(40px,11vw,48px)!important;border-radius:12px!important;font-size:clamp(18px,5vw,22px)!important;flex:0 0 auto!important;}" +
            ".answers{gap:clamp(6px,1dvh,10px)!important;}" +
            ".answer{padding:clamp(10px,1.4dvh,14px) clamp(10px,2.5vw,14px)!important;min-height:clamp(48px,6.8dvh,62px)!important;border-radius:clamp(13px,3.5vw,16px)!important;font-size:clamp(15px,4.2vw,18px)!important;line-height:1.2!important;}" +
            ".feedback{min-height:clamp(22px,3.4dvh,32px)!important;margin-top:clamp(4px,.7dvh,8px)!important;font-size:clamp(12px,3.5vw,15px)!important;}" +
            ".actions{gap:clamp(5px,1.7vw,8px)!important;margin-top:clamp(4px,.7dvh,8px)!important;flex-wrap:nowrap!important;}" +
            ".btn{min-width:0!important;padding:clamp(9px,1.3dvh,12px) clamp(6px,1.8vw,10px)!important;border-radius:13px!important;font-size:clamp(12px,3.5vw,15px)!important;white-space:nowrap!important;}" +
            ".settings{grid-template-columns:1fr 1fr!important;gap:clamp(5px,1.5vw,8px)!important;margin-top:clamp(7px,1dvh,11px)!important;}" +
            "select,label.switch{padding:clamp(8px,1.2dvh,11px) clamp(7px,2vw,10px)!important;border-radius:12px!important;font-size:clamp(11px,3.2vw,14px)!important;min-width:0!important;}" +
            ".small{font-size:clamp(9.5px,2.7vw,12px)!important;line-height:1.35!important;margin-top:clamp(7px,1dvh,11px)!important;}" +
            ".sheet{width:min(720px,100%)!important;max-height:calc(100dvh - var(--android-top-inset) - var(--android-bottom-inset) - 16px)!important;border-radius:20px 20px 12px 12px!important;}" +
            "@media(max-width:380px){.brand{font-size:16px!important}.stat span{font-size:9px!important}.answer{font-size:14px!important}.btn{font-size:12px!important}.small{font-size:9px!important}}" +
            "@media(max-height:720px){.top{margin-bottom:4px!important}.panel{padding:8px!important}.stat{padding:5px 2px!important}.progress{margin:4px 0 8px!important}.word-row{margin:2px 0 7px!important}.answers{gap:5px!important}.answer{min-height:44px!important;padding:8px 10px!important}.feedback{min-height:18px!important;margin-top:3px!important}.btn{padding:8px 5px!important}.settings{margin-top:6px!important}.small{margin-top:6px!important;line-height:1.25!important}}" +
            "@media(min-width:600px){.wrap{max-width:760px!important}.brand{font-size:22px!important}.answer{font-size:18px!important}}";

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

        webView.setOnApplyWindowInsetsListener((v, insets) -> {
            int top;
            int bottom;
            int left;
            int right;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                );
                top = bars.top;
                bottom = bars.bottom;
                left = bars.left;
                right = bars.right;
            } else {
                top = insets.getSystemWindowInsetTop();
                bottom = insets.getSystemWindowInsetBottom();
                left = insets.getSystemWindowInsetLeft();
                right = insets.getSystemWindowInsetRight();
            }

            float density = getResources().getDisplayMetrics().density;
            topInsetCss = Math.round(top / density);
            bottomInsetCss = Math.round(bottom / density);
            leftInsetCss = Math.round(left / density);
            rightInsetCss = Math.round(right / density);

            applyInsetsToPage();
            return insets;
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = "(function(){" +
                        "var old=document.getElementById('android-responsive');if(old)old.remove();" +
                        "var s=document.createElement('style');s.id='android-responsive';" +
                        "s.innerHTML=" + quoteJs(MOBILE_CSS) + ";document.head.appendChild(s);" +
                        "})();";
                view.evaluateJavascript(js, null);
                pageReady = true;
                applyInsetsToPage();
            }
        });

        webView.addJavascriptInterface(new AndroidBridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
        webView.post(webView::requestApplyInsets);
    }

    private void applyInsetsToPage() {
        if (!pageReady || webView == null) return;

        String js = "(function(){var r=document.documentElement.style;" +
                "r.setProperty('--android-top-inset','" + topInsetCss + "px');" +
                "r.setProperty('--android-bottom-inset','" + bottomInsetCss + "px');" +
                "r.setProperty('--android-left-inset','" + leftInsetCss + "px');" +
                "r.setProperty('--android-right-inset','" + rightInsetCss + "px');" +
                "})();";
        webView.post(() -> webView.evaluateJavascript(js, null));
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
