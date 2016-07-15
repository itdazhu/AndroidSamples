package com.gyz.androidsamples.view.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.gyz.androidsamples.R;

import java.util.logging.Logger;

/**
 * Created by guoyizhe on 16/6/28.
 * 邮箱:gyzboy@126.com
 */
public class ASWebview extends Activity {
    private WebView wb;
    private ImageView imageView;
    private final String TAG = ASWebview.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        wb = (WebView) findViewById(R.id.webview);
        imageView = (ImageView) findViewById(R.id.image);
//        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl("http://www.baidu.com");//有重定向
//        wb.loadUrl("http://www.jb51.com");//没有重定向


        setWebSettings();
        setWebClient();
        setWebChromeClient();


    }

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private void setWebChromeClient() {
        wb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //当网页加载进度变化时调用
                Log.d("pageProgress",String.valueOf(newProgress));
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                imageView.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("title",title);
            }

            CustomViewCallback customCallback;
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                //当前web页面全屏时调用
                customCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                //退出全屏时调用
                customCallback.onCustomViewHidden();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                //<a> 标签的 target 属性:
                //<a href="http://www.baidu.com" target="_blank">百度一下</a>
//                tartget有4个属性：
//                _blank
//                浏览器总在一个新打开、未命名的窗口中载入目标文档。
//                _self
//                这个目标的值对所有没有指定目标的 <a> 标签是默认目标，它使得目标文档载入并显示在相同的框架或者窗口中作为源文档。这个目标是多余且不必要的，除非和文档标题 <base> 标签中的 target 属性一起使用。
//                _parent
//                这个目标使得文档载入父窗口或者包含来超链接引用的框架的框架集。如果这个引用是在窗口或者在顶级框架中，那么它与目标 _self 等效。
//                _top
//                这个目标使得文档载入包含这个超链接的窗口，用 _top 目标将会清除所有被包含的框架并将文档载入整个浏览器窗口。

//                参数说明:
//                view :请求新窗口的WebView
//                isDialog ： 如果是true，代表这个新窗口只是个对话框，如果是false，则是一个整体的大小的窗口
//                isUserGesture 如果是true，代表这个请求是用户触发的，例如点击一个页面上的一个连接
//                resultMsg ，当一个新的WebView被创建时这个只被传递给他，resultMsg.obj是一个WebViewTransport的对象，它被用来传送给新创建的WebView，使用方法：
//                WebView.WebViewTransport.setWebView(WebView)
//                返回值：这个方法如果返回true，代表这个主机应用会创建一个新的窗口，否则应该返回fasle。如果你返回了false，但是依然发送resulMsg会导致一个未知的结果。
            }
        });
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private void setWebClient() {
        wb.setWebViewClient(new WebViewClient() {

            //页面有重定向，代码执行顺序:
            //pageStart(初始界面地址)----->overrideUrl(先解析到重定向的地址)----->pageStart(重定向界面地址)----->pageFinish
            //                                |                                    |
            //                                 ---------有多个重定向的话循环多次-------


            //页面没有重定向，代码执行顺序:
            //pageStart----->pageFinish
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //不设置的话默认由ActivityManager选择如何加载webview，返回true表示
                //由手机选择打开方式，返回false表示由本webview加载url，默认super中返回
                //false。

                //POST方式不调用此方法,loadUrl才会调用此方法
                Log.d("pageOverrideUrlLoading", url);
//                if (Uri.parse(url).getHost().contains("baidu")) {
//                    return false;
//                }
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
//                return true;
                return false;
//                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                //每次加载页面都会调用,当页面有重定向时可执行多次

                Log.d("pageStart", url);
                imageView.setImageBitmap(favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //如果有重定向，当最后一个界面加载完成时调用

                Log.d("pageFinish", url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                //页面加载资源时调用,会显示所有资源的url
                Log.d("pageLoadResource", url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.d("pageInterceptRequest", url);

                //在pageStart前调用，加载资源的url，返回null的话就加载原地址资源
                //这个在其他线程中进行，如果对UI或其他在主线程中进行的代码需要注意

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d("pageError", failingUrl);
                //ERROR_* 错误
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                //HTTP错误码时调用，资源或者其他url加载错误时都会调用
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
                //处理SSL认证请求，在UI线程中进行，在回调完成前request会被挂起，默认是cancel，没有认证
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                //接收http的认证请求，使用HttpAuthHandler去处理，默认是cancel，没有认证
            }
        });
    }

    private void setWebSettings() {
        WebSettings settings = wb.getSettings();
        settings.setAllowFileAccess(true);//启用或禁止WebView访问文件数据,默认允许，只对file有影响，对assets、res文件无影响
        settings.setBlockNetworkImage(true);// 是否禁止显示网络图像，默认false，受setBlockNetworkLoads和getLoadsImagesAutomatically影响
        settings.setBuiltInZoomControls(true);// 设置是否支持缩放，默认false
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置缓存的模式，默认LOAD_DEFAULT
        settings.setDefaultFontSize(20);// 设置默认的字体大小，默认16，1-72之间
        settings.setDefaultTextEncodingName("utf-8");// 设置在解码时使用的默认编码，默认UTF-8
        settings.setFixedFontFamily("monospace");// 设置固定使用的字体,默认monospace
        settings.setJavaScriptEnabled(true);// 设置是否支持Javascript，默认false
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 设置布局方式,支持内容重新布局，默认Narrow
        settings.setSupportZoom(true);// 设置是否支持缩放，默认为true
        settings.setSaveFormData(true);// 是否保存数据，默认为true
        settings.setTextZoom(100);// 设置文字缩放比 默认100、
        settings.setUseWideViewPort(false);//将图片调整到适合webview的大小，false的话使用css中的width，true的话使用tag中的width
        settings.setSupportMultipleWindows(true);//多窗口,默认false,配合onCreateWindow使用
        settings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点，默认true
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口，默认false，true的话js可通过window.open()打开窗口
        settings.setLoadWithOverviewMode(true);//缩放至屏幕的大小，默认false
        settings.setLoadsImagesAutomatically(true);//支持自动加载图片
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wb.destroy();
    }
}
