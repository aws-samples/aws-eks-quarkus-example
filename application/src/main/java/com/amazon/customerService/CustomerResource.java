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
import io.quarkus.logging.Log;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.time.Instant;
import java.util.*;

@Path("/customers")
public class CustomerResource {

    private static final Logger LOG = Logger.getLogger(CustomerResource.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<Customer> customers = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));
    @Inject
    CustomerService customerService;
    @Inject
    EventBridgeService eventBridgeService;

    @GET
    public List<Customer> list() {
        LOG.info("List all customers");
        return customerService.findAll();
    }

    @GET
    @Path("{id}")
    public Customer getSingle(String id) {
        return customerService.get(id);
    }

    @POST
    public Set<Customer> add(Customer customer) {
        customers.add(customer);

        try {
            UUID uuid = UUID.randomUUID();
            customer.setId(uuid.toString());
            customer.setRegDate(Instant.now());

            List<Customer> resultList = customerService.add(customer);
            Log.info(resultList);
            String jsonValue = objectMapper.writeValueAsString(new CustomerCommand(CustomerCommand.ADD, customer.getId()));
            eventBridgeService.writeMessageToEventBridge(jsonValue);
        } catch (JsonProcessingException exc) {
            LOG.error(exc);
        }

        return customers;
    }

    @DELETE
    @Path("{id}")
    public Set<Customer> delete(String id) {
        customers.removeIf(existingCustomer -> existingCustomer.getId().contentEquals(id));

        try {
            Customer deleteCustomer = customerService.delete(id);
            LOG.info("Deleted customer " + deleteCustomer);
            String jsonValue = objectMapper.writeValueAsString(new CustomerCommand(CustomerCommand.DELETE, id));
            eventBridgeService.writeMessageToEventBridge(jsonValue);
        } catch (JsonProcessingException exc) {
            LOG.error(exc);
        }

        return customers;
    }
}
