package com.example.appandroid.listViewClass.film;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.example.appandroid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFilm extends BaseAdapter {
	private final Context listViewContext;
	private final List<Film> listItemFilm ;
	private final LayoutInflater inflater;

	private final int CURRENT_STATE ;
	public static final int FILM_IN_LISTA = 0 ;
	public static final int FILM_IN_QUERY_RICERCA = 1;
	public static final int FILM_IN_COMUNE = 2;
	private final View.OnClickListener listenerIcona ;


	public AdapterFilm(Context context, List<Film> listitem, int state, View.OnClickListener listenerIcona) {
		super();
		this.listViewContext = context;
		this.listItemFilm = listitem;
		this.CURRENT_STATE = state;

		if(context!=null)
			inflater = (LayoutInflater) listViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		else
			inflater=null;

		this.listenerIcona = listenerIcona;
	}

	@Override
	public int getCount() {
		if(listItemFilm == null)
			return 0;
		return this.listItemFilm.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listItemFilm.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long) position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.riga_film, parent, false);

		TextView nomeFilm =  view.findViewById(R.id.riga_nome_film);
		ImageView immagineFilm =  view.findViewById(R.id.riga_immagine_film);
		ImageView iconaMenuFilm = view.findViewById(R.id.riga_icona_menu_film_in_lista);

		Film currentFilm = listItemFilm.get(position);

		String currentNomeFilm = currentFilm.getNome();
		String currentImmagineCopertinaURL = currentFilm.getImmagineCopertina();

		nomeFilm.setText(currentNomeFilm);

		if(currentImmagineCopertinaURL.equals("N/A")){
			Picasso.get().load("https://bucketrisorsas3162443-dev.s3.eu-central-1.amazonaws.com/public/No_Preview_image.png").into(immagineFilm);
		}
		else{
			Picasso.get().load(currentImmagineCopertinaURL).into(immagineFilm);
		}



		iconaMenuFilm.setTag(position);

		switch (CURRENT_STATE){
			case FILM_IN_LISTA:
				setupIconAction(iconaMenuFilm,R.drawable.ic_baseline_more_vert_24);
				break;
			case FILM_IN_QUERY_RICERCA:
				setupIconAction(iconaMenuFilm,R.drawable.ic_baseline_add_24);
				break;
			case FILM_IN_COMUNE:
				setupIconAction(iconaMenuFilm,0);
				break;
		}


		return view;
	}

	public void setupIconAction(ImageView icona, @DrawableRes int risorsaIcona){
		if(risorsaIcona != 0){
			icona.setImageResource(risorsaIcona);
			if(listenerIcona != null)
				icona.setOnClickListener(listenerIcona);
		}
	}
}
