package com.storm.fliplayout.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
	public List<T> list;
	public Context context;
	private ArrayFilter mFilter;
	private ArrayList<T> mOriginalValues;
	private final Object mLock = new Object();

	public MyBaseAdapter(Context context, List<T> list) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	public Filter getFilter(Handler handler) {
		if (mFilter == null) {
			mFilter = new ArrayFilter(handler);
		}
		return mFilter;
	}
	//listView的数据筛选
	private class ArrayFilter extends Filter {
		private Handler handler;
		
		public ArrayFilter(Handler handler) {
			super();
			this.handler = handler;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<T>(list);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<T> list;
				synchronized (mLock) {
					list = new ArrayList<T>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				ArrayList<T> values;
				synchronized (mLock) {
					values = new ArrayList<T>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<T> newValues = new ArrayList<T>();

				for (int i = 0; i < count; i++) {
					final T value = values.get(i);
					final String valueText = value.toString().toLowerCase();
					//只要包含此字符串就筛选出来
					if (valueText.contains(prefixString)) {
						newValues.add(value);
					} 

					// 只筛选以此字符串开头的
//					if (valueText.startsWith(prefixString)) {
//						newValues.add(value);
//					} else {
//						final String[] words = valueText.split(" ");
//						final int wordCount = words.length;
//
//						// Start at index 0, in case valueText starts with
//						// space(s)
//						for (int k = 0; k < wordCount; k++) {
//							if (words[k].startsWith(prefixString)) {
//								newValues.add(value);
//								break;
//							}
//						}
//					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked
			list = (List<T>) results.values;
			Message message = handler.obtainMessage();
			message.what = 3;
			message.obj  = list;
			handler.sendMessage(message);
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	@Override
	public String toString() {
		return list.size()+"";
	}
	
}