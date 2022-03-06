package com.example.appandroid.ui.notifiche;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.notifica.AdapterNotifica;
import com.example.appandroid.listViewClass.notifica.Notifica;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class NotificheFragment extends Fragment implements DefaultMethodsFragment {

	private ListView listViewNotifiche;
	private View root;
	private NotificheViewModel viewModel;
	private AdapterNotifica adapterNotifica ;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	 	root = inflater.inflate(R.layout.fragment_notifiche, container, false);
		viewModel =  new ViewModelProvider(this).get(NotificheViewModel.class);

		flowInitFragment();



	return root;
	}


	@Override
	public void initViewId(){
		listViewNotifiche = root.findViewById(R.id.listViewNotifiche);
	}

	@Override
	public void initViewListener() {

	}

	@Override
	public void initObserver() {

		final Observer<List<Notifica>> observer = new Observer<List<Notifica>>() {
			@Override
			public void onChanged(List<Notifica> films) {
				adapterNotifica.notifyDataSetChanged();
			}
		};


		viewModel.getNotificheUtente().observe(getViewLifecycleOwner(), observer);
		initListView();
	}

	@Override
	public void fetchData() {
		new Thread(()->{
			try {
				viewModel.recuperaNotificheUtente();
			} catch (JSONException | TimeoutException e) {
				e.printStackTrace();
				UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);
			}
		}).start();
	}

	@Override
	public void initEmptyDateVM() {
		viewModel.init();
	}

	public void initListView(){
		adapterNotifica = new AdapterNotifica(getContext(),viewModel.getNotificheUtente().getValue(),null);
		System.out.println("lista notifiche"+ listViewNotifiche);
		listViewNotifiche.setAdapter(adapterNotifica);
	}
}
