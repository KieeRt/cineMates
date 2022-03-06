package com.example.appandroid.aws.s3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.amplifyframework.storage.options.StorageUploadFileOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Guida ufficiale per lo storage
 * https://docs.amplify.aws/lib/storage/getting-started/q/platform/android
* */
public class S3Class {
	private final Context context ;
	private InputStream exampleInputStream;

	public S3Class(Context context){
		this.context=context;
	}


	public void uploadFile(Uri uri, String key) {

		try {

			//create a file to write bitmap data
			//File f = new File(context.getFilesDir(), "file");
			File f = new File(context.getFilesDir() + "/download");

			f.createNewFile();


			// recupera bytes dal immagine
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

			float height = bitmap.getHeight();
			float width = bitmap.getWidth();
			float n_height = 400, n_width = 400;

			if(height > width){
				n_height = (height/width)*400;
			}
			else if(height < width){
				n_width = (width/height)*400;
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, (int)n_width, (int)n_height, false);
			newbitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

			byte[] bitmapdata = bos.toByteArray();


			//write the bytes in file
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();

			Amplify.Storage.uploadFile(
					key,
					f,
					StorageUploadFileOptions.defaultInstance(),
					progress -> Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted()),
					result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
					storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
			);


		} catch (IOException e) {
			e.printStackTrace();
		}



	}


	public void uploadInputStream() {
		// exampleInputStream = context.getContentResolver().openInputStream(uri);

		Amplify.Storage.uploadInputStream(
				"ExampleKey",
				exampleInputStream,
				result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
				storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
		);
	}

	public File getFile(String key,String finalPath){

		File f = new File(context.getFilesDir() + "/" + finalPath);


		System.out.println(context.getApplicationContext().getFilesDir() + "/download");
		AtomicInteger flag = new AtomicInteger();
		System.out.println("SONO DOPO QUALCOSA");

		Amplify.Storage.downloadFile(
				key,
				f,
				StorageDownloadFileOptions.defaultInstance(),
				progress -> Log.i("MyAmplifyApp", "Fraction completed: " + progress.getFractionCompleted()),
				result -> {
					Log.i("MyAmplifyApp", "Download succefful");
					synchronized(flag){
						flag.set(1);
						System.out.println("SONO DENTRO NOTIFY");
						flag.notify();
					}

					},
				error -> {
					Log.e("MyAmplifyApp",  "Download Failure");
					synchronized(flag){
						flag.set(1);
						System.out.println("SONO DENTRO NOTIFY ERROR");
						flag.notify();
					}

				}
		);



		Thread td = new Thread(() -> {
			synchronized(flag){
				while(flag.get() == 0 ){
					try {
						flag.wait();
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Sono sveglio");

			}

		});

		td.start();

		try {
			td.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return f;
	}


}
