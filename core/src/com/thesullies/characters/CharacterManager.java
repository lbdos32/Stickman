package com.thesullies.characters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kosullivan on 18/01/2017.
 */

public class CharacterManager {


    public List<GameObject> entities = new ArrayList<GameObject>(); //extant entities
    public List<GameObject> entitiesToAdd = new ArrayList<GameObject>(); //forthcoming entities
    public List<GameObject> entitiesToRemove = new ArrayList<GameObject>(); //erstwhile entities <-- the important one for you.

    public void remove(GameObject gameObject) {
        if (!this.entitiesToRemove.contains(gameObject))
            this.entitiesToRemove.add(gameObject);
        //or just use a Set<Entity>, as dual additions are implicitly prevented.
    }

    //also add(), and other utility functions for managing entities.

}
