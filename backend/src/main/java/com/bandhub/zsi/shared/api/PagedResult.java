package com.bandhub.zsi.shared.api;

import java.util.List;

public record PagedResult<T>(List<T> content, long totalElements) {}
