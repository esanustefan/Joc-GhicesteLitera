package org.example;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@javax.persistence.Entity
@Table( name = "configuratie" )
public class Configuratie extends Entity<Long> implements Serializable {

    private Long id;
    @Column( name = "litere" )
    private String litere;
    @Column( name = "punctaj1" )
    private Long punctaj1;
    @Column( name = "punctaj2" )
    private Long punctaj2;
    @Column( name = "punctaj3" )
    private Long punctaj3;
    @Column( name = "punctaj4" )
    private Long punctaj4;

    public Configuratie() {
    }

    public Configuratie(String litere, Long punctaj1, Long punctaj2, Long punctaj3, Long punctaj4) {
        this.litere = litere;
        this.punctaj1 = punctaj1;
        this.punctaj2 = punctaj2;
        this.punctaj3 = punctaj3;
        this.punctaj4 = punctaj4;
    }

    public Long getPunctaj4() {
        return punctaj4;
    }

    public void setPunctaj4(Long punctaj4) {
        this.punctaj4 = punctaj4;
    }

    public String getLitere() {
        return litere;
    }

    public void setLitere(String litere) {
        this.litere = litere;
    }

    public Long getPunctaj1() {
        return punctaj1;
    }

    public void setPunctaj1(Long punctaj1) {
        this.punctaj1 = punctaj1;
    }

    public Long getPunctaj2() {
        return punctaj2;
    }

    public void setPunctaj2(Long punctaj2) {
        this.punctaj2 = punctaj2;
    }

    public Long getPunctaj3() {
        return punctaj3;
    }

    public void setPunctaj3(Long punctaj3) {
        this.punctaj3 = punctaj3;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public void setId(Long aLong) {
        this.id = aLong;
    }
}
