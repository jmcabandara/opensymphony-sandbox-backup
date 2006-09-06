package com.opensymphony.able.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Build {
    private long build;
    private Long id;

    public Build() {
    }

    public Build(long build) {
        this.build = build;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getBuild() {
        return build;
    }

    public void setBuild(long build) {
        this.build = build;
    }

    public void increment() {
        build++;
    }

    public String toString() {
        return String.valueOf(build);
    }
}
