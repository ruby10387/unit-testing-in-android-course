package com.techyourchance.unittesting.screens.questiondetails;

import static org.mockito.Mockito.*;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {
    private static final String QUESTION_ID = "questionId";
    private static final QuestionDetails QUESTION_DETAILS = QuestionDetailsTestData.getQuestionDetails();
    private UseCaseTd mUseCaseTd;
    @Mock ScreensNavigator mScreensNavigatorMock;
    @Mock ToastsHelper mToastsHelperMock;
    @Mock QuestionDetailsViewMvc mQuestionDetailsViewMvcMock;

    QuestionDetailsController SUT;

    @Before
    public void setup() throws Exception {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionDetailsController(mUseCaseTd, mScreensNavigatorMock, mToastsHelperMock);
        SUT.bindView(mQuestionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }

    // onStart - show progress indication
    @Test
    public void onStart_progressIndicationShown() throws Exception {
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock).showProgressIndication();
    }

    // on successful details fetch - hide progress indication
    @Test
    public void onStart_successfulResponse_progressIndicationHidden() throws Exception {
        success();
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }

    // onStart - fetch question details
    // on successful details fetch - bind the details to the view
    @Test
    public void onStart_successfulResponse_detailsBoundToView() throws Exception {
        success();
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    // on failed details fetch - hide progress indication
    @Test
    public void onStart_failure_progressIndicationHidden() throws Exception {
        failure();
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }

    // on failed details fetch - error toast shown
    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        failure();
        SUT.onStart();
        verify(mToastsHelperMock).showUseCaseError();
    }

    // on failed details fetch - details not bound to the view
    @Test
    public void onStart_failure_detailsNotBoundToView() throws Exception {
        failure();
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock, never()).bindQuestion(any(QuestionDetails.class));
    }

    // onStart - register listeners
    @Test
    public void onStart_listenersRegistered() throws Exception {
        SUT.onStart();
        verify(mQuestionDetailsViewMvcMock).registerListener(SUT);
        mUseCaseTd.verifyListenerRegistered(SUT);
    }

    // onStop - unregister listeners
    @Test
    public void onStop_listenersUnregistered() throws Exception {
        SUT.onStop();
        verify(mQuestionDetailsViewMvcMock).unregisterListener(SUT);
        mUseCaseTd.verifyListenerNotRegistered(SUT);
    }

    // onNavigateUpClicked - navigated to up
    @Test
    public void onNavigateUpClicked_navigatesToUp() throws Exception {
        SUT.onNavigateUpClicked();
        verify(mScreensNavigatorMock).navigateUp();
    }

    //==============================================================================================
    // helper methods
    private void success() {
        // currently no-op
    }

    private void failure() {
        mUseCaseTd.mFailure = true;
    }

    //==============================================================================================
    // helper classes
    private static class UseCaseTd extends FetchQuestionDetailsUseCase {

        public boolean mFailure;

        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onQuestionDetailsFetchFailed();
                } else {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }
            }
        }

        public void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerNotRegistered(QuestionDetailsController candidate) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener not registered");
                }
            }
        }
    }
}