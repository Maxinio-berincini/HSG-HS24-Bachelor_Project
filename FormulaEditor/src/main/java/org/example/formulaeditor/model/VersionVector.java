package org.example.formulaeditor.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VersionVector {
    private final Map<String, Integer> versions;
    private boolean edited;

    public VersionVector() {
        versions = new HashMap<>();
        edited = false;
    }

    public VersionVector(Map<String, Integer> versions) {
        this.versions = new HashMap<>(versions);
        edited = false;
    }

    public void increment(String instanceId) {
        if (!edited) {
            versions.put(instanceId, getVersion(instanceId) + 1);
            edited = true;
        }
    }

    public int getVersion(String instanceId) {
        return versions.getOrDefault(instanceId, 0);
    }

    public Map<String, Integer> getVersions() {
        return versions;
    }

    public Set<String> getInstances() {
        return versions.keySet();
    }

    public boolean isNewerVersion(VersionVector other) {
        boolean atLeastOneGreater = false;

        Set<String> allInstances = new HashSet<>(this.getInstances());
        allInstances.addAll(other.getInstances());

        for (String instanceId : allInstances) {
            int thisVersion = this.getVersion(instanceId);
            int otherVersion = other.getVersion(instanceId);

            if (thisVersion < otherVersion) {
                return false;
            } else if (thisVersion > otherVersion) {
                atLeastOneGreater = true;
            }
        }
        return atLeastOneGreater;
    }

    public void merge(VersionVector other) {
        // create vector with all components
        Set<String> allInstances = new HashSet<>();
        allInstances.addAll(this.getInstances());
        allInstances.addAll(other.getInstances());

        // choose maximum value of each component
        for (String instanceId : allInstances) {
            int thisVersion = this.getVersion(instanceId);
            int otherVersion = other.getVersion(instanceId);
            versions.put(instanceId, Math.max(thisVersion, otherVersion));
        }

        edited = false;
    }

    @Override
    public String toString() {
        return versions.toString();
    }
}
