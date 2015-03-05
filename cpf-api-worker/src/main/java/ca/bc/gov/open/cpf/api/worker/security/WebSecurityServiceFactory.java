/*
 * Copyright © 2008-2015, Province of British Columbia
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
package ca.bc.gov.open.cpf.api.worker.security;

import ca.bc.gov.open.cpf.client.httpclient.DigestHttpClient;
import ca.bc.gov.open.cpf.plugin.impl.module.Module;
import ca.bc.gov.open.cpf.plugin.impl.security.AbstractCachingSecurityService;
import ca.bc.gov.open.cpf.plugin.impl.security.AbstractSecurityServiceFactory;

public class WebSecurityServiceFactory extends AbstractSecurityServiceFactory {

  private final DigestHttpClient httpClient;

  public WebSecurityServiceFactory(final DigestHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public void close() {
  }

  @Override
  protected AbstractCachingSecurityService createSecurityService(
    final Module module,
    final String consumerKey) {
    return new WebSecurityService(httpClient, module, consumerKey);
  }

}
