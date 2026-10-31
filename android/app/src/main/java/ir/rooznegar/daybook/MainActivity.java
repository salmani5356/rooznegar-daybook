package ir.rooznegar.daybook;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.net.Uri;
public class MainActivity extends Activity {
 private static final String APP_URL="https://salmani5356.github.io/rooznegar-daybook/";
 private WebView webView;
 @Override public void onCreate(Bundle state){super.onCreate(state);webView=new WebView(this);setContentView(webView);WebSettings s=webView.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setDatabaseEnabled(true);s.setSupportZoom(false);webView.setWebChromeClient(new WebChromeClient());webView.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){Uri u=r.getUrl();return false;}});if(state!=null)webView.restoreState(state);else webView.loadUrl(APP_URL);}
 @Override protected void onSaveInstanceState(Bundle out){webView.saveState(out);super.onSaveInstanceState(out);}
 @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
