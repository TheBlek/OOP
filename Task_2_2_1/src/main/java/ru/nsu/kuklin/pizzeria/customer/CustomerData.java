package ru.nsu.kuklin.pizzeria.customer;

import ru.nsu.kuklin.pizzeria.Order;

public record CustomerData(String name, Order[] orders) {}
