package com.example.resource.controller;

import com.example.resource.dto.ResourceRequest;
import com.example.resource.dto.ResourceResponse;
import com.example.resource.entity.Resource;
import com.example.resource.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(
            @Valid @RequestBody ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setPrice(request.getPrice());
        resource.setAvailable(request.getAvailable());

        Resource savedResource =
                resourceService.createResource(resource);

        ResourceResponse response = new ResourceResponse(
                savedResource.getId(),
                savedResource.getName(),
                savedResource.getDescription(),
                savedResource.getType(),
                savedResource.getPrice(),
                savedResource.isAvailable()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {

        List<ResourceResponse> response =
                resourceService.getAllResources()
                        .stream()
                        .map(resource -> new ResourceResponse(
                                resource.getId(),
                                resource.getName(),
                                resource.getDescription(),
                                resource.getType(),
                                resource.getPrice(),
                                resource.isAvailable()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable Long id) {

        Resource resource =
                resourceService.getResourceById(id);

        ResourceResponse response = new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getType(),
                resource.getPrice(),
                resource.isAvailable()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setPrice(request.getPrice());
        resource.setAvailable(request.getAvailable());

        Resource updatedResource =
                resourceService.updateResource(id, resource);

        ResourceResponse response = new ResourceResponse(
                updatedResource.getId(),
                updatedResource.getName(),
                updatedResource.getDescription(),
                updatedResource.getType(),
                updatedResource.getPrice(),
                updatedResource.isAvailable()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);

        return ResponseEntity.noContent().build();
    }
}