package de.qaware.cloudid.lib;

import de.qaware.cloudid.util.Updater;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages workload Ids.
 */
public interface IdManager extends Updater<WorkloadIds> {

    /**
     * Get the current set of workload Ids.
     * <p>
     * Blocks until workload Ids become available.
     *
     * @return workload Ids
     */
    WorkloadIds get();

    /**
     * Add a listener that gets notified whenever the current set of workload Ids changes.
     *
     * Listeners will be notified immediately if workload Ids were available before they are added.
     *
     * @param listener listener
     */
    void addListener(Consumer<WorkloadIds> listener);

    /**
     * Get the single workload Id if there is only one.
     *
     * @return workload Id
     */
    default WorkloadId getWorkloadId() {
        List<WorkloadId> workloadIdList = get().getWorkloadIdList();
        Validate.isTrue(workloadIdList.size() == 1);
        return workloadIdList.get(0);
    }

}
