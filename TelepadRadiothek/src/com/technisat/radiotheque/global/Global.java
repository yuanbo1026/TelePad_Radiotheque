package com.technisat.radiotheque.global;

import java.util.List;

import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.Genre;

public class Global {

	private static Global instance;

	private int data;
	private List<Genre> gList;
	private List<Country> cList;

	private Global() {
	}

	public void setData(int d) {
		this.data = d;
	}

	public int getData() {
		return this.data;
	}

	public static synchronized Global getInstance() {
		if (instance == null) {
			instance = new Global();
		}
		return instance;
	}

	public List<Genre> getgList() {
		return gList;
	}

	public void setgList(List<Genre> gList) {
		this.gList = gList;
	}

	public List<Country> getcList() {
		return cList;
	}

	public void setcList(List<Country> cList) {
		this.cList = cList;
	}
}
