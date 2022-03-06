package com.example.appandroid.listViewClass.notifica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appandroid.R;

import java.util.List;

public class AdapterNotifica extends BaseAdapter {
	private final Context listViewContext;
	private final List<Notifica> listItemNotifica;
	private final LayoutInflater inflater;
	private final View.OnClickListener listener ;

	public AdapterNotifica(Context listViewContext, List<Notifica> listItemNotifica, View.OnClickListener listener) {
		this.listViewContext = listViewContext;
		this.listItemNotifica = listItemNotifica;
		this.listener = listener;
		if(listViewContext != null)
			inflater = (LayoutInflater) listViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		else
			inflater=null;
	}

	@Override
	public int getCount() {
		if(listItemNotifica == null)
			return 0;
		return this.listItemNotifica.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listItemNotifica.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long) position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.riga_notifica, parent, false);

		TextView messaggioNotifica = view.findViewById(R.id.testoNotifica);
		ImageView immagineNotifica = view.findViewById(R.id.immagineNotifica);

		Notifica currentNotifica = listItemNotifica.get(position);


		String testoDaMostrare = currentNotifica.getMessaggio();
		if(testoDaMostrare.contains("segnalazione")){
			immagineNotifica.setImageResource(R.drawable.ic_baseline_report_gmailerrorred_24);
		}else{
			immagineNotifica.setImageResource(R.drawable.ic_baseline_group_24);

		}

		messaggioNotifica.setText(testoDaMostrare);

		//Todo : Bisogna settare l'immagine

		return view;
	}

}
