/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 * @author hcadavid
 */
@Component
@Qualifier("Memory")
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{


    private final int VALUE_PRINTS = 5;

    private final Map<Tuple<String,String>,Blueprint> blueprints=new HashMap<>();

    public InMemoryBlueprintPersistence() {
        //load stub data
        Point[] pts=new Point[]{new Point(140, 140),new Point(115, 115)};
        Blueprint bp=new Blueprint("_authorname_", "_bpname_ ",pts);
        blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        initializePrints();
        
    }

    /**
     * Funcion generada para crear dos blueprints que esten con el mismo autor y una cantidad definida por el
     * usuario donde no se puedan repetir (Segundo ciclo for)
     */
    private void initializePrints() {
        Random random = new Random();
        //Dos prints que tienen que estar con el mismo autor
        for(int i = 0;i<2;i++){
            Blueprint newBp = new Blueprint("Diego Gonzalez","Blueprint"+i);
            blueprints.put(new Tuple<>(newBp.getAuthor(),newBp.getName()),newBp);
        }
        //Da valores aleatorios para cada autor para que nunca se repitan
        for(int i = 0;i<VALUE_PRINTS;i++){
            Blueprint bp = new Blueprint("Author"+random.nextInt(100)+10,"Blueprint"+i);
            blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()),bp);
        }

    }
    
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (this.blueprints.containsKey(new Tuple<>(bp.getAuthor(),bp.getName()))){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
        else{
            this.blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        }        
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        System.out.println(blueprints.size());
        return blueprints.get(new Tuple<>(author, bprintname));
    }

    @Override
    public Set<Blueprint> getBluePrints() throws BlueprintPersistenceException, BlueprintNotFoundException {
        Set<Blueprint> prints = new HashSet<>();
        for(Tuple<String,String> tuple: this.blueprints.keySet()){
                prints.add(blueprints.get(tuple));
        }
        return prints;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        //El hashset ayuda a determinar si un objeto ya esta en la lista o no mediante la matriz. Misma funcion que Set
        Set<Blueprint> prints = new HashSet<>();
        for(Tuple<String,String> tuple: this.blueprints.keySet()){
            if(tuple.o1.equals(author)){
                prints.add(blueprints.get(tuple));
            }
        }
        return prints;
    }
}
