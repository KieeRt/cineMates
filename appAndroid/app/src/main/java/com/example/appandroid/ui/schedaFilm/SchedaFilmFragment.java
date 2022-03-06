package com.example.appandroid.ui.schedaFilm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DisplayDialog;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SchedaFilmFragment extends Fragment implements DefaultMethodsFragment {
    private View root ;
    private SchedaFilmViewModel viewModel;
    private Film film;
    private String idFilmDaMostrare ;
    private ImageView immagine ;
    private TextView titolo ;
    private TextView descrizione ;
    private TextView durata ;
    private TextView genere ;
    private TextView valutazione ;
    private FloatingActionButton pulsanteAddInLista ;

    private List<ListaPersonalizzata> elencoListe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_scheda_film, container, false);
        viewModel = new ViewModelProvider(this).get(SchedaFilmViewModel.class);

        if(getArguments()!=null){
            idFilmDaMostrare = getArguments().getString("idFilm");
        }

        flowInitFragment();



        return root;
    }
    @Override
    public void initViewId() {
        immagine = root.findViewById(R.id.immagineFilm);
        titolo = root.findViewById(R.id.titoloFilm);
        descrizione = root.findViewById(R.id.descrizioneFilm);
        durata = root.findViewById(R.id.durataFilm);
        genere = root.findViewById(R.id.genereFilm);
        valutazione = root.findViewById(R.id.valutazioneFilm);
        pulsanteAddInLista = root.findViewById(R.id.pulsanteAddInListaSchedaFilm);
    }

    @Override
    public void initViewListener() {
        pulsanteAddInLista.setOnClickListener(this::showTendina);
    }

    @Override
    public void initObserver() {
        final Observer<Film> observer = new Observer<Film>() {
            @Override
            public void onChanged(@Nullable final Film filmAggiornato) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI","AGGIORNO !!");
                setFilmInfo();

            }
        };


        viewModel.getFilm().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            viewModel.recuperaFilm(idFilmDaMostrare);
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }


    public void setFilmInfo() {
        film = viewModel.getFilm().getValue();
        if(film != null){
            if(!film.getImmagineCopertina().equals("N/A"))
                Picasso.get().load(film.getImmagineCopertina()).into(immagine);
            else{
                Picasso.get().load("https://bucketrisorsas3162443-dev.s3.eu-central-1.amazonaws.com/public/No_Preview_image.png").into(immagine);
            }
            titolo.setText(film.getNome());
            descrizione.setText(film.getTrama());
            durata.setText(film.getDurata());
            genere.setText(film.getGenere());
            valutazione.setText(film.getValutazione());
        }
        else{
            setFilmDefault();
        }
    }

    public void setFilmDefault() {
        String testoDefault= "NESSUNO";
        titolo.setText(testoDefault);
        descrizione.setText(testoDefault);
        durata.setText(testoDefault);
        genere.setText(testoDefault);
        valutazione.setText(testoDefault);
        Picasso.get().load("https://bucketrisorsas3162443-dev.s3.eu-central-1.amazonaws.com/public/No_Preview_image.png").into(immagine);
        UtilsToast.stampaToast(requireActivity(),"Errore Connessione", Toast.LENGTH_SHORT);
    }




    public void showTendina(View view){
        requireActivity().runOnUiThread(()->new DisplayDialog(getContext(),requireActivity()).mostraTendinaSalvaFilmInLista(film));
    }

}