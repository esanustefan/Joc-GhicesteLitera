package org.example;

import org.example.databases.ClasamentHibernateDB;
import org.example.databases.ConfiguratieHibernateDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ghicestelitere")
public class ClasamentRestController {
    private static final String template = "Hello, %s!";

    @Autowired
    private ClasamentHibernateDB clasamente;

    @Autowired
    private ConfiguratieHibernateDB configuratieDB;

    @RequestMapping(value = "/clasament/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Long id) throws SQLException {
        System.out.println("Get by id " + id);
        Clasament clasament = clasamente.findOne(id);
        if(clasament == null){
            return new ResponseEntity<String>("Clasament not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Clasament>(clasament, HttpStatus.OK);
    }
    @RequestMapping(value = "/clasament/username/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> getClasamenteByUsername(@PathVariable String username) throws SQLException {
        System.out.println("Get by username " + username);
        Iterable<Clasament> clasamenteList = clasamente.findAll();
        List<Clasament> clasamenteListRESULT = new ArrayList<>();
        for(Clasament clasament : clasamenteList){
            if(clasament.getUsername().equals(username)){
                clasamenteListRESULT.add(clasament);
            }
        }
        if(clasamenteListRESULT.isEmpty()){
            return new ResponseEntity<String>("Clasament not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Clasament>>(clasamenteListRESULT, HttpStatus.OK);
    }
    @RequestMapping(value="/configuratie", method = RequestMethod.POST)
    public Configuratie create(@RequestBody Configuratie configuratie) throws FileNotFoundException {
        System.out.println("Adding configuratie...");
        configuratieDB.save(configuratie);
        return configuratie;
    }
}
