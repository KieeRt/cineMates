package com.example.appandroid.globalUtils.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.appandroid.aws.s3.S3Class;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

	private final BackgroundBitmapCache cache;

	private final int reqWidth;
	private final int reqHeight;
	private final String path;
	private final Reference<Listener> refListener;
	private final Context context;
	public static final String DEFAULT_PATH_NOIMAGE = "No_Preview_image.png";

	public interface Listener {
		void onPostExecuted(Bitmap bitmap);
	}

	public DecodeBitmapTask(String path,
							int reqWidth, int reqHeight,
							@NonNull Listener listener, Context context)
	{
		this.cache = BackgroundBitmapCache.getInstance();
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
		this.refListener = new WeakReference<>(listener);
		this.path = path;
		this.context=context;
	}

	@Override
	protected Bitmap doInBackground(Void... voids) {
		Bitmap cachedBitmap = cache.getBitmapFromBgMemCache(path);
		if (cachedBitmap != null) {
			return cachedBitmap;
		}

		if(path.equals(DEFAULT_PATH_NOIMAGE)){
			Bitmap defaultBitmap = loadDefaultBipmap();
			cache.addBitmapToBgMemoryCache(path,defaultBitmap);
			return defaultBitmap ;
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();

		final int width = options.outWidth;
		final int height = options.outHeight;

		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			int halfWidth = width / 2;
			int halfHeight = height / 2;

			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth
					&& !isCancelled() )
			{
				inSampleSize *= 2;
			}
		}

		if (isCancelled()) {
			return null;
		}

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		Bitmap scaledBitmap;
		try {
			Bitmap decodedBitmap = downloadImage(path, options);
			scaledBitmap = Bitmap.createScaledBitmap(decodedBitmap, reqHeight, reqHeight, true);
		}
		catch (RuntimeException e){
			Bitmap defaultBitmap = loadDefaultBipmap();
			cache.addBitmapToBgMemoryCache(path,defaultBitmap);
			return defaultBitmap;
		}

		cache.addBitmapToBgMemoryCache(path, scaledBitmap);
		return scaledBitmap;
	}


	@Override
	final protected void onPostExecute(Bitmap bitmap) {
		final Listener listener = this.refListener.get();
		if (listener != null) {
			listener.onPostExecuted(bitmap);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pixels, int width, int height) {
		final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int sourceWidth = bitmap.getWidth();
		final int sourceHeight = bitmap.getHeight();

		float xScale = (float) width / bitmap.getWidth();
		float yScale = (float) height / bitmap.getHeight();
		float scale = Math.max(xScale, yScale);

		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		float left = (width - scaledWidth) / 2;
		float top = (height - scaledHeight) / 2;

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		final RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, targetRect, paint);

		return output;
	}


	public static  Bitmap downloadImage(String url , BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream stream;

		try {
			stream = getHttpConnection(url);
			bitmap = BitmapFactory.decodeStream(stream, null, options);
			if(stream!=null)
				stream.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("downloadImage"+ e1.toString());
		}
		return bitmap;
	}

	public static InputStream getHttpConnection(String urlString)  throws IOException {

		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("downloadImage" + ex.toString());
		}
		return stream;
	}

	public Bitmap loadDefaultBipmap(){
		S3Class s3Class = new S3Class(context);
		File mSaveBit = s3Class.getFile(DEFAULT_PATH_NOIMAGE,"Download");
		String filePath	 = mSaveBit.getPath();
		return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(filePath), reqHeight, reqHeight, true);
	}

}