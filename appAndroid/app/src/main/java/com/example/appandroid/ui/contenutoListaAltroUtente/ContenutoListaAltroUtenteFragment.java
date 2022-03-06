package com.example.appandroid.ui.contenutoListaAltroUtente;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DisplayDialog;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.AdapterFilm;
import com.example.appandroid.listViewClass.film.Film;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class ContenutoListaAltroUtenteFragment extends Fragment implements DefaultMethodsFragment {

    private View root ;
    private ContenutoListaAltroUtenteViewModel viewModel;

    private int idLista ;
    private String nome ;
    private String descrizione ;

    private boolean censored;

    private TextView titoloLista;
    private TextInputEditText descrizioneLista ;
    private TextInputLayout descrizioneLayout ;

    private ListView listViewFilm ;
    private ImageView pulsanteSegnalazione;
    private AdapterFilm adapterFilm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contenuto_lista_altro_utente, container, false);

        getBundleArgument();


        viewModel = new ViewModelProvider(this, new ContenutoListaAltroUtenteViewModelFactory(idLista)).get(ContenutoListaAltroUtenteViewModel.class);

        flowInitFragment();

        initListInfoOnView();
        initListViewFilm();

        return root ;
    }


    public void getBundleArgument(){
        if (getArguments() != null) {
            idLista = getArguments().getInt("idLista");
            descrizione = getArguments().getString("descrizione");
            nome = getArguments().getString("nome");
            censored = getArguments().getBoolean("censored");

        }
        else{
            idLista = 0 ;
            descrizione = "Nessuna" ;
            nome = "Nessuno" ;
            censored = false ;
        }
    }


    public void initListInfoOnView(){
        if(censored){
            System.out.println("Sono initListViewInfo");
            float radius = titoloLista.getTextSize() / 3;
            BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
            titoloLista.getPaint().setMaskFilter(filter);
            descrizioneLista.getPaint().setMaskFilter(filter);
        }
        descrizioneLista.setText(descrizione);
        titoloLista.setText(nome);
    }

    @Override
    public void initViewId(){
        listViewFilm = root.findViewById(R.id.listViewFilmInListaAltroUtente);
        descrizioneLista = root.findViewById(R.id.descrizioneListaAltroUtente);
        descrizioneLayout = root.findViewById(R.id.textInputLayout_descrizione);
        titoloLista=root.findViewById(R.id.textView_nome_lista);
        pulsanteSegnalazione = root.findViewById(R.id.imageView_segnalazione_lista);
    }

    @Override
    public void initViewListener(){
        pulsanteSegnalazione.setOnClickListener((v)->new DisplayDialog(getContext(),requireActivity()).mostraTendinaSegnalazione(idLista));


        titoloLista.setOnClickListener(v -> {
            if(censored){
                if(titoloLista.getPaint().getMaskFilter()!=null) {
                    titoloLista.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                    titoloLista.getPaint().setMaskFilter(null);
                    descrizioneLista.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                    descrizioneLista.getPaint().setMaskFilter(null);
                    //TRIGGER PER TOGLIERE BLUR
                    descrizioneLista.setText(descrizioneLista.getText());

                }
                else{
                    float radius = titoloLista.getTextSize() / 3;
                    BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                    titoloLista.setLayerType(View.LAYER_TYPE_SOFTWARE,new Paint());
                    titoloLista.getPaint().setMaskFilter(filter);
                    descrizioneLista.setLayerType(View.LAYER_TYPE_SOFTWARE,new Paint());
                    descrizioneLista.getPaint().setMaskFilter(filter);
                    //TRIGGER PER METTERE BLUR
                    descrizioneLista.setText(descrizioneLista.getText());
                }
            }

        });
    }

    @Override
    public void initObserver() {
        final Observer<List<Film>> observer = new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable final List<Film> isCensored) {
                // Update the UI
                Log.d("AGGIORNAMENTO UI","AGGIORNO !! Contenuto Lista altro utente");
                adapterFilm.notifyDataSetChanged();
            }
        };

        viewModel.getListaFilm().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            try {
                viewModel.recuperaContenutoLista();
            } catch (JSONException | TimeoutException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }

    public void initListViewFilm(){
        View.OnClickListener iconListener = generaListenerPerIconaMenu();
        adapterFilm = new AdapterFilm(getContext(),viewModel.getListaFilm().getValue(),AdapterFilm.FILM_IN_LISTA,iconListener);
        listViewFilm.setAdapter(adapterFilm);
        listViewFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Logica dopo selezione di un film dall'elenco
                Bundle bundle = new Bundle();
                bundle.putString("idFilm",viewModel.getListaFilm().getValue().get(position).getIdFilm());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.moveToSchedaFilm,bundle);
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
        return item -> {
            int itemCliccato = item.getItemId();
            int position = (int)viewPremuta.getTag();
            Film filmInteressato = viewModel.getListaFilm().getValue().get(position);
            if(itemCliccato == R.id.item_salva_in_una_lista_ListaAltri){
                showTendinaSalvaFilmInLista(filmInteressato);
            }

            return false;
        };


    }


    public void showTendinaSalvaFilmInLista(Film film) {
        requireActivity().runOnUiThread(() -> new DisplayDialog(getContext(), requireActivity()).mostraTendinaSalvaFilmInLista(film));
    }


}