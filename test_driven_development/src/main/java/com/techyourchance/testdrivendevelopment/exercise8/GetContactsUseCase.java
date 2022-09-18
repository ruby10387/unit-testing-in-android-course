package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class GetContactsUseCase {
    private final GetContactsHttpEndpoint m_GetContactsHttpEndpoint;
    private final List<Listener> m_Listeners = new ArrayList<>();

    public interface Listener {
        void onContactsGotten(List<Contact> capture);
        void onGetContactFailed();
    }

    public GetContactsUseCase(GetContactsHttpEndpoint m_getContactsHttpEndpoint) {
        m_GetContactsHttpEndpoint = m_getContactsHttpEndpoint;
    }

    public void getContactsAndNotify(String filterTerm) {
        m_GetContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {

            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                for (Listener listener : m_Listeners) {
                    listener.onContactsGotten(contactFromSchema(contactSchemas));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                        for (Listener listener : m_Listeners) {
                            listener.onGetContactFailed();
                        }
                        break;
                }
            }
        });
    }

    private List<Contact> contactFromSchema(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : contactSchemas) {
            contacts.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        }
        return contacts;
    }

    public void registerListener(Listener listener) {
        m_Listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        m_Listeners.remove(listener);
    }
}
