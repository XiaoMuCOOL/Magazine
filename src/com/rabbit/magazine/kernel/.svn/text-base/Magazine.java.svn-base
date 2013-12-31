package com.rabbit.magazine.kernel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 杂志
 * 
 * @author litingwen
 * 
 */
public class Magazine {
	private String magID;

	private String title;
	private String description;
	private String background;

	private List<Category> categorys = new ArrayList<Category>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<Category> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
	}

	@Override
	public String toString() {
		String cates = categorys.size() + "章";
		for (Iterator iterator = categorys.iterator(); iterator.hasNext();) {
			Category category = (Category) iterator.next();
			cates += category.toString();
		}
		return cates;
	}

	public void rebuild() {
		for (Iterator iterator = categorys.iterator(); iterator.hasNext();) {
			Category category = (Category) iterator.next();
			List<Page> pages = category.getPages();
			for (Iterator iterator2 = pages.iterator(); iterator2.hasNext();) {
				Page page = (Page) iterator2.next();
				List<Picture> pictures = page.getPictures();
				boolean virtual = false;
				Group group;
				group = new Group();
				group.setFrame("0,0,1024,768");
				// 创建虚拟Group节点,并且把page下边的picture直接挂在虚拟Group节点下
				if (pictures != null && pictures.size() > 0) {
					virtual = true;
					group.getPictures().addAll(pictures);
				}
				List<Hot> hots = page.getHots();
				if (hots != null && hots.size() > 0) {
					virtual = true;
					group.getHots().addAll(hots);
				}
				List<Layer> layers=page.getLayers();
				if(layers!=null&&layers.size()!=0){
					virtual = true;
					group.getLayers().addAll(layers);
				}
				List<PictureSet> pictureSets=page.getPictureSets();
				if(pictureSets!=null&&pictureSets.size()!=0){
					virtual = true;
					group.getPictureSets().addAll(pictureSets);
				}
				// 
				List<Video> videos=page.getVideos();
				if(videos!=null&&videos.size()!=0){
					virtual = true;
					group.getVideos().addAll(videos);
				}
				
				List<Slider> sliders=page.getSliders();
				if(sliders!=null&&sliders.size()!=0){
					virtual = true;
					group.getSliders().addAll(sliders);
				}
				
				List<Shutter> shutters=page.getShutters();
				if(shutters!=null&&shutters.size()!=0){
					virtual = true;
					group.getShutters().addAll(shutters);
				}
				
				//
				if (virtual) {
					page.getGroups().add(group);
				}
			}
		}
	}

	public void setMagID(String magID) {
		this.magID = magID;
	}

	public String getMagID() {
		return magID;
	}

}
