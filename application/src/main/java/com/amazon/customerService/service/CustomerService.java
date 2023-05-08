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

import com.amazon.customerService.model.Customer;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class CustomerService extends AbstractService {

    DynamoDbClient dynamoDB;

    public CustomerService() {

        System.setProperty(SdkSystemSetting.SYNC_HTTP_SERVICE_IMPL.property(),
                           "software.amazon.awssdk.http.apache.ApacheSdkHttpService");

        dynamoDB = DynamoDbClient
            .builder()
            .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
            .httpClient(ApacheHttpClient.create())
            .build();
    }

    public List<Customer> findAll() {
        return dynamoDB
            .scanPaginator(scanRequest())
            .items()
            .stream()
            .map(Customer::from)
            .collect(Collectors.toList());
    }

    public Customer add(Customer customer) {
        dynamoDB.putItem(putRequest(customer));
        return customer;
    }

    public Customer get(String id) {
        return Customer.from(dynamoDB
                                 .getItem(getRequest(id))
                                 .item());
    }

    public Customer delete(String id) {
        Customer customer = get(id);
        dynamoDB.deleteItem(deleteRequest(id));

        return customer;
    }
}
