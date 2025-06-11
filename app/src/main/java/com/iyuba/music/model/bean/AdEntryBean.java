package com.iyuba.music.model.bean;

import com.google.gson.annotations.SerializedName;

public class AdEntryBean {


    @SerializedName("result")
    private String result;
    @SerializedName("data")
    private DataDTO data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("adId")
        private String adId;
        @SerializedName("firstLevel")
        private String firstLevel;
        @SerializedName("secondLevel")
        private String secondLevel;
        @SerializedName("thirdLevel")
        private String thirdLevel;
        @SerializedName("title")
        private String title;
        @SerializedName("id")
        private String id;
        @SerializedName("startuppic_StartDate")
        private String startuppicStartdate;
        @SerializedName("startuppic_EndDate")
        private String startuppicEnddate;
        @SerializedName("startuppic")
        private String startuppic;
        @SerializedName("type")
        private String type;
        @SerializedName("startuppic_Url")
        private String startuppicUrl;
        @SerializedName("classNum")
        private String classNum;




        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getFirstLevel() {
            return firstLevel;
        }

        public void setFirstLevel(String firstLevel) {
            this.firstLevel = firstLevel;
        }

        public String getSecondLevel() {
            return secondLevel;
        }

        public void setSecondLevel(String secondLevel) {
            this.secondLevel = secondLevel;
        }

        public String getThirdLevel() {
            return thirdLevel;
        }

        public void setThirdLevel(String thirdLevel) {
            this.thirdLevel = thirdLevel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStartuppicStartdate() {
            return startuppicStartdate;
        }

        public void setStartuppicStartdate(String startuppicStartdate) {
            this.startuppicStartdate = startuppicStartdate;
        }

        public String getStartuppicEnddate() {
            return startuppicEnddate;
        }

        public void setStartuppicEnddate(String startuppicEnddate) {
            this.startuppicEnddate = startuppicEnddate;
        }

        public String getStartuppic() {
            return startuppic;
        }

        public void setStartuppic(String startuppic) {
            this.startuppic = startuppic;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartuppicUrl() {
            return startuppicUrl;
        }

        public void setStartuppicUrl(String startuppicUrl) {
            this.startuppicUrl = startuppicUrl;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }
    }
}
