package ir.rooznegar.daybook;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.net.Uri;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.Manifest;
public class MainActivity extends Activity {
 private static final String APP_URL="https://salmani5356.github.io/rooznegar-daybook/";
 private WebView webView;
 @Override public void onCreate(Bundle state){super.onCreate(state);webView=new WebView(this);setContentView(webView);WebSettings s=webView.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setDatabaseEnabled(true);s.setSupportZoom(false);webView.addJavascriptInterface(new Object(){@JavascriptInterface public void setReminders(String json){Reminders.save(getApplicationContext(),json);if(Build.VERSION.SDK_INT>=33)runOnUiThread(new Runnable(){public void run(){requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},1);}});}@JavascriptInterface public boolean available(){return true;}},"RooznegarAndroid");webView.setWebChromeClient(new WebChromeClient());webView.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){Uri u=r.getUrl();return false;}});if(state!=null)webView.restoreState(state);else webView.loadUrl(APP_URL);}
 @Override protected void onSaveInstanceState(Bundle out){webView.saveState(out);super.onSaveInstanceState(out);}
 @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
