package com.iyuba.music.entity;

import com.google.gson.annotations.SerializedName;


public class SupportQQ {

    @SerializedName("editor")
    private String editor;
    @SerializedName("technician")
    private String technician;
    @SerializedName("manager")
    private String manager;

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
