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

package com.amazon.customerService.model;

import static com.amazon.customerService.service.AbstractService.ACCOUNT_NUMBER_COLUMN;
import static com.amazon.customerService.service.AbstractService.EMAIL_COLUMN;
import static com.amazon.customerService.service.AbstractService.ID_COLUMN;
import static com.amazon.customerService.service.AbstractService.NAME_COLUMN;
import static com.amazon.customerService.service.AbstractService.REGISTRATION_DATE_COLUMN;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
@Data
@NoArgsConstructor
public class Customer {

  private String id, name, email, accountNumber;
  private Instant regDate;

  public Customer(final Customer customer) {
    this.id            = customer.id;
    this.name          = customer.name;
    this.accountNumber = customer.accountNumber;
    this.email         = customer.email;
    this.regDate       = customer.regDate;
  }

  public static Customer from(Map<String, AttributeValue> item) {
    Customer customer = new Customer();

    customer.setAccountNumber(item
                                  .get(ACCOUNT_NUMBER_COLUMN)
                                  .s());
    customer.setEmail(item
                          .get(EMAIL_COLUMN)
                          .s());
    customer.setName(item
                         .get(NAME_COLUMN)
                         .s());
    customer.setAccountNumber(item
                                  .get(ACCOUNT_NUMBER_COLUMN)
                                  .s());
    customer.setId(item
                       .get(ID_COLUMN)
                       .s());

    customer.setRegDate(LocalDateTime
                            .parse(item
                                       .get(REGISTRATION_DATE_COLUMN)
                                       .s(), ISO_DATE_TIME)
                            .toInstant(UTC));

    return customer;
  }


}
