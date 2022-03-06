package com.example.appandroid.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.appandroid.R;
import com.example.appandroid.listViewClass.utente.Utente;

import com.example.appandroid.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {



	private AppBarConfiguration mAppBarConfiguration;
	private Toolbar toolbar;
	private NavigationView navigationView;
	private NavController navController;
	private BottomNavigationView bottomNavigationView;
	private DrawerLayout drawer;
	private MainActivityViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = new ViewModelProvider(this, new MainActivityViewModelFactory(this.getApplication())).get(MainActivityViewModel.class);
		if(viewModel.getUserLocale() == null){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("notifica", "new");
			startActivity(intent);
			finish();
		}

		setContentView(R.layout.activity_main);

		init();
		viewModel.init();
		String menuFragment = getIntent().getStringExtra("notifica");
		if (menuFragment != null) {
			if (menuFragment.equals("new")) {
				Toast.makeText(this.getApplicationContext(), "NOTIFICA ARRIVATA", Toast.LENGTH_SHORT).show();
				navController.navigate(R.id.action_nav_home_to_nav_notifiche);
			}
		}

	}

	public void init(){
		initViewID();
		setupTopBar();
		setupBottomBar();
	}



	public void setupTopBar(){
		setSupportActionBar(toolbar);
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_home, R.id.nav_profilo, R.id.nav_Info, R.id.nav_logout)
				.setDrawerLayout(drawer)
				.build();

		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);
		initNotificationButton();
		initLogoutButton();
	}

	public void initNotificationButton(){
		AppCompatImageButton image = findViewById(R.id.pulsanteNotifiche);
		image.setOnClickListener(view -> {
			if(navController.getCurrentDestination().getId() != R.id.nav_notifiche) {
				navController.navigate(R.id.nav_notifiche);
			}
		});
	}

	public void initLogoutButton(){
		navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
			viewModel.logout(this);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		});

	}

	public void setupBottomBar(){
		NavigationUI.setupWithNavController(bottomNavigationView, navController);
		navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
			@Override
			public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
				int destinationId = destination.getId();
				if(destinationId == R.id.nav_home || destinationId == R.id.ricercaFragment || destinationId == R.id.amiciFragment ){
					bottomNavigationView.setVisibility(View.VISIBLE);
				}
				else{
					bottomNavigationView.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	public void initViewID(){
		bottomNavigationView = findViewById(R.id.navigation_bar);
		navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		toolbar = findViewById(R.id.toolbar);
		drawer = findViewById(R.id.drawer_layout);
		navigationView = findViewById(R.id.nav_view);
	}

	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}


	@Override
	protected  void onPause() {
		super.onPause();
		System.out.println("Home Messo in pausa");


	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("Home stoppato");

		//unregisterReceiver(br);
	//	Notifiche.cancellaNotifiche(context);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("Home distrutta");

	}





}