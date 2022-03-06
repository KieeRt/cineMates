package com.example.appandroid.ui.filmInComune;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.AdapterFilm;
import com.example.appandroid.listViewClass.film.Film;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class FilmInComuneFragment extends Fragment implements DefaultMethodsFragment {

    private View root ;
    private FilmInComuneViewModel viewModel;

    private List<Film> filmInComune ;
    private ListView listViewFilm ;
    private String emailAltroUtente ;

    private AdapterFilm adapterFilm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(FilmInComuneViewModel.class);
        root = inflater.inflate(R.layout.fragment_film_in_comune, container, false);

        getBundleArguments();

        flowInitFragment();

        initListViewFilm();

        return root;
    }

    @Override
    public void initViewId(){
        listViewFilm= root.findViewById(R.id.listView_film_in_comune);
    }

    @Override
    public void initViewListener() {

    }

    @Override
    public void initObserver() {
        final Observer<List<Film>> observer = new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable final List<Film> filmInComuneAggiornato) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI ","AGGIORNO !! Film in comune");
                adapterFilm.notifyDataSetChanged();
            }
        };

        viewModel.getFilmInComune().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            try {
                viewModel.recuperaFilmInComune();
            } catch (JSONException | TimeoutException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init(emailAltroUtente);

    }


    public void getBundleArguments(){
        if(getArguments()!=null)
            emailAltroUtente=getArguments().getString("email");
        else
            emailAltroUtente="NESSUNA";
    }

    public void initListViewFilm(){

        adapterFilm = new AdapterFilm(getContext(),viewModel.getFilmInComune().getValue(),AdapterFilm.FILM_IN_COMUNE,null);
        listViewFilm.setAdapter(adapterFilm);
        listViewFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Logica dopo selezione di un film dall'elenco
                Bundle bundle = new Bundle();
                bundle.putString("idFilm",viewModel.getFilmInComune().getValue().get(position).getIdFilm());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.moveToSchedaFilm,bundle);
            }
        });
    }

}