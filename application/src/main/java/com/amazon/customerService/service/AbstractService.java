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

import static java.time.ZoneOffset.UTC;

import com.amazon.customerService.model.Customer;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public class AbstractService {

    public static final String TABLE_NAME               = "Customer";
    public static final String ID_COLUMN                = "Id";
    public static final String NAME_COLUMN              = "Name";
    public static final String EMAIL_COLUMN             = "Email";
    public static final String ACCOUNT_NUMBER_COLUMN    = "AccountNumber";
    public static final String REGISTRATION_DATE_COLUMN = "RegistrationDate";

    private final DateTimeFormatter formatter;

    public AbstractService() {
        formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .withZone(UTC);
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    protected ScanRequest scanRequest() {
        return ScanRequest
            .builder()
            .tableName(getTableName())
            .attributesToGet(ID_COLUMN, NAME_COLUMN, EMAIL_COLUMN, ACCOUNT_NUMBER_COLUMN,
                             REGISTRATION_DATE_COLUMN)
            .build();
    }

    protected PutItemRequest putRequest(Customer customer) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put(ID_COLUMN, AttributeValue
            .builder()
            .s(customer.getId())
            .build());
        item.put(NAME_COLUMN, AttributeValue
            .builder()
            .s(customer.getName())
            .build());
        item.put(EMAIL_COLUMN, AttributeValue
            .builder()
            .s(customer.getEmail())
            .build());
        item.put(ACCOUNT_NUMBER_COLUMN,
                 AttributeValue
                     .builder()
                     .s(customer.getAccountNumber())
                     .build());
        item.put(REGISTRATION_DATE_COLUMN,
                 AttributeValue
                     .builder()
                     .s(formatter.format(customer.getRegDate()))
                     .build());

        return PutItemRequest
            .builder()
            .tableName(getTableName())
            .item(item)
            .build();
    }

    protected DeleteItemRequest deleteRequest(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(ID_COLUMN, AttributeValue
            .builder()
            .s(id)
            .build());

        return DeleteItemRequest
            .builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build();
    }

    protected GetItemRequest getRequest(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(ID_COLUMN, AttributeValue
            .builder()
            .s(id)
            .build());

        return GetItemRequest
            .builder()
            .tableName(TABLE_NAME)
            .key(key)
            .build();
    }
}
