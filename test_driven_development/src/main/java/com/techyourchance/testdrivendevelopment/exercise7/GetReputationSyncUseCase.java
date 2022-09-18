package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.GetReputationSyncUseCase.UseCaseStatus.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.GetReputationSyncUseCase.UseCaseStatus.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

public class GetReputationSyncUseCase {

    private GetReputationHttpEndpointSync m_getReputationHttpEndpointSync;

    public GetReputationSyncUseCase(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        m_getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public UseCaseResult getReputationSync() {
        EndpointResult result = m_getReputationHttpEndpointSync.getReputationSync();
        if (result.getStatus() == GENERAL_ERROR) {
            return new UseCaseResult(FAILURE, 0);
        } else if (result.getStatus() == NETWORK_ERROR) {
            return new UseCaseResult(FAILURE, 0);
        } else {
            return new UseCaseResult(SUCCESS, result.getReputation());
        }
    }


    public enum UseCaseStatus {
        SUCCESS,
        FAILURE
    }

    class UseCaseResult {
        private final UseCaseStatus m_useCaseStatus;
        private final int m_iReputation;

        public UseCaseResult(UseCaseStatus useCaseStatus, int reputation) {
            m_useCaseStatus = useCaseStatus;
            m_iReputation = reputation;
        }

        public UseCaseStatus getStatus() {
            return m_useCaseStatus;
        }

        public int getReputation() {
            return m_iReputation;
        }
    }
}
