package com.oauth.authorization.global.domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@MappedSuperclass
@EqualsAndHashCode
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
}
