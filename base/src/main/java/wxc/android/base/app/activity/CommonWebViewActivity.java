package wxc.android.base.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import wxc.android.base.R;
import wxc.android.base.utils.V;

public class CommonWebViewActivity extends BaseActivity {
    private static final String ARGS_URL = "url";

    private ProgressBar mProgressBar;
    private WebView mWebView;

    public static void goTo(Context context, String url) {
        Intent intent = new Intent(context, CommonWebViewActivity.class);
        intent.putExtra(ARGS_URL, url);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = V.f(this, R.id.webview);
        mProgressBar = V.f(this, R.id.progress_bar);
        String url = getIntent().getStringExtra(ARGS_URL);
        setupWebView();
        mWebView.loadUrl(url);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setNeedInitialFocus(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setUseWideViewPort(false);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setBackgroundColor(Color.WHITE);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebChromeClient(new WebBrowserClient());
        // 设置WebViewClient防止跳转
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // 修复WebView焦点Bug
        mWebView.setOnTouchListener((v, event) -> {
            if (!v.hasFocus()) {
                v.clearFocus();
                v.requestFocusFromTouch();
            }
            return false;
        });
        mWebView.requestFocusFromTouch();
    }

    private final class WebBrowserClient extends WebChromeClient {

        private int mCurrentProgress;

        WebBrowserClient() {
            resetProgress();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > mCurrentProgress) {
                mCurrentProgress = newProgress;
            }
            if (mCurrentProgress > 90) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
        }

        void resetProgress() {
            mCurrentProgress = 10;
            mProgressBar.setProgress(mCurrentProgress);
            mProgressBar.setVisibility(View.VISIBLE);
        }

    }
}
