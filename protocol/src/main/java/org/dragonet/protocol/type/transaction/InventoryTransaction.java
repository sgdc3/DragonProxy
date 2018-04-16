package org.dragonet.protocol.type.transaction;

import java.util.Set;

import org.dragonet.protocol.type.transaction.action.InventoryAction;

/**
 * @author CreeperFace
 */
public interface InventoryTransaction {

    long getCreationTime();

    Set<InventoryAction> getActions();

    Set<Integer> getInventories();

    void addAction(InventoryAction action);

    boolean canExecute();

    boolean execute();

    boolean hasExecuted();

}
