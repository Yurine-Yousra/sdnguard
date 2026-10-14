/*
 * Copyright (c) 2026 Yousra BOUHRIZ DAIDJ. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.github.yurine_yousra.sdnguard;

/**
 * Une regle de transfert simple : sur le switch nodeId, les paquets IPv4
 * de srcIp vers dstIp sortent par le port outPort.
 *
 * @param flowId   identifiant unique de la regle
 * @param nodeId   switch sur lequel la regle est installee, par exemple openflow:1
 * @param priority priorite OpenFlow de la regle
 * @param srcIp    adresse IPv4 source, par exemple 10.0.1.1
 * @param dstIp    adresse IPv4 destination, par exemple 10.0.1.2
 * @param outPort  numero du port de sortie, par exemple 2
 */
public record ForwardingRule(
    String flowId,
    String nodeId,
    int priority,
    String srcIp,
    String dstIp,
    String outPort) {
}