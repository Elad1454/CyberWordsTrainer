package com.elad.cyberwordstrainer;
import android.app.Activity; import android.os.Bundle; import android.webkit.*; import android.speech.tts.TextToSpeech; import java.util.Locale;
public class MainActivity extends Activity {
 TextToSpeech tts; boolean ready=false; WebView web;
 public void onCreate(Bundle b){super.onCreate(b); tts=new TextToSpeech(this,s->{if(s==TextToSpeech.SUCCESS){ready=tts.setLanguage(Locale.US)>=0;tts.setSpeechRate(.85f);}}); web=new WebView(this); web.setBackgroundColor(0xff101426); web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);web.addJavascriptInterface(new Object(){@JavascriptInterface public void speak(String word){runOnUiThread(()->{if(ready)tts.speak(word,TextToSpeech.QUEUE_FLUSH,null,"word");else android.widget.Toast.makeText(MainActivity.this,"English TTS unavailable",0).show();});}},"Android");setContentView(web);web.loadUrl("file:///android_asset/keywords.html");}
 protected void onDestroy(){if(tts!=null)tts.shutdown();super.onDestroy();}
}