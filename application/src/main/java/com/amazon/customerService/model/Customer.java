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

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.amazon.customerService.service.AbstractService.*;

@RegisterForReflection
public class Customer {

    private static final SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    private String id, name, email, accountNumber;
    private Instant regDate;

    public Customer() {
    }

    public Customer(final Customer customer) {
        this.id = customer.id;
        this.name = customer.name;
        this.accountNumber = customer.accountNumber;
        this.email = customer.email;
        this.regDate = customer.regDate;
    }

    public static Customer from(Map<String, AttributeValue> item) {
        Customer customer = new Customer();

        customer.setAccountNumber(item.get(ACCOUNT_NUMBER_COLUMN).s());
        customer.setEmail(item.get(EMAIL_COLUMN).s());
        customer.setName(item.get(NAME_COLUMN).s());
        customer.setAccountNumber(item.get(ACCOUNT_NUMBER_COLUMN).s());
        customer.setId(item.get(ID_COLUMN).s());

        Date registrationDate = null;

        try {
            registrationDate = sdf.parse(item.get(REGISTRATION_DATE_COLUMN).s());
        } catch (ParseException exc) {
        }

        customer.setRegDate(registrationDate.toInstant());

        return customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Instant getRegDate() {
        return regDate;
    }

    public void setRegDate(Instant regDate) {
        this.regDate = regDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", regDate=" + regDate +
                '}';
    }
}
