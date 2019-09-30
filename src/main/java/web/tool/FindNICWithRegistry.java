package web.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FindNICWithRegistry {

    public static final String REGISTRY_NETWORK_CARDS_KEY = "\"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\NetworkCards\"";

    public static void main(String[] args) {
        List<NetworkCard> cards = getOutputs("REG QUERY " + REGISTRY_NETWORK_CARDS_KEY).stream()
            .map(s -> String.format("REG QUERY \"%s\" /s", s))
            .map(FindNICWithRegistry::getOutputs)
            .map(NetworkCard::parse)
            .collect(Collectors.toList());
        cards.forEach(System.out::println);
    }

    public static List<NetworkCard> retrieveRegistryNetworkCards() {
        return getOutputs("REG QUERY " + REGISTRY_NETWORK_CARDS_KEY).stream()
            .map(s -> String.format("REG QUERY \"%s\" /s", s))
            .map(FindNICWithRegistry::getOutputs)
            .map(NetworkCard::parse)
            .collect(Collectors.toList());
    }

    private static List<String> getOutputs(String command) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);
            List<String> lines = new ArrayList<>();
            return readLines(proc, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static List<String> readLines(Process proc, List<String> lines) throws IOException {
        try (InputStream in = proc.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            addNotEmptyLine(lines, br);
            return lines;
        }
    }

    private static void addNotEmptyLine(List<String> lines, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
    }

    public static class NetworkCard {
        private final int id;
        private final String serviceName;
        private final String description;

        public NetworkCard(int id, String serviceName, String description) {
            this.id = id;
            this.serviceName = serviceName;
            this.description = description;
        }

        public static NetworkCard parse(List<String> lines) {
            int id = -1;
            String serviceName = null;
            String description = null;
            for (String line : lines) {
                if (line.trim().startsWith("HKEY_LOCAL_MACHINE")) {
                    String[] tokens = line.trim().split("\\\\");
                    id = Integer.parseInt(tokens[tokens.length - 1]);
                }
                if (line.trim().startsWith("ServiceName")) {
                    serviceName = line.trim().split("\\s+", 3)[2];
                }
                if (line.trim().startsWith("Description")) {
                    description = line.trim().split("\\s+", 3)[2];
                }
            }
            return new NetworkCard(id, serviceName, description);
        }

        public int getId() {
            return id;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("NetworkCard { ServiceName: '%s', Description: '%s' }", serviceName, description);
        }
    }
}
