package com.example.appandroid.ui.contenutoLista;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.appandroid.globalUtils.AlertDialogUtils;
import com.example.appandroid.globalUtils.KeyboardUtils;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.AdapterFilm;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.AdapterTendinaSalvaFilmInLista;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ContenutoListaFragment extends Fragment implements DefaultMethodsFragment {

    private View root ;
    private ContenutoListaViewModel viewModel;

    private ListView listViewFilm ;
    private TextInputEditText descrizioneLista ;
    private TextInputLayout layoutDescrizioneLista ;
    private TextView titoloLista ;


    private int idListaDaMostrare ;
    private AdapterFilm adapterFilm ;


    private final int CAMBIO_DESCRIZIONE_IN_CORSO = 0 ;
    private final int CAMBIO_DESCRIZIONE_NON_IN_CORSO = 1 ;
    private int statoModificaDescrizione = CAMBIO_DESCRIZIONE_NON_IN_CORSO;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contenuto_lista_propria, container, false);
        viewModel = new ViewModelProvider(this).get(ContenutoListaViewModel.class);

        getBundleArgument();
        flowInitFragment();
        initListViewFilm();

        return root;
    }

    private void getBundleArgument() {
        if( getArguments() != null ){
            idListaDaMostrare = getArguments().getInt("idLista");
        }
        else{
            idListaDaMostrare=0;
        }
    }


    public void initListInfoOnView(){
        if(viewModel.getCensored().getValue()!=null) {
            aggiornaPerCensura(viewModel.getCensored().getValue());
        }

        descrizioneLista.setText(viewModel.getDescrizione().getValue());
        titoloLista.setText(viewModel.getTitolo().getValue());
    }

    public void initViewId(){
        listViewFilm = root.findViewById(R.id.listViewElencoFilmInLista);
        descrizioneLista = root.findViewById(R.id.editTextDescrizioneLista);
        layoutDescrizioneLista = root.findViewById(R.id.textInputLayoutDescrizioneLista);
        titoloLista=root.findViewById(R.id.textViewTitoloLista);
    }

    public void initViewListener(){
        layoutDescrizioneLista.setEndIconOnClickListener(this::modificaDescrizionePremuta);

        titoloLista.setOnClickListener(v -> {
            if(viewModel.getCensored().getValue()){
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
        final Observer<List<Film>> observerLista = new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable final List<Film> listaAggiornata) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI","AGGIORNO !! Contenuto lista proprio");
                adapterFilm.notifyDataSetChanged();
            }
        };

        final Observer<String> observerTitolo = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String titoloAggiornato) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI","AGGIORNO !! Titolo lista propria");
                titoloLista.setText(titoloAggiornato);
            }
        };

        final Observer<String> observerDescrizione = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String descrizioneAggiornata) {
                // Update the UI
                Log.d("AGGIORNAMENTO UI","AGGIORNO !! Descrizione lista propria");
                descrizioneLista.setText(descrizioneAggiornata);
            }
        };

        final Observer<Boolean> observerCensura = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isCensored) {
                // Update the UI
                Log.d("AGGIORNAMENTO UI","AGGIORNO !! Censurato lista propria");
                aggiornaPerCensura(isCensored);
            }
        };

        viewModel.getListaFilm().observe(getViewLifecycleOwner(), observerLista);
        viewModel.getDescrizione().observe(getViewLifecycleOwner(), observerDescrizione);
        viewModel.getTitolo().observe(getViewLifecycleOwner(), observerTitolo);
        viewModel.getCensored().observe(getViewLifecycleOwner(), observerCensura);
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

        new Thread(()->{
            try {
                viewModel.recuperaInfoLista();
            } catch (JSONException | TimeoutException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init(idListaDaMostrare);
    }


    public void initListViewFilm(){
        View.OnClickListener iconListener = generaListenerPerIconaMenu(viewModel.getListaFilm().getValue());
        adapterFilm = new AdapterFilm(getContext(),viewModel.getListaFilm().getValue(),AdapterFilm.FILM_IN_LISTA,iconListener);
        listViewFilm.setAdapter(adapterFilm);
        listViewFilm.setOnItemClickListener((parent, view, position, id) -> {
            //Logica dopo selezione di un film dall'elenco
            Bundle bundle = new Bundle();
            bundle.putString("idFilm",viewModel.getListaFilm().getValue().get(position).getIdFilm());
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.moveToSchedaFilm,bundle);
        });
    }


    public View.OnClickListener generaListenerPerIconaMenu(List<Film> elencoFilm){
        return v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.inflate(R.menu.menu_opzioni_film_lista_propria);
            popup.show();
            popup.setOnMenuItemClickListener(generaListenerPerPopup(v,elencoFilm));
        };
    }

    public  PopupMenu.OnMenuItemClickListener generaListenerPerPopup(View viewPremuta,List<Film> elencoFilm){
        return item -> {
            int itemCliccato = item.getItemId();
            int position = (int)viewPremuta.getTag();
            Film filmInteressato = elencoFilm.get(position);

            if(itemCliccato == R.id.item_salva_in_una_lista_ListaPropia){
                mostraTendinaSalvaFilmInLista(filmInteressato,viewModel.getListeUtente());
            }

            else if(itemCliccato == R.id.item_Rimuovi_dalla_lista_ListaPropia){
                new Thread(()->{
                    try {
                        viewModel.flowRimozioneFilm(idListaDaMostrare,filmInteressato);
                        UtilsToast.stampaToast(requireActivity(),"Elemento cancellato dalla lista",Toast.LENGTH_SHORT);
                    } catch (TimeoutException | JSONException e) {
                        e.printStackTrace();
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                    }
                }).start();
            }
            return false;
        };

    }


    public void modificaDescrizionePremuta(View v){
        if(statoModificaDescrizione == CAMBIO_DESCRIZIONE_IN_CORSO){
            disableEditText(descrizioneLista);
            KeyboardUtils.hideKeyboard(getActivity());
            layoutDescrizioneLista.setEndIconDrawable(R.drawable.ic_baseline_edit_24);
            statoModificaDescrizione = CAMBIO_DESCRIZIONE_NON_IN_CORSO;


            new Thread(()->{
                try {
                    viewModel.updateDescrizione(descrizioneLista.getText().toString(),idListaDaMostrare);
                    UtilsToast.stampaToast(requireActivity(),"Descrizione aggiornata con successo",Toast.LENGTH_SHORT);
                } catch (TimeoutException | JSONException e) {
                    e.printStackTrace();
                    UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                }
            }).start();

        }
        else{
            enableEditText(descrizioneLista);
            KeyboardUtils.showKeyboard(getActivity(), root);
            layoutDescrizioneLista.setEndIconDrawable(R.drawable.ic_baseline_check_24);
            statoModificaDescrizione = CAMBIO_DESCRIZIONE_IN_CORSO;

        }
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
    }


    public void aggiornaPerCensura(boolean censurato){
        if(censurato){
            float radius = titoloLista.getTextSize() / 3;
            BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
            titoloLista.getPaint().setMaskFilter(filter);
            descrizioneLista.getPaint().setMaskFilter(filter);
        }
        else{
            titoloLista.getPaint().setMaskFilter(null);
            descrizioneLista.getPaint().setMaskFilter(null);
        }
    }


    //QUESTO METODO E' DA ORDINARE
    public void mostraTendinaSalvaFilmInLista(Film film, List<ListaPersonalizzata> elencoListe){
        AlertDialogUtils costruttoreDialog = new AlertDialogUtils(getContext(), R.layout.tendina_aggiungi_film_in_lista);
        View layoutDialog = costruttoreDialog.getLayout();
        List<ListaPersonalizzata> listeChecked = new ArrayList<>();
        List<ListaPersonalizzata> listeUnchecked = new ArrayList<>();


        TextView linkCreaNuovaLista = layoutDialog.findViewById(R.id.linkCreaNuovaListaTendinaSalvaFilmInLista);
        linkCreaNuovaLista.setOnClickListener(this::mostraAlertDialogCreaNuovaLista);

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            int posizioneLista = (int)buttonView.getTag();
            ListaPersonalizzata listaInteressata = elencoListe.get(posizioneLista);
            if(buttonView.isChecked()){
                listeChecked.add(listaInteressata);
                listeUnchecked.remove(listaInteressata);
            }
            else {
                listeChecked.remove(listaInteressata);
                listeUnchecked.add(listaInteressata);
            }
        };

        ListView listViewListe = layoutDialog.findViewById(R.id.ListViewListeTendinaSalvaFilmInLista);
        AdapterTendinaSalvaFilmInLista adapter = new AdapterTendinaSalvaFilmInLista(getContext(),elencoListe, film, listener);
        listViewListe.setAdapter(adapter);

        costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaTendinaSalvaFilmInLista, v1 -> {
            v1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            costruttoreDialog.chiudiAlert();
        });

        costruttoreDialog.initAlertButtonAction(R.id.buttonFattoTendinaSalvaFilmInLista, v2 -> {
            v2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            new Thread(()->{
                try {
                    viewModel.aggiornaListeUtente(film,listeChecked,listeUnchecked);
                    UtilsToast.stampaToast(requireActivity(),"Cambiamenti salvati",Toast.LENGTH_SHORT);
                } catch (TimeoutException | JSONException e) {
                    e.printStackTrace();
                    UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                }
            }).start();

            costruttoreDialog.chiudiAlert();
        });

        costruttoreDialog.mostraAlertDialog();

    }

    public void mostraAlertDialogCreaNuovaLista(View view){
        AlertDialogUtils costruttore = new AlertDialogUtils(getContext(),R.layout.tendina_crea_nuova_lista);

        View layout = costruttore.getLayout();

        TextInputEditText viewTitoloLista = layout.findViewById(R.id.titoloCreazioneLista);
        TextInputEditText viewDescrizioneLista = layout.findViewById(R.id.descirzioneCreazioneLista);
        TextView viewMessaggioErrore = layout.findViewById(R.id.messaggioErrore);


        costruttore.initAlertButtonAction(R.id.buttonAnnullaTendinaCreaNuovaLista,v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            costruttore.chiudiAlert();
        });

        costruttore.initAlertButtonAction(R.id.buttonSalvaTendinaCreaNuovaLista,v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            if(!viewTitoloLista.getText().toString().equals("")){
                String nuovoTitolo = viewTitoloLista.getText().toString();
                String nuovaDescrizione = viewDescrizioneLista.getText().toString();

               new Thread(()->{
                   try {
                       viewModel.creaNuovaLista(nuovoTitolo,nuovaDescrizione);
                       UtilsToast.stampaToast(requireActivity(),"Lista creata con successo",Toast.LENGTH_SHORT);
                   } catch (TimeoutException | JSONException e) {
                       e.printStackTrace();
                       UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                   }
               }).start();

               requireActivity().runOnUiThread(()->Toast.makeText(getContext(),"Lista creata con successo...",Toast.LENGTH_SHORT).show());
               costruttore.chiudiAlert();
            }
            else{
                viewMessaggioErrore.setText("Titolo mancante, riprova...");
                new Thread(()->{
                    requireActivity().runOnUiThread(() -> viewMessaggioErrore.setAlpha(1));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    requireActivity().runOnUiThread(() -> viewMessaggioErrore.setAlpha(0));
                }).start();
            }



        });
        costruttore.mostraAlertDialog();
    }



}