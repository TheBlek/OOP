package ru.nsu.kuklin.pizzeria.customer;

import ru.nsu.kuklin.pizzeria.Order;

/**
 *  Data for Customer.
 */
public record CustomerData(String name, Order[] orders) {}
