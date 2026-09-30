package com.example.resource.service;
import com.example.resource.entity.Resource;

import java.util.List;
public interface ResourceService {
    Resource createResource(Resource resource);

    List<Resource> getAllResources();

    Resource getResourceById(Long id);

    Resource updateResource(Long id, Resource resource);

    void deleteResource(Long id);
}
