package org.workspace7;

import lombok.extern.slf4j.Slf4j;
import org.jolokia.client.J4pClient;

/**
 * @author kameshs
 */
@Slf4j
public class FuseJolokiaUtilMain {
    public static void main(String[] args) {

        FuseJolokiaUtil clientMain = new FuseJolokiaUtil();

        J4pClient j4pClient = J4pClient
                .url("http://localhost:8182/jolokia")
                .user("admin")
                .password("admin")
                .build();

        clientMain.readData(j4pClient,
                "org.apache.camel:type=context,context=camel-fuse-jenkins-demo-1,name=\"camel-jenkins-context\""
                , new String[]{"ExchangesCompleted", "LastProcessingTime"});

        String felixBundleStateMBean = clientMain.search(j4pClient,
                "osgi.core:type=bundleState,version=1.7," +
                        "framework=org.apache.felix.framework,uuid=*").get(0);

        long bundleId = clientMain.searchBundle(j4pClient, felixBundleStateMBean,
                "camel-fuse-jenkins-demo", "1.0.1.SNAPSHOT");

        String felixFrameworkMBean = clientMain.search(j4pClient,
                "osgi.core:type=framework,version=1.7," +
                        "framework=org.apache.felix.framework,uuid=*").get(0);

        String operationName = args[0];

        if (felixFrameworkMBean != null) {
            log.info("Executing Operation on {} ", felixFrameworkMBean);
            clientMain.executeBundleOperation(j4pClient,
                    felixFrameworkMBean,
                    operationName,
                    bundleId);

        }

    }
}
