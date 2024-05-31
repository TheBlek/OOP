package ru.nsu.kuklin.parallelprime;

import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public record IPConfig(InetAddress ip, InetAddress broadcast) {
    static IPConfig getFromArgs(String []args) throws SocketException {
        InetAddress ip = null;
        InetAddress localBroadcast = null;
        switch (args.length) {
            case 1:
                try {
                    ip = InetAddress.getByName(args[0]);
                    for (var it = NetworkInterface.getNetworkInterfaces(); it.hasMoreElements(); ) {
                        var netInterface = it.nextElement();
                        for (var address : netInterface.getInterfaceAddresses()) {
                            if (address.getAddress().equals(ip)) {
                                localBroadcast = address.getBroadcast();
                                break;
                            }
                        }
                        if (localBroadcast != null) {
                            break;
                        }
                    }
                    if (localBroadcast == null) {
                        System.out.println("Given address isn't an address on this machine");
                        return null;
                    }
                    return new IPConfig(ip, localBroadcast);
                } catch (UnknownHostException e) {
                    System.out.println("IP address specified in the first parameter is invalid");
                    return null;
                }
            case 0:
                var addresses = new ArrayList<InterfaceAddress>();
                for (var it = NetworkInterface.getNetworkInterfaces(); it.hasMoreElements(); ) {
                    addresses.addAll(it.nextElement().getInterfaceAddresses());
                }
                System.out.println("Choose which network to use for broadcast: ");
                for (int i = 0; i < addresses.size(); i++) {
                    System.out.printf("%d: %s\n", i, addresses.get(i));
                }
                var scanner = new Scanner(System.in);
                int index = scanner.nextInt();
                while (index < 0 || index >= addresses.size()) {
                    index = scanner.nextInt();
                }
                var interfaceAddr = addresses.get(index);
                ip = interfaceAddr.getAddress();
                localBroadcast = interfaceAddr.getBroadcast();
                return new IPConfig(ip, localBroadcast);
            default:
                System.out.println("Too many parameters");
                return null;
        }
    }
}
