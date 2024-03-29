package com.example.sstubot.database.model;

import com.example.sstubot.initial.MetaInfoAboutUserIntoDirection;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table
public class Exam
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    // TODO: 13.05.2023 , unique = true 
    @Column(name = "name")
    protected String name;
    @ManyToOne
    @JoinColumn(name = "meta_id",insertable = false,updatable = false)
    protected MetaInfoAboutUserIntoDirection metaInfo;
    public Exam(){}

    public Exam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Exam exam = (Exam) o;
        return id != null && Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public MetaInfoAboutUserIntoDirection getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(MetaInfoAboutUserIntoDirection metaInfo) {
        this.metaInfo = metaInfo;
    }
}
