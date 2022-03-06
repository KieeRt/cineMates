package com.example.appandroid.globalUtils.cards;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.utils.DecodeBitmapTask;

public class SliderCard extends RecyclerView.ViewHolder implements DecodeBitmapTask.Listener {

	private static int viewWidth = 0;
	private static int viewHeight = 0;

	private final ImageView imageView;
	private final Context context;
	private DecodeBitmapTask task;


	public SliderCard(View itemView, Context context) {
		super(itemView);
		imageView = itemView.findViewById(R.id.image);
		this.context = context;
	}

	void setContent(String path) {

		if (viewWidth == 0) {
			itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					viewWidth = itemView.getWidth();
					viewHeight = itemView.getHeight();
					loadBitmap(path);
				}
			});
		} else {
			loadBitmap(path);
		}
	}

	void clearContent() {
		if (task != null) {
			task.cancel(true);
		}
	}

	private void loadBitmap(String path) {
		if(path==null || path.equals("null")) {
			path = DecodeBitmapTask.DEFAULT_PATH_NOIMAGE;
		}
			task = new DecodeBitmapTask(path, viewWidth, viewHeight, this,context);
			task.execute();

	}

	@Override
	public void onPostExecuted(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}


}