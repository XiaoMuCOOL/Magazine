package com.rabbit.magazine.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.util.ImageUtil;

public class ImageAdapter extends BaseAdapter{  
    private Context mContext;
    private List<Magazineinfo> magList;
    
    public ImageAdapter(Context context,List<Magazineinfo> magList) {  
        this.mContext=context; 
        this.magList = magList;
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
    public View getView(int position, View convertView, ViewGroup parent) {  
        //定义一个ImageView,显示在GridView里  
        ImageView imageView;
        TextView textView;
        if(convertView==null){
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bookshef2, null);
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
        }else{  
            imageView = null;  
        }  
        //imageView.setImageResource(mThumbIds[position]);  
        return convertView;  
    }  
      

      
}  