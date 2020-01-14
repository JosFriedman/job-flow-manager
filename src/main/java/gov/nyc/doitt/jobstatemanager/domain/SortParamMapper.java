package gov.nyc.doitt.jobstatemanager.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class SortParamMapper {

	public static Sort getSort(String[] sortParams, String defaultProperty, Sort.Direction defaultDirection) {

		if (ArrayUtils.isEmpty(sortParams)) {
			return Sort.by(new Order(defaultDirection, defaultProperty));
		}

		if (sortParams.length == 2) {
			if (!sortParams[0].contains(",")) {
				return Sort.by(Direction.fromString(sortParams[1]), sortParams[0]);
			}
		}

		List<Order> orders = Arrays.asList(sortParams).stream().map(p -> {
			String[] sortPair = p.split(",");
			return new Order(Direction.fromString(sortPair[1]), sortPair[0]);
		}).collect(Collectors.toList());

		return Sort.by(orders);
	}
}
