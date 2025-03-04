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

public interface UserGroupPermission extends Common {
  String RESOURCE_CLASS = "RESOURCE_CLASS";

  String RESOURCE_ID = "RESOURCE_ID";

  String ACTION_NAME = "ACTION_NAME";

  String ACTIVE_IND = "ACTIVE_IND";

  String USER_GROUP_PERMISSION_ID = "USER_GROUP_PERMISSION_ID";

  String USER_GROUP_ID = "USER_GROUP_ID";

  String MODULE_NAME = "MODULE_NAME";

  PathName USER_GROUP_PERMISSION = PathName.newPathName("/CPF/CPF_USER_GROUP_PERMISSIONS");

}
