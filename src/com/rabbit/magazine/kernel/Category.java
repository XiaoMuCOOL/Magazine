package com.rabbit.magazine.kernel;

import java.util.ArrayList;
import java.util.List;

/**
 * 杂志章节
 * 
 * @author litingwen
 * 
 */
public class Category {

	private String description;
	private String image;
	private String title;

	private List<Page> pages = new ArrayList<Page>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return description + "|" + title + pages.size();
	}

}
