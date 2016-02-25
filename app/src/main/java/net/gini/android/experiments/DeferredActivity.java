package net.gini.android.experiments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeferredActivity extends AppCompatActivity {

    @Bind(R.id.button_resolve)
    Button mButtonResolve;
    @Bind(R.id.button_fail)
    Button mButtonFail;
    @Bind(R.id.button_progress)
    Button mButtonProgress;
    @Bind(R.id.text_resolved_count)
    TextView mResolvedCount;
    @Bind(R.id.text_failed_count)
    TextView mFailedCount;
    @Bind(R.id.text_progress_count)
    TextView mProgressCount;

    private Deferred<Void, Void, Void> mDeferredObject = new DeferredObject<>();
    private Promise<Void, Void, Void> mPromise = mDeferredObject.promise();

    private int mResolvedCounter = 0;
    private int mFailedCounter = 0;
    private int mProgressCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deferred);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

       addCallbacksToPromise(mPromise);
    }

    private void addCallbacksToPromise(Promise<Void, Void, Void> promise) {
        promise.done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void result) {
                mResolvedCounter++;
                mResolvedCount.setText(String.valueOf(mResolvedCounter));
            }
        }).fail(new FailCallback<Void>() {
            @Override
            public void onFail(Void result) {
                mFailedCounter++;
                mFailedCount.setText(String.valueOf(mFailedCounter));
            }
        }).progress(new ProgressCallback<Void>() {
            @Override
            public void onProgress(Void progress) {
                mProgressCounter++;
                mProgressCount.setText(String.valueOf(mProgressCounter));
            }
        });
    }

    @OnClick(R.id.button_resolve)
    public void onResolveClicked() {
        if (!mDeferredObject.isResolved() && !mDeferredObject.isRejected()) {
            mDeferredObject.resolve(null);
        } else {
            mButtonResolve.setEnabled(false);

            if (mDeferredObject.isRejected()) {
                Snackbar.make(mButtonFail, "Already rejected.", Snackbar.LENGTH_SHORT).show();
            } else if (mDeferredObject.isResolved()) {
                Snackbar.make(mButtonFail, "Already resolved.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.button_fail)
    public void onFailClicked() {
        if (!mDeferredObject.isRejected() && !mDeferredObject.isResolved()) {
            mDeferredObject.reject(null);
        } else {
            mButtonFail.setEnabled(false);

            if (mDeferredObject.isRejected()) {
                Snackbar.make(mButtonFail, "Already rejected.", Snackbar.LENGTH_SHORT).show();
            } else if (mDeferredObject.isResolved()) {
                Snackbar.make(mButtonFail, "Already resolved.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.button_progress)
    public void onProgressClicked() {
        if (!mDeferredObject.isRejected() && !mDeferredObject.isResolved()) {
            mDeferredObject.notify(null);
        } else {
            mButtonProgress.setEnabled(false);

            if (mDeferredObject.isRejected()) {
                Snackbar.make(mButtonFail, "Already rejected.", Snackbar.LENGTH_SHORT).show();
            } else if (mDeferredObject.isResolved()) {
                Snackbar.make(mButtonFail, "Already resolved.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.button_new_promise)
    public void onNewPromiseClicked() {
        addCallbacksToPromise(mDeferredObject.promise());
    }

    @OnClick(R.id.button_add_callbacks)
    public void onAddCallbacksClicked() {
        addCallbacksToPromise(mPromise);
    }
}
