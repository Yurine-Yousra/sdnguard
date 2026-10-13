/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import java.util.List;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.binding.DataObjectReference;
import org.opendaylight.yangtools.concepts.Registration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Point d'entree de SDN-Guard : abonne les listeners de topologie et installe les regles de test.
 */
@Component(service = { }, immediate = true)
public final class SdnGuardProvider {
  private static final Logger LOG = LoggerFactory.getLogger(SdnGuardProvider.class);

  private final DataBroker dataBroker;
  private final Registration linkRegistration;
  private final Registration nodeRegistration;
  private final FlowWriter flowWriter;
  private final List<ForwardingRule> testRules;

  /**
   * Demarre SDN-Guard.
   *
   * @param dataBroker le DataBroker fourni par OSGi
   */
  @Activate
  public SdnGuardProvider(@Reference final DataBroker dataBroker) {
    this.dataBroker = dataBroker;
    LOG.info("SDN-Guard demarre, DataBroker recu : {}", this.dataBroker);

    // Jalon 2 : ecouter les liens et les nodes de la topologie flow:1
    DataObjectReference<Link> linksPath = DataObjectReference.builder(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("flow:1")))
            .child(Link.class)
            .build();
    DataObjectReference<Node> nodesPath = DataObjectReference.builder(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId("flow:1")))
            .child(Node.class)
            .build();

    this.linkRegistration = dataBroker.registerTreeChangeListener(
            LogicalDatastoreType.OPERATIONAL, linksPath, new TopologyListener());
    LOG.info("SDN-Guard : abonne aux liens de la topologie flow:1");

    this.nodeRegistration = dataBroker.registerTreeChangeListener(
            LogicalDatastoreType.OPERATIONAL, nodesPath, new NodesListener());
    LOG.info("SDN-Guard : abonne aux nodes de la topologie flow:1");

    // Jalon 3 : je prepare une liste de regles et je l'envoie a flowWriter.install()
    this.flowWriter = new FlowWriter(dataBroker);
    this.testRules = List.of(
            new ForwardingRule("test-h1-h2", "openflow:1", 200, "10.0.1.1", "10.0.1.2", "2"),
            new ForwardingRule("test-h2-h1", "openflow:1", 200, "10.0.1.2", "10.0.1.1", "1"));
    flowWriter.install(testRules);
  }

  @Deactivate
  void deactivate() {
    flowWriter.remove(testRules);
    linkRegistration.close();
    nodeRegistration.close();
    LOG.info("SDN-Guard arrete");
  }
}