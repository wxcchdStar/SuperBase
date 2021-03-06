package wxc.android.base.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import wxc.android.base.R;
import wxc.android.base.utils.V;

public class CommonLayout extends FrameLayout {

    private int mEmptyLayoutId;
    private int mErrorLayoutId;
    private int mLoadingLayoutId;
    private int mContentLayoutId;

    private ViewStub mEmptyStub;
    private ViewStub mErrorStub;
    private ViewStub mLoadingStub;
    private ViewStub mContentStub;

    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;
    private View mContentView;

    private OnClickListener mOnErrorClickListener;

    public CommonLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CommonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CommonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (mEmptyStub != null) return;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonLayout);
        mEmptyLayoutId = ta.getResourceId(R.styleable.CommonLayout_layout_empty, R.layout.common_empty);
        mErrorLayoutId = ta.getResourceId(R.styleable.CommonLayout_layout_error, R.layout.common_error);
        mLoadingLayoutId = ta.getResourceId(R.styleable.CommonLayout_layout_loading, R.layout.common_loading);
        mContentLayoutId = ta.getResourceId(R.styleable.CommonLayout_layout_content, 0);
        ta.recycle();

        View view = inflate(context, R.layout.common_layout, this);

        mEmptyStub = V.f(view, R.id.vs_empty);
        mEmptyStub.setLayoutResource(mEmptyLayoutId);

        mErrorStub = V.f(view, R.id.vs_error);
        mErrorStub.setLayoutResource(mErrorLayoutId);

        mLoadingStub = V.f(view, R.id.vs_loading);
        mLoadingStub.setLayoutResource(mLoadingLayoutId);

        mContentStub = V.f(view, R.id.vs_content);
        mContentStub.setLayoutResource(mContentLayoutId);
    }

    public void showLoading() {
        if (mLoadingLayoutId == 0) return;

        setContentViewVisible(GONE);
        setErrorViewVisible(GONE);
        setEmptyViewVisible(GONE);
        setLoadingViewVisible(VISIBLE);
    }

    public void showEmpty() {
        if (mEmptyLayoutId == 0) return;

        setContentViewVisible(GONE);
        setErrorViewVisible(GONE);
        setEmptyViewVisible(VISIBLE);
        setLoadingViewVisible(GONE);
    }

    public void showError() {
        if (mErrorLayoutId == 0) return;

        setContentViewVisible(GONE);
        setErrorViewVisible(VISIBLE);
        setEmptyViewVisible(GONE);
        setLoadingViewVisible(GONE);
    }

    public void showContent() {
        if (mContentLayoutId == 0) return;

        setContentViewVisible(VISIBLE);
        setErrorViewVisible(GONE);
        setEmptyViewVisible(GONE);
        setLoadingViewVisible(GONE);
    }

    private void setEmptyViewVisible(int visible) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visible);
        } else if (visible == VISIBLE) {
            mEmptyView = mEmptyStub.inflate();
            mEmptyView.setVisibility(VISIBLE);
        }
    }

    private void setErrorViewVisible(int visible) {
        if (mErrorView != null) {
            mErrorView.setVisibility(visible);
        } else if (visible == VISIBLE) {
            mErrorView = mErrorStub.inflate();
            mErrorView.setVisibility(VISIBLE);
            mErrorView.setOnClickListener(mOnErrorClickListener);
        }
    }

    private void setLoadingViewVisible(int visible) {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(visible);
        } else if (visible == VISIBLE) {
            mLoadingView = mLoadingStub.inflate();
            mLoadingView.setVisibility(VISIBLE);
        }
    }

    private void setContentViewVisible(int visible) {
        if (mContentView != null) {
            mContentView.setVisibility(visible);
        } else if (visible == VISIBLE) {
            mContentView = mContentStub.inflate();
            mContentView.setVisibility(VISIBLE);
        }
    }

    public void setEmptyLayoutId(int layoutId) {
        mEmptyLayoutId = layoutId;
        mEmptyStub.setLayoutResource(layoutId);
    }

    public void setErrorLayoutId(int layoutId) {
        mErrorLayoutId = layoutId;
        mErrorStub.setLayoutResource(layoutId);
    }

    public void setLoadingLayoutId(int layoutId) {
        mLoadingLayoutId = layoutId;
        mLoadingStub.setLayoutResource(layoutId);
    }

    public void setContentLayoutId(int layoutId) {
        mContentLayoutId = layoutId;
        mContentStub.setLayoutResource(layoutId);
    }

    public View getContentView() {
        return mContentView;
    }

    public void setOnErrorClickListener(OnClickListener listener) {
        mOnErrorClickListener = listener;
    }
}
