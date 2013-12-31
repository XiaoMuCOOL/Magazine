package com.rabbit.magazine.view;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rabbit.magazine.R;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Page;

public class CategoryAdapter extends BaseAdapter {

	private List<Category> categorys;
	private Activity activity;

	public CategoryAdapter(Activity context, List<Category> categorys) {
		super();
		this.categorys = categorys;
		this.activity = context;
	}

	@Override
	public int getCount() {
		return categorys.size();
	}

	@Override
	public Object getItem(int position) {
		return categorys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Category item = (Category) getItem(position);
		final LayoutInflater inflater = activity.getLayoutInflater();
		View inflate = inflater.inflate(R.layout.category, null);
		TextView textview = (TextView) inflate.findViewById(R.id.textView1);
		textview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int height = activity.getWindowManager().getDefaultDisplay().getHeight();
				int width = activity.getWindowManager().getDefaultDisplay().getWidth();
				LinearLayout inflate2 = (LinearLayout) inflater.inflate(R.layout.pageview, null);
				activity.setContentView(inflate2);
				LayoutParams params=new LayoutParams(width, height);
				inflate2.setLayoutParams(params);
				List<Page> pages = item.getPages();
				RelativeLayout layout = (RelativeLayout) inflate2.findViewById(R.string.pageframe);
				// 第一页显示
				PageView pview = new PageView(activity, pages,0);
				pview.showPageContent();
				layout.addView(pview);
			}
		});
		textview.setText(item.getTitle() + "页数" + item.getPages().size());
		return inflate;
	}

}
