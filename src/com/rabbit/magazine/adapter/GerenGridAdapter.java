package com.rabbit.magazine.adapter;

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rabbit.magazine.GerenInfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

public class GerenGridAdapter extends BaseAdapter{
	
	private List<GerenInfo> mList;
	private BookshelfActivity mActivity;
	public List<GerenInfo> getList(){
		return mList;
	}
	public GerenGridAdapter(BookshelfActivity activity,List<GerenInfo> list){
		this.mList=list;
		this.mActivity=activity;
	}

	@Override
	public int getCount() {
		return this.mList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView!=null){
			view=convertView;
		}else{
			view=mActivity.getLayoutInflater().inflate(R.layout.item_bookshef, null);
		}
		final ImageView img=(ImageView) view.findViewById(R.id.img);
		GerenInfo info=this.mList.get(position);
		String cover=info.getCover();
		Bitmap bm=ImageUtil.loadImage(cover);
		img.setImageBitmap(bm);
		int[] imgParams=FrameUtil.autoAdjust(new int[]{110,138}, mActivity);
		img.setLayoutParams(new android.widget.LinearLayout.LayoutParams(imgParams[0],imgParams[1]));
		
		LinearLayout layout=(LinearLayout)view.findViewById(R.id.layout);
		android.widget.LinearLayout.LayoutParams params=(android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
		int[] layoutFrames=FrameUtil.autoAdjust(new int[]{20,-100,10,-100}, mActivity);
		params.setMargins(layoutFrames[2], 0, 0, 0);
		
		TextView titleTv=(TextView)view.findViewById(R.id.title);
		String title=info.getTitle();
		titleTv.setText(title);
		titleTv.setTextColor(Color.BLACK);
		
		TextView priceTv=(TextView)view.findViewById(R.id.price);
		String price=info.getPrice();
		priceTv.setText(price);
		priceTv.setTextColor(Color.BLACK);
		
		Button readBtn=(Button)view.findViewById(R.id.download);
		readBtn.setVisibility(View.VISIBLE);
		readBtn.setText("阅读");
		readBtn.setTag(info);
		readBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GerenInfo info=(GerenInfo) v.getTag();
				mActivity.openMagaine(info.getMagId(),info.getTitle(),0);
			}
		});
		Button delBtn=(Button)view.findViewById(R.id.preview);
		delBtn.setText("删除");
		
		delBtn.setTag(info);
		delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GerenInfo info=(GerenInfo) v.getTag();
				MagazineService magService=new MagazineService(mActivity);
				magService.deleteGeren(info.getId());
				mList.remove(info);
				ImageUtil.recycle(img);
				notifyDataSetChanged();
			}
		});
		return view;
	}
	
}
