package web.tool.packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FindWlanName {

    public static void main(String[] args) throws IOException {
        NetworkInterface nif = NetworkInterfaceService.findByName(findWlanNicName());
        System.out.println(nif.getName());
        System.out.println(nif.getDescription());
        System.out.println(nif.getMacAddresses());
    }

    public static String findWlanNicName() throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec("netsh wlan show interfaces");
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.trim().startsWith("GUID")) {
                break;
            }
        }

        return String.format("\\Device\\NPF_{%s}", line.split(": ", 2)[1].toUpperCase());
    }
}
