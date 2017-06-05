package io.bisq.network.p2p.peers.peerexchange.messages;

import io.bisq.common.app.Capabilities;
import io.bisq.common.proto.network.NetworkEnvelope;
import io.bisq.generated.protobuffer.PB;
import io.bisq.network.p2p.NodeAddress;
import io.bisq.network.p2p.SendersNodeAddressMessage;
import io.bisq.network.p2p.SupportedCapabilitiesMessage;
import io.bisq.network.p2p.peers.peerexchange.Peer;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Value
public final class GetPeersRequest implements PeerExchangeMessage, SendersNodeAddressMessage, SupportedCapabilitiesMessage {
    private final NodeAddress senderNodeAddress;
    private final int nonce;
    private final HashSet<Peer> reportedPeers;
    private final ArrayList<Integer> supportedCapabilities = Capabilities.getCapabilities();

    public GetPeersRequest(NodeAddress senderNodeAddress, int nonce, HashSet<Peer> reportedPeers) {
        checkNotNull(senderNodeAddress, "senderNodeAddress must not be null at GetPeersRequest");
        this.senderNodeAddress = senderNodeAddress;
        this.nonce = nonce;
        this.reportedPeers = reportedPeers;
    }

    @Override
    public PB.NetworkEnvelope toProtoNetworkEnvelope() {
        return NetworkEnvelope.getDefaultBuilder()
                .setGetPeersRequest(PB.GetPeersRequest.newBuilder()
                        .setSenderNodeAddress(senderNodeAddress.toProtoMessage())
                        .setNonce(nonce)
                        .addAllReportedPeers(reportedPeers.stream()
                                .map(Peer::toProtoMessage)
                                .collect(Collectors.toList()))
                        .addAllSupportedCapabilities(supportedCapabilities))
                .build();
    }

    public static GetPeersRequest fromProto(PB.GetPeersRequest proto) {
        return new GetPeersRequest(NodeAddress.fromProto(proto.getSenderNodeAddress()),
                proto.getNonce(),
                new HashSet<>(proto.getReportedPeersList().stream()
                        .map(Peer::fromProto)
                        .collect(Collectors.toSet())));
    }
}