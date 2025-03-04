/*
 * Copyright © 2008-2016, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.bc.gov.open.cpf.plugin.impl.geometry;

import org.jeometry.common.data.type.AbstractDataType;

import ca.bc.gov.open.cpf.plugin.api.GeometryFactory;

import com.vividsolutions.jts.geom.Geometry;

public class JtsGeometryDataType extends AbstractDataType {

  public JtsGeometryDataType(final String name, final Class<? extends Geometry> javaClass) {
    super(name, javaClass, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V toObject(final String string) {
    final GeometryFactory factory = GeometryFactory.getFactory();
    return (V)factory.createGeometry(string);
  }

  @Override
  protected Object toObjectDo(final Object value) {
    final String string = value.toString();
    final GeometryFactory factory = GeometryFactory.getFactory();
    return factory.createGeometry(string);
  }

  @Override
  public String toStringDo(final Object value) {
    final Geometry geometry = (Geometry)value;
    return JtsWktWriter.toString(geometry, true);
  }
}
