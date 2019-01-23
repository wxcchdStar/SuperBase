package wxc.android.base.demo.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import wxc.android.base.app.fragment.BaseFragment;
import wxc.android.base.demo.R;
import wxc.android.base.demo.ui.music.MusicRankingListActivity;

public class MainTabFragment extends BaseFragment {
    private static final String ARGS_POSITION = "args_position";

    public static MainTabFragment newInstance(int position) {
        MainTabFragment fragment = new MainTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_tab, container, false);
        Button button = view.findViewById(R.id.btn_button);
        button.setText("ApiDemo-" + getArguments().getInt(ARGS_POSITION, -1));
        button.setOnClickListener(v -> MusicRankingListActivity.goTo(getContext()));
        return view;
    }

}
