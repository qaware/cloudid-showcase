package de.qaware.cloudid.lib;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * List of identities for a workload.
 */
@Data
public class WorkloadIds {

    private final List<WorkloadId> workloadIdList;
    private final Instant expiry;

    /**
     * Tells whether this list of Id is empty.
     *
     * @return this list of Id is empty
     */
    public boolean isEmpty() {
        return workloadIdList.isEmpty();
    }

}
