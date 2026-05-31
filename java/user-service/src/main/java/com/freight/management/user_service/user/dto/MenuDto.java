package com.freight.management.user_service.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDto {

	private String menuId;
	private String parentId;
	private String title;
	private String angularRoute;
	private Integer displayOrder;
	private String icon;
	private Integer level;
	private Boolean isActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// For hierarchical structure
	private List<MenuDto> children;
}
