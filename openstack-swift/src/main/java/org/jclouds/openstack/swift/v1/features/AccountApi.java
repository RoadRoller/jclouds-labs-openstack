/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.v1.features;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.binders.BindAccountMetadataToHeaders;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.functions.ParseAccountFromHeaders;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * Provides access to the Swift Account API.
 * 
 * <p/>
 * Account metadata prefixed with {@code X-Account-Meta-} will be converted
 * appropriately using a binder/parser.
 * 
 * @see {@link Account}
 * @see metadata
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-account-services.html">
 *      Storage Account Services API</a>
 */
@RequestFilters(AuthenticateRequest.class)
public interface AccountApi {

   /**
    * Gets the {@link Account}.
    * 
    * @return the Account.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-account-metadata.html">
    *      Get Account Metadata API</a>
    */
   @Named("account:get")
   @Consumes(MediaType.APPLICATION_JSON)
   @HEAD
   @ResponseParser(ParseAccountFromHeaders.class)
   @Path("/")
   Account get();

   /**
    * Creates or updates the Account metadata.
    * 
    * @param metadata
    *           the Account metadata to create or update.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/create-update-account-metadata.html">
    *      Create or Update Account Metadata API</a>
    * 
    * @return <code>true</code> if the Account Metadata was successfully created
    *         or updated, false if not.
    */
   @Named("account:createOrUpdateMetadata")
   @Consumes
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/")
   boolean createOrUpdateMetadata(@BinderParam(BindAccountMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes Account metadata.
    * 
    * @param metadata
    *           the Account metadata to delete.
    * 
    * @return <code>true</code> if the Account Metadata was successfully
    *         deleted, false if not.
    * 
    * @see <a
    *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/delete-account-metadata.html">
    *      Delete Account Metadata API</a>
    */
   @Named("account:deleteMetadata")
   @Consumes
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/")
   boolean deleteMetadata(@BinderParam(BindAccountMetadataToHeaders.InRemoval.class) Map<String, String> metadata);
}
