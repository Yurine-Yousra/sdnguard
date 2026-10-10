/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import java.util.List;
import org.opendaylight.mdsal.binding.api.DataObjectDeleted;
import org.opendaylight.mdsal.binding.api.DataObjectModification;
import org.opendaylight.mdsal.binding.api.DataObjectWritten;
import org.opendaylight.mdsal.binding.api.DataTreeChangeListener;
import org.opendaylight.mdsal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TopologyListener implements DataTreeChangeListener<Link> {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyListener.class);

    @Override
    public void onDataTreeChanged(final List<DataTreeModification<Link>> changes) {
        LOG.info("SDN-Guard : {} changement(s) de liens recus", changes.size());
        for (DataTreeModification<Link> change : changes) {
            DataObjectModification<Link> modification = change.getRootNode();

            if (modification instanceof DataObjectWritten<Link> written) {
                Link link = written.dataAfter();
                String source = link.getSource().getSourceTp().getValue();
                String destination = link.getDestination().getDestTp().getValue();
                LOG.info("SDN-Guard LINKS : lien AJOUTE {} -> {}", source, destination);

            } else if (modification instanceof DataObjectDeleted<Link> deleted) {
                Link link = deleted.dataBefore();
                String source = link.getSource().getSourceTp().getValue();
                String destination = link.getDestination().getDestTp().getValue();
                LOG.info("SDN-Guard LINKS : lien SUPPRIME {} -> {}", source, destination);
            }
        }
    }

    @Override
    public void onInitialData() {
        LOG.info("SDN-Guard : aucun lien au moment de l'abonnement");
    }
}