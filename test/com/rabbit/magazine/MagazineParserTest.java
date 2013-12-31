package com.rabbit.magazine;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.parser.MagazineParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.util.Log;

public class MagazineParserTest extends AndroidTestCase {

	public void testParser() {
		Context context = getContext();
		File filesDir = context.getFilesDir();
		Log.d("MagazineMainActivity", filesDir.getAbsolutePath());
		AssetManager assets = context.getAssets();
		// Sax方式进行xml解析
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			InputStream stream = assets.open("content.xml");
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			MagazineParser saxHandler = new MagazineParser();
			parser.parse(stream, saxHandler);
			// 获取解析后的列表数据
			Magazine magazine = saxHandler.getMagazine();
			System.out.println(magazine.getTitle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
