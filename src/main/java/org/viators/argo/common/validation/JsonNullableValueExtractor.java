package org.viators.argo.common.validation;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Bean Validation {@link ValueExtractor} that unwraps {@link JsonNullable} containers,
 * allowing standard Jakarta constraint annotations (e.g. {@code @Size}, {@code @PastOrPresent})
 * to be placed directly on {@code JsonNullable<T>} fields.
 *
 * Registered via {@code META-INF/services/jakarta.validation.valueextraction.ValueExtractor}
 * so Hibernate Validator discovers it automatically at startup.
 *
 * Behavior:
 * - Undefined (field absent from JSON): no value is extracted, so constraints are skipped.
 * - Present: the inner value is extracted and validated against the declared constraints.
 *
 * @see org.viators.argo.common.util.PatchUtils#applyIfPresent
 */
@UnwrapByDefault
public class JsonNullableValueExtractor implements ValueExtractor<JsonNullable<@ExtractedValue ?>> {

    @Override
    public void extractValues(JsonNullable<?> originalValue, ValueReceiver receiver) {
        if (originalValue != null && originalValue.isPresent()) {
            receiver.value(null, originalValue.get());
        }
    }
}
