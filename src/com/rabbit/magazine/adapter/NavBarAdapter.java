package com.rabbit.magazine.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.parser.MagazineReader;

public class NavBarAdapter extends BaseAdapter {
	
	public Bitmap[] bms;
	public boolean[] b;
	
	private Context context;
	

	private Magazine magazine = null;
	
	public NavBarAdapter(Bitmap[] bms,Context context){
		this.bms=bms;
		this.context=context;
	}

	public NavBarAdapter(boolean[] b,Bitmap[] bms,Context context){
		this.b = b;
		this.bms=bms;
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return bms.length;
	}

	@Override
	public Object getItem(int position) {
		return bms[position];
	}

	public Boolean setMagazine() {
		Boolean result = false; 
		if(magazine == null){
			MagazineReader magareader = new MagazineReader();
			String path = AppConfigUtil.getAppContent(AppConfigUtil.MAGAZINE_ID);
			File content = new File(path);
			if (!content.exists()) {
				return result;
			}
			try {
				InputStream stream = new FileInputStream(content);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setContentHandler(magareader.getRootElement().getContentHandler());
				reader.parse(new InputSource(stream));
				magazine=magareader.getMagazine();
				magazine.rebuild();
			} catch (FileNotFoundException e) {
				return result;
			} catch (ParserConfigurationException e) {
				return result;
			} catch (SAXException e) {
				return result;
			} catch (IOException e) {
				return result;
			}
		}
		return true;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*ImageView img;
		if (convertView == null) {
			img = new ImageView(context);
			img.setLayoutParams(new Gallery.LayoutParams(200,200));
			Bitmap bm=bms[position];
			img.setImageBitmap(bm);
			img.setLayoutParams(new Gallery.LayoutParams(200,200));
			img.setBackgroundResource(R.drawable.imageview_background_unselected);
		}else{
			img=(ImageView)convertView;
		}
		
		return img;*/
		
		
		ImageView img;
        TextView txt;
        if(convertView==null){
        	convertView = LayoutInflater.from(context).inflate(R.layout.item_navbar, null);
    		
        }
        setMagazine();
        img=(ImageView) convertView.findViewById(R.id.item_img); 
        txt=(TextView) convertView.findViewById(R.id.item_txt); 
        //img.setLayoutParams(new Gallery.LayoutParams(200,200));
		Bitmap bm=bms[position];
		img.setImageBitmap(bm);
		//img.setLayoutParams(new Gallery.LayoutParams(200,200));
		if(b[position]){
			img.setBackgroundResource(R.drawable.c_red);
		}
		//txt.setText(position);
		txt.setText(magazine.getCategorys().get(0).getPages().get(position).getTitle());
		return convertView;
	}

}
