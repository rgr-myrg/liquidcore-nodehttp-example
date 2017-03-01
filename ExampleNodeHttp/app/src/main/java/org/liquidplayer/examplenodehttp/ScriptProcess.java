package org.liquidplayer.examplenodehttp;

import android.content.Context;
import android.util.Log;

import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.node.Process;

/**
 * Helper class that creates a node.js process and implements the process event listener
 */

public class ScriptProcess implements Process.EventListener {
    public static final String TAG = ScriptProcess.class.getSimpleName();

    private String mScript;
    private OnComplete mOnComplete;
    private JSContext mJsContext;

    /**
     * Creates a node.js process
     * @param script the context object
     * @param script the javascript string
     * @param onComplete the OnComplete interface object
     */
    public ScriptProcess(Context context, String script, OnComplete onComplete) {
        this.mScript = script;
        this.mOnComplete = onComplete;

        new Process(context, "_script_id_", Process.kMediaAccessPermissionsRW, this);
    }

    @Override
    public void onProcessStart(Process process, JSContext context) {
        Log.i(TAG, "onProcessStart");
        mJsContext = context;
        mJsContext.evaluateScript(mScript);
    }

    @Override
    public void onProcessAboutToExit(Process process, int exitCode) {
        Log.i(TAG, "onProcessAboutToExit");

        if (mOnComplete == null) {
            return;
        }

        mOnComplete.run(mJsContext);
    }

    @Override
    public void onProcessExit(Process process, int exitCode) {
        Log.i(TAG, "onProcessExit");
    }

    @Override
    public void onProcessFailed(Process process, Exception error) {
        Log.i(TAG, "onProcessFailed " + error.getMessage());
    }

    public static interface OnComplete {
        void run(JSContext jsContext);
    }
}
