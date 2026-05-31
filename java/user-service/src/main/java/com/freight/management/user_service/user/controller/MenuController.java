package com.freight.management.user_service.user.controller;

import com.freight.management.user_service.user.dto.MenuDto;
import com.freight.management.user_service.user.model.Menu;
import com.freight.management.user_service.user.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController {

	@Autowired
	private MenuService menuService;

	/**
	 * Get all active menus in hierarchical structure
	 */
	@GetMapping
	public ResponseEntity<List<MenuDto>> getAllMenus() {
		List<MenuDto> menus = menuService.getAllMenus();
		return ResponseEntity.ok(menus);
	}

	/**
	 * Get all menus including inactive ones
	 */
	@GetMapping("/all")
	public ResponseEntity<List<MenuDto>> getAllMenusIncludeInactive() {
		List<MenuDto> menus = menuService.getAllMenusIncludeInactive();
		return ResponseEntity.ok(menus);
	}

	/**
	 * Get root level menus only
	 */
	@GetMapping("/root")
	public ResponseEntity<List<MenuDto>> getRootMenus() {
		List<MenuDto> menus = menuService.getRootMenus();
		return ResponseEntity.ok(menus);
	}

	/**
	 * Get children of a specific menu
	 */
	@GetMapping("/{parentId}/children")
	public ResponseEntity<List<MenuDto>> getMenuChildren(@PathVariable String parentId) {
		List<MenuDto> menus = menuService.getMenusByParentId(parentId);
		return ResponseEntity.ok(menus);
	}

	/**
	 * Get a specific menu by ID
	 */
	@GetMapping("/{menuId}")
	public ResponseEntity<MenuDto> getMenuById(@PathVariable String menuId) {
		Optional<MenuDto> menu = menuService.getMenuById(menuId);
		return menu.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Create a new menu
	 */
	@PostMapping
	public ResponseEntity<MenuDto> createMenu(@RequestBody Menu menu) {
		MenuDto savedMenu = menuService.saveMenu(menu);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
	}

	/**
	 * Update an existing menu
	 */
	@PutMapping("/{menuId}")
	public ResponseEntity<MenuDto> updateMenu(@PathVariable String menuId, @RequestBody Menu menu) {
		menu.setMenuId(menuId);
		MenuDto updatedMenu = menuService.saveMenu(menu);
		return ResponseEntity.ok(updatedMenu);
	}

	/**
	 * Delete a menu
	 */
	@DeleteMapping("/{menuId}")
	public ResponseEntity<Void> deleteMenu(@PathVariable String menuId) {
		menuService.deleteMenu(menuId);
		return ResponseEntity.noContent().build();
	}
}
