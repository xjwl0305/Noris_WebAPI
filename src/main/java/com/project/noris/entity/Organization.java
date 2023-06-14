package com.project.noris.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Setter
//@Getter
//@Entity(name = "organization")
//@Table(name = "organization")
//public class Organization extends BaseTime {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String ancestor;
//
//    private String descendant;
//
//    @Column
//    private int depth;
//
//    @ManyToOne
//    @JoinColumn(name="company_id")
//    private Company company_id;
//}

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "organization")
public class Organization extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    private String name;

    private int listOrder;

    @OneToMany(mappedBy = "parent")
    private List<Organization> children = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company_id;

    @OneToOne
    @JoinColumn(name="department_id")
    private Department department_id;
}