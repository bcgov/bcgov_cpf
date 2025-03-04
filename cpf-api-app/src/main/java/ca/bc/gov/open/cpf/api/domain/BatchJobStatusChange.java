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
package ca.bc.gov.open.cpf.api.domain;

import org.jeometry.common.io.PathName;

public interface BatchJobStatusChange extends Common {
  PathName BATCH_JOB_STATUS_CHANGE = PathName.newPathName("/CPF/CPF_BATCH_JOB_STATUS_CHANGE");

  String BATCH_JOB_STATUS_CHANGE_ID = "BATCH_JOB_STATUS_CHANGE_ID";

  String BATCH_JOB_ID = "BATCH_JOB_ID";

  String JOB_STATUS = "JOB_STATUS";
}
