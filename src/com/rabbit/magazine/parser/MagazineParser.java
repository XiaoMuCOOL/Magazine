package com.rabbit.magazine.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;

public class MagazineParser extends DefaultHandler {

	private static final String VISIBLE = "visible";

	private static final String TAG = "tag";

	private static final String LAYER = "layer";

	private static final String ARGUMENT = "argument";

	private static final String ACTION = "action";

	private static final String HOT = "hot";

	private static final String INDEX = "index";

	private static final String ZOOMABLE = "zoomable";

	private static final String SIZE = "size";

	private static final String PAGED = "paged";

	private static final String CONTENT_SIZE = "contentSize";

	private static final String ORIENTATION = "orientation";

	private static final String FRAME = "frame";

	private static final String TITLE = "title";

	private static final String GROUP = "group";

	private static final String NAME = "name";
	
	private static final String EFFECT="effect";

	private static final String IMG = "img";

	private static final String PAGE = "page";

	private static final String ROOT = "root";

	private static final String DESCRIPTION = "description";

	private static final String CATEGORY = "category";

	private static final String PICTURE = "picture";

	private static final Object PICTURESET = "pictureSet";

	private Magazine magazine;
	private Category currCategory;
	private Object currObj;
	private Page currPage;
	private Group currGroup;
	private Picture currPicture;

	private Picture picture;

	private PictureSet pictureSet;

	private String parent;

	private String tagName;

	private Hot hot;

	private Layer layer;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (localName.equals(ROOT)) {
			magazine = new Magazine();
			String description = attributes.getValue(DESCRIPTION);
			magazine.setDescription(description);
		}
		if (localName.equals(CATEGORY)) {
			currCategory = new Category();
			String description = attributes.getValue(DESCRIPTION);
			String image = attributes.getValue(IMG);
			String title = attributes.getValue(TITLE);
			currCategory.setTitle(title);
			currCategory.setDescription(description);
			currCategory.setImage(image);
		}
		if (localName.equals(PAGE)) {
			currPage = new Page();
			String description = attributes.getValue(DESCRIPTION);
			String bgColor = attributes.getValue("bgColor");
			String title = attributes.getValue(TITLE);
			currPage.setBgColor(bgColor);
			currPage.setTitle(title);
			currPage.setDescription(description);
		}
		if (localName.equals(GROUP)) {
			currGroup = new Group();
			String frame = attributes.getValue(FRAME);
			String orientation = attributes.getValue(ORIENTATION);
			String contentSize = attributes.getValue(CONTENT_SIZE);
			String paged = attributes.getValue(PAGED);
			currGroup.setFrame(frame);
			currGroup.setContentSize(contentSize);
			currGroup.setPaged(paged);
			currGroup.setOrientation(orientation);
		}
		if (localName.equals(PICTURE)) {
			picture = new Picture();
			String frame = attributes.getValue(FRAME);
			String orientation = attributes.getValue(ORIENTATION);
			String size = attributes.getValue(SIZE);
			String zoomable = attributes.getValue(ZOOMABLE);
			String name = attributes.getValue(NAME);
			picture.setName(name);
			picture.setFrame(frame);
			picture.setOrientation(orientation);
			picture.setSize(size);
			picture.setZoomable(zoomable);
		}
		if (localName.equals(PICTURESET)) {
			pictureSet = new PictureSet();
			String frame = attributes.getValue(FRAME);
			String index = attributes.getValue(INDEX);
			String orientation = attributes.getValue(ORIENTATION);
			pictureSet.setFrame(frame);
			pictureSet.setIndex(index);
			pictureSet.setOrientation(orientation);
		}
		if (localName.equals(HOT)) {
			hot = new Hot();
			String action = attributes.getValue(ACTION);
			String argument = attributes.getValue(ARGUMENT);
			String frame = attributes.getValue(FRAME);
			hot.setFrame(frame);
			hot.setAction(action);
			hot.setArgument(argument);
		}
		if (localName.equals(LAYER)) {
			layer = new Layer();
			String name = attributes.getValue(NAME);
			String frame = attributes.getValue(FRAME);
			String tag = attributes.getValue(TAG);
			String visible = attributes.getValue(VISIBLE);
			String effect=attributes.getValue(EFFECT);
			layer.setFrame(frame);
			layer.setName(name);
			layer.setTag(tag);
			if(effect!=null){
				layer.setEffect(effect);
			}
			layer.setVisible(visible);
		}
		tagName = localName;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (localName.equals(CATEGORY)) {
			this.magazine.getCategorys().add(currCategory);
		}
		if (localName.equals(PAGE)) {
			this.currCategory.getPages().add(currPage);
		}
		if (localName.equals(PICTURE)) {
			this.currGroup.getPictures().add(picture);
		}
		if (localName.equals(GROUP)) {
			if (parent.equals(GROUP)) {
			} else {
				this.currPage.getGroups().add(currGroup);
			}
		}
		if (localName.equals(HOT)) {
			this.currGroup.getHots().add(hot);
		}
		if (localName.equals(LAYER)) {
			this.currGroup.getLayers().add(layer);
		}
	}

	public void setMagazine(Magazine magazine) {
		this.magazine = magazine;
	}

	public Magazine getMagazine() {
		return magazine;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if (tagName.equals(PICTURE)) {
			picture.setResource(value);
		}
		if (tagName.equals(PICTURESET)) {
			String[] split = value.split("/");
			pictureSet.setResources(split);
		}
	}
}
