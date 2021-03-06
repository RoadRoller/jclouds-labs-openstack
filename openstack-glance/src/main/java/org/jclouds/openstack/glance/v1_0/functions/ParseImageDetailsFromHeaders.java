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
package org.jclouds.openstack.glance.v1_0.functions;

import static org.jclouds.openstack.glance.v1_0.options.ImageField.CHECKSUM;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.CONTAINER_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.CREATED_AT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.DELETED_AT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.DISK_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.ID;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.IS_PUBLIC;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.LOCATION;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_DISK;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_RAM;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.NAME;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.OWNER;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.PROPERTY;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.SIZE;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.STATUS;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.UPDATED_AT;

import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image.Status;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;

import com.google.common.base.Function;

import java.util.Map;

/**
 * This parses {@link ImageDetails} from HTTP headers.
 */
public class ParseImageDetailsFromHeaders implements Function<HttpResponse, ImageDetails> {
   private final DateService dateService;

   @Inject
   public ParseImageDetailsFromHeaders(DateService dateService) {
      this.dateService = dateService;
   }

   @Override
   public ImageDetails apply(HttpResponse from) {
      ImageDetails.Builder<?> builder = ImageDetails.builder()
                .id(from.getFirstHeaderOrNull(ID.asHeader()))
                .name(from.getFirstHeaderOrNull(NAME.asHeader()))
                .checksum(from.getFirstHeaderOrNull(CHECKSUM.asHeader()))
                .minDisk(Long.parseLong(from.getFirstHeaderOrNull(MIN_DISK.asHeader())))
                .minRam(Long.parseLong(from.getFirstHeaderOrNull(MIN_RAM.asHeader())))
                .isPublic(Boolean.parseBoolean(from.getFirstHeaderOrNull(IS_PUBLIC.asHeader())))
                .createdAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull(CREATED_AT.asHeader())))
                .updatedAt(dateService.iso8601SecondsDateParse(from.getFirstHeaderOrNull(UPDATED_AT.asHeader())))
                .owner(from.getFirstHeaderOrNull(OWNER.asHeader()))
                .location(from.getFirstHeaderOrNull(LOCATION.asHeader()))
                .status(Status.fromValue(from.getFirstHeaderOrNull(STATUS.asHeader())));

      String containerFormat = from.getFirstHeaderOrNull(CONTAINER_FORMAT.asHeader());
      String diskFormat = from.getFirstHeaderOrNull(DISK_FORMAT.asHeader());
      String deletedAt = from.getFirstHeaderOrNull(DELETED_AT.asHeader());
      String size = from.getFirstHeaderOrNull(SIZE.asHeader());

      if (containerFormat != null) builder.containerFormat(ContainerFormat.fromValue(containerFormat));
      if (diskFormat != null) builder.diskFormat(DiskFormat.fromValue(diskFormat));
      if (deletedAt != null) builder.deletedAt(dateService.iso8601SecondsDateParse(deletedAt));
      if (size != null) builder.size(Long.parseLong(size));

      // There may be multiple headers that begin with the prefix x-image-meta-property-. These headers are free-form
      // key/value pairs that have been saved with the image metadata. The key is the string after
      // x-image-meta-property- and the value is the value of the header
      Map<String, String> properties = Maps.newHashMap();
      String propertyHeader = PROPERTY.asHeader();
      for (Map.Entry<String, String> headerEntry : from.getHeaders().entries()) {
         String headerName = headerEntry.getKey();
         if (!Strings.isNullOrEmpty(headerName) && headerName.startsWith(propertyHeader)
                 && headerName.length() > propertyHeader.length()) {
            String propertyName = headerName.substring(PROPERTY.asHeader().length() + 1).toLowerCase();
            String propertyValue = headerEntry.getValue();
            properties.put(propertyName, propertyValue);
         }
      }
      builder.properties(properties);

      return builder.build();
   }
}
