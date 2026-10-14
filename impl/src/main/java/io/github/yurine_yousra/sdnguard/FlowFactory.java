/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchBuilder;
import org.opendaylight.yangtools.binding.util.BindingMap;
import org.opendaylight.yangtools.yang.common.Uint16;
import org.opendaylight.yangtools.yang.common.Uint32;
import org.opendaylight.yangtools.yang.common.Uint8;

/**
 * Traduit une ForwardingRule (regle simple de SDN-Guard) en Flow OpenDaylight.
 */
public final class FlowFactory {
    // 0x0800 = 2048 : "ce paquet est de l'IPv4"
    private static final long ETHERTYPE_IPV4 = 0x0800;
    // table 0 du switch
    private static final Uint8 TABLE_ID = Uint8.ZERO;
    // longueur max envoyee (valeur standard)
    private static final Uint16 MAX_LENGTH = Uint16.valueOf(65535);

    private FlowFactory() {
        // classe utilitaire : pas d'objet FlowFactory
    }

    /**
     * Construit le Flow ODL correspondant a la regle.
     *
     * @param rule la regle a traduire
     * @return le Flow OpenDaylight equivalent
     */
    public static Flow toOdlFlow(final ForwardingRule rule) {
        return new FlowBuilder()
            .setId(new FlowId(rule.flowId()))
            .setFlowName(rule.flowId())
            .setTableId(TABLE_ID)
            .setPriority(Uint16.valueOf(rule.priority()))
            .setIdleTimeout(Uint16.ZERO)
            .setHardTimeout(Uint16.ZERO)
            .setMatch(buildMatch(rule))
            .setInstructions(buildOutputInstructions(rule.outPort()))
            .build();
    }

    private static Match buildMatch(final ForwardingRule rule) {
        EthernetType ethernetType = new EthernetTypeBuilder()
            .setType(new EtherType(Uint32.valueOf(ETHERTYPE_IPV4)))
            .build();

        EthernetMatch ethernetMatch = new EthernetMatchBuilder()
            .setEthernetType(ethernetType)
            .build();

        Ipv4Match ipv4Match = new Ipv4MatchBuilder()
            .setIpv4Source(new Ipv4Prefix(rule.srcIp() + "/32"))
            .setIpv4Destination(new Ipv4Prefix(rule.dstIp() + "/32"))
            .build();

        return new MatchBuilder()
            .setEthernetMatch(ethernetMatch)
            .setLayer3Match(ipv4Match)
            .build();
    }

    private static Instructions buildOutputInstructions(final String outPort) {
        OutputAction outputAction = new OutputActionBuilder()
            .setOutputNodeConnector(new Uri(outPort))
            .setMaxLength(MAX_LENGTH)
            .build();

        OutputActionCase outputActionCase = new OutputActionCaseBuilder()
            .setOutputAction(outputAction)
            .build();

        Action action = new ActionBuilder()
            .setOrder(0)
            .setAction(outputActionCase)
            .build();

        ApplyActions applyActions = new ApplyActionsBuilder()
            .setAction(BindingMap.of(action))
            .build();

        ApplyActionsCase applyActionsCase = new ApplyActionsCaseBuilder()
            .setApplyActions(applyActions)
            .build();

        Instruction instruction = new InstructionBuilder()
            .setOrder(0)
            .setInstruction(applyActionsCase)
            .build();

        return new InstructionsBuilder()
            .setInstruction(BindingMap.of(instruction))
            .build();
    }
}