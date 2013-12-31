package com.rabbit.magazine.kernel;

/**
 * 图形
 * 
 * @author litingwen
 * 
 */
public class Picture extends BasicView {

	private String size;
	
	private String zoomedImage;
	
	private String zoomIcon;

	private String zoomable;
	
	private String closeIcon;

	private String resource;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getZoomable() {
		return zoomable;
	}

	public void setZoomable(String zoomable) {
		this.zoomable = zoomable;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getZoomIcon() {
		return zoomIcon;
	}

	public void setZoomIcon(String zoomIcon) {
		this.zoomIcon = zoomIcon;
	}

	public String getZoomedImage() {
		return zoomedImage;
	}

	public void setZoomedImage(String zoomedImage) {
		this.zoomedImage = zoomedImage;
	}

	public String getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(String closeIcon) {
		this.closeIcon = closeIcon;
	}

}
