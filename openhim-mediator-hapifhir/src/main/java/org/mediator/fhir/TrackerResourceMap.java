package org.mediator.fhir;

import java.util.List;

/**
 * Created by server-hit on 4/18/17.
 */
public class TrackerResourceMap {
    public String getProgramId() {
        return programId;
    }
    public String getStageId() {
        return stageId;
    }
    public String getStageName() {
        return stageName;
    }

    String programId;
    String stageId;
    String stageName;
}
