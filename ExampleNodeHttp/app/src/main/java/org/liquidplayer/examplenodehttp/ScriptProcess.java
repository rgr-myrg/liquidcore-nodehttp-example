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

	private String mId;
	private String mScript;
	private OnComplete mOnComplete;
	private OnError mOnError;
	private JSContext mJsContext;

	public ScriptProcess() {}

	public void startNewProcess(Context context) {
		new Process(context, mId, Process.kMediaAccessPermissionsRW, this);
	}

	public static class Builder {
		private ScriptProcess scriptProcess;

		public Builder() {
			scriptProcess = new ScriptProcess();
		}

		public Builder setScriptId(String id) {
			scriptProcess.mId = id;
			return this;
		}

		public Builder setJsString(String jsScript) {
			scriptProcess.mScript = jsScript;
			return this;
		}

		public Builder onComplete(OnComplete onComplete) {
			scriptProcess.mOnComplete = onComplete;
			return this;
		}

		public Builder onError(OnError onError) {
			scriptProcess.mOnError = onError;
			return this;
		}

		public Builder buildWithContext(Context context) {
			scriptProcess.startNewProcess(context);
			return this;
		}
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
		if (mOnError == null) {
			return;
		}

		mOnError.run(error.getMessage());
	}

	public static interface OnComplete {
		void run(JSContext jsContext);
	}

	public static interface OnError {
		void run(String message);
	}
}
