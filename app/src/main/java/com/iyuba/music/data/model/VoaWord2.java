package com.iyuba.music.data.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class VoaWord2 implements Serializable {


	/**
	 * def : v. 与……见面
	 * updateTime : 2020-03-18 14:30:30.0
	 * book_id : 280
	 * version : 0
	 * examples : 1
	 * videoUrl : http://static2.iyuba.cn/video/321/321031/321031_10_1.mp4
	 * pron : mi:t
	 * voa_id : 321031
	 * idindex : 10
	 * audio : http://res.iciba.com/resource/amp3/oxford/0/21/53/2153aadb03f8449fcdab68faa4daf00e.mp3
	 * position : 1
	 * Sentence_cn : 你好，很高兴见到你。
	 * pic_url : 280/1/1.jpg
	 * unit_id : 1
	 * word : meet
	 * Sentence : Hello! Nice to meet you!
	 * Sentence_audio : http://staticvip.iyuba.cn/sounds/voa/sentence/202006/321031/321031_10_1.wav
	 */

	@SerializedName("def")
	public String def;
	@SerializedName("updateTime")
	public String updateTime;
	@SerializedName("book_id")
	public String bookId;
	@SerializedName("version")
	public String version;

	@SerializedName("examples")
	public int examples;
	@SerializedName("videoUrl")
	public String videoUrl;
	@SerializedName("pron")
	public String pron;
	@SerializedName("voa_id")
	public String voaId;

	@SerializedName("idindex")
	public String idindex;
	@SerializedName("audio")
	public String audio;
	@SerializedName("position")
	public int position;
	@SerializedName("Sentence_cn")
	public String SentenceCn;

	@SerializedName("pic_url")
	public String picUrl;
	@SerializedName("unit_id")
	public String unitId;
	@SerializedName("word")
	public String word;
	@SerializedName("Sentence")
	public String Sentence;
	@SerializedName("Sentence_audio")
	public String SentenceAudio;

    public String answer;


    // 17个参数 艹 +1 answer 18个了
}

