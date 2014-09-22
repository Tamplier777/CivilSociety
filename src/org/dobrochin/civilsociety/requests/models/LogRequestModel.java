package org.dobrochin.civilsociety.requests.models;

public class LogRequestModel {
	public static final String TASK_NAME = "log";
	private String dts;
	private int uid;
	private int prid;
	private int sid;
	private int snid;
	public LogRequestModel(String dateTime, int userId, int eventId, int essenceId, int essenceName)
	{
		setDts(dateTime);
		setUid(userId);
		setPrid(eventId);
		setSid(essenceId);
		setSnid(essenceName);
	}
	public String getDts() {
		return dts;
	}
	public void setDts(String dts) {
		this.dts = dts;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getPrid() {
		return prid;
	}
	public void setPrid(int prid) {
		this.prid = prid;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public int getSnid() {
		return snid;
	}
	public void setSnid(int snid) {
		this.snid = snid;
	}
}
