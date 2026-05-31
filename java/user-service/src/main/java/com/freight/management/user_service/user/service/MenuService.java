package com.freight.management.user_service.user.service;

import com.freight.management.user_service.user.dto.MenuDto;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	/**
	 * Get all active menus in hierarchical structure
	 */
	public List<MenuDto> getAllMenus() {
		List<Menu> allMenus = menuRepository.findByIsActiveTrueOrderByDisplayOrder();
		return buildMenuHierarchy(allMenus);
	}

	/**
	 * Get all menus (including inactive) in hierarchical structure
	 */
	public List<MenuDto> getAllMenusIncludeInactive() {
		List<Menu> allMenus = menuRepository.findAllByOrderByDisplayOrder();
		return buildMenuHierarchy(allMenus);
	}

	/**
	 * Get root level menus only
	 */
	public List<MenuDto> getRootMenus() {
		List<Menu> rootMenus = menuRepository.findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrder();
		return rootMenus.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Get menus by parent ID
	 */
	public List<MenuDto> getMenusByParentId(String parentId) {
		List<Menu> childMenus = menuRepository.findByParentIdAndIsActiveTrueOrderByDisplayOrder(parentId);
		return childMenus.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	/**
	 * Get menu by ID
	 */
	public Optional<MenuDto> getMenuById(String menuId) {
		return menuRepository.findByMenuId(menuId).map(this::convertToDto);
	}

	/**
	 * Save or update a menu item
	 */
	public MenuDto saveMenu(Menu menu) {
		Menu savedMenu = menuRepository.save(menu);
		return convertToDto(savedMenu);
	}

	/**
	 * Delete a menu item
	 */
	public void deleteMenu(String menuId) {
		menuRepository.deleteById(menuId);
	}

	/**
	 * Build hierarchical structure from flat menu list
	 */
	private List<MenuDto> buildMenuHierarchy(List<Menu> menus) {
		// Convert all to DTOs
		Map<String, MenuDto> menuMap = menus.stream()
				.collect(Collectors.toMap(Menu::getMenuId, this::convertToDto, (existing, replacement) -> existing));

		// Build hierarchy
		List<MenuDto> rootMenus = new ArrayList<>();

		for (MenuDto menuDto : menuMap.values()) {
			if (menuDto.getParentId() == null || menuDto.getParentId().isEmpty()) {
				rootMenus.add(menuDto);
			} else {
				MenuDto parentMenu = menuMap.get(menuDto.getParentId());
				if (parentMenu != null) {
					if (parentMenu.getChildren() == null) {
						parentMenu.setChildren(new ArrayList<>());
					}
					parentMenu.getChildren().add(menuDto);
				}
			}
		}

		// Sort by display order
		rootMenus.sort(Comparator.comparingInt(m -> m.getDisplayOrder() != null ? m.getDisplayOrder() : 0));
		rootMenus.forEach(menu -> sortChildren(menu));

		return rootMenus;
	}

	/**
	 * Recursively sort children by display order
	 */
	private void sortChildren(MenuDto menu) {
		if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
			menu.getChildren()
					.sort(Comparator.comparingInt(m -> m.getDisplayOrder() != null ? m.getDisplayOrder() : 0));
			menu.getChildren().forEach(this::sortChildren);
		}
	}

	/**
	 * Convert Menu entity to MenuDto
	 */
	private MenuDto convertToDto(Menu menu) {
		MenuDto dto = new MenuDto();
		dto.setMenuId(menu.getMenuId());
		dto.setParentId(menu.getParentId());
		dto.setTitle(menu.getTitle());
		dto.setAngularRoute(menu.getAngularRoute());
		dto.setDisplayOrder(menu.getDisplayOrder());
		dto.setIcon(menu.getIcon());
		dto.setLevel(menu.getLevel());
		dto.setIsActive(menu.getIsActive());
		dto.setCreatedAt(menu.getCreatedAt());
		dto.setUpdatedAt(menu.getUpdatedAt());
		return dto;
	}
}
