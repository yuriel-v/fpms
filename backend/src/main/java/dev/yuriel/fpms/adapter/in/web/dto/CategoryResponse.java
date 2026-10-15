package dev.yuriel.fpms.adapter.in.web.dto;

/**
 * Inbound web DTO describing a category returned to the client.
 *
 * @param id the category id (string form of the UUID)
 * @param name the category name
 * @param type the classification (INCOME or EXPENSE)
 */
public record CategoryResponse(String id, String name, String type) {}
