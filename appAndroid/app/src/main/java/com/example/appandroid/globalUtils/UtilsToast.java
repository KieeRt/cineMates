package com.example.appandroid.globalUtils;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class UtilsToast {

	public static void stampaToast(FragmentActivity activity, String msg, int durata) {
		if (activity != null && activity.getApplicationContext() != null) {
			if (Thread.currentThread().getName().equals("main")) {
				System.out.println(" STAMPO ");
				Toast.makeText(activity.getApplicationContext(), msg, durata).show();
			} else {
				System.out.println(" STAMPO ");
				activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), msg, durata).show());
			}
		}

	}

	public static void stampaToast(Context context, String msg, int durata) {
		if (context != null) {
			Toast.makeText(context, msg, durata).show();
		}

	}
}