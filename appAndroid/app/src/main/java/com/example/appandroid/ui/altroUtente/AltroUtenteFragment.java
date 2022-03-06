package com.example.appandroid.ui.altroUtente;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.Rotante;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class AltroUtenteFragment extends Fragment implements DefaultMethodsFragment {

    private AltroUtenteViewModel viewModel;
    private View root;
    private TextView textView_nomeUtente;
    private TextView textView_ElencoListe;
    private TextView textView_FilmInComune;

    private ImageView imageView_opzioniUtente1;
    private ImageView imageView_opzioniUtente2;
    private ImageView imageView_immagine_utente;

    private RecyclerView recyclerViewFilmInComune ;
    private NavController navController;

    private String emailAltroUtente;

    private View.OnClickListener listenerRimuoviAmicizia;
    private View.OnClickListener listenerInviaRichiesta;
    private View.OnClickListener listenerAccettaAmicizia;
    private View.OnClickListener listenerRifiutaAmicizia;
    private View.OnClickListener listenerRimuoviRichiesta;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_altro_utente, container, false);

        if (getArguments() != null) {
            emailAltroUtente = getArguments().getString("email");
            viewModel = new ViewModelProvider(this, new AltroUtenteViewModelFactory(emailAltroUtente,requireActivity().getApplication())).get(AltroUtenteViewModel.class);
        }

        flowInitFragment();

        initButtonOptionListner();

        return root;
    }

    @Override
    public void fetchData(){
        new Thread(()->{
            try {
                viewModel.recuperaListeUtente();
            } catch (TimeoutException | JSONException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
            }
        }).start();


        new Thread(()->{
            viewModel.recuperaBitmapImmagineUtente();
        }).start();


        new Thread(()->{
            try {
                viewModel.recuperaStatusAmicizia();

            } catch (TimeoutException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            try {
                viewModel.recuperaUsernameUtente();
            } catch (TimeoutException | JSONException e) {
                e.printStackTrace();

            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }

    @Override
    public void initObserver(){
        final Observer<List<ListaPersonalizzata>> observer = new Observer<List<ListaPersonalizzata>>() {
            @Override
            public void onChanged(@Nullable final List<ListaPersonalizzata> listaAggiornata) {
                // Update the UI
                mostraRotanteListeUtente();
            }
        };

        final Observer<String> observerNome = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String nomeAggiornato) {
                // Update the UI
                textView_nomeUtente.setText(nomeAggiornato);
            }
        };


        final Observer<Bitmap> observerImmagine = new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable final Bitmap bitmapAggiornato) {
                // Update the UI
                if(bitmapAggiornato==null)
                    imageView_immagine_utente.setImageResource(R.drawable.no_preview_image);
                else
                    imageView_immagine_utente.setImageBitmap(bitmapAggiornato);
            }
        };

        final Observer<Integer> observerStatus = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer status) {
                // Update the UI
                setSchermataWithState(status);
                if(status==0){
                    //Mostra rotante film in comune...
                    new Thread(()->{
                        try {
                            viewModel.caricaFilmInComune();
                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                        }
                    }).start();

                    mostraFilmInComune();
                }
                else{
                    //Nascondi rotante film in comune...
                    nascondiFilmInComune();
                }
            }
        };

        final Observer<List<Film>> observerFilmComune = new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable final List<Film> filmInComuneAggiornato) {
                // Update the UI
                mostraRotanteFilmInComune();
            }
        };

        viewModel.getListeAltroUtente().observe(getViewLifecycleOwner(), observer);
        viewModel.getNomeAltroUtente().observe(getViewLifecycleOwner(), observerNome);
        viewModel.getStatusFriend().observe(getViewLifecycleOwner(),observerStatus);
        viewModel.getImmagineAltroUtente().observe(getViewLifecycleOwner(), observerImmagine);
        viewModel.getFilmInComune().observe(getViewLifecycleOwner(), observerFilmComune);

    }

    @Override
    public void initViewId(){
        textView_nomeUtente = root.findViewById(R.id.textView_nomeUtente_altroUtente);
        imageView_immagine_utente = root.findViewById(R.id.imageView_immagine_altroUtente);
        textView_ElencoListe = root.findViewById(R.id.textView_liste_altro_utente);
        imageView_opzioniUtente1 = root.findViewById(R.id.imageView_opzione_altro_utente_1);
        imageView_opzioniUtente2 = root.findViewById(R.id.imageView_opzione_altro_utente_2);
        textView_FilmInComune=root.findViewById(R.id.textView_film_in_comune_schermata_altro_utente);
        recyclerViewFilmInComune=root.findViewById(R.id.recyclerViewFilm);
    }

    @Override
    public void initViewListener(){
        textView_ElencoListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putString("email", emailAltroUtente);
                navController.navigate(R.id.action_altroUtente_to_listeAltroUtenteFragment,bundle);
            }
        });

        textView_FilmInComune.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("email", emailAltroUtente);
                navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_altroUtente_to_filmInComuneFragment,bundle);
            }
        });
    }

    public void mostraFilmInComune(){
        textView_FilmInComune.setVisibility(View.VISIBLE);
        recyclerViewFilmInComune.setVisibility(View.VISIBLE);
    }

    public void nascondiFilmInComune(){
        textView_FilmInComune.setVisibility(View.INVISIBLE);
        recyclerViewFilmInComune.setVisibility(View.INVISIBLE);
    }

    public void setSchermataWithState(int state){
        switch (state){
            case 0: // amico
                imageView_opzioniUtente2.setImageResource(R.drawable.ic_baseline_person_off_24);
                imageView_opzioniUtente2.setOnClickListener(listenerRimuoviAmicizia);
                break;
            case 1: // non amico
                imageView_opzioniUtente2.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24);
                imageView_opzioniUtente2.setOnClickListener(listenerInviaRichiesta);
                break;
            case 2: // richiede amicizia
                imageView_opzioniUtente1.setVisibility(View.VISIBLE);
                imageView_opzioniUtente1.setImageResource(R.drawable.ic_baseline_check_24);
                imageView_opzioniUtente2.setImageResource(R.drawable.ic_baseline_close_24);

                imageView_opzioniUtente1.setOnClickListener(listenerAccettaAmicizia);
                imageView_opzioniUtente2.setOnClickListener(listenerRifiutaAmicizia);
                break;
            case 3: // amicizia inviata
                imageView_opzioniUtente2.setImageResource(R.drawable.ic_baseline_person_off_24);
                imageView_opzioniUtente2.setOnClickListener(listenerRimuoviRichiesta);
                break;

        }
    }


    public void mostraRotanteListeUtente(){
        if(getContext()!=null) {
            Rotante<ListaPersonalizzata> rotanteListe = new Rotante<>(viewModel.getListeAltroUtente().getValue(), R.id.recyclerViewLista, root, getContext(), Rotante.ROTANTE_ALTRO_UTENTE);
            rotanteListe.init();
        }
    }

    public void mostraRotanteFilmInComune() {
        if (getContext() != null) {
            Rotante<Film> rotanteFilm = new Rotante<>(viewModel.getFilmInComune().getValue(), R.id.recyclerViewFilm, root, getContext(), Rotante.ROTANTE_ALTRO_UTENTE);
            rotanteFilm.init();
        }
    }

    public void initButtonOptionListner() {
        listenerRimuoviAmicizia=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_opzioniUtente2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
                new Thread(()->{
                    try {
                        viewModel.rimuoviAmicizia();
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rimossa), Toast.LENGTH_SHORT);
                    } catch (JSONException | TimeoutException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();
            }
        };
        listenerInviaRichiesta=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_opzioniUtente2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
                new Thread(()->{
                    try {
                        viewModel.inviaRichiestaAmicizia();
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_inviata), Toast.LENGTH_SHORT);
                    } catch (JSONException | TimeoutException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();
            }
        };
        listenerRifiutaAmicizia=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_opzioniUtente2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
                imageView_opzioniUtente1.setVisibility(View.INVISIBLE);
                new Thread(()->{
                    try {
                        viewModel.rifiutaRichiestaAmicizia();
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rifiutata), Toast.LENGTH_SHORT);
                    } catch (JSONException | TimeoutException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();
            }
        };
        listenerAccettaAmicizia=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_opzioniUtente1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
                imageView_opzioniUtente1.setVisibility(View.INVISIBLE);

                new Thread(()->{
                    try {
                        viewModel.accettaRichiestaAmicizia();
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_accettata), Toast.LENGTH_SHORT);
                    } catch (JSONException | TimeoutException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();


            }
        };
        listenerRimuoviRichiesta=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_opzioniUtente2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
                new Thread(()->{
                    try {
                        viewModel.rimuoviRichiestaAmiciziaInviata();
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_richiesta_amicizia_rimossa),Toast.LENGTH_SHORT);
                    } catch (JSONException | TimeoutException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();
            }
        };
    }




}