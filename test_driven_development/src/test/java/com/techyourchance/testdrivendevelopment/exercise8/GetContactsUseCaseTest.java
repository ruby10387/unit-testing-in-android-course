package com.techyourchance.testdrivendevelopment.exercise8;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetContactsUseCaseTest {
    public static final String FILTER_TERM = "filterTerm";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    public static final String IMAGE_URL = "imageUrl";
    public static final int AGE = 20;

    @Mock GetContactsHttpEndpoint m_GetContactsHttpEndpointMock;
    @Mock GetContactsUseCase.Listener m_ListenerMock1;
    @Mock GetContactsUseCase.Listener m_ListenerMock2;
    @Captor ArgumentCaptor<List<Contact>> m_AcListContact;

    GetContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new GetContactsUseCase(m_GetContactsHttpEndpointMock);
        success();
    }

    // correct filterTerm passed to the endpoint
    @Test
    public void getContacts_correctFilterTermPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        SUT.getContactsAndNotify(FILTER_TERM);
        verify(m_GetContactsHttpEndpointMock).getContacts(acString.capture(), any(Callback.class));
        assertThat(acString.getValue(), is(FILTER_TERM));
    }

    // success - all observers notified with correct data
    @Test
    public void getContacts_success_observersNotifiedWithCorrectData() throws Exception {
        SUT.registerListener(m_ListenerMock1);
        SUT.registerListener(m_ListenerMock2);
        SUT.getContactsAndNotify(FILTER_TERM);
        verify(m_ListenerMock1).onContactsGotten(m_AcListContact.capture());
        verify(m_ListenerMock2).onContactsGotten(m_AcListContact.capture());
        List<List<Contact>> captures = m_AcListContact.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContact()));
        assertThat(capture2, is(getContact()));
    }

    // success - unsubscribe observers not notified
    @Test
    public void getContacts_success_unsubscribeObserversNotNotified() throws Exception {
        SUT.registerListener(m_ListenerMock1);
        SUT.registerListener(m_ListenerMock2);
        SUT.unregisterListener(m_ListenerMock2);
        SUT.getContactsAndNotify(FILTER_TERM);
        verify(m_ListenerMock1).onContactsGotten(any(List.class));
        verifyNoMoreInteractions(m_ListenerMock2);
    }

    // general error - observers notified of failure
    @Test
    public void getContacts_generalError_observersNotifiedOfFailure() throws Exception {
        generalError();
        SUT.registerListener(m_ListenerMock1);
        SUT.registerListener(m_ListenerMock2);
        SUT.getContactsAndNotify(FILTER_TERM);
        verify(m_ListenerMock1).onGetContactFailed();
        verify(m_ListenerMock2).onGetContactFailed();
    }

    // network error - observers notified of failure
    @Test
    public void getContacts_networkError_observersNotifiedOfFailure() throws Exception {
        networkError();
        SUT.registerListener(m_ListenerMock1);
        SUT.registerListener(m_ListenerMock2);
        SUT.getContactsAndNotify(FILTER_TERM);
        verify(m_ListenerMock1).onGetContactFailed();
        verify(m_ListenerMock2).onGetContactFailed();
    }

    //==============================================================================================
    // Helper methods
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactSchema());
                return null;
            }
        }).when(m_GetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(m_GetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(m_GetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactSchema() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return contactSchemas;
    }

    private List<Contact> getContact() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }

}