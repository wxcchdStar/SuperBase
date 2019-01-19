package wxc.android.base.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle3.components.support.RxFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import wxc.android.base.app.activity.BaseActivity;
import wxc.android.base.app.presenter.linker.FragmentLinker;

public abstract class BaseFragment extends RxFragment {

    private FragmentLinker mLinker = new FragmentLinker();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLinker.register(this);
        mLinker.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinker.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initContentView(inflater, container, savedInstanceState);
        mLinker.initContentView(view);
        return view;
    }

    protected abstract View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLinker.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLinker.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLinker.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLinker.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLinker.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLinker.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLinker.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLinker.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mLinker.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mLinker.onSaveInstanceState(outState);
    }

    public static boolean isAvailable(Fragment fragment) {
        if (fragment == null) return false;

        return BaseActivity.isAvailable(fragment.getActivity());
    }
}
