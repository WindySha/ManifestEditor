package com.wind.meditor.property;

import com.wind.meditor.utils.PermissionType;

public interface PermissionMapper {
  String map(PermissionType type, String permission);
}
