package com.example.appandroid.listViewClass.listaPersonalizzata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.appandroid.R;
import com.example.appandroid.listViewClass.film.Film;

import java.util.List;

public class AdapterTendinaSalvaFilmInLista extends BaseAdapter {
	private final Context listViewContext;
	private final List<ListaPersonalizzata> listItemListePersonalizzate;
	private final LayoutInflater inflater;
	private final Film film;
	private final CompoundButton.OnCheckedChangeListener listener ;

	public AdapterTendinaSalvaFilmInLista(Context context, List<ListaPersonalizzata> listitem, Film film, CompoundButton.OnCheckedChangeListener listener) {
		super();
		this.listViewContext = context;
		this.listItemListePersonalizzate = listitem;
		this.film=film;
		this.listener=listener;
		if(listViewContext != null)
			inflater = (LayoutInflater) listViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		else
			inflater=null;
	}

	@Override
	public int getCount() {
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
			view = inflater.inflate(R.layout.riga_lista_in_tendina, parent, false);

		CheckBox checkBox = view.findViewById(R.id.checkBoxTendinaSalvaFilmInLista);
		ListaPersonalizzata currentLista = listItemListePersonalizzate.get(position);
		String currentName = currentLista.getNome();
		checkBox.setText(currentName);
		checkBox.setTag(position);
		checkBox.setChecked(currentLista.filmIsInLista(film.getIdFilm()));
		checkBox.setOnCheckedChangeListener(listener);

		return view;
	}

}
