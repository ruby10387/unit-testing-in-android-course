package com.techyourchance.testdoublesfundamentals.exercise4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

public class FetchUserProfileUseCaseSyncTest {
    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    private UserProfileHttpEndpointSyncTd m_UserProfileHttpEndpointSyncTd;
    private UsersCacheTd m_UsersCacheTd;
    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        m_UserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        m_UsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(m_UserProfileHttpEndpointSyncTd, m_UsersCacheTd);
    }

    // userId passed to the endpoint
    @Test
    public void fetchUserProfileSync_success_userIdPassedToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(m_UserProfileHttpEndpointSyncTd.m_userId, is(USER_ID));
    }

    // if success - user must be cached
    @Test
    public void fetchUserProfileSync_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        //這邊應該要呼叫裡面的方法做推斷
//        assertThat(m_UsersCacheTd.getUser(USERID), is(new User(USERID, FULLNAME, IMAGEURL)));
        User cachedUser = m_UsersCacheTd.getUser(USER_ID);
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
    }

    // if failed - user id is not changed
    @Test
    public void fetchUserProfileSync_authError_userIdNotCached() {
        m_UserProfileHttpEndpointSyncTd.m_isAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(m_UsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_serverError_userIdNotCached() {
        m_UserProfileHttpEndpointSyncTd.m_isServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(m_UsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_generalError_userIdNotCached() {
        m_UserProfileHttpEndpointSyncTd.m_isGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(m_UsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    // if success - success returned
    @Test
    public void fetchUserProfileSync_success_successReturned() {
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    // if failed - fail returned
    @Test
    public void fetchUserProfileSync_authError_failureReturned() {
        m_UserProfileHttpEndpointSyncTd.m_isAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() {
        m_UserProfileHttpEndpointSyncTd.m_isServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_generalError_failureReturned() {
        m_UserProfileHttpEndpointSyncTd.m_isGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // network - network error returned
    @Test
    public void fetchUserProfileSync_networkError_networkErrorReturned() {
        m_UserProfileHttpEndpointSyncTd.m_isNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }


    //==============================================================================================
    // Helper classes
    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String m_userId = "";
        public boolean m_isAuthError;
        public boolean m_isServerError;
        public boolean m_isGeneralError;
        public boolean m_isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            m_userId = userId;
            if (m_isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (m_isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (m_isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (m_isNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {
        User m_user = null;

        @Override
        public void cacheUser(User user) {
            m_user = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            return m_user;
        }
    }
}