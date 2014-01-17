package com.rabbit.magazine.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.util.ImageUtil;

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
        // TODO Auto-generated method stub  
        return 0;  
    }  

    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        //定义一个ImageView,显示在GridView里  
        ImageView imageView;
        TextView textView;
        if(convertView==null){
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bookshef, null);
            imageView=(ImageView) convertView.findViewById(R.id.item_imageView);  
            String path=AppConfigUtil.getCoverImgPath(String.valueOf(magList.get(position).getId()));
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            imageView.setImageBitmap(bm);*/
            Bitmap bm=ImageUtil.loadImage(path);
    		imageView.setImageBitmap(bm);
        	textView = (TextView)convertView.findViewById(R.id.item_textView); 
    		textView.setText(magList.get(position).getTitle());
    		/*ImageView readBtn = (ImageView)convertView.findViewById(R.id.reading); 
    		ImageView downBtn = (ImageView)convertView.findViewById(R.id.download); */
    		Button readBtn = (Button)convertView.findViewById(R.id.reading); 
    		Button downBtn = (Button)convertView.findViewById(R.id.download);
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
    		
    		if(status==4){
    			//progressBar.setVisibility(View.GONE);
    			downBtn.setVisibility(View.GONE);
    			readBtn.setVisibility(View.VISIBLE);
    			//previewBtn.setVisibility(View.VISIBLE);
    			//unzipTv.setVisibility(View.GONE);
    		}else{
    			if(fromMagazineActivity){
    				if(status==1){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(false);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("下载...");
    				}else if(status==2){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(true);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("解压...");
    				}else if(status==3){
    					//progressBar.setVisibility(View.VISIBLE);
    					//progressBar.setIndeterminate(true);
    					downBtn.setVisibility(View.GONE);
    					readBtn.setVisibility(View.GONE);
    					//previewBtn.setVisibility(View.GONE);
    					//unzipTv.setVisibility(View.VISIBLE);
    					//unzipTv.setText("生成缩略图...");
    				}else if(status==0){
    					//progressBar.setVisibility(View.GONE);
    					downBtn.setVisibility(View.VISIBLE);
    					readBtn.setVisibility(View.GONE);
    					//previewBtn.setVisibility(View.VISIBLE);
    					//unzipTv.setVisibility(View.GONE);
    				}
    			}else{
    				//progressBar.setVisibility(View.GONE);
    				downBtn.setVisibility(View.VISIBLE);
    				readBtn.setVisibility(View.GONE);
    				//previewBtn.setVisibility(View.VISIBLE);
    				//unzipTv.setVisibility(View.GONE);
    			}
    		}
        }else{  
            imageView = null;  
        }  
        //imageView.setImageResource(mThumbIds[position]);  
        return convertView;  
    }  
      

      
}  