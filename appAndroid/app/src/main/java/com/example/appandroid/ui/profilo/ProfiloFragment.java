package com.example.appandroid.ui.profilo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ProfiloFragment extends Fragment implements DefaultMethodsFragment {

	private View root;
	private ProfiloViewModel profiloViewModel;

	private ImageView immagineProfilo;
	private TextView linkCambioImmagineProfilo;
	private TextView usernameProfilo;
	private TextView emailProfilo;
	private TextView linkCambioPassword;
	private ProgressBar progressBar ;
	private final String flag = "";
	private File mSaveBit;
	private Bitmap bitmap;



	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		profiloViewModel =  new ViewModelProvider(this, new ProfiloViewModelFactory(requireActivity().getApplication())).get(ProfiloViewModel.class);

		root = inflater.inflate(R.layout.fragment_profilo, container, false);


		flowInitFragment();

		personalizzaViewByAccount();


		return root;
	}





	public void showProgresBar(){
		progressBar.setVisibility(View.VISIBLE);
	}

	public void hideProgresBar(){
		progressBar.setVisibility(View.INVISIBLE);
	}

	private void personalizzaViewByAccount(){

		if( Profile.getCurrentProfile() != null || GoogleSignIn.getLastSignedInAccount(getContext()) != null ){
			synchronized (flag){
				linkCambioPassword.setAlpha(0);
				linkCambioPassword.setOnClickListener(null);
			}

		}
	}


	private void tendinaCambioImmagine(View view) {
		Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(gallery, 100);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 100) {
			if(getContext()!=null)
				new Thread(()->{
					profiloViewModel.aggiornaImmagineUtente(data);
					UtilsToast.stampaToast(requireActivity(),"Immagine aggiornata", Toast.LENGTH_SHORT);
				}).start();

		}
	}


	@Override
	public void initViewId() {
		immagineProfilo = root.findViewById(R.id.imageView_immagineProfilo);
		linkCambioImmagineProfilo = root.findViewById(R.id.textView_linkCambioImmagineProfilo);
		usernameProfilo = root.findViewById(R.id.textView_usernameProfilo);
		emailProfilo = root.findViewById(R.id.textView_emailProfilo);
		linkCambioPassword = root.findViewById(R.id.textView_linkCambioPasswordProfilo);
		progressBar = root.findViewById(R.id.progressBar);
	}

	@Override
	public void initViewListener() {
		synchronized (flag){
			if(linkCambioPassword.getAlpha() != 0)
				linkCambioPassword.setOnClickListener((v)-> new DisplayDialog(getContext(),requireActivity()).mostraTendinaCambioPassword());
		}
		linkCambioImmagineProfilo.setOnClickListener(this::tendinaCambioImmagine);
	}

	@Override
	public void initObserver() {
		final Observer<String> observerNomeUtente = new Observer<String>() {
			@Override
			public void onChanged(@Nullable final String nomeAggiornato) {
				// Update the UI, in this case, a TextView.
				Log.d("AGGIORNAMENTO UI","AGGIORNO !! NomeUtente in profilo");
				usernameProfilo.setText(nomeAggiornato);
			}
		};

		final Observer<String> observerEmail = new Observer<String>() {
			@Override
			public void onChanged(@Nullable final String emailAggiornata) {
				// Update the UI, in this case, a TextView.
				Log.d("AGGIORNAMENTO UI","AGGIORNO !! email in profilo");
				emailProfilo.setText(emailAggiornata);
			}
		};

		final Observer<Bitmap> observerImmagineProfilo = new Observer<Bitmap>() {
			@Override
			public void onChanged(@Nullable final Bitmap bitmapImmagineAggiornata) {
				// Update the UI, in this case, a TextView.
				Log.d("AGGIORNAMENTO UI","AGGIORNO !! immagine profilo in profilo");
				if(bitmapImmagineAggiornata!=null)
					immagineProfilo.setImageBitmap(bitmapImmagineAggiornata);
				else
					immagineProfilo.setImageResource(R.drawable.no_preview_image);
			}
		};
		final Observer<Boolean> observerInCaricamento = new Observer<Boolean>() {
			@Override
			public void onChanged(@Nullable final Boolean inCaricamento) {
				// Update the UI, in this case, a TextView.
				Log.d("AGGIORNAMENTO UI","AGGIORNO !! immagine profilo in profilo");
				if(inCaricamento)
					showProgresBar();
				else
					hideProgresBar();
			}
		};




		profiloViewModel.getNomeUtente().observe(getViewLifecycleOwner(), observerNomeUtente);
		profiloViewModel.getEmailUtente().observe(getViewLifecycleOwner(), observerEmail);
		profiloViewModel.getImmagineProfiloUtente().observe(getViewLifecycleOwner(), observerImmagineProfilo);
		profiloViewModel.isInCaricamento().observe(getViewLifecycleOwner(),observerInCaricamento);
	}

	@Override
	public void fetchData() {
		new Thread(()->{
			profiloViewModel.recuperaBitmapImmagineUtente();
		}).start();
	}

	@Override
	public void initEmptyDateVM() {
		profiloViewModel.init();
	}
}