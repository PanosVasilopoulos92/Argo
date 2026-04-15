package org.viators.argo.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.viators.argo.common.enums.ReportPeriod;

import java.util.Arrays;

@Component
public class ReportPeriodConverter implements Converter<String, ReportPeriod> {
    @Override
    public ReportPeriod convert(String source) {
        int value = Integer.parseInt(source);

        return Arrays.stream(ReportPeriod.values())
            .filter(period -> period.getDays() == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Invalid period: %s. Allowed values: 30, 60, 90".formatted(source)));
    }
}
