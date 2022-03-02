/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import java.util.*;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.impl.InMemoryBlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.impl.Tuple;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

/**
 *
 * @author hcadavid
 */
@Service
@RestController
public class BlueprintAPIController {

    @Autowired
    //@Qualifier("Service")
    BlueprintsServices service;

    @RequestMapping(value = "/blueprints",method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetBluePrints(){
        ResponseEntity<?> mensaje = null;
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            bps = service.getAllBlueprints();
            service.applyFilter(bps);
            mensaje = new ResponseEntity<>(bps,HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<>("No se encontro el autor",HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException e) {
            mensaje = new ResponseEntity<>("Algo salio mal", HttpStatus.BAD_REQUEST);
        }
        return mensaje;
    }

    @RequestMapping(value = "/blueprints/{author}/{bpname}")
    public ResponseEntity<?> namejadorGetBluePrint(@PathVariable String author, @PathVariable String bpname){
        Blueprint bp = null;
        InMemoryBlueprintPersistence imbp = null;
        ResponseEntity<?> mensaje;
        try{
            bp = service.getBlueprint(author,bpname);
            mensaje = new ResponseEntity<>(bp,HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<>("No existe autor o plano con ese nombre.",HttpStatus.NOT_FOUND);
        }
        return mensaje;
    }

    @RequestMapping(value = "/blueprints/{author}",method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetBluePrintsByAuthor(@PathVariable String author){
        ResponseEntity<?> mensaje;
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            bps = service.getBlueprintsByAuthor(author);
            //System.out.println("Diego-------------------------"+bps.toString());
            mensaje = new ResponseEntity<>(bps,HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<>("No se encontro el autor",HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException e) {
            mensaje = new ResponseEntity<>("Algo salio mal", HttpStatus.BAD_REQUEST);
        }
        return mensaje;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> manejadorPostRecursoBluePrint(@RequestBody Blueprint bp){
        ResponseEntity<?> mensaje;
        try {
            //registrar dato
            service.addNewBlueprint(bp);
            mensaje = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException e) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.FATAL, null, e);
            mensaje = new ResponseEntity<>("EL nombre del plano ya existe",HttpStatus.NOT_ACCEPTABLE);
        }
        return mensaje;
    }


    
    
    
    
}

