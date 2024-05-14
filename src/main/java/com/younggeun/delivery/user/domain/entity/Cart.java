package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.user.domain.dto.CartDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    private List<CartDto> items;

}
