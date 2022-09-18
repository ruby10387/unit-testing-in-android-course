package com.techyourchance.testdrivendevelopment.exercise6;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final User USER = new User(USER_ID, USER_NAME);

    private FetchUserHttpEndpointSyncTd m_FetchUserHttpEndpointSyncTd;
    @Mock UsersCache m_UsersCacheMock;
    FetchUserUseCaseSyncImpl SUT;

    @Before
    public void setup() throws Exception {
        m_FetchUserHttpEndpointSyncTd = new FetchUserHttpEndpointSyncTd();
        SUT = new FetchUserUseCaseSyncImpl(m_FetchUserHttpEndpointSyncTd, m_UsersCacheMock);
        userNotInCache();
    }

    // user ID is not in the cache - correct userId passed to endpoint
    @Test
    public void fetchUserSync_notInCache_correctUserIdPassedToEndpoint() throws Exception {
        SUT.fetchUserSync(USER_ID);
        assertThat(m_FetchUserHttpEndpointSyncTd.m_userId, is(USER_ID));
    }

    // user ID is not in the cache and endpoint success - status success returned
    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_successStatusReturned() throws Exception {
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    // user ID is not in the cache and endpoint success - correct user returned
    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_correctUserReturned() throws Exception {
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(USER));
    }

    // user ID is not in the cache and endpoint success - user cached
    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(m_UsersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    // user ID is not in the cache and endpoint auth error - status failure returned
    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_failureStatusReturned() throws Exception {
        endpointAuthError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    // user ID is not in the cache and endpoint auth error - null user returned
    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nullUserReturned() throws Exception {
        endpointAuthError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    // user ID is not in the cache and endpoint auth error - user not cached
    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_userNotCached() throws Exception {
        endpointAuthError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // 檢查沒有執行特定方法
//        verifyNoMoreInteractions(m_UsersCacheMock);
        verify(m_UsersCacheMock, never()).cacheUser(any(User.class));
    }

    // user ID is not in the cache and endpoint general error - status failure returned
    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_failureStatusReturned() throws Exception {
        endpointGeneralError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.FAILURE));
    }

    // user ID is not in the cache and endpoint general error - null user returned
    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_nullUserReturned() throws Exception {
        endpointGeneralError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    // user ID is not in the cache and endpoint general error - user not cached
    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_userNotCached() throws Exception {
        endpointGeneralError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // 檢查沒有執行特定方法
//        verifyNoMoreInteractions(m_UsersCacheMock);
        verify(m_UsersCacheMock, never()).cacheUser(any(User.class));
    }

    // user ID is not in the cache and endpoint network error - status network error returned
    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_networkErrorStatusReturned() throws Exception {
        endpointNetworkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    // user ID is not in the cache and endpoint network error - null user returned
    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nullUserReturned() throws Exception {
        endpointNetworkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(nullValue()));
    }

    // user ID is not in the cache and endpoint network error - user not cached
    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_userNotCached() throws Exception {
        endpointNetworkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // 檢查沒有執行特定方法
//        verifyNoMoreInteractions(m_UsersCacheMock);
        verify(m_UsersCacheMock, never()).cacheUser(any(User.class));
    }

    // correct userId and userName passed to cached
    @Test
    public void fetchUserSync_correctUserIdPassedToCache() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        verify(m_UsersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    // user ID is in the cache - status success returned
    @Test
    public void fetchUserSync_isInCache_successStatusReturned() throws Exception {
        userInCached();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getStatus(), is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    // user ID is in the cache - cached user returned
    @Test
    public void fetchUserSync_isInCache_cachedUserReturned() throws Exception {
        userInCached();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(result.getUser(), is(USER));
    }

    // user ID is in the cache - not poll
    @Test
    public void fetchUserSync_isInCache_notPoll() throws Exception {
        userInCached();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        assertThat(m_FetchUserHttpEndpointSyncTd.m_iCount, is(0));
    }

    //==============================================================================================
    // Helper methods
    private void userNotInCache() {
        when(m_UsersCacheMock.getUser(any(String.class))).thenReturn(null);
    }

    private void userInCached() {
        when(m_UsersCacheMock.getUser(any(String.class))).thenReturn(USER);
    }

    private void endpointAuthError() {
        m_FetchUserHttpEndpointSyncTd.m_isAuthError = true;
    }

    private void endpointGeneralError() {
        m_FetchUserHttpEndpointSyncTd.m_isGeneralError = true;
    }

    private void endpointNetworkError() {
        m_FetchUserHttpEndpointSyncTd.m_isNetworkError = true;
    }

    //==============================================================================================
    // Helper classes
    private static class FetchUserHttpEndpointSyncTd implements FetchUserHttpEndpointSync {
        private int m_iCount;
        public String m_userId = "";
        public boolean m_isAuthError;
        public boolean m_isGeneralError;
        public boolean m_isNetworkError;

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {
            m_iCount++;
            m_userId = userId;
            if (m_isAuthError) {
                return new EndpointResult(EndpointStatus.AUTH_ERROR, "", "");
            } else if (m_isGeneralError) {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, "", "");
            } else if (m_isNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USER_NAME);
            }
        }
    }
}
