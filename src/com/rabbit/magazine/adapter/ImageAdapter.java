package com.rabbit.magazine.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.ProgressView;

public class ImageAdapter extends BaseAdapter{  
    private Context mContext;
    private List<Magazineinfo> magList;
    private boolean fromMagazineActivity;
    
    public ImageAdapter(Context context,List<Magazineinfo> magList,boolean fromMagazineActivity) {  
        this.mContext=context; 
        this.magList = magList;
        this.fromMagazineActivity = fromMagazineActivity;
    }  

    @Override  
    public int getCount() {  
        return magList.size();  
    }  

    @Override  
    public Object getItem(int position) {  
        return magList.get(position);  
    }  

    @Override  
    public long getItemId(int position) {  
        return 0;  
    }  

    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        //定义一个ImageView,显示在GridView里  
        ImageView imageView;
        TextView textView;
        if(convertView==null){
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bookshef, null);
        }
            imageView=(ImageView) convertView.findViewById(R.id.item_imageView);  
            String path=AppConfigUtil.getCoverImgPath(String.valueOf(magList.get(position).getId()));
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            imageView.setImageBitmap(bm);*/
            Bitmap bm=ImageUtil.loadImage(path);
    		imageView.setImageBitmap(bm);
        	textView = (TextView)convertView.findViewById(R.id.item_textView); 
    		textView.setText(magList.get(position).getTitle()+" ID:" + magList.get(position).getId() + " \r\n position:" + position);
    		/*ImageView readBtn = (ImageView)convertView.findViewById(R.id.reading); 
    		ImageView downBtn = (ImageView)convertView.findViewById(R.id.download); */
    		Button readBtn = (Button)convertView.findViewById(R.id.reading); 
    		Button downBtn = (Button)convertView.findViewById(R.id.download);
    		ImageView new_del = (ImageView)convertView.findViewById(R.id.new_del);
    		ImageView item_mask = (ImageView)convertView.findViewById(R.id.item_mask);
    		Animation animation = AnimationUtils.loadAnimation((BookshelfActivity)mContext, R.anim.rotate_shake);
    		
    		imageView.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
					BookshelfActivity act = (BookshelfActivity)mContext;
    				int status=magList.get(position).getStatus();
    				if(status==4){
        				act.openMagaine(String.valueOf(magList.get(position).getId()),magList.get(position).getTitle(),0);
    				}else{
						Integer index = position;
						act.setCurrentMag(index);
						RelativeLayout relative = (RelativeLayout)act.findViewById(R.id.curMagLayout);
						relative.setVisibility(View.VISIBLE);
						act.findViewById(R.id.rightDownloadBtn).setVisibility(View.VISIBLE);
						act.findViewById(R.id.rightDownloadBtn).setTag(index);
    				}
					
    			}
    		});
    		readBtn.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				BookshelfActivity act = (BookshelfActivity)mContext;
    				act.openMagaine(String.valueOf(magList.get(position).getId()),magList.get(position).getTitle(),0);
    			}
    		});
    		
    		downBtn.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				BookshelfActivity act = (BookshelfActivity)mContext;
    				if(AppConfigUtil.curDownloader!=null||AppConfigUtil.servicerunning){
    					Toast.makeText(act, "当前有杂志正在下载，请耐心等待...", Toast.LENGTH_SHORT).show();
    				}else{
    					Integer index = position;
    					act.downloadMag(index,mContext);
    				}
    			}
    		});
    		
    		int status=magList.get(position).getStatus();
    		BookshelfActivity act = (BookshelfActivity)mContext;
    		new_del.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				BookshelfActivity act = (BookshelfActivity)mContext;
    				MagazineService magService=new MagazineService(act);
