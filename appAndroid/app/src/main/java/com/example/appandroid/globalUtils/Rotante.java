package com.example.appandroid.globalUtils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.cards.SliderAdapter;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Rotante <T>{

	private CardSliderLayoutManager layoutManager;
	private RecyclerView recyclerView;
	private int currentPosition;

	private String[] immagini ;
	private String[] titoli ;

	private List<T> elencoDaMostrare ;

	private final int recyclerViewId;
	private SliderAdapter sliderAdapter;
	private final View root ;
	private final Context context;
	private final int MODE ;
	public final static int ROTANTE_UTENTE = 0;
	public final static int ROTANTE_ALTRO_UTENTE = 1;
	//public final static int ROTANTE_UTENTE_NON_AMICO = 2;

	private TextView titolo1TextView;
	private TextView titolo2TextView;
	private int countryOffset1;
	private int countryOffset2;
	private long countryAnimDuration;



	public Rotante(List<T> elencoDaMostrare, int recyclerViewId, View root, Context context, int MODE) {
		this.root = root ;
		this.elencoDaMostrare=elencoDaMostrare;
		this.recyclerViewId = recyclerViewId;
		this.context=context;
		this.MODE=MODE;

	}

	public void init(){
		initImmagini();
		sliderAdapter= new SliderAdapter(immagini, new OnCardClickListener(),context);
		initRecyclerView();

		if(titoli!=null)
			initCountryText();




	}

	public void initImmagini(){
		List<String> immaginiDaMosrare = new ArrayList<>();
		List<String> titoliDaMostrare = new ArrayList<>();

		for(T obj : elencoDaMostrare){
			if(obj instanceof  Film){
				immaginiDaMosrare.add(((Film) obj).getImmagineCopertina());
			}
			else if(obj instanceof  ListaPersonalizzata){

				titoliDaMostrare.add(((ListaPersonalizzata) obj).getNome());
				immaginiDaMosrare.add(((ListaPersonalizzata) obj).getImmagineCopertina());
			}
		}
		immagini=new String[immaginiDaMosrare.size()];
		immagini=immaginiDaMosrare.toArray(immagini);
		if(!titoliDaMostrare.isEmpty()) {
			titoli = new String[titoliDaMostrare.size()];
			titoli = titoliDaMostrare.toArray(titoli);
		}

	}

	private void initRecyclerView() {

		recyclerView = root.findViewById(recyclerViewId);
		recyclerView.setAdapter(sliderAdapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					onActiveCardChange();
				}
			}
		});

		layoutManager = new CardSliderLayoutManager(context){
			@Override
			public Parcelable onSaveInstanceState() {
				return null;
			}
		};

		recyclerView.setLayoutManager(layoutManager);


		try{
			new CardSnapHelper().attachToRecyclerView(recyclerView);
		}catch (IllegalStateException e){

		}
	}



	private void onActiveCardChange() {
		final int pos = layoutManager.getActiveCardPosition();

		if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
			return;
		}


		onActiveCardChange(pos);
	}

	private void onActiveCardChange(int pos) {
		int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
		int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

		final boolean left2right = pos < currentPosition;
		if (left2right) {
			animH[0] = R.anim.slide_in_left;
			animH[1] = R.anim.slide_out_right;

			animV[0] = R.anim.slide_in_bottom;
			animV[1] = R.anim.slide_out_top;
		}
		if(titoli != null && titoli.length>0){
			setCountryText(titoli[pos % titoli.length], left2right);
		}


		currentPosition = pos;
	}

	private class OnCardClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

			if (lm.isSmoothScrolling()) {
				return;
			}

			final int activeCardPosition = lm.getActiveCardPosition();
			if (activeCardPosition == RecyclerView.NO_POSITION) {
				return;
			}

			final int clickedPosition = recyclerView.getChildAdapterPosition(view);
			if (clickedPosition == activeCardPosition) {
				Bundle bundle = new Bundle();
				T obj = elencoDaMostrare.get(clickedPosition);
				if(obj instanceof Film){
					bundle.putString("idFilm",((Film) obj).getIdFilm());
					NavController navController = Navigation.findNavController(view);
					navController.navigate(R.id.moveToSchedaFilm,bundle);

				}
				else if(obj instanceof ListaPersonalizzata){
					bundle.putInt("idLista", ((ListaPersonalizzata) obj).getIdLista());
					bundle.putString("descrizione", ((ListaPersonalizzata) obj).getDescrizione());
					bundle.putString("nome", ((ListaPersonalizzata) obj).getNome());
					bundle.putBoolean("censored",((ListaPersonalizzata) obj).isCensored());
					NavController navController = Navigation.findNavController(view);
					switch (MODE){
						case ROTANTE_UTENTE:
							navController.navigate(R.id.moveToContenutoLista,bundle);
							break;
						case ROTANTE_ALTRO_UTENTE:
							navController.navigate(R.id.action_altroUtente_to_contenutoListaAltroUtente,bundle);
							break;
						default:
							Log.d("ROTANTE ","MODE SCONOSCIUTA....");
					}
				}

			} else if (clickedPosition > activeCardPosition) {

				recyclerView.smoothScrollToPosition(clickedPosition);
				onActiveCardChange(clickedPosition);
			}
		}
	}


	private void initCountryText() {
		countryAnimDuration = 350;
		countryOffset1 = 150;
		countryOffset2 = 230;
		titolo1TextView = root.findViewById(R.id.titolo1);
		titolo2TextView = root.findViewById(R.id.titolo2);

		titolo1TextView.setX(countryOffset1);
		titolo2TextView.setX(countryOffset2);

		CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();


		if(lm != null) {
			currentPosition = lm.getActiveCardPosition();
		}

		T obj = elencoDaMostrare.get(0);

		if(((ListaPersonalizzata) obj).isCensored()) {
			float radius = titolo1TextView.getTextSize() / 3;
			BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
			titolo1TextView.getPaint().setMaskFilter(filter);
		}

		titolo1TextView.setText(titoli[0]);



		titolo2TextView.setAlpha(0f);
		titolo1TextView.setAlpha(0.60F);


	}


	private void setCountryText(String text, boolean left2right) {
		final TextView invisibleText;
		final TextView visibleText;

		if (titolo1TextView.getAlpha() > titolo2TextView.getAlpha()) {
			visibleText = titolo1TextView;
			invisibleText = titolo2TextView;
		} else {
			visibleText = titolo2TextView;
			invisibleText = titolo1TextView;
		}

		final int vOffset;
		if (left2right) {
			invisibleText.setX(0);
			vOffset = countryOffset2;
		} else {
			invisibleText.setX(countryOffset2);
			vOffset = 0;
		}

		for(T obj : elencoDaMostrare){
			if(((ListaPersonalizzata)obj).getNome().equals(text)){
				if(((ListaPersonalizzata) obj).isCensored()){
					float radius = invisibleText.getTextSize() / 3;
					BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
					invisibleText.getPaint().setMaskFilter(filter);
				}
				else{
					invisibleText.getPaint().setMaskFilter(null);
				}
			}
		}

		invisibleText.setText(text);

		final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 0.60F);
		final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
		final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
		final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

		final AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(iAlpha, vAlpha, iX, vX);
		animSet.setDuration(countryAnimDuration);
		animSet.start();
	}

}