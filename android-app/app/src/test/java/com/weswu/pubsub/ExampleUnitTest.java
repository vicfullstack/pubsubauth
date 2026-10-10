package com.weswu.pubsub;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Local unit tests for Pub/Sub message formatting and GA4 event payload encoding.
 */
public class ExampleUnitTest {
    @Test
    public void pubsubMessage_base64EncodesUtf8Json() throws Exception {
        Map<String, Object> payload = AsyncTaskPubsub.buildTestData();
        String jsonStr = Json.encode(payload);
        PubsubMessage message = new PubsubMessage(jsonStr);
        assertNotNull(message.getData());

        String decoded = new String(Base64.decodeBase64(message.getData()), StandardCharsets.UTF_8);
        assertTrue(decoded.contains("\"event_name\":\"doc_open\""));
        assertTrue(decoded.contains("\"usage\":\"Test\""));
    }
}