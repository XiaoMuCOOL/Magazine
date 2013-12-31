package com.rabbit.magazine.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.rabbit.magazine.kernel.Category;

public class CategoryParser extends DefaultHandler {

	private static final String DESCRIPTION = "description";

	private static final String CATEGORY = "category";

	private Category category = new Category();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (localName.equals(CATEGORY)) {
			setCategory(new Category());
			String description = attributes.getValue(DESCRIPTION);
			getCategory().setDescription(description);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (localName.equals(CATEGORY)) {
		}
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}
}
