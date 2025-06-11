package com.iyuba.music.data.model;


import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class ChildWord implements Serializable {

	public ChildWord(String title, int size, int unitNum, List<VoaWord2> list) {
		this.title = title;
		this.size = size;
		this.unitNum = unitNum;
		this.list = list;
	}

	public String title;

     public int size;

     public int unitNum;

     public List<VoaWord2> list;
}

