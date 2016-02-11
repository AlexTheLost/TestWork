package com.alex.dto;


public class UnprocessedMsg {
    private String index;

    private String source;


    public UnprocessedMsg() {
    }


    public UnprocessedMsg(String index, String source) {
        this.index = index;
        this.source = source;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String index) {
        this.index = index;
    }


    public String getSource() {
        return source;
    }


    public void setSource(String source) {
        this.source = source;
    }
}
