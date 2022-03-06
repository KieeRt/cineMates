package com.example.appandroid.globalUtils.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import com.example.appandroid.R;

import org.jetbrains.annotations.NotNull;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

	private final int count ;
	private final String[] content;
	private final View.OnClickListener listener;
	private final Context context;

	public SliderAdapter(String[] content, View.OnClickListener listener, Context context) {
		this.content = content;
		this.listener = listener;
		this.count=content.length;
		this.context = context;

	}

	@NotNull
	@Override
	public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.layout_slider_card, parent, false);

		if (listener != null) {
			view.setOnClickListener(listener);
		}
		return new SliderCard(view,context);
	}

	@Override
	public void onBindViewHolder(SliderCard holder, int position) {
		holder.setContent(content[position % content.length]);
	}

	@Override
	public void onViewRecycled(SliderCard holder) {
		holder.clearContent();
	}

	@Override
	public int getItemCount() {
		return count;
	}



}
