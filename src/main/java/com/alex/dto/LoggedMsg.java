package com.alex.dto;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "es_msg", type = "es_msg", shards = 1, replicas = 0)
public class LoggedMsg {
    @Id
    private String id;

    private String category;

    private String jsonMsg;


    public LoggedMsg() {
    }


    public LoggedMsg(String category, String jsonMsg) {
        this.category = category;
        this.jsonMsg = jsonMsg;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getJsonMsg() {
        return jsonMsg;
    }


    public void setJsonMsg(String jsonMsg) {
        this.jsonMsg = jsonMsg;
    }
}
