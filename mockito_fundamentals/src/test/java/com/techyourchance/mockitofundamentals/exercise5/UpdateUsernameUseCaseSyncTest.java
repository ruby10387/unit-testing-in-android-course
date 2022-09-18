package com.techyourchance.mockitofundamentals.exercise5;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class UpdateUsernameUseCaseSyncTest {
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";

    UpdateUsernameHttpEndpointSync m_UpdateUsernameHttpEndpointSyncMock;
    UsersCache m_UsersCacheMock;
    EventBusPoster m_EventBusPosterMock;
    UpdateUsernameUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        m_UpdateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        m_UsersCacheMock = mock(UsersCache.class);
        m_EventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(m_UpdateUsernameHttpEndpointSyncMock, m_UsersCacheMock, m_EventBusPosterMock);
        success();
    }

    // userId and userName be passed to the endpoint
    @Test
    public void updateUsernameSync_success_userIdAndUserNamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verify(m_UpdateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USER_NAME));
    }

    // if success - user must be cached
    @Test
    public void updateUsernameSync_success_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verify(m_UsersCacheMock).cacheUser(ac.capture());
        //這邊應該要呼叫裡面的方法做推斷
//        assertThat(ac.getValue(), is(instanceOf(User.class)));
        User user = ac.getValue();
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getUsername(), is(USER_NAME));
    }

    // if failed - userName not be changed
    @Test
    public void updateUsernameSync_generalError_userNotBeCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_UsersCacheMock);
    }

    @Test
    public void updateUsernameSync_authError_userNotBeCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_UsersCacheMock);
    }

    @Test
    public void updateUsernameSync_serverError_userNotBeCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_UsersCacheMock);
    }

    // if success - the event posted
    @Test
    public void updateUsernameSync_success_updateEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verify(m_EventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    // if failed - nothing to do with event poster
    @Test
    public void updateUsernameSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_EventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_EventBusPosterMock);
    }

    @Test
    public void updateUsernameSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(m_EventBusPosterMock);
    }

    // if success - success returned
    @Test
    public void updateUsernameSync_success_successReturned() throws Exception {
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    // if failed - fail returned
    @Test
    public void updateUsernameSync_generalError_failureReturned() throws Exception {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_authError_failureReturned() throws Exception {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_serverError_failureReturned() throws Exception {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_networkError_failureReturned() throws Exception {
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws NetworkErrorException {
        when(m_UpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USER_NAME));
    }

    private void generalError() throws NetworkErrorException {
        when(m_UpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws NetworkErrorException {
        when(m_UpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws NetworkErrorException {
        when(m_UpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException {
        doThrow(new NetworkErrorException())
                .when(m_UpdateUsernameHttpEndpointSyncMock).updateUsername(any(String.class), any(String.class));
    }
}