package com.absabanking.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Restriction extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Account account;
}