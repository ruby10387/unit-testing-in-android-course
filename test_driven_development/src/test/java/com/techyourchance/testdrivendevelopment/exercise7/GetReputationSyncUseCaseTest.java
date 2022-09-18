package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.GetReputationSyncUseCase.UseCaseStatus.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.GetReputationSyncUseCase.UseCaseStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.techyourchance.testdrivendevelopment.exercise7.GetReputationSyncUseCase.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GetReputationSyncUseCaseTest {
    private static final int REPUTATION = 1;

    GetReputationSyncUseCase SUT;
    GetReputationHttpEndpointSyncTd m_GetReputationHttpEndpointSyncTd;

    @Before
    public void setup() throws Exception {
        m_GetReputationHttpEndpointSyncTd = new GetReputationHttpEndpointSyncTd();
        SUT = new GetReputationSyncUseCase(m_GetReputationHttpEndpointSyncTd);
    }

    @Test
    public void getReputationSync_success_successStatus() throws Exception {
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getStatus(), is(SUCCESS));
    }

    @Test
    public void getReputationSync_success_reputationHasValue() throws Exception {
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void getReputationSync_generalError_failureStatus() throws Exception {
        generalError();
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void getReputationSync_generalError_reputationDefaultZero() throws Exception {
        generalError();
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void getReputationSync_networkError_failureStatus() throws Exception {
        networkError();
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void getReputationSync_networkError_reputationDefaultZero() throws Exception {
        networkError();
        UseCaseResult result = SUT.getReputationSync();
        assertThat(result.getReputation(), is(0));
    }

    //==============================================================================================
    // Helper methods
    private void generalError() {
        m_GetReputationHttpEndpointSyncTd.m_GeneralError = true;
    }

    private void networkError() {
        m_GetReputationHttpEndpointSyncTd.mNetworkError = true;
    }

    //==============================================================================================
    // Helper classes
    private class GetReputationHttpEndpointSyncTd implements GetReputationHttpEndpointSync {
        public boolean m_GeneralError;
        public boolean mNetworkError;

        @Override
        public EndpointResult getReputationSync() {
            if (m_GeneralError) {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, 0);
            } else if (mNetworkError) {
                return new EndpointResult(EndpointStatus.NETWORK_ERROR, 0);
            } else {
                return new EndpointResult(EndpointStatus.SUCCESS, REPUTATION);
            }
        }
    }
}