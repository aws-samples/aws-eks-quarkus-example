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

package com.amazon.customerService;

import com.amazon.customerService.model.Customer;
import com.amazon.customerService.model.CustomerCommand;
import com.amazon.customerService.service.CustomerService;
import com.amazon.customerService.service.EventBridgeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.extern.jbosslog.JBossLog;

@Path("/customers")
@JBossLog
public class CustomerResource {

  @Inject
  CustomerService    customerService;
  @Inject
  EventBridgeService eventBridgeService;

  @Inject
  ObjectMapper objectMapper;

  @GET
  public List<Customer> list() {
    log.info("List all customers");
    return customerService.findAll();
  }

  @GET
  @Path("{id}")
  public Customer getSingle(String id) {
    return customerService.get(id);
  }

  @POST
  public Customer add(Customer customer) {

    Customer result = null;

    try {
      UUID uuid = UUID.randomUUID();
      customer.setId(uuid.toString());
      customer.setRegDate(Instant.now());

      result = customerService.add(customer);
      log.info(result);
      String jsonValue = objectMapper.writeValueAsString(
          new CustomerCommand(CustomerCommand.ADD, customer.getId()));
      log.info(jsonValue);
      eventBridgeService.writeMessageToEventBridge(jsonValue);
    }
    catch (JsonProcessingException exc) {
      log.error(exc);
      throw new RuntimeException("JsonProcessingException: ", exc);
    }

    return result;
  }

  @DELETE
  @Path("{id}")
  public Customer delete(String id) {
    Customer deleteCustomer = null;

    try {
      deleteCustomer = customerService.delete(id);
      log.info("Deleted customer " + deleteCustomer);
      String jsonValue = objectMapper.writeValueAsString(
          new CustomerCommand(CustomerCommand.DELETE, id));
      eventBridgeService.writeMessageToEventBridge(jsonValue);
    }
    catch (JsonProcessingException exc) {
      log.error(exc);
      throw new RuntimeException("JsonProcessingException: ", exc);
    }

    return deleteCustomer;

  }
}
