package co.tton.android.base.app.presenter.linker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import co.tton.android.base.app.activity.BaseActivity;
import co.tton.android.base.app.presenter.BaseActivityPresenter;
import co.tton.android.base.app.presenter.Presenter;
import timber.log.Timber;

public class ActivityLinker extends BaseActivityPresenter {

    private static final int ON_CREATE = 1;
    private static final int ON_START = 2;
    private static final int ON_RESUME = 3;
    private static final int ON_PAUSE = 4;
    private static final int ON_STOP = 5;
    private static final int ON_DESTROY = 6;
    private static final int ON_SAVE_STATE = 7;

    private ArrayList<BaseActivityPresenter> mActivityCallbacks = new ArrayList<>();

    // 只有被@Presenter标注的BasePresenter才能与Activity生命周期关联
    public void register(BaseActivity source) {
        Class<?> clazz = source.getClass();
        do {
            if (clazz == null) break;
            registerClass(source, clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != BaseActivity.class);
    }

    private void registerClass(BaseActivity obj, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Presenter) {
                    try {
                        BaseActivityPresenter presenter = (BaseActivityPresenter) field.get(obj);
                        presenter.setActivity(obj);
                        addActivityCallbacks(presenter);
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
    public void onCreate(Bundle savedInstanceState) {
        onLifecycleCallbacks(ON_CREATE, savedInstanceState);
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
    public void onDestroy() {
        onLifecycleCallbacks(ON_DESTROY);
        mActivityCallbacks.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        onLifecycleCallbacks(ON_SAVE_STATE, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mActivityCallbacks.isEmpty()) {
            boolean result = true;
            for (BaseActivityPresenter callbacks : mActivityCallbacks) {
                if (callbacks != null) {
                    result &= callbacks.onOptionsItemSelected(item);
                }
            }
            return result;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (BaseActivityPresenter callbacks : mActivityCallbacks) {
            if (callbacks != null) {
                callbacks.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void addActivityCallbacks(BaseActivityPresenter callbacks) {
        if (!mActivityCallbacks.contains(callbacks)) {
            mActivityCallbacks.add(callbacks);
        }
    }

    private void onLifecycleCallbacks(int level) {
        onLifecycleCallbacks(level, null);
    }

    private void onLifecycleCallbacks(int level, Bundle savedInstanceState) {
        for (BaseActivityPresenter callbacks : mActivityCallbacks) {
            if (callbacks != null) {
                switch (level) {
                    case ON_CREATE:
                        Timber.d("onCreate: %s", callbacks);
                        callbacks.onCreate(savedInstanceState);
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
                    case ON_DESTROY:
                        Timber.d("onDestroy: %s", callbacks);
                        callbacks.onDestroy();
                        break;
                    case ON_SAVE_STATE:
                        Timber.d("onSaveInstanceState: %s", callbacks);
                        callbacks.onSaveInstanceState(savedInstanceState);
                        break;
                    default:
                }
            }
        }
    }
}
