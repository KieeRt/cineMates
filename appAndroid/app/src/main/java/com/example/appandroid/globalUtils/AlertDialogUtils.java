package com.example.appandroid.globalUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Classe di supporto per inizializzare un alert dialog.
 * Il dialog viene costruito nel costruttore della classe.
 *
 */



public class AlertDialogUtils {
	private final View layout ;
	private final AlertDialog alertDialog ;



	/**
	 *	Primo argomento dev'essere il contesto in cui mostrare il dialog.
	 * 	Secondo argomento dev'essere una risorsa Layout xml "LayoutRes" che rappresenti
	 * 	l'interfaccia della tendina.
	 * */
	public AlertDialogUtils(@NonNull Context context ,@LayoutRes int resLayoutId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		layout = inflater.inflate(resLayoutId,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setCancelable(false);
	}

	public void mostraAlertDialog(){
		alertDialog.show();
		alertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
	}

	public void chiudiAlert(){
		alertDialog.cancel();
	}

	public void initAlertButtonAction(@IdRes int resButton, View.OnClickListener clickListener){
		Button button = layout.findViewById(resButton);
		if(button != null){
			button.setOnClickListener(clickListener);
		}
	}

	public View getLayout() {
		return layout;
	}

	public AlertDialog getAlertDialog() {
		return alertDialog;
	}
}

