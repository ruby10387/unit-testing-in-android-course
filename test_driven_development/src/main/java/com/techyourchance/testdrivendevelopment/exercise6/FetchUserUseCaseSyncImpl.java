package com.techyourchance.testdrivendevelopment.exercise6;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus.SUCCESS;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync m_fetchUserHttpEndpointSync;
    private UsersCache m_usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        m_fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        m_usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        FetchUserHttpEndpointSync.EndpointResult result;
        User user;

        try {
            user = m_usersCache.getUser(userId);
            if (user != null) {
                return new UseCaseResult(Status.SUCCESS, user);
            }
            result = m_fetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        if (result.getStatus() == SUCCESS) {
            user = new User(result.getUserId(), result.getUsername());
            m_usersCache.cacheUser(user);
            return new UseCaseResult(Status.SUCCESS, user);
        } else if (result.getStatus() == AUTH_ERROR) {
            return new UseCaseResult(Status.FAILURE, null);
        } else if (result.getStatus() == GENERAL_ERROR) {
            return new UseCaseResult(Status.FAILURE, null);
        }

        throw new RuntimeException("invalid status: " + result.getStatus());
    }
}
