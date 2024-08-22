package com.oauth.authorization.global.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oauth.authorization.global.util.References;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EqualsAndHashCode
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @CreatedDate
    @Field(type = FieldType.Date,  format = {}, pattern = References.TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = References.TIME_FORMAT)
    private LocalDateTime insertTime;

    @LastModifiedDate
    @Field(type = FieldType.Date, format = {}, pattern = References.TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = References.TIME_FORMAT)
    private LocalDateTime updateTime;
}
