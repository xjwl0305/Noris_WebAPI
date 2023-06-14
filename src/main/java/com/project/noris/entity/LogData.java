package com.project.noris.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity(name = "log_data")
@Table(name = "log_data")
public class LogData extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String process_name;

    @Column
    private Date log_time;

    @Column
    private String process_title;

    @Column
    private String status;

    @Column
    private String action;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Users user_id;
}
