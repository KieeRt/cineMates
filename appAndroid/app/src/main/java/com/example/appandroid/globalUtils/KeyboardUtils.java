package com.example.appandroid.globalUtils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public abstract class KeyboardUtils {

	public static void hideKeyboard(Activity activity) {
		View view = activity.findViewById(android.R.id.content);
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void showKeyboard(Activity activity, View root){
		View view = activity.findViewById(android.R.id.content);
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, 0);
		}
	}

	/**
	 * La funzione nascode la tastiera in caso in cui viene tolto focus dalla view
	 * @override setOnFocusChangeListener
	 * @param view la View sulla quale viene fissato listen
	 * @param activity activity attuale nella quale è presente la nostra view
	 */
	public static void HideKeyboardOnFocusChangeListener(@NonNull View view, @NonNull Activity activity){
		view.setOnFocusChangeListener((v, hasFocus) -> {
			if(!hasFocus) {
				KeyboardUtils.hideKeyboard(activity);
			}
		});
	}


}
