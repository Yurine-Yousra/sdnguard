/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import java.util.List;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NodesListener implements DataTreeChangeListener<Node> {
    private static final Logger LOG = LoggerFactory.getLogger(NodesListener.class);

    @Override
    public void onDataTreeChanged(final List<DataTreeModification<Node>> changes) {
        LOG.info("SDN-Guard : {} changement(s) de nodes recus", changes.size());
        for (DataTreeModification<Node> change : changes) {
            LOG.info("SDN-Guard NODES : changement recu sur node : {}", change.path());
        }
    }

    @Override
    public void onInitialData() {
        LOG.info("SDN-Guard : aucun node au moment de l'abonnement");
    }
}