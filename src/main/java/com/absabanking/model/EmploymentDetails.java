package com.absabanking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@JsonIgnoreProperties
public class EmploymentDetails extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private float nettIncome;
    private float grossIncome;
    private String employmentStatus;
    private String employer;
    private String payroll;
    private String payrollNo;
    private String yearEngaged;
    private String position;
    private String occupation;
    private String company;
}
