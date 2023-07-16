package org.example;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.*;


@Entity
@Table( name = "clasament" )
public class Clasament extends org.example.Entity<Long>{
    private Long id;
    @Column( name = "username" )
    private String username;
    @Column( name = "start" )
    private String start;
    @Column( name = "punctaj" )
    private Long punctaj;
    @Column( name = "id_joc" )
    private Long id_joc;
    @Column( name = "litere-alese" )
    private String litere_alese;

    public Clasament() {
        // this form used by Hibernate
    }

    public Clasament(String username, String start, Long punctaj, Long id_joc, String litere_alese) {
        this.username = username;
        this.start = start;
        this.punctaj = punctaj;
        this.id_joc = id_joc;
        this.litere_alese = litere_alese;
    }

    public Long getId_joc() {
        return id_joc;
    }

    public void setId_joc(Long id_joc) {
        this.id_joc = id_joc;
    }

    public String getLitere_alese() {
        return litere_alese;
    }

    public void setLitere_alese(String cuvinteghicite) {
        this.litere_alese = cuvinteghicite;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Long getPunctaj() {
        return punctaj;
    }

    public void setPunctaj(Long punctaj) {
        this.punctaj = punctaj;
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