//    				magService.deleteGeren(Integer.parseInt(magList.get(position).getId()));
    				magService.updateMagazineStatus(magList.get(position).getId(),0);
    				//ImageUtil.recycle(img);
    				GridView gridv = (GridView)act.findViewById(R.id.scroll_layout2);
    				LinearLayout itemLayout=(LinearLayout) gridv.getChildAt(position - gridv.getFirstVisiblePosition());
    				final Button readBtn = (Button)itemLayout.findViewById(R.id.reading); 
    	    		final Button downBtn = (Button)itemLayout.findViewById(R.id.download);
    	    		final ImageView delBtn = (ImageView)v;

					final ProgressView progressBar2 = (ProgressView)itemLayout.findViewById(R.id.pro2);


    	    		AlertDialog.Builder tDialog = new AlertDialog.Builder(act);
    				tDialog.setMessage("确定要删除吗？");
    				tDialog.setCancelable(false);
    				tDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {

    	    				downBtn.setVisibility(View.VISIBLE);
    						readBtn.setVisibility(View.GONE);
    						delBtn.setVisibility(View.GONE);
    						progressBar2.setProgress(0);
    						magList.get(position).setStatus(0);
    					}
    				});
    				tDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    					}
    				});
    				tDialog.show();
    				
    			}
    		});
