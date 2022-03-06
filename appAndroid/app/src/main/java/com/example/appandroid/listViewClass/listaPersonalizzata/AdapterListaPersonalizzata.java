package com.example.appandroid.listViewClass.listaPersonalizzata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.utils.BackgroundBitmapCache;
import com.example.appandroid.globalUtils.utils.DecodeBitmapTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AdapterListaPersonalizzata extends BaseAdapter{
	private final Context listViewContext;
	private final List<ListaPersonalizzata> listItemListePersonalizzate;
	private final LayoutInflater inflater;
	private final View.OnClickListener listenerIconaMenu ;

	private final int CURRENT_STATE ;
	public static final int LISTA_PROPRIA = 0 ;
	public static final int LISTA_ALTRO_UTENTE = 1;

	public AdapterListaPersonalizzata(Context context, List<ListaPersonalizzata> listitem , View.OnClickListener listenerIconaMenu, int state) {
		super();
		this.listViewContext = context;
		this.listItemListePersonalizzate = listitem;
		this.listenerIconaMenu = listenerIconaMenu;
		this.CURRENT_STATE=state;
		if(context!=null)
			inflater = (LayoutInflater) listViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		else
			inflater=null;
	}



	@Override
	public int getCount() {
		if(listItemListePersonalizzate == null)
			return 0;
		return this.listItemListePersonalizzate.size();
	}

	@Override
	public Object getItem(int position) {
		return this.listItemListePersonalizzate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long) position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.riga_lista_personalizzata, parent, false);

		TextView nomeLista =  view.findViewById(R.id.riga_nome_lista);
		TextView numeroDiFilm = view.findViewById(R.id.riga_lista_n_film);
		ImageView immagineLista =  view.findViewById(R.id.riga_immagine_lista);
		ImageView iconaMenuLista =  view.findViewById(R.id.riga_icona_menu_lista);

		ListaPersonalizzata currentLista = listItemListePersonalizzate.get(position);



		String currentName = currentLista.getNome();
		String currentImage = currentLista.getImmagineCopertina();
		int currentNumFilm = currentLista.getnFilmContenuti();
		String stringaNumFilm = currentNumFilm+" Film";

		if(currentLista.isCensored()){
			float radius = nomeLista.getTextSize() / 3;
			BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
			nomeLista.getPaint().setMaskFilter(filter);
		}

		nomeLista.setText(currentName);
		numeroDiFilm.setText(stringaNumFilm);
		if(currentImage != null && !currentImage.equals("null")){
			Picasso.get().load(currentImage).into(immagineLista);
		}else{
			Picasso.get().load("https://bucketrisorsas3162443-dev.s3.eu-central-1.amazonaws.com/public/No_Preview_image.png").into(immagineLista);
		}
		iconaMenuLista.setTag(position);

		if(currentName.equals("Film da guardare")){
			setupIconAction(iconaMenuLista,0);
		}
		else{
			switch (CURRENT_STATE){
				case LISTA_ALTRO_UTENTE:
					setupIconAction(iconaMenuLista,0);
					break;
				case LISTA_PROPRIA:
					setupIconAction(iconaMenuLista,R.drawable.ic_baseline_more_vert_24);
					break;
				default:
					break;
			}
		}



		return view;
	}

	public void setupIconAction(ImageView icona, @DrawableRes int risorsaIcona){
		if(risorsaIcona != 0){
			icona.setImageResource(risorsaIcona);
			if(listenerIconaMenu != null)
				icona.setOnClickListener(listenerIconaMenu);
		}
	}

}
