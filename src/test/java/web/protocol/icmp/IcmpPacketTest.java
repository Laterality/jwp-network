package web.protocol.icmp;

import org.junit.jupiter.api.Test;
import web.protocol.SimplePacket;
import web.protocol.ethernet.EthernetPacket;
import web.protocol.ethernet.Type;
import web.protocol.helper.PacketTestHelper;
import web.protocol.icmp.IcmpPacket.IcmpHeader;
import web.protocol.ip.Flag;
import web.protocol.ip.IpPacket;
import web.protocol.ip.IpPacket.IpHeader;
import web.protocol.ip.ProtocolIdentifier;
import web.protocol.ip.Version;
import web.tool.packet.PacketNativeException;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static web.protocol.ethernet.EthernetPacketTest.createEthernetHeader;

public class IcmpPacketTest extends PacketTestHelper {

    @Test
    void constructor() {
        IcmpHeader icmpHeader = createIcmpHeader();
        IcmpPacket packet = new IcmpPacket(icmpHeader, new SimplePacket());

        assertThat(packet).isNotNull();
    }

    @Test
    void ping() throws PacketNativeException, UnknownHostException {
        IcmpPacket icmpPacket = new IcmpPacket(createIcmpHeader(), new SimplePacket());
        IpPacket ipPacket = new IpPacket(createIpHeader(), icmpPacket);
        EthernetPacket expected = new EthernetPacket(createEthernetHeader(Type.IPV4), ipPacket);

        handler.sendPacket(expected);

        save(PCAP_FILE, handler, expected);
    }

    private IcmpHeader createIcmpHeader() {
        return IcmpPacket.IcmpHeader.builder()
            //TODO: ICMP Header를 구성한다.
            .type(IcmpPacket.IcmpType.ECHO_REQUEST)
            .code(IcmpPacket.IcmpCode.NETWORK_UNREACHABLE)
            .sequenceNumber((short) 22)
            .identification((short) 1)
            .checksum((short) 32)
            .build();
    }

    public static IpHeader createIpHeader() throws UnknownHostException {
        return IpHeader.builder()
            .tos(() -> (byte) 0)
            .srcAddr((Inet4Address) Inet4Address.getByName("192.168.7.163"))
            .dstAddr((Inet4Address) Inet4Address.getByName("192.168.7.254"))
            .flag(Flag.DONT_FRAGMENT)
            .fragmentOffset((short) 0)
            .headerChecksum((short) 0)
            .identification((short) 3)
            .ihl((byte) IpHeader.MIN_IPV4_HEADER_SIZE)
            .ttl((byte) 255)
            .version(Version.IPV4)
            .protocolIdentifier(ProtocolIdentifier.ICMP_V4)
            .build();
    }
}