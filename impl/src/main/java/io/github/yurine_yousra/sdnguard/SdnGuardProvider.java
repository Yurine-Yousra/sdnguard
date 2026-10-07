/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import org.opendaylight.mdsal.binding.api.DataBroker;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Point d'entree de SDN-Guard dans OpenDaylight.
 * Jalon 1 : verifier que le bundle demarre et recoit le DataBroker.
 */
@Component(service = {}, immediate = true)
public final class SdnGuardProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SdnGuardProvider.class);

    private final DataBroker dataBroker;

    @Activate
    public SdnGuardProvider(@Reference final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        LOG.info("SDN-Guard demarre, DataBroker recu : {}", this.dataBroker);
    }

    @Deactivate
    void deactivate() {
        LOG.info("SDN-Guard arrete");
    }
}