/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.WriteTransaction;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.mdsal.common.api.LogicalDatastoreType;
import org.opendaylight.mdsal.common.api.OnCommitCallback;
import org.opendaylight.mdsal.common.api.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.yang.common.Uint8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Ecrit et supprime des regles de transfert dans le datastore CONFIGURATION.
 * Chaque appel utilise une seule transaction : tout est applique, ou rien.
 */
public final class FlowWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FlowWriter.class);

    private final DataBroker dataBroker;

    /**
     * Cree un FlowWriter.
     *
     * @param dataBroker le DataBroker, donne par le Provider
     */
    public FlowWriter(final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Installe toutes les regles dans une seule transaction.
     *
     * @param rules les regles a installer
     */
    @SuppressWarnings("deprecation")
    public void install(final List<ForwardingRule> rules) {
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        for (ForwardingRule rule : rules) {
            Flow flow = FlowFactory.toOdlFlow(rule);
            tx.mergeParentStructurePut(LogicalDatastoreType.CONFIGURATION, flowPath(rule), flow);
        }
        tx.commit(new OnCommitCallback() {
            @Override
            public void onSuccess(final CommitInfo commitInfo) {
                LOG.info("SDN-Guard WRITER : {} regle(s) installee(s)", rules.size());
            }

            @Override
            public void onFailure(final TransactionCommitFailedException cause) {
                LOG.error("SDN-Guard WRITER : echec de l'installation de {} regle(s)", rules.size(), cause);
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * Supprime toutes les regles dans une seule transaction.
     *
     * @param rules les regles a supprimer
     */
    public void remove(final List<ForwardingRule> rules) {
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        for (ForwardingRule rule : rules) {
            tx.delete(LogicalDatastoreType.CONFIGURATION, flowPath(rule));
        }
        tx.commit(new OnCommitCallback() {
            @Override
            public void onSuccess(final CommitInfo commitInfo) {
                LOG.info("SDN-Guard WRITER : {} regle(s) supprimee(s)", rules.size());
            }

            @Override
            public void onFailure(final TransactionCommitFailedException cause) {
                LOG.error("SDN-Guard WRITER : echec de la suppression de {} regle(s)", rules.size(), cause);
            }
        }, MoreExecutors.directExecutor());
    }

    // L'adresse exacte d'une regle : nodes / node / table 0 / flow.
    //   Nodes.class           -> opendaylight-inventory:nodes
    //   Node.class + cle      -> /node=openflow:1
    //   FlowCapableNode.class -> le prefixe flow-node-inventory: (augmentation)
    //   Table.class + cle     -> /table=0
    //   Flow.class + cle      -> /flow=<flowId>
    private static DataObjectIdentifier<Flow> flowPath(final ForwardingRule rule) {
        return DataObjectIdentifier.builder(Nodes.class)
            .child(Node.class, new NodeKey(new NodeId(rule.nodeId())))
            .augmentation(FlowCapableNode.class)
            .child(Table.class, new TableKey(Uint8.ZERO))
            .child(Flow.class, new FlowKey(new FlowId(rule.flowId())))
            .build();
    }
}