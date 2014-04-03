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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindContainerMetadataToHeaders;
import org.jclouds.openstack.swift.v1.binders.BindMetadataToHeaders.BindRemoveContainerMetadataToHeaders;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.functions.FalseOnAccepted;
import org.jclouds.openstack.swift.v1.functions.ParseContainerFromHeaders;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.collect.FluentIterable;

/**
 * Provides access to the Swift Container API features.
 * <p/>
 * This API is new to jclouds and hence is in Beta. That means we need people to use it and give us feedback. Based
 * on that feedback, minor changes to the interfaces may happen. This code will replace
 * org.jclouds.openstack.swift.SwiftClient in jclouds 2.0 and it is recommended you adopt it sooner than later.
 *
 * @author Adrian Cole
 * @author Zack Shoylev
 * @author Jeremy Daggett
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(APPLICATION_JSON)
public interface ContainerApi {

   /**
    * Lists up to 10,000 containers.
    * 
    * @return a list of {@link Container containers} ordered by name.
    */
   @Named("container:list")
   @GET
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<Container> list();

   /**
    * Lists containers with the supplied {@link ListContainerOptions}.
    * 
    * @param options
    *          the options to control the output list.
    * 
    * @return a list of {@link Container containers} ordered by name.
    */
   @Named("container:list")
   @GET
   @QueryParams(keys = "format", values = "json")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   @Path("/")
   FluentIterable<Container> list(ListContainerOptions options);

   /**
    * Creates a container, if not already present.
    * 
    * @param containerName
    *           corresponds to {@link Container#getName()}.
    * @param options
    *           the options to use when creating the container.
    * 
    * @return {@code true} if the container was created, {@code false} if the container already existed.
    */
   @Named("container:createIfAbsent")
   @PUT
   @ResponseParser(FalseOnAccepted.class)
   @Path("/{containerName}")
   boolean createIfAbsent(@PathParam("containerName") String containerName, CreateContainerOptions options);

   /**
    * Gets the {@link Container}.
    * 
    * @param containerName
    *           corresponds to {@link Container#getName()}.
    * 
    * @return the {@link Container}, or {@code null} if not found.
    */
   @Named("container:get")
   @HEAD
   @ResponseParser(ParseContainerFromHeaders.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{containerName}")
   @Nullable
   Container get(@PathParam("containerName") String containerName);

   /**
    * Creates or updates the {@link Container} metadata.
    * 
    * @param containerName
    *           the container name corresponding to {@link Container#getName()}.
    * @param metadata
    *           the container metadata to create or update.
    * 
    * @return {@code true} if the container metadata was successfully created or updated,
    *         {@code false} if not.
    */
   @Named("container:updateMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean updateMetadata(@PathParam("containerName") String containerName,
         @BinderParam(BindContainerMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes {@link Container} metadata.
    * 
    * @param containerName
    *           corresponds to {@link Container#getName()}.
    * @param metadata
    *           the container metadata to delete.
    * 
    * @return {@code true} if the container metadata was successfully deleted, 
    *         {@code false} if not.
    */
   @Named("container:deleteMetadata")
   @POST
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean deleteMetadata(@PathParam("containerName") String containerName,
         @BinderParam(BindRemoveContainerMetadataToHeaders.class) Map<String, String> metadata);

   /**
    * Deletes a {@link Container}, if empty.
    * 
    * @param containerName
    *           corresponds to {@link Container#getName()}.
    * 
    * @return {@code false} if the container was not present.
    * 
    * @throws IllegalStateException if the container was not empty.
    */
   @Named("container:deleteIfEmpty")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/{containerName}")
   boolean deleteIfEmpty(@PathParam("containerName") String containerName) throws IllegalStateException;
}
