package com.example.appandroid.ui.info;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amplifyframework.api.ApiException;
import com.amplifyframework.api.aws.ApiAuthProviders;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.example.appandroid.R;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InfoFragment extends Fragment {

	private com.example.appandroid.ui.info.InfoViewModel InfoViewModel;

	@RequiresApi(api = Build.VERSION_CODES.N)
	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		InfoViewModel =  new ViewModelProvider(this).get(com.example.appandroid.ui.info.InfoViewModel.class);

		View root = inflater.inflate(R.layout.fragment_info, container, false);

		final TextView textView = root.findViewById(R.id.text_slideshow);

		InfoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
			@Override
			public void onChanged(@Nullable String s) {
				textView.setText(s);
			}
		});

		return root;
	}
}