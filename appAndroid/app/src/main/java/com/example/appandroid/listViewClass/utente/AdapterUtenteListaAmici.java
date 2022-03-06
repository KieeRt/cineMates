package com.example.appandroid.listViewClass.utente;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appandroid.R;
import com.example.appandroid.repository.Repository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterUtenteListaAmici extends BaseAdapter implements Filterable {

    private final Context listViewContext;



    private List<Utente> listItemUtenti = new ArrayList<>();
    private List<Utente> lista_di_riferimento;
    private final LayoutInflater inflater;

    private int CURRENT_STATE ;
    public static final int UTENTE_AMICO = 0 ;
    public static final int UTENTE_NONAMICO = 1 ;
    public static final int UTENTE_RICHIEDE_AMICIZIA = 2 ;
    public static final int UTENTE_AMICIZIA_INVIATA = 3 ;

    private final View.OnClickListener listener;
    private Repository repository;


    public AdapterUtenteListaAmici(Context context, List<Utente> listitem, View.OnClickListener listenerOpzioniUtente) {
        super();
        this.listViewContext = context;
        this.lista_di_riferimento = listitem;
        //this.listItemUtenti = listitem;
        listItemUtenti.addAll(lista_di_riferimento);
        this.listener = listenerOpzioniUtente;
        if(context != null)
            inflater = (LayoutInflater) listViewContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        else
            inflater=null;


    }



    @Override
    public int getCount() {
        if(listItemUtenti == null)
            return 0;
        return this.listItemUtenti.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listItemUtenti.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null)
            view = inflater.inflate(R.layout.riga_utente, parent, false);

        TextView nomeProfilo = (TextView) view.findViewById(R.id.riga_nome_profilo);
        ImageView immagineProfilo = (ImageView) view.findViewById(R.id.riga_immagine_profilo);
        ImageView pulsanteFinale = (ImageView) view.findViewById(R.id.riga_bottone_finale);
        ImageView pulsanteFinale2 = (ImageView) view.findViewById(R.id.riga_bottone_finale_2);

        Utente currentUser = listItemUtenti.get(position);

        String currentNome = currentUser.getUsername();
        String currentEmail = currentUser.getEmail();
        Bitmap bitmap = currentUser.getBitmapImmagine();
        if(bitmap == null){
            immagineProfilo.setImageResource(R.drawable.no_preview_image);

           // Picasso.get().load("https://bucketrisorsas3162443-dev.s3.eu-central-1.amazonaws.com/public/No_Preview_image.png").into(immagineProfilo);
        }else{
            immagineProfilo.setImageBitmap(bitmap);
        }

        CURRENT_STATE = currentUser.getCURRENT_STATE();




        nomeProfilo.setText(currentNome);

        //Bisogna settare l'immagine..

        //Bisogna settare i due pulsanti finali...

        switch (CURRENT_STATE){
            case UTENTE_AMICO:
                pulsanteFinale.setVisibility(View.INVISIBLE);
                pulsanteFinale2.setImageResource(R.drawable.ic_baseline_person_off_24);
                pulsanteFinale2.setOnClickListener(listener);
                pulsanteFinale2.setTag(R.string.operazione, "remove_friend");
                pulsanteFinale2.setTag(R.string.posizione,position);
                pulsanteFinale2.setTag(R.string.username_tag, nomeProfilo.getText());
                break;
            case UTENTE_NONAMICO:
                pulsanteFinale.setVisibility(View.INVISIBLE);
                pulsanteFinale2.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24);
                pulsanteFinale2.setOnClickListener(listener);
                pulsanteFinale2.setTag(R.string.operazione, "add_request");
                pulsanteFinale2.setTag(R.string.posizione,position);
                pulsanteFinale2.setTag(R.string.username_tag, nomeProfilo.getText());
                break;
            case UTENTE_RICHIEDE_AMICIZIA:
                pulsanteFinale.setImageResource(R.drawable.ic_baseline_check_24);
                pulsanteFinale.setOnClickListener(listener);
                pulsanteFinale.setTag(R.string.operazione, "accept_request");
                pulsanteFinale.setTag(R.string.posizione,position);
                pulsanteFinale.setTag(R.string.username_tag, nomeProfilo.getText());
                pulsanteFinale2.setImageResource(R.drawable.ic_baseline_close_24);
                pulsanteFinale2.setOnClickListener(listener);
                pulsanteFinale2.setTag(R.string.operazione, "refuse_request");
                pulsanteFinale2.setTag(R.string.posizione,position);
                pulsanteFinale2.setTag(R.string.username_tag, nomeProfilo.getText());

                break;
            case UTENTE_AMICIZIA_INVIATA:
                pulsanteFinale.setVisibility(View.INVISIBLE);
                pulsanteFinale2.setImageResource(R.drawable.ic_baseline_person_off_24);
                pulsanteFinale2.setOnClickListener(listener);
                pulsanteFinale2.setTag(R.string.operazione, "cancel_request");
                pulsanteFinale2.setTag(R.string.posizione,position);
                pulsanteFinale2.setTag(R.string.username_tag, nomeProfilo.getText());

                break;
        }


        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listItemUtenti = (List<Utente>) results.values;
                notifyDataSetChanged();
            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint){

                FilterResults results = new FilterResults();
                List<Utente> lista_filtrata = new ArrayList<>();
                if(constraint.length() > 0 ) {
                    for (Utente u : lista_di_riferimento) {
                        if (u.getUsername().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            lista_filtrata.add(u);
                        }
                    }
                }else {
                    lista_filtrata.addAll(lista_di_riferimento);
                }


                results.count = lista_filtrata.size();
                results.values = lista_filtrata;

                return results;
            }
        };

        return filter;
    }

    public void saveUnfiltredList(){
        listItemUtenti.clear();
        listItemUtenti.addAll(lista_di_riferimento);
    }
}
