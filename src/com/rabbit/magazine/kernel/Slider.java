package com.rabbit.magazine.kernel;

import java.util.ArrayList;
import java.util.List;

public class Slider  extends BasicView{

	private String selected;
	
	private String unselected;
	
	private List<Picture> pictures=new ArrayList<Picture>();

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getUnselected() {
		return unselected;
	}

	public void setUnselected(String unselected) {
		this.unselected = unselected;
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
	}

	
	
}
