/*
 * Created by Storm Zhang, Apr 2, 2014.
 */

package com.storm.fliplayout.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.storm.fliplayout.R.id;
import com.storm.fliplayout.R.layout;
import com.storm.fliplayout.lib.FlipLayout;

public class FlipAdapter extends BaseAdapter {
	private Context mContext;

	public FlipAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return 30;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, layout.listitem_flip, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.flipLayout.reset();
		return convertView;
	}

	private class Holder {
		public FlipLayout flipLayout;

		public Holder(View view) {
			flipLayout = (FlipLayout) view.findViewById(id.flipLayout);
		}
	}
}
