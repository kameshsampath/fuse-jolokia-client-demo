package org.workspace7;

import lombok.extern.slf4j.Slf4j;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.*;
import org.json.simple.JSONObject;

import javax.management.MalformedObjectNameException;
import java.util.Collections;
import java.util.List;

/**
 * @author kameshs
 */
@Slf4j
public class FuseJolokiaUtil {


    /**
     * @param j4pClient
     * @param mBeanObjectName
     * @param symbolicName
     * @param bundleVersion
     * @return
     */
    public long searchBundle(J4pClient j4pClient, String mBeanObjectName, String symbolicName, String bundleVersion) {
        try {
            J4pExecRequest j4pExecRequest = new J4pExecRequest(mBeanObjectName, "listBundles()");
            J4pExecResponse j4pExecResponse = j4pClient.execute(j4pExecRequest);
            JSONObject jsonResponse = j4pExecResponse.asJSONObject();

            for (Object key : jsonResponse.keySet()) {
                if ("value".equalsIgnoreCase((String) key)) {
                    Object o = jsonResponse.get(key);
                    if (o instanceof JSONObject) {
                        JSONObject vJson = (JSONObject) o;
                        for (Object valueKey : vJson.keySet()) {
                            JSONObject bundleJson = (JSONObject) vJson.get(valueKey);
                            String symName = (String) bundleJson.get("SymbolicName");
                            String bVersion = (String) bundleJson.get("Version");
                            if (symbolicName.equalsIgnoreCase(symName)
                                    && bundleVersion.equalsIgnoreCase(bVersion)) {
                                Long bundleId = (Long) bundleJson.get("Identifier");
                                log.info("Bundle ID:{}", bundleId);
                                return bundleId;
                            }
                        }
                    }
                }
            }

        } catch (MalformedObjectNameException e) {
            log.error("Error executing Search Operation ", e);
        } catch (J4pException e) {
            log.error("Error executing Search Operation ", e);
        }

        return 0;
    }

    /**
     * @param j4pClient
     * @param query
     * @return
     */
    public List<String> search(J4pClient j4pClient, String query) {
        try {
            J4pSearchRequest j4pSearchRequest = new J4pSearchRequest(query);
            J4pSearchResponse j4pSearchResponse = j4pClient.execute(j4pSearchRequest);
            // assuming only one container runs on the server typically as Kube Pods
            return j4pSearchResponse.getMBeanNames();
        } catch (MalformedObjectNameException e) {
            log.error("Error executing Search Operation ", e);
        } catch (J4pException e) {
            log.error("Error executing Search Operation ", e);
        }
        return Collections.emptyList();
    }

    /**
     * @param j4pClient
     * @param mBeanObjectNAme
     * @param operationName
     * @param bundleId
     */
    public JSONObject executeBundleOperation(J4pClient j4pClient, String mBeanObjectNAme, String operationName, long bundleId) {
        try {
            J4pExecRequest j4pExecRequest = new J4pExecRequest(mBeanObjectNAme, operationName, bundleId);
            J4pExecResponse j4pExecResponse = j4pClient.execute(j4pExecRequest);
            log.info("Response: {}", j4pExecResponse.asJSONObject().toJSONString());
            return j4pExecResponse.asJSONObject();
        } catch (MalformedObjectNameException e) {
            log.error("Error executing Bundle Operation ", e);
        } catch (J4pException e) {
            log.error("Error executing Bundle Operation ", e);
        }
        return null;
    }

    /**
     * @param j4pClient
     * @param mbeanObjectName
     * @param attributes
     * @return
     */
    public JSONObject readData(J4pClient j4pClient, String mbeanObjectName, String... attributes) {
        log.info("Executing read on mBean {} ", mbeanObjectName);
        try {
            J4pReadRequest j4pReadRequest = new J4pReadRequest(mbeanObjectName, attributes);
            J4pReadResponse readResponse = j4pClient.execute(j4pReadRequest);
            return readResponse.asJSONObject();
        } catch (MalformedObjectNameException e) {
            log.error("Error reading data ", e);
        } catch (J4pException e) {
            log.error("Error reading data ", e);
        }
        return null;
    }
}
