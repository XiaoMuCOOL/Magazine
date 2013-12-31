package com.rabbit.magazine.kernel;

public class Hot {

	private String action;
	private String argument;
	private String frame;
	private String tag;
	private String picture;
	
	/**
	 * 全屏模式 “TRUE”
	 */
	private String fullscreenMode;
	
	
	private Object actionObject;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public Object getActionObject() {
		return actionObject;
	}

	public void setActionObject(Object actionObject) {
		this.actionObject = actionObject;
	}

	public void setFullscreenMode(String fullscreenMode) {
		this.fullscreenMode = fullscreenMode;
	}

	public String getFullscreenMode() {
		return fullscreenMode;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}
