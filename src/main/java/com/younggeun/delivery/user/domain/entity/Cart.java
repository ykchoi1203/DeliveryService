package com.younggeun.delivery.user.domain.entity;

import static com.younggeun.delivery.global.exception.type.CommonErrorCode.DESERIALIZING_CART_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.SERIALIZING_CART_EXCEPTION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.user.domain.dto.CartDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    private List<CartDto> items;

    public Cart(Cart cart) {
        this.items = cart.getItems();
    }

    public String serialize() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RestApiException(SERIALIZING_CART_EXCEPTION);
        }
    }

    public static Cart deserialize(RedisTemplate<String, Object> redisTemplate, String email) {
        try {
            return new ObjectMapper().readValue((String)redisTemplate.opsForValue().get(email), Cart.class);
        } catch (JsonProcessingException e) {
            throw new RestApiException(DESERIALIZING_CART_EXCEPTION);
        }
    }

}
