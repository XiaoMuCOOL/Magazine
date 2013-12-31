package com.rabbit.magazine.kernel;

/**
 * 视频播放
 * 
 * @author litingwen
 *
 */
public class Video extends BasicView {

	/**
	 * "TRUE"
	 */
	private String automatic = "TRUE";

	private String fullscreen = "TRUE";

	private String closesOnEnd = "TRUE";
	
	private String resource;
	
	private String preview;

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getPlayIcon() {
		return playIcon;
	}

	public void setPlayIcon(String playIcon) {
		this.playIcon = playIcon;
	}

	private String playIcon;

	public String getAutomatic() {
		return automatic;
	}

	public void setAutomatic(String automatic) {
		this.automatic = automatic;
	}

	public String getFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(String fullscreen) {
		this.fullscreen = fullscreen;
	}

	public String getClosesOnEnd() {
		return closesOnEnd;
	}

	public void setClosesOnEnd(String closesOnEnd) {
		this.closesOnEnd = closesOnEnd;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResource() {
		return resource;
	}

}
