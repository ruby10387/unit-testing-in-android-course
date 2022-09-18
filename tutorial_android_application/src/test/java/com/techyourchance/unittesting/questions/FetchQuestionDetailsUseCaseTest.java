package com.techyourchance.unittesting.questions;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {
    private EndpointTD m_EndpointTD;
    @Mock FetchQuestionDetailsUseCase.Listener m_Listener1;
    @Mock FetchQuestionDetailsUseCase.Listener m_Listener2;

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        m_EndpointTD = new EndpointTD();
        SUT = new FetchQuestionDetailsUseCase(m_EndpointTD);
    }

    // success - listeners notified of success with correct data
    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        success();
        SUT.registerListener(m_Listener1);
        SUT.registerListener(m_Listener2);
        SUT.fetchQuestionDetailsAndNotify("id1");
        verify(m_Listener1).onQuestionDetailsFetched(ac.capture());
        verify(m_Listener2).onQuestionDetailsFetched(ac.capture());
        assertThat(ac.getValue(), is(getExpectedQuestionDetails("id1")));
    }

    // failure - listeners notified of failure
    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        failure();
        SUT.registerListener(m_Listener1);
        SUT.registerListener(m_Listener2);
        SUT.fetchQuestionDetailsAndNotify("id1");
        verify(m_Listener1).onQuestionDetailsFetchFailed();
        verify(m_Listener2).onQuestionDetailsFetchFailed();
    }

    //==============================================================================================
    // Helper methods
    private void success() {
        // currently no-op
    }

    private void failure() {
        m_EndpointTD.m_bFailure = true;
    }

    private QuestionDetails getExpectedQuestionDetails(String questionId) {
        List<QuestionDetails> questionDetails = new LinkedList<>();
        questionDetails.add(new QuestionDetails("id1", "title1", "body1"));
        questionDetails.add(new QuestionDetails("id2", "title2", "body2"));
        for (QuestionDetails details : questionDetails) {
            if (details.getId().equals(questionId)) {
                return details;
            }
        }
        return null;
    }

    //==============================================================================================
    // Helper classes
    private static class EndpointTD extends FetchQuestionDetailsEndpoint {
        public boolean m_bFailure;

        public EndpointTD() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (m_bFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                List<QuestionSchema> questionSchemas = new LinkedList<>();
                questionSchemas.add(new QuestionSchema("title1", "id1", "body1"));
                questionSchemas.add(new QuestionSchema("title2", "id2", "body2"));
                for (QuestionSchema schema : questionSchemas) {
                    if (schema.getId().equals(questionId)) {
                        listener.onQuestionDetailsFetched(schema);
                    }
                }
            }
        }
    }
}