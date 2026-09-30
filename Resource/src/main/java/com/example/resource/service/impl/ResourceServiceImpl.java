package com.example.resource.service.impl;

import com.example.resource.entity.Resource;
import com.example.resource.repository.ResourceRepository;
import com.example.resource.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found with id: " + id
                        ));
    }

    @Override
    public Resource updateResource(Long id, Resource resource) {

        Resource existingResource = getResourceById(id);

        existingResource.setName(resource.getName());
        existingResource.setDescription(resource.getDescription());
        existingResource.setType(resource.getType());
        existingResource.setPrice(resource.getPrice());
        existingResource.setAvailable(resource.isAvailable());

        return resourceRepository.save(existingResource);
    }

    @Override
    public void deleteResource(Long id) {

        Resource existingResource = getResourceById(id);

        resourceRepository.delete(existingResource);
    }
}