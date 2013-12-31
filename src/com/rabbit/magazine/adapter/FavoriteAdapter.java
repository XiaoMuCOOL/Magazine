package com.rabbit.magazine.adapter;

import java.util.List;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rabbit.magazine.FavoriteInfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

public class FavoriteAdapter extends BaseAdapter{
	
	private List<FavoriteInfo> mList;
	private BookshelfActivity mActivity;
	
	public FavoriteAdapter(BookshelfActivity activity,List<FavoriteInfo> list){
		this.mList=list;
		this.mActivity=activity;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView==null){
			view=mActivity.getLayoutInflater().inflate(R.layout.item_shuqian, null);
		}else{
			view=convertView;
		}
		FavoriteInfo info=mList.get(position);
		final ImageView imgView=(ImageView) view.findViewById(R.id.img);
		Bitmap bm=ImageUtil.loadImage(info.getImgPath());
		imgView.setImageBitmap(bm);
		imgView.setTag(info);
		int[] imgParams=FrameUtil.autoAdjust(new int[]{110,138}, mActivity);
		imgView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(imgParams[0],imgParams[1]));
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FavoriteInfo info=(FavoriteInfo) v.getTag();
				mActivity.openMagaine(info.getMagId(),info.getTitle(),info.getIndex());
			}
		});
		
		LinearLayout layout=(LinearLayout)view.findViewById(R.id.layout);
		layout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(imgParams[0],android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		
		
		TextView descTv=(TextView)view.findViewById(R.id.desc);
		descTv.setText(info.getTitle()+"("+(info.getIndex()+1)+"/"+info.getPageSize()+")");
		
		Button delBtn=(Button)view.findViewById(R.id.del);
		delBtn.setTag(info);
		delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FavoriteInfo info=(FavoriteInfo) v.getTag();
				MagazineService magService=new MagazineService(mActivity);
				magService.deleteFavorite(info.getId());
				mList.remove(info);
				ImageUtil.recycle(imgView);
				notifyDataSetChanged();
			}
		});
		return view;
	}

	public List<FavoriteInfo> getmList() {
		return mList;
	}

	public void setmList(List<FavoriteInfo> mList) {
		this.mList = mList;
	}
	
}
