package com.freight.management.user_service.user.repository;

import com.freight.management.user_service.user.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

	/**
	 * Find all menu items that are active
	 */
	List<Menu> findByIsActiveTrueOrderByDisplayOrder();

	/**
	 * Find root menu items (those without a parent)
	 */
	List<Menu> findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrder();

	/**
	 * Find child menu items for a given parent
	 */
	List<Menu> findByParentIdAndIsActiveTrueOrderByDisplayOrder(String parentId);

	/**
	 * Find menu item by menu ID
	 */
	Optional<Menu> findByMenuId(String menuId);

	/**
	 * Find all menus, active and inactive
	 */
	List<Menu> findAllByOrderByDisplayOrder();
}
