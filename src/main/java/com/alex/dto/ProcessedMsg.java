package com.alex.dto;


public class ProcessedMsg {
    private String category;

    private Object msg;


    public ProcessedMsg() {
    }


    public ProcessedMsg(String category, Object msg) {
        this.category = category;
        this.msg = msg;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public Object getMsg() {
        return msg;
    }


    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
