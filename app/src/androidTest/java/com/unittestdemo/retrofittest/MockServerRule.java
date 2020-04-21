package com.unittestdemo.retrofittest;

import android.util.Log;

import androidx.test.rule.UiThreadTestRule;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * JUnit  rule that starts and stops a mock web server for test runner
 */
public class MockServerRule extends UiThreadTestRule {

    public MockWebServer mServer;
    public Dispatcher dispatcher;

    public static final int MOCK_WEBSERVER_PORT = 8000;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startServer();
                try {
                    base.evaluate();
                } finally {
                    stopServer();
                }
            }
        };
    }

    /**
     * Returns the started web server instance
     *
     * @return mock server
     */
    public MockWebServer server() {
        return mServer;
    }

    public void startServer() throws IOException, NoSuchAlgorithmException {
        mServer = new MockWebServer();
        try {
            mServer.start(MOCK_WEBSERVER_PORT);
        } catch (IOException e) {
            throw new IllegalStateException("mock server start issue", e.getCause());
        }
    }

    public void stopServer() {
        try {
            mServer.shutdown();
        } catch (IOException e) {
            Log.e("MockServer", "mock server shutdown error");
        }
    }

    public void setDispatcher() {

        dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                //return new MockResponse().setResponseCode(200);
                switch (request.getPath()) {
                    case "/users/1":
                        return new MockResponse().setResponseCode(200);
                    case "v1/check/version/":
                        return new MockResponse().setResponseCode(200).setBody("version=9");
                    case "/users":
                        return new MockResponse().setResponseCode(200).setBody("{\\\"info\\\":{\\\"name\":\"Lucas Albuquerque\",\"age\":\"21\",\"gender\":\"male\"}}");
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mServer.setDispatcher(dispatcher);
    }
}