package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
//    Order order = new Order();


    @Test
    void shouldSaveOrder() {

        Order order = new Order();
        order.setOrderNumber(10100);
        order.setOrderDate(LocalDate.now());
        order.setRequiredDate(LocalDate.now().plusDays(5));
        order.setStatus("In Process");
        order.setComments("Test Order");

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder);
        assertEquals(10100, savedOrder.getOrderNumber());
    }
}
