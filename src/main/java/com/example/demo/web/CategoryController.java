package com.example.demo.web;

import com.example.demo.entity.Category;
import com.example.demo.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Endpoints for managing categories")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Endpoint for retrieving all categories",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categories retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "status" : "error",
                                                        "message" : "Internal server error"
                                                    }
                                                    """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Endpoint for creating a new category. Only allowed categories are: Circular economy, Disease Resistance, Deficit Irrigation and Salinity Management, Weed Management",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                {
                                                    "status" : "error",
                                                    "message" : "Category name is not allowed"
                                                }
                                                """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                {
                                                    "status" : "error",
                                                    "message" : "Internal server error"
                                                }
                                                """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<?> createCategory(@RequestParam String categoryName) {
        Category createdCategory = categoryService.createCategory(categoryName);
        return ResponseEntity.ok(createdCategory);
    }
}
