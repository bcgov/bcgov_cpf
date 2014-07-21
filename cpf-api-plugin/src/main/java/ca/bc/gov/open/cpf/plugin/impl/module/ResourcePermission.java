package ca.bc.gov.open.cpf.plugin.impl.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.revolsys.data.record.Record;
import com.revolsys.util.CollectionUtil;
import com.revolsys.util.Property;

public class ResourcePermission {
  public static List<ResourcePermission> getPermissions(
    final List<Map<String, Object>> permissions) {
    final List<ResourcePermission> resourcePermissions = new ArrayList<ResourcePermission>();
    if (permissions != null) {
      for (final Map<String, Object> permission : permissions) {
        final ResourcePermission resourcePermission = new ResourcePermission(
          permission);
        resourcePermissions.add(resourcePermission);
      }
    }
    return resourcePermissions;
  }

  public static final String ALL = "ALL";

  private String actionName;

  private String resourceClass;

  private String resourceId;

  public ResourcePermission() {
  }

  public ResourcePermission(final Map<String, Object> permission) {
    setResourceClass(CollectionUtil.get(permission, "resourceClass", ALL));
    setResourceId(CollectionUtil.get(permission, "resourceId", ALL));
    setActionName(CollectionUtil.get(permission, "action", ALL));
  }

  public ResourcePermission(final Record permission) {
    setResourceClass(CollectionUtil.get(permission, "RESOURCE_CLASS", ALL));
    setResourceId(CollectionUtil.get(permission, "RESOURCE_ID", ALL));
    setActionName(CollectionUtil.get(permission, "ACTION_NAME", ALL));
  }

  public ResourcePermission(final ResourcePermission permission) {
    this.resourceClass = permission.getResourceClass();
    this.resourceId = permission.getResourceId();
    this.actionName = permission.getActionName();
  }

  public ResourcePermission(final String resourceClass,
    final String resourceId, final String actionName) {
    setResourceClass(resourceClass);
    setResourceId(resourceId);
    setActionName(actionName);
  }

  public boolean canAccess(final ResourcePermission permission) {
    if (equalOrAll(permission.resourceClass, this.resourceClass)) {
      if (equalOrAll(permission.resourceId, this.resourceId)) {
        if (equalOrAll(permission.actionName, this.actionName)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean equalOrAll(final String value1, final String value2) {
    if (value1.equals(value2)) {
      return true;
    } else if (value1.equals(ALL)) {
      return true;
    } else if (value2.equals(ALL)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean equals(final Object object) {
    if (object instanceof ResourcePermission) {
      final ResourcePermission permission = (ResourcePermission)object;
      if (permission.resourceClass.equals(this.resourceClass)) {
        if (permission.resourceId.equals(this.resourceId)) {
          if (permission.actionName.equals(this.actionName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public String getActionName() {
    return this.actionName;
  }

  public String getResourceClass() {
    return this.resourceClass;
  }

  public String getResourceId() {
    return this.resourceId;
  }

  @Override
  public int hashCode() {
    return this.resourceClass.hashCode() + this.resourceId.hashCode()
        + this.actionName.hashCode();
  }

  public void setActionName(final String actionName) {
    if (Property.hasValue(actionName)) {
      this.actionName = actionName;
    } else {
      this.actionName = ALL;
    }
  }

  public void setResourceClass(final String resourceClass) {
    if (Property.hasValue(resourceClass)) {
      this.resourceClass = resourceClass;
    } else {
      this.resourceClass = ALL;
    }
  }

  public void setResourceId(final String resourceId) {
    if (Property.hasValue(resourceId)) {
      this.resourceId = resourceId;
    } else {
      this.resourceId = ALL;
    }
  }

  @Override
  public String toString() {
    return this.resourceClass + ":" + this.resourceId + ":" + this.actionName;
  }
}
