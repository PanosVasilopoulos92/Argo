package org.viators.argo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.function.Consumer;

/**
 * Utility for applying {@link JsonNullable} fields during partial (PATCH) updates.
 *
 * <p>Encapsulates the "if present, apply" pattern to reduce boilerplate in service
 * methods. Each method checks {@code isPresent()} before invoking the setter,
 * ensuring undefined fields are silently skipped.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * PatchUtils.applyIfPresent(request.getName(), product::setName);
 * PatchUtils.applyIfPresent(request.getPrice(), product::setPrice);
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PatchUtils {

    /**
     * Applies the wrapped value to the given setter if the field was present in the request.
     *
     * <p>If the {@code JsonNullable} is undefined (field absent from JSON), the setter
     * is NOT called and the entity's existing value remains unchanged. If the field is
     * present — even with a null value — the setter IS called with {@code field.get()}.</p>
     *
     * @param field  the JsonNullable field from the patch request DTO
     * @param setter the entity setter method reference to apply the value to
     * @param <T>    the type of the wrapped value
     */
    public static <T> void applyIfPresent(JsonNullable<T> field, Consumer<T> setter) {
        if (field != null && field.isPresent()) {
            setter.accept(field.get());
        }
    }
}
