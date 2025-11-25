package HCMUT.TutorSytem.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class để xử lý pagination
 * Mặc định: 10 items per page
 */
public class PaginationUtil {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /**
     * Tạo Pageable với page mặc định, size mặc định (10 items)
     */
    public static Pageable createDefaultPageable() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    /**
     * Tạo response metadata cho pagination
     */
    public static Map<String, Object> createPaginationMetadata(Page<?> page) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("currentPage", page.getNumber());
        metadata.put("totalPages", page.getTotalPages());
        metadata.put("totalItems", page.getTotalElements());
        metadata.put("pageSize", page.getSize());
        metadata.put("hasNext", page.hasNext());
        metadata.put("hasPrevious", page.hasPrevious());
        return metadata;
    }

    /**
     * Tạo full pagination response với data và metadata
     */
    public static Map<String, Object> createPaginationResponse(Page<?> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("pagination", createPaginationMetadata(page));
        return response;
    }
}

