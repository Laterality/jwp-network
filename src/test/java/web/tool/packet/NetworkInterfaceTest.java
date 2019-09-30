package web.tool.packet;

import org.hyperic.sigar.SigarException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.tool.NetInfo;

class NetworkInterfaceTest {

    @Test
    @DisplayName("자신의 MAC 주소를 확인한다.")
    void checkMacAddress() throws SigarException {
        NetInfo netInfo = new NetInfo();
        System.out.println(netInfo.getMacAddress());
    }
}