package com.freight.management.user_service.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

	@Id
	@Column(name = "menu_id")
	private String menuId;

	@Column(name = "parent_id")
	private String parentId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "angular_route")
	private String angularRoute;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@Column(name = "icon")
	private String icon;

	@Column(name = "level")
	private Integer level;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (isActive == null) {
			isActive = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
