package com.rabbit.magazine.kernel;

import com.rabbit.magazine.view.PictureSetView;

import android.widget.PopupWindow;


/**
 * 图片集
 * 
 * @author litingwen
 * 
 */
public class PictureSet extends BasicView {

	private String index;

	private String[] resources;
	
	private String envelope;
	
	private PictureSetView pictureSetView;
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public String[] getResources() {
		return resources;
	}

	public PictureSetView getPictureSetView() {
		return pictureSetView;
	}

	public void setPictureSetView(PictureSetView pictureSetView) {
		this.pictureSetView = pictureSetView;
	}

	public String getEnvelope() {
		return envelope;
	}

	public void setEnvelope(String envelope) {
		this.envelope = envelope;
	}

}
