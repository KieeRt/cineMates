package com.example.appandroid.aws;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.aws.ApiAuthProviders;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

import java.util.concurrent.CompletableFuture;

public class MyAmplifyApp  extends Application {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCreate() {
        super.onCreate();
        try {



            Amplify.addPlugin(new AWSApiPlugin());

            //Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());

            // per recuperare i token
            ApiAuthProviders authProviders = ApiAuthProviders.builder()
                    .oidcAuthProvider(() -> {
                        CompletableFuture<String> future = new CompletableFuture<>();
                        Amplify.Auth.fetchAuthSession(
                                session -> future.complete(((AWSCognitoAuthSession) session)
                                        .getUserPoolTokens()
                                        .getValue()
                                        .getIdToken()),
                                future::completeExceptionally
                        );
                        try {
                            return future.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .build();

            Amplify.addPlugin(new AWSApiPlugin(authProviders));

            //Storage s3 plugin
            Amplify.addPlugin(new AWSS3StoragePlugin());

            Amplify.configure(getApplicationContext());
            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }
}
