package com.rabbit.magazine.kernel;

/**
 * 360度旋转的模型
 * 
 * @author litingwen
 *
 */
public class Rotater extends BasicView{
	
	/**
	 * 资源
	 */
	private String resource="";
	
	/**
	 * 资源分割之后的图片Path数组,在显示View中使用
	 */
	private String[] images;
	
	String BLANK = "\n";
	
	public void setResource(String resource) {
		this.resource = resource;
		images=resource.split(BLANK);
	}

	public String getResource() {
		return resource;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	public String[] getImages() {
		return images;
	}
	

}
