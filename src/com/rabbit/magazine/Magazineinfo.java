package com.rabbit.magazine;

import java.util.List;

/**
 * 杂志数据
 * 
 * @author litingwen
 * 
 */
public class Magazineinfo {

	private int dbid;
	private String id;
	private String cover_image;
	private String zip_url;
	private String preview_zip_url;
	private String bytes;
	private String page_number;
	private String title;
	private String description;
	private String iosprice;
	private String product_id;
	private String updatetick;
	private List<String> preview_image;
	private List<String> preview_image_test;
	private String displayorder;
	private int status;//0、未下载；1、正在下载；2、正在解压；3、正在生成缩略图；4、阅读
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCover_image() {
		return cover_image;
	}
	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}
	public String getZip_url() {
		return zip_url;
	}
	public void setZip_url(String zip_url) {
		this.zip_url = zip_url;
	}
	public String getPreview_zip_url() {
		return preview_zip_url;
	}
	public void setPreview_zip_url(String preview_zip_url) {
		this.preview_zip_url = preview_zip_url;
	}
	public String getBytes() {
		return bytes;
	}
	public void setBytes(String bytes) {
		this.bytes = bytes;
	}
	public String getPage_number() {
		return page_number;
	}
	public void setPage_number(String page_number) {
		this.page_number = page_number;
	}
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
	public String getIosprice() {
		return iosprice;
	}
	public void setIosprice(String iosprice) {
		this.iosprice = iosprice;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getUpdatetick() {
		return updatetick;
	}
	public void setUpdatetick(String updatetick) {
		this.updatetick = updatetick;
	}
	public List<String> getPreview_image() {
		return preview_image;
	}
	public void setPreview_image(List<String> preview_image) {
		this.preview_image = preview_image;
	}
	public List<String> getPreview_image_test() {
		return preview_image_test;
	}
	public void setPreview_image_test(List<String> preview_image_test) {
		this.preview_image_test = preview_image_test;
	}
	public String getDisplayorder() {
		return displayorder;
	}
	public void setDisplayorder(String displayorder) {
		this.displayorder = displayorder;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getDbid() {
		return dbid;
	}
	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

}
