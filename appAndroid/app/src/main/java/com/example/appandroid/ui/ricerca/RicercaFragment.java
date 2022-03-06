package com.example.appandroid.ui.ricerca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DisplayDialog;
import com.example.appandroid.globalUtils.KeyboardUtils;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.listViewClass.film.AdapterFilm;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class RicercaFragment extends Fragment implements DefaultMethodsFragment {
	private View root;
	private RicercaViewModel ricercaViewModel;
	private TextInputEditText textInputEditText;
	private TextInputLayout textInputLayout;
	private ListView listViewFilm;
	private AdapterFilm adapterFilm;
	private List<ListaPersonalizzata> elencoListe;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		ricercaViewModel = 	new ViewModelProvider(this).get(RicercaViewModel.class);
		root = inflater.inflate(R.layout.fragment_ricerca_film, container, false);



		flowInitFragment();

		initListViewFilm();

		return root;
	}

	public void showTendina(Film filmPremuto){
		requireActivity().runOnUiThread(() ->  new DisplayDialog(getContext(),requireActivity()).mostraTendinaSalvaFilmInLista(filmPremuto));
	}

	@Override
	public void initViewId(){
		textInputLayout = root.findViewById(R.id.TextInputLaout_ricercaFilm);
		textInputEditText = root.findViewById(R.id.TextInputEditText_ricercaFilm);
		listViewFilm = root.findViewById(R.id.listViewRicercaFilm);
	}

	@Override
	public void initViewListener() {
		KeyboardUtils.HideKeyboardOnFocusChangeListener(textInputEditText, getActivity());
		textInputLayout.setEndIconOnClickListener(this::effettuaRicercaPremuto);
	}

	@Override
	public void initObserver() {
		final Observer<List<Film>> observer = new Observer<List<Film>>() {
			@Override
			public void onChanged(List<Film> films) {
				adapterFilm.notifyDataSetChanged();
			}
		};

		final Observer<Boolean> observerRicercaFinita = new Observer<Boolean>() {
			@Override
			public void onChanged(Boolean finito) {
				if(finito){
					if(ricercaViewModel.getRisultati().getValue().isEmpty())
						Toast.makeText(getContext(),"Nessun risultato trovato",Toast.LENGTH_SHORT).show();
				}
			}
		};

		ricercaViewModel.getRisultati().observe(getViewLifecycleOwner(), observer);
		ricercaViewModel.getRicercaFinita().observe(getViewLifecycleOwner(), observerRicercaFinita);
	}

	@Override
	public void fetchData() {

	}

	@Override
	public void initEmptyDateVM() {
		ricercaViewModel.init();

	}



	public void initListViewFilm(){
		View.OnClickListener iconListener = generaListenerPerIconaMenu();
		adapterFilm = new AdapterFilm(getContext(),ricercaViewModel.getRisultati().getValue(),AdapterFilm.FILM_IN_QUERY_RICERCA,iconListener);

		listViewFilm.setAdapter(adapterFilm);
		listViewFilm.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Logica dopo selezione di un film dall'elenco
				Bundle bundle = new Bundle();
				bundle.putString("idFilm", ricercaViewModel.getRisultati().getValue().get(position).getIdFilm());
				NavController navController = Navigation.findNavController(view);
				navController.navigate(R.id.action_ricercaFragment_to_schedaFilm,bundle);
			}
		});
	}

	public View.OnClickListener generaListenerPerIconaMenu(){
		return new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getContext(), v);
				popup.inflate(R.menu.menu_opzioni_film_lista_altri);
				popup.show();
				popup.setOnMenuItemClickListener(generaListenerPerPopup(v));
			}
		};
	}

	public  PopupMenu.OnMenuItemClickListener generaListenerPerPopup(View viewPremuta){
		return new PopupMenu.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				int itemCliccato = item.getItemId();
				if(itemCliccato == R.id.item_salva_in_una_lista_ListaAltri){
					//Todo : logica salvataggio film in lista
					//Dobrebbe Aprire tendina salva film in lista
					System.out.println("PREMUTO IL PULSANTE +");
					int position = (int)viewPremuta.getTag();
					showTendina(ricercaViewModel.getRisultati().getValue().get(position));
				}
				return false;
			}
		};
	}

	private void effettuaRicercaPremuto(View view) {
		// TODO: operazione da effettuare sul pulsante ricerca
		KeyboardUtils.hideKeyboard(getActivity());
		new Thread(()->{
			ricercaViewModel.effettuaRicerca(textInputEditText.getText().toString());
		}).start();

	}




}

