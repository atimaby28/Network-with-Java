package ip;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternetProtocol {
    public static void main(String[] args) {
        try {
            InetAddress local = InetAddress.getLocalHost();
            System.out.println("내 컴퓨터 IP 주소 : " + local.getHostAddress());

            InetAddress[] ipArr = InetAddress.getAllByName("atimaby28-portfolio.info");

            for (InetAddress remote : ipArr) {
                System.out.println("내 포트폴리오 IP 주소 : " + remote.getHostAddress());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