//    		GridView gridv = (GridView)act.findViewById(R.id.scroll_layout2);
//			View itemLayout=gridv.getChildAt(position - gridv.getFirstVisiblePosition());
    		if(status==4&&act.isDel){
    			new_del.setVisibility(View.VISIBLE);
    			item_mask.setVisibility(View.GONE);
    			//if(itemLayout!=null){
    				convertView.startAnimation(animation);
    			//}
    		}else{
    			new_del.setVisibility(View.GONE);
    			item_mask.setVisibility(View.VISIBLE);
    			//if(itemLayout!=null){
    				convertView.clearAnimation();
    			//}
    		}
    		if(status==4){
    			//progressBar.setVisibility(View.GONE);
    			downBtn.setVisibility(View.GONE);
    			readBtn.setVisibility(View.VISIBLE);
    			item_mask.setVisibility(View.GONE);
    			//previewBtn.setVisibility(View.VISIBLE);
    			//unzipTv.setVisibility(View.GONE);
    		}else{

    			if(fromMagazineActivity){
    				if(status==1){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(false);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					item_mask.setVisibility(View.VISIBLE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("下载...");
    				}else if(status==2){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(true);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					item_mask.setVisibility(View.VISIBLE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("解压...");
    				}else if(status==3){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(true);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					item_mask.setVisibility(View.VISIBLE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("生成缩略图...");
    				}else if(status==0){
    					//progressBar.setVisibility(View.GONE);
    					downBtn.setVisibility(View.VISIBLE);
    					readBtn.setVisibility(View.GONE);
    					item_mask.setVisibility(View.VISIBLE);
    					//previewBtn.setVisibility(View.VISIBLE);
    					//unzipTv.setVisibility(View.GONE);
    				}
    			}else{
    				//progressBar.setVisibility(View.GONE);
    				downBtn.setVisibility(View.VISIBLE);
    				readBtn.setVisibility(View.GONE);
    				item_mask.setVisibility(View.VISIBLE);
    				//previewBtn.setVisibility(View.VISIBLE);
    				//unzipTv.setVisibility(View.GONE);
    			}
    		}
//        }else{  
//            imageView=(ImageView) convertView.findViewById(R.id.item_imageView);  
//            String path=AppConfigUtil.getCoverImgPath(String.valueOf(magList.get(position).getId()));
//            /*BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
//            Bitmap bm = BitmapFactory.decodeFile(path, options);
//            imageView.setImageBitmap(bm);*/
//            Bitmap bm=ImageUtil.loadImage(path);
//    		imageView.setImageBitmap(bm);
//        	textView = (TextView)convertView.findViewById(R.id.item_textView); 
//    		textView.setText(magList.get(position).getTitle()+" ID:" + magList.get(position).getId() + " \r\n position:" + position);
//    		/*ImageView readBtn = (ImageView)convertView.findViewById(R.id.reading); 
//    		ImageView downBtn = (ImageView)convertView.findViewById(R.id.download); */
//    		ProgressView progressBar2 = (ProgressView)convertView.findViewById(R.id.pro2);
//    		TextView unzipTv=(TextView)convertView.findViewById(R.id.unzip);
//    		Button readBtn = (Button)convertView.findViewById(R.id.reading); 
//    		Button downBtn = (Button)convertView.findViewById(R.id.download);
//    		ImageView new_del = (ImageView)convertView.findViewById(R.id.new_del);
//    		readBtn.setOnClickListener(new OnClickListener() {
//    			@Override
//    			public void onClick(View v) {
//    				BookshelfActivity act = (BookshelfActivity)mContext;
//    				act.openMagaine(String.valueOf(magList.get(position).getId()),magList.get(position).getTitle(),0);
//    			}
//    		});
//    		
////    		imageView.setOnClickListener(new OnClickListener() {
////    			@Override
////    			public void onClick(View v) {
////    				BookshelfActivity act = (BookshelfActivity)mContext;
////					Integer index = position;
////					act.setCurrentMag(index);
////					RelativeLayout relative = (RelativeLayout)act.findViewById(R.id.curMagLayout);
////					relative.setVisibility(View.VISIBLE);
////					act.findViewById(R.id.rightDownloadBtn).setTag(index);
////					
////    			}
////    		});
//    		
//    		downBtn.setOnClickListener(new OnClickListener() {
//    			@Override
//    			public void onClick(View v) {
//    				BookshelfActivity act = (BookshelfActivity)mContext;
//    				if(AppConfigUtil.curDownloader!=null||AppConfigUtil.servicerunning){
//    					Toast.makeText(act, "当前有杂志正在下载，请耐心等待...", Toast.LENGTH_SHORT).show();
//    				}else{
//    					Integer index = position;
//    					act.downloadMag(index,mContext);
//    				}
//    			}
//    		});
//    		
//    		
//    		int status=magList.get(position).getStatus();
//    		BookshelfActivity act = (BookshelfActivity)mContext;
//
//    		new_del.setOnClickListener(new OnClickListener() {
//    			@Override
//    			public void onClick(View v) {
//    				BookshelfActivity act = (BookshelfActivity)mContext;
//    				MagazineService magService=new MagazineService(act);
////    				magService.deleteGeren(Integer.parseInt(magList.get(position).getId()));
//    				magService.updateMagazineStatus(magList.get(position).getId(),0);
//    				//ImageUtil.recycle(img);
//    				GridView gridv = (GridView)act.findViewById(R.id.scroll_layout2);
//    				LinearLayout itemLayout=(LinearLayout) gridv.getChildAt(position - gridv.getFirstVisiblePosition());
////    				Button readBtn = (Button)itemLayout.findViewById(R.id.reading); 
////    	    		Button downBtn = (Button)itemLayout.findViewById(R.id.download);
////    				downBtn.setVisibility(View.VISIBLE);
////					readBtn.setVisibility(View.GONE);
////					v.setVisibility(View.GONE);
////					ProgressView progressBar2 = (ProgressView)itemLayout.findViewById(R.id.pro2);
////					progressBar2.setProgress(0);
////					magList.get(position).setStatus(0);
//					
//					final Button readBtn = (Button)itemLayout.findViewById(R.id.reading); 
//    	    		final Button downBtn = (Button)itemLayout.findViewById(R.id.download);
//    	    		final ImageView delBtn = (ImageView)v;
//    	    		final ProgressView progressBar2 = (ProgressView)itemLayout.findViewById(R.id.pro2);
//					
//					AlertDialog.Builder tDialog = new AlertDialog.Builder(act);
//    				tDialog.setMessage("确定要删除吗？");
//    				tDialog.setCancelable(false);
//    				tDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//    					@Override
//    					public void onClick(DialogInterface dialog, int which) {
//
//    	    				downBtn.setVisibility(View.VISIBLE);
//    						readBtn.setVisibility(View.GONE);
//    						delBtn.setVisibility(View.GONE);
//    						progressBar2.setProgress(0);
//    						magList.get(position).setStatus(0);
//    					}
//    				});
//    				tDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//    					@Override
//    					public void onClick(DialogInterface dialog, int which) {
//    					}
//    				});
//    				tDialog.show();
//					
//					
//					
//					
//    			}
//    		});
//    		if(status==4&&act.isDel){
//    			new_del.setVisibility(View.VISIBLE);
//    		}else{
//    			new_del.setVisibility(View.GONE);
//    		}
//    		
//    		if(status==4){
//    			//progressBar.setVisibility(View.GONE);
//    			downBtn.setVisibility(View.GONE);
//    			readBtn.setVisibility(View.VISIBLE);
//    			progressBar2.setVisibility(View.GONE);
//    			//previewBtn.setVisibility(View.VISIBLE);
//    			unzipTv.setVisibility(View.GONE);
//    		}else{
//
//
//	    		imageView.setOnClickListener(new OnClickListener() {
//	    			@Override
//	    			public void onClick(View v) {
//	    				BookshelfActivity act = (BookshelfActivity)mContext;
//						Integer index = position;
//						act.setCurrentMag(index);
//						RelativeLayout relative = (RelativeLayout)act.findViewById(R.id.curMagLayout);
//						relative.setVisibility(View.VISIBLE);
//						act.findViewById(R.id.rightDownloadBtn).setTag(index);
//						
//	    			}
//	    		});
//    			if(fromMagazineActivity){
//    				if(status==1){
//    					//progressBar.setVisibility(View.VISIBLE);
//    					//progressBar.setIndeterminate(false);
//    					downBtn.setVisibility(View.GONE);
//    					readBtn.setVisibility(View.GONE);
//    					progressBar2.setVisibility(View.VISIBLE);
//    					//previewBtn.setVisibility(View.GONE);
//    					unzipTv.setVisibility(View.VISIBLE);
//    					unzipTv.setText("下载...");
//    				}else if(status==2){
//    					//progressBar.setVisibility(View.VISIBLE);
//    					//progressBar.setIndeterminate(true);
//    					downBtn.setVisibility(View.GONE);
//    					readBtn.setVisibility(View.GONE);
//    					progressBar2.setVisibility(View.GONE);
//    					new_del.setVisibility(View.GONE);
//    					//previewBtn.setVisibility(View.GONE);
//    					unzipTv.setVisibility(View.VISIBLE);
//    					unzipTv.setText("解压...");
//    				}else if(status==3){
//    					//progressBar.setVisibility(View.VISIBLE);
//    					//progressBar.setIndeterminate(true);
//    					downBtn.setVisibility(View.GONE);
//    					readBtn.setVisibility(View.GONE);
//    					progressBar2.setVisibility(View.GONE);
//    					new_del.setVisibility(View.GONE);
//    					//previewBtn.setVisibility(View.GONE);
//    					unzipTv.setVisibility(View.VISIBLE);
//    					unzipTv.setText("生成缩略图...");
//    				}else if(status==0){
//    					//progressBar.setVisibility(View.GONE);
//    					downBtn.setVisibility(View.VISIBLE);
//    					readBtn.setVisibility(View.GONE);
//    					progressBar2.setVisibility(View.GONE);
//    					new_del.setVisibility(View.GONE);
//    					//previewBtn.setVisibility(View.VISIBLE);
//    					unzipTv.setVisibility(View.GONE);
//    				}
//    			}else{
//    				//progressBar.setVisibility(View.GONE);
//    				downBtn.setVisibility(View.VISIBLE);
//    				readBtn.setVisibility(View.GONE);
//    				progressBar2.setVisibility(View.GONE);
//    				new_del.setVisibility(View.GONE);
//    				//previewBtn.setVisibility(View.VISIBLE);
//    				unzipTv.setVisibility(View.GONE);
//    			}
//    		}
//        }  
        return convertView;
    }  
      
      
}  