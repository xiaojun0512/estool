package com.cetc10.utils.model;

import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
//@Document(indexName = "user", shards = 3, replicas = 5)
public class User {

    private String id;

//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

//    @Field(type = FieldType.Keyword)
    private Integer age;

//    @Field(type = FieldType.Keyword)
    private Date createTime;

    private String address;

    public User() {

    }

    public User(String id, String name, Integer age, Date createTime, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.createTime = createTime;
        this.address = address;
    }
}
