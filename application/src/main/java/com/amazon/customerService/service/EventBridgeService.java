/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.amazon.customerService.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

@ApplicationScoped
@JBossLog
public class EventBridgeService {

  private final EventBridgeClient eventBridgeClient;

  public EventBridgeService() {
    eventBridgeClient = EventBridgeClient
        .builder()
        .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
        .httpClient(ApacheHttpClient.create())
        .build();
  }

  public void writeMessageToEventBridge(String message) {

    PutEventsRequestEntry reqEntry = PutEventsRequestEntry
        .builder()
        .source("com.amazon.customerservice")
        .eventBusName("com.amazon.customerservice")
        .detail(message)
        .detailType("com.amazon.customerservice")
        .build();

    PutEventsRequest eventsRequest = PutEventsRequest
        .builder()
        .entries(reqEntry)
        .build();

    PutEventsResponse result = eventBridgeClient.putEvents(eventsRequest);
    for (PutEventsResultEntry resultEntry : result.entries()) {
      if (resultEntry.eventId() != null) {
        log.info("Event Id: " + resultEntry.eventId());
      }
      else {
        log.error("Injection failed with Error Code: " + resultEntry.errorCode());
        log.error("Error Message: " + resultEntry.errorMessage());
        throw new RuntimeException(
            "EventBridge error, Error code: " + resultEntry.errorCode() + "Error Message: "
                + resultEntry.errorMessage());
      }
    }
  }
}
