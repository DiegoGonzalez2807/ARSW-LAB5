/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import java.util.*;

import edu.eci.arsw.blueprints.model.Blueprint;
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
    public ResponseEntity<String> manejadorGetBluePrints(){
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            bps = service.getAllBlueprints();
            //System.out.println("Antes----------------------------------"+bps.toString());
            service.applyFilter(bps);
            //System.out.println("Despues---------------------------------"+bps.toString());
        } catch(BlueprintNotFoundException e){
            e.printStackTrace();
        }catch(BlueprintPersistenceException bpPe){
            bpPe.printStackTrace();
        }
        return new ResponseEntity<String> (bps.toString(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/blueprints/{author}",method = RequestMethod.GET)
    public ResponseEntity<String> manejadorGetBluePrintsByAuthor(@PathVariable String author){
        ResponseEntity<String> mensaje;
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            bps = service.getBlueprintsByAuthor(author);
            //System.out.println("Diego-------------------------"+bps.toString());
            mensaje = new ResponseEntity<String>(bps.toString(),HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<String>("No se encontro el autor",HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException e) {
            mensaje = new ResponseEntity<String>("Algo salio mal", HttpStatus.BAD_REQUEST);
        }
        return mensaje;
    }

    
    
    
    
}

