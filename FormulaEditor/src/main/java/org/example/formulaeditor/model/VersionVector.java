package org.example.formulaeditor.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VersionVector {
    private final Map<String, Integer> versions;

    public VersionVector() {
        versions = new HashMap<>();
    }

    public VersionVector(Map<String, Integer> versions) {
        this.versions = new HashMap<>(versions);
    }

    public void increment(String InstanceId) {
        versions.put(InstanceId, getVersion(InstanceId) + 1);
    }

    public int getVersion(String InstanceId) {
        return versions.getOrDefault(InstanceId, 0);
    }

    public Map<String, Integer> getVersions() {
        return versions;
    }

    public Set<String> getInstances() {
        return versions.keySet();
    }

    public boolean isNewerVersion(VersionVector other) {
        //TODO add logic to check if this is a newer version
        /*
        boolean atLeastOneGreater = false;
        Set<String> allInstances = new HashSet<>();
        allInstances.addAll(this.getInstances());
        allInstances.addAll(other.getInstances());

        for (String nodeId : allInstances) {
            int thisVersion = getVersion(nodeId);
            int otherVersion = other.getVersion(nodeId);

            if (thisVersion < otherVersion) {
                return false;
            }
            if (thisVersion > otherVersion) {
                atLeastOneGreater = true;
            }
        }
        return atLeastOneGreater;

         */
        return false;
    }

    public void merge(VersionVector other) {
        for (String InstanceId : other.getInstances()) {
            int otherVersion = other.getVersion(InstanceId);
            int thisVersion = getVersion(InstanceId);
            versions.put(InstanceId, Math.max(thisVersion, otherVersion));
        }
    }

    @Override
    public String toString() {
        return versions.toString();
    }
}
