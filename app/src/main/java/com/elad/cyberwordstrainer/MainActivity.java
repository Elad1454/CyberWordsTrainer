package com.elad.cyberwordstrainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;
    private TextToSpeech tts;

    private static final String MOBILE_CSS =
            "html,body{width:100%!important;height:100%!important;max-width:100%!important;overflow:hidden!important;}" +
            "body{margin:0!important;min-height:100%!important;background:#0f1020!important;}" +
            ".wrap{width:100%!important;height:100%!important;max-width:none!important;margin:0!important;padding:clamp(6px,1.2vh,12px) clamp(8px,2.2vw,16px) clamp(8px,1.4vh,14px)!important;display:flex!important;flex-direction:column!important;}" +
            ".top{flex:0 0 auto!important;margin:0 0 clamp(6px,1vh,10px)!important;padding:0!important;min-height:clamp(36px,5.5vh,50px)!important;align-items:center!important;}" +
            ".brand{font-size:clamp(17px,4.8vw,23px)!important;line-height:1.05!important;white-space:nowrap!important;}" +
            ".badge{font-size:clamp(10px,2.8vw,13px)!important;padding:clamp(5px,.8vh,7px) clamp(8px,2vw,11px)!important;white-space:nowrap!important;}" +
            ".panel{flex:1 1 auto!important;min-height:0!important;border-radius:clamp(14px,2.5vw,20px)!important;padding:clamp(9px,1.35vh,14px)!important;box-shadow:none!important;display:flex!important;flex-direction:column!important;overflow:hidden!important;}" +
            ".stats{flex:0 0 auto!important;grid-template-columns:repeat(4,minmax(0,1fr))!important;gap:clamp(4px,1.3vw,8px)!important;margin-bottom:clamp(6px,.9vh,10px)!important;}" +
            ".stat{padding:clamp(5px,.9vh,8px) 2px!important;border-radius:clamp(10px,2vw,14px)!important;min-width:0!important;}" +
            ".stat b{font-size:clamp(15px,4vw,19px)!important;line-height:1!important;}" +
            ".stat span{font-size:clamp(8.5px,2.5vw,11px)!important;white-space:nowrap!important;}" +
            ".counter{flex:0 0 auto!important;font-size:clamp(10px,3vw,13px)!important;line-height:1.15!important;}" +
            ".progress{flex:0 0 auto!important;height:clamp(5px,.8vh,8px)!important;margin:clamp(5px,.8vh,8px) 0 clamp(8px,1.2vh,13px)!important;}" +
            ".word-row{flex:0 0 auto!important;gap:clamp(7px,2vw,10px)!important;margin:clamp(3px,.6vh,6px) 0 clamp(8px,1.1vh,12px)!important;min-height:clamp(44px,7vh,62px)!important;}" +
            ".word{font-size:clamp(24px,8.2vw,38px)!important;line-height:1.02!important;max-width:calc(100% - 56px)!important;}" +
            ".icon-btn{width:clamp(38px,10vw,46px)!important;height:clamp(38px,10vw,46px)!important;border-radius:12px!important;font-size:clamp(17px,5vw,21px)!important;flex:0 0 auto!important;}" +
            ".answers{flex:1 1 auto!important;min-height:0!important;display:grid!important;grid-template-rows:repeat(4,minmax(0,1fr))!important;gap:clamp(6px,1vh,10px)!important;}" +
            ".answer{height:100%!important;min-height:0!important;padding:clamp(8px,1.3vh,13px) clamp(9px,2.2vw,13px)!important;border-radius:clamp(12px,2vw,16px)!important;font-size:clamp(14px,4vw,18px)!important;line-height:1.18!important;display:flex!important;align-items:center!important;justify-content:flex-start!important;}" +
            ".feedback{flex:0 0 auto!important;min-height:0!important;height:0!important;margin:0!important;overflow:visible!important;position:relative!important;z-index:20!important;font-size:clamp(13px,3.7vw,17px)!important;font-weight:800!important;text-align:center!important;}" +
            ".feedback.show-feedback{height:auto!important;margin:clamp(4px,.7vh,7px) 0!important;padding:clamp(6px,.9vh,9px) 10px!important;border-radius:12px!important;background:#202546!important;}" +
            ".feedback.correct-feedback{background:#173f2e!important;color:#c9ffe4!important;}" +
            ".feedback.wrong-feedback{background:#4b2224!important;color:#ffd7d7!important;}" +
            ".actions{flex:0 0 auto!important;display:grid!important;grid-template-columns:repeat(3,minmax(0,1fr))!important;gap:clamp(5px,1.2vw,8px)!important;margin-top:clamp(7px,1vh,10px)!important;}" +
            ".btn{min-width:0!important;padding:clamp(8px,1.2vh,12px) clamp(5px,1.3vw,9px)!important;border-radius:12px!important;font-size:clamp(12px,3.5vw,15px)!important;line-height:1.15!important;}" +
            ".settings{flex:0 0 auto!important;grid-template-columns:1fr 1fr!important;gap:clamp(5px,1.2vw,8px)!important;margin-top:clamp(7px,1vh,10px)!important;}" +
            "select,label.switch{padding:clamp(8px,1.1vh,11px) clamp(7px,1.7vw,10px)!important;border-radius:12px!important;font-size:clamp(11px,3.2vw,14px)!important;min-width:0!important;}" +
            ".small{flex:0 0 auto!important;font-size:clamp(9px,2.6vw,11px)!important;line-height:1.25!important;margin-top:clamp(6px,.9vh,9px)!important;max-height:clamp(34px,5.3vh,54px)!important;overflow:hidden!important;}" +
            ".sheet{width:100%!important;max-height:88%!important;border-radius:20px 20px 12px 12px!important;}" +
            "@media(max-height:760px){.top{min-height:34px!important}.panel{padding:8px!important}.stat{padding:4px 2px!important}.progress{margin:4px 0 7px!important}.word-row{margin:2px 0 6px!important;min-height:38px!important}.word{font-size:clamp(22px,7.4vw,31px)!important}.answers{gap:5px!important}.answer{font-size:clamp(13px,3.7vw,16px)!important;padding:6px 9px!important}.actions{margin-top:5px!important}.settings{margin-top:5px!important}.small{display:none!important}}" +
            "@media(max-width:360px){.brand{font-size:16px!important}.badge{font-size:9px!important}.stat span{font-size:8px!important}.btn{font-size:11px!important}.answer{font-size:13px!important}}";

    private static final String QUIZ_JS =
            "(function(){" +
            "if(window.__androidQuizEnhanced)return;window.__androidQuizEnhanced=true;" +
            "function enhance(){" +
            "if(typeof choose!=='function'||typeof nextWord!=='function')return;" +
            "var oldChoose=choose;" +
            "choose=function(btn){" +
            "if(window.__autoMoving)return;" +
            "oldChoose(btn);" +
            "var fb=document.getElementById('feedback');" +
            "if(fb){var t=fb.textContent||'';fb.classList.add('show-feedback');fb.classList.remove('correct-feedback','wrong-feedback');if(t.indexOf('✅')>=0)fb.classList.add('correct-feedback');else if(t.indexOf('❌')>=0)fb.classList.add('wrong-feedback');}" +
            "window.__autoMoving=true;" +
            "setTimeout(function(){if(fb){fb.classList.remove('show-feedback','correct-feedback','wrong-feedback');}window.__autoMoving=false;nextWord();},950);" +
            "};" +
            "var n=document.getElementById('next');if(n){n.style.display='none';}" +
            "}" +
            "enhance();setTimeout(enhance,250);" +
            "})();";

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
                String js = "(function(){" +
                        "var old=document.getElementById('android-responsive');if(old)old.remove();" +
                        "var s=document.createElement('style');s.id='android-responsive';s.innerHTML=" + quoteJs(MOBILE_CSS) + ";document.head.appendChild(s);" +
                        QUIZ_JS +
                        "})();";
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
