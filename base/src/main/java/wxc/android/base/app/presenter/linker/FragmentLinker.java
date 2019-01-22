package wxc.android.base.app.presenter.linker;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import wxc.android.base.app.fragment.BaseFragment;
import wxc.android.base.app.presenter.BaseFragmentPresenter;
import wxc.android.base.app.presenter.Presenter;
import timber.log.Timber;

public class FragmentLinker extends BaseFragmentPresenter {

    private static final int ON_ATTACH = 1;
    private static final int ON_CREATE = 2;
    private static final int ON_CREATE_VIEW = 3;
    private static final int ON_ACTIVITY_CREATED = 4;
    private static final int ON_START = 5;
    private static final int ON_RESUME = 6;
    private static final int ON_PAUSE = 7;
    private static final int ON_STOP = 8;
    private static final int ON_DESTROY_VIEW = 9;
    private static final int ON_DESTROY = 10;
    private static final int ON_DETACH = 11;
    private static final int ON_SAVE_STATE = 12;

    private final ArrayList<BaseFragmentPresenter> mFragmentCallbacks = new ArrayList<>();

    // 只有被@Presenter标注的BasePresenter才能与Activity生命周期关联
    public void register(BaseFragment source) {
        Class<?> clazz = source.getClass();
        do {
            if (clazz == null) break;
            registerClass(source, clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != BaseFragment.class);
    }

    private void registerClass(BaseFragment obj, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Presenter) {
                    try {
                        BaseFragmentPresenter presenter = (BaseFragmentPresenter) field.get(obj);
                        presenter.setFragment(obj);
                        addFragmentCallbacks(presenter);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            field.setAccessible(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onLifecycleCallbacks(ON_ATTACH, context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        onLifecycleCallbacks(ON_CREATE, savedInstanceState);
    }

    @Override
    public void initContentView(View view) {
        super.initContentView(view);
        onLifecycleCallbacks(ON_CREATE_VIEW, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onLifecycleCallbacks(ON_ACTIVITY_CREATED, savedInstanceState);
    }

    @Override
    public void onStart() {
        onLifecycleCallbacks(ON_START);
    }

    @Override
    public void onResume() {
        onLifecycleCallbacks(ON_RESUME);
    }

    @Override
    public void onPause() {
        onLifecycleCallbacks(ON_PAUSE);
    }

    @Override
    public void onStop() {
        onLifecycleCallbacks(ON_STOP);
    }

    @Override
    public void onDestroyView() {
        onLifecycleCallbacks(ON_DESTROY_VIEW);
    }

    @Override
    public void onDestroy() {
        onLifecycleCallbacks(ON_DESTROY);
    }

    @Override
    public void onDetach() {
        onLifecycleCallbacks(ON_DETACH);
        mFragmentCallbacks.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        onLifecycleCallbacks(ON_SAVE_STATE, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mFragmentCallbacks.isEmpty()) {
            boolean result = true;
            for (BaseFragmentPresenter callbacks : mFragmentCallbacks) {
                if (callbacks != null) {
                    result &= callbacks.onOptionsItemSelected(item);
                }
            }
            return result;
        }
        return false;
    }

    public void addFragmentCallbacks(BaseFragmentPresenter callbacks) {
        if (!mFragmentCallbacks.contains(callbacks)) {
            mFragmentCallbacks.add(callbacks);
        }
    }

    private void onLifecycleCallbacks(int level) {
        onLifecycleCallbacks(level, null);
    }

    private void onLifecycleCallbacks(int level, Object object) {
        for (BaseFragmentPresenter callbacks : mFragmentCallbacks) {
            if (callbacks != null) {
                switch (level) {
                    case ON_ATTACH:
                        Timber.d("onAttach: %s", callbacks);
                        callbacks.onAttach((Context) object);
                        break;
                    case ON_CREATE:
                        Timber.d("onCreate: %s", callbacks);
                        callbacks.onCreate((Bundle) object);
                        break;
                    case ON_CREATE_VIEW:
                        Timber.d("initContentView: %s", callbacks);
                        callbacks.initContentView((View) object);
                        break;
                    case ON_ACTIVITY_CREATED:
                        Timber.d("onActivityCreated: %s", callbacks);
                        callbacks.onActivityCreated((Bundle) object);
                        break;
                    case ON_START:
                        Timber.d("onStart: %s", callbacks);
                        callbacks.onStart();
                        break;
                    case ON_RESUME:
                        Timber.d("onResume: %s", callbacks);
                        callbacks.onResume();
                        break;
                    case ON_PAUSE:
                        Timber.d("onPause: %s", callbacks);
                        callbacks.onPause();
                        break;
                    case ON_STOP:
                        Timber.d("onStop: %s", callbacks);
                        callbacks.onStop();
                        break;
                    case ON_DESTROY_VIEW:
                        Timber.d("onDestroyView: %s", callbacks);
                        callbacks.onDestroyView();
                        break;
                    case ON_DESTROY:
                        Timber.d("onDestroy: %s", callbacks);
                        callbacks.onDestroy();
                        break;
                    case ON_SAVE_STATE:
                        Timber.d("onSaveInstanceState: %s", callbacks);
                        callbacks.onSaveInstanceState((Bundle) object);
                        break;
                    default:
                }
            }
        }
    }
}
