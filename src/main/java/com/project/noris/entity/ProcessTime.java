package com.project.noris.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity(name = "process_time")
@Table(name = "process_time")
public class ProcessTime extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Users user_id;

    @Column
    private double percent;

    @Column
    private String process_name;

    @Column
    private Date start_time;

    @Column
    private Date end_time;
}
