package edu.eci.arsw.blueprints.filter.services;

import edu.eci.arsw.blueprints.filter.types.filterType;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FilterService {
    @Autowired
    @Qualifier("Redundancy")
    filterType filter;



    public void filterBlueprint(Blueprint bp) throws BlueprintNotFoundException {
        filter.filterBlueprint(bp);
    }

    public void filterBlueprints(Set<Blueprint> blueprints) throws BlueprintNotFoundException, BlueprintPersistenceException {
        filter.filterBlueprints(blueprints);

    }
}
