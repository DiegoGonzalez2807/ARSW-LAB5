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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@Service
@RestController
@RequestMapping(value = "/blueprints")
public class BlueprintAPIController {

    @Autowired
    //@Qualifier("Service")
    BlueprintsServices service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> manejadorGetRecursoXX(){
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

    
    
    
    
}

